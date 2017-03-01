package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;

public class GreedyAlgorithm implements DefaultCtpAlgorithm {

	public Result solve(DefaultCtp ctp, Agent agent) {
		agent.senseAction(ctp.g);
    	traverseByGa(ctp, agent, false);
		return new Result(agent, "greedy");
	}
	
	public void traverseByGa(DefaultCtp ctp, Agent agent, boolean oneCycle){
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		boolean travelFinished;
    	do {
    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
    		shortestPath = dsp.getPath(agent.getCurrentVertex(), ctp.t);
    		travelFinished = agent.traversePathWithSensing(shortestPath);
    	}while (!(travelFinished || oneCycle));
	}
}
