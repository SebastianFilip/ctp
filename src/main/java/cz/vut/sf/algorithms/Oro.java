package cz.vut.sf.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.Vertex;
/**
 * 
 * @author Seba
 * Optimistic Rollout Algorithm, ORO algorithm introduced by EYERICH (2009)
 * Algorithm will simulate possible outcomes of graph (rollouts)
 * according to probability of its stochastic edges,
 * then it simulate traversal optimistically (by Greedy Algorithm), 
 * action with the lowest expected mean value will be chosen.
 */
public class Oro implements DefaultCtpAlgorithm {
	public int totalRollouts = 100;
	public Result solve(DefaultCtp ctp, Agent agent) {
		Vertex chosenVtx = null;
		int currentRollout = 0;
    	try {
			do {
				currentRollout = 0;
				// removes blocked edges adjacent to agent's current position
				agent.senseAction(ctp.g);
				
				// init fields
				Set<Vertex> adjVertexes = StochasticWeightedGraph.getAdjacentVertexes(agent.getCurrentVertex(), ctp.g);
				List<Simulator> simulators = new ArrayList<Simulator>();
				for (Vertex vtx : adjVertexes) {
					simulators.add(new Simulator(agent.getCurrentVertex(), vtx, ctp));
				}
				
				if(!(simulators.size() == 1)){
			    	do{
			    		StochasticWeightedGraph rolloutedGraph = ctp.g.doRollout();
			    		currentRollout ++;
//			    		System.out.println(rolloutedGraph);
				    	for(int i = 0; i < simulators.size(); i++){
				    		// I want my simulate agent to be always in same position and 
				    		// I am using him only for creating traveling agent
				    		Agent travellingAgent = new Agent(simulators.get(i).agent);
				    		travellingAgent.senseAction(rolloutedGraph);
				    		GreedyAlgorithm.traverseByGa(rolloutedGraph, rolloutedGraph.getTargetVtx(), travellingAgent, false);
				    		simulators.get(i).totalCost += travellingAgent.getTotalCost();
				    		simulators.get(i).totalIterations ++;
				    	}
			    	}while(currentRollout < totalRollouts);
			    	chosenVtx = getBestAction(simulators);
				}else{
					// there is only one possible way so go through it
					chosenVtx = simulators.get(0).agent.getCurrentVertex();
				}
				agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			}while (!(agent.getCurrentVertex().equals(ctp.t)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(agent, "ORO");
	}

	private Vertex getBestAction(List<Simulator> simulators) {
		Vertex result = null;
		double expectedMinCost = Double.MAX_VALUE;
		for(int i = 0; i < simulators.size(); i++){
			
	    	System.out.println("average cost for Vtx [" + simulators.get(i).startingVtx 
					+"] is : "+ simulators.get(i).totalCost/simulators.get(i).totalIterations);
	    	
			double averageCost = simulators.get(i).totalCost/simulators.get(i).totalIterations;
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = simulators.get(i).startingVtx;
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
		@Override
		public String toString(){
			return "average cost for Vtx [" + startingVtx +"] is : "+ totalCost/totalIterations;
		}
	}

}
