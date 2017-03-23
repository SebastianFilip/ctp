package cz.vut.sf.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
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
static int currentRollout = 0;	
public int totalRollouts = 100;
	public Result solve(DefaultCtp ctp, Agent agent) {
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		Vertex chosenVtx = null;
		int blockedEdgesRevealed = 0;
		boolean isGraphChanged = false;
    	try {
			do {
				currentRollout = 0;	
				// removes blocked edges adjacent to agent's current position
				agent.senseAction(ctp.g);
				if(blockedEdgesRevealed!=ctp.g.getBlockedEdgesRevealed()){
					blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
					isGraphChanged = true;
				}else{
					isGraphChanged = false;
				}
				// init fields
				Set<Vertex> adjVertexes = StochasticWeightedGraph.getAdjacentVertexes(agent.getCurrentVertex(), ctp.g);
				List<Simulator> simulators = new ArrayList<Simulator>();
				for (Vertex vtx : adjVertexes) {
					simulators.add(new Simulator(agent.getCurrentVertex(), vtx, ctp));
				}
				
				if(!(simulators.size() == 1)){
			    	do{
			    		currentRollout ++;
			    		StochasticWeightedGraph rolloutedGraph = ctp.g.doRollout();
			    		rolloutedGraph.removeAllBlockedEdges();
			    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(rolloutedGraph);
				    	for(int i = 0; i < simulators.size(); i++){
				    		// I want my simulate agent to be always in same position and 
				    		// I am using him only for creating traveling agent
				    		Agent travellingAgent = new Agent(simulators.get(i).agent);
				    		shortestPath = dsp.getPath(travellingAgent.getCurrentVertex(), rolloutedGraph.getTargetVtx());
				    		if(shortestPath == null){
				    			System.out.println(new GraphChecker().isGraphConnected(rolloutedGraph));
				    		}
				    		double costOfEdgeToChosenVtx = simulators.get(i).agent.getTotalCost();
				    		simulators.get(i).totalCost += shortestPath.getWeight() + costOfEdgeToChosenVtx;
				    		simulators.get(i).totalIterations ++;
//				    		System.out.println("average cost for Vtx [" + simulators.get(i).startingVtx 
//				    				+"] is : "+ simulators.get(i).totalCost/simulators.get(i).totalIterations);
				    	}
			    	}while(currentRollout < totalRollouts);
			    	chosenVtx = getBestAction(simulators);
			    	//tyto podmínky jsou zavedeny protoze dochazelo k nekonecnym cyklum v subotimpu x->y->x->y->x
			    	//kdyby to dochazelo mezi tremi neni vyreseno todo x->y->x->z->x->y->x->z...
					if(!isGraphChanged){
						int traversalHistorySize = agent.getTraversalHistory().size();
						if(traversalHistorySize >= 3){
							//predposledni vtx je stejny?
							if(agent.getTraversalHistory().get(traversalHistorySize-2).equals(chosenVtx)){
								int best = getBestActionIndex(simulators);
								simulators.get(best).totalCost = Double.MAX_VALUE;
								chosenVtx = simulators.get(0).agent.getCurrentVertex();
							}
						}
					}
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

	private Vertex getBestAction(List<Simulator> simulators) {
		Vertex result = null;
		double expectedMinCost = Double.MAX_VALUE;
		for(int i = 0; i < simulators.size(); i++){
//	    	System.out.println("average cost for Vtx [" + simulators.get(i).startingVtx 
//					+"] is : "+ simulators.get(i).totalCost/simulators.get(i).totalIterations);
	    	
			double averageCost = simulators.get(i).totalCost/simulators.get(i).totalIterations;
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = simulators.get(i).startingVtx;
			}
		}
		return result;
	}
	
	private int getBestActionIndex(List<Simulator> simulators) {
		int result = 0;
		double expectedMinCost = Double.MAX_VALUE;
		for(int i = 0; i < simulators.size(); i++){	    	
			double averageCost = simulators.get(i).totalCost/simulators.get(i).totalIterations;
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = i;
			}
		}
		return result;
	}
	
	//holds copies of agent and moves them towards to specified vtx
	public class Simulator{
		public final Agent agent;
		public final Vertex startingVtx;
		public double totalCost = 0;
		public int totalIterations = 0;
		public Simulator(Vertex currentVtx, Vertex startingVtx, DefaultCtp ctp){
			this.agent = new Agent(currentVtx);
			this.startingVtx = startingVtx;
			this.agent.traverseToAdjancetVtx(ctp.g, this.startingVtx);
		}
	}
}
