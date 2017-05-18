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
public class Hop extends DefaultCtpAlgorithm {
	
public Hop(DefaultCtp ctp, Agent agent) {
		super(ctp, agent);
	}

private int totalRollouts = 100;
// expandedHistory Set bias HOP to do more exploring by "remembering" previously visited vtxs
// and setting its estimated cost for all other vertexes but chosen one. This is done till new blockage 
// is explored, then Set is nulled.
// TODO think if only last visit estimation should be used than all of the previous visits cost estimations
private Set<ExpandedVtx> expandedHistory = new HashSet<ExpandedVtx>(); 
	@Override
	public Result solve() {
		LOG.info("Starting HOP, total rollouts = " + totalRollouts);
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
				
				initSimulators(ctp, agent, adjVertexes, simulators);
				
				if(!(simulators.size() == 1)){
			    	doSimulation(ctp, simulators);
			    	int best = Simulator.getBestActionIndex(simulators);
			    	chosenVtx = simulators.get(best).startingVtx;
			    	actualizeExpandedHistory(best, simulators, agent);
//			    	chosenVtx = getBestAction(agent, simulators);		
				}else{
					// there is only one possible way so go through it
					chosenVtx = simulators.get(0).agent.getCurrentVertex();
				}
				LOG.debug("Chosen vtx = " + chosenVtx);
				agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			}while (!(agent.getCurrentVertex().equals(ctp.t)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(agent, "HOP");
	}

	private void actualizeExpandedHistory(int best, List<Simulator> simulators, Agent agent) {
		ExpandedVtx expandedVtx = getPreviouslyVisitedVtx(agent.getCurrentVertex());
		
		if(expandedVtx != null){
			expandedVtx.setData(simulators, best);
		}else{
			expandedVtx = new ExpandedVtx(agent.getCurrentVertex());
			expandedVtx.setData(simulators, best);
			expandedHistory.add(expandedVtx);
		}
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

	private void doSimulation(DefaultCtp ctp, List<Simulator> simulators) {
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
	
	
	public void setTotalRollouts(int r){
		totalRollouts = r;
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
		
		@Override
		public String toString() {
			return expandedVtx.toString() + " ~= " + totalExpectedCost/totalIterationsMade;
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
