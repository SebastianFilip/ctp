package cz.vut.sf.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.Vertex;
/**
 * 
 * @author Seba
 * Hindsight Optimization, HOP algorithm introduced by EYERICH (2009)
 * Algorithm will simulate possible outcomes of graph (rollouts)
 * according to probability of its stochastic edges,
 * then it solves SPP, action with the lowest expected
 * mean value will be chosen
 */
public class Hop implements DefaultCtpAlgorithm {
	
public int totalRollouts = 100;
private Set<ExpandedVtx> expandedHistory = new HashSet<ExpandedVtx>(); 

	public Result solve(DefaultCtp ctp, Agent agent) {
		Vertex chosenVtx = null;
		int blockedEdgesRevealed = 0;
    	try {
			do {
				// removes blocked edges adjacent to agent's current position
				agent.senseAction(ctp.g);
				
				if(blockedEdgesRevealed!=ctp.g.getBlockedEdgesRevealed()){
					blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
					expandedHistory = new HashSet<ExpandedVtx>();
				}
				
				Set<Vertex> adjVertexes = StochasticWeightedGraph.getAdjacentVertexes(agent.getCurrentVertex(), ctp.g);
				List<Simulator> simulators = new ArrayList<Simulator>();
				
//				System.out.println(agent.getTraversalHistory());
//				System.out.println("visited size: "+ expandedHistory.size());
				
				initSimulators(ctp, agent, adjVertexes, simulators);
				
				if(!(simulators.size() == 1)){
			    	simulateTravelsals(ctp, simulators);
			    	
			    	chosenVtx = getBestAction(agent, simulators);		
				}else{
					// there is only one possible way so go through it
					chosenVtx = simulators.get(0).agent.getCurrentVertex();
				}
				agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			}while (!(agent.getCurrentVertex().equals(ctp.t)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(agent, "HOP");
	}

	private Vertex getBestAction(Agent agent, List<Simulator> simulators) {
		Vertex chosenVtx;
		int best = Simulator.getBestActionIndex(simulators);
		chosenVtx = simulators.get(best).startingVtx;
		simulators.get(best).agent = null;
		ExpandedVtx expandedVtx = getPreviouslyVisitedVtx(agent.getCurrentVertex());
		
		if(expandedVtx != null){
			expandedVtx.setData(simulators, best);
		}else{
			expandedVtx = new ExpandedVtx(agent.getCurrentVertex());
			expandedVtx.setData(simulators, best);
			expandedHistory.add(expandedVtx);
		}
		return chosenVtx;
	}
	
	private void initSimulators(DefaultCtp ctp, Agent agent,
			Set<Vertex> adjVertexes, List<Simulator> simulators) {
		for (Vertex vtx : adjVertexes) {
			Simulator temp = new Simulator(agent.getCurrentVertex(), vtx, ctp.g);
			ExpandedVtx exVtx = getPreviouslyVisitedVtx(vtx);
			if(exVtx != null){
				temp.totalCost += exVtx.totalExpectedCost;
				temp.totalIterations += exVtx.totalIterationsMade;
			}
			simulators.add(temp);
		}
	}

	private void simulateTravelsals(DefaultCtp ctp, List<Simulator> simulators) {
		int currentRollout = 0;	
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		do{
			currentRollout ++;
			StochasticWeightedGraph rolloutedGraph = ctp.g.doRollout();
			rolloutedGraph.removeAllBlockedEdges();
			dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(rolloutedGraph);
			for(int i = 0; i < simulators.size(); i++){
				// I want my simulate agent to be always in same position and 
				// I am using him only for creating traveling agent
				Agent travellingAgent = new Agent(simulators.get(i).agent);
				shortestPath = dsp.getPath(travellingAgent.getCurrentVertex(), rolloutedGraph.getTerminalVtx());
				if(shortestPath == null){
					System.out.println(new GraphChecker().isGraphConnected(rolloutedGraph));
				}
				double costOfEdgeToChosenVtx = simulators.get(i).agent.getTotalCost();
				simulators.get(i).totalCost += shortestPath.getWeight() + costOfEdgeToChosenVtx;
				simulators.get(i).totalIterations ++;
			}
		}while(currentRollout < totalRollouts);
	}

	private ExpandedVtx getPreviouslyVisitedVtx(Vertex current) {
		for(ExpandedVtx vtx:expandedHistory){
			if(vtx.expandedVtx == current){
				return vtx;
			}
		}
		return null;
	}
	
	private class ExpandedVtx{
		public double totalExpectedCost = 0;
		public int totalIterationsMade = 0;
		public final Vertex expandedVtx;
		public ExpandedVtx(Vertex expanded){
			expandedVtx = expanded;
		}
		
		@Override
		public int hashCode() {
			return expandedVtx.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return expandedVtx.equals(obj);
		}
		
		public void setData(List<Simulator> sims, int chosenOne){
			for(int i = 0; i < sims.size(); i++){
				if(i == chosenOne){continue;}
				this.totalExpectedCost += sims.get(i).totalCost;
				this.totalIterationsMade += sims.get(i).totalIterations;
			}
		}
	}
}
