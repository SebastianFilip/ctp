package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.Vertex;

public class GreedyAlgorithm extends LoggerClass implements DefaultCtpAlgorithm {

	public Result solve(DefaultCtp ctp, Agent agent) {
		LOG.info("Starting Greedy Algorithm");
		agent.senseAction(ctp.g);
    	traverseByGa(ctp.g, ctp.t, agent, false);
		return new Result(agent, "greedy");
	}
	/**
	 * Traverse path with sensing by GA (if agent's current position was not sensed yet it may cause failures)
	 * @param g - graph on which traverse will be done
	 * @param t - termination Vertex
	 * @param agent
	 * @param oneCycle - if true method will be terminated after blocked edge on chosen SPP is revealed
	 */
	public static void traverseByGa(StochasticWeightedGraph g, Vertex t, Agent agent, boolean oneCycle){
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		boolean travelFinished;
    	do {
    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(g);
    		shortestPath = dsp.getPath(agent.getCurrentVertex(), t);
    		travelFinished = agent.traversePathWithSensing(shortestPath);
    	}while (!(travelFinished || oneCycle));
	}
	
	public static boolean traverseByGa(StochasticWeightedGraph g, Vertex t, Agent agent){
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		boolean travelFinished;
    	do {
    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(g);
    		shortestPath = dsp.getPath(agent.getCurrentVertex(), t);
    		if(shortestPath == null){
    			//graph is not connected to the desired target t
    			return false;
    		}
    		travelFinished = agent.traversePathWithSensing(shortestPath);
    	}while (!(travelFinished));
		return true;
	}
}
