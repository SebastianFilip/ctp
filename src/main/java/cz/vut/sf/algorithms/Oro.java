package cz.vut.sf.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
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
public class Oro extends LoggerClass implements DefaultCtpAlgorithm {
	public int totalRollouts = 500;
	public Result solve(DefaultCtp ctp, Agent agent) {
		LOG.info("Starting ORO, total rollouts = " + totalRollouts);
		Vertex chosenVtx = null;
    	try {
			do {
				// removes blocked edges adjacent to agent's current position
				agent.senseAction(ctp.g);
				
				// init fields
				Set<Vertex> adjVertexes = StochasticWeightedGraph.getAdjacentVertexes(agent.getCurrentVertex(), ctp.g);
				List<Simulator> simulators = new ArrayList<Simulator>();
				for (Vertex vtx : adjVertexes) {
					simulators.add(new Simulator(agent.getCurrentVertex(), vtx, ctp.g));
				}
				
				if(!(simulators.size() == 1)){
			    	simulateTravelsals(ctp, simulators);
			    	chosenVtx = Simulator.getBestAction(simulators);
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
		return new Result(agent, "ORO");
	}
	private void simulateTravelsals(DefaultCtp ctp, List<Simulator> simulators) {
		int currentRollout = 0;
		do{
			currentRollout ++;
			StochasticWeightedGraph rolloutedGraph = ctp.g.doRollout();
			for(int i = 0; i < simulators.size(); i++){
				// I want my simulate agent to be always in same position and 
				// I am using him only for creating traveling agent
				StochasticWeightedGraph travellingGraph = (StochasticWeightedGraph) rolloutedGraph.clone();
				Agent travellingAgent = new Agent(simulators.get(i).agent);
				travellingAgent.senseAction(travellingGraph);
				GreedyAlgorithm.traverseByGa(travellingGraph, travellingGraph.getTerminalVtx(), travellingAgent, false);
				simulators.get(i).totalCost += travellingAgent.getTotalCost();
				simulators.get(i).totalIterations ++;
			}
		}while(currentRollout < totalRollouts);
	}
}
