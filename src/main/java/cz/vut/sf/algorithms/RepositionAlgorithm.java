package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;

public class RepositionAlgorithm implements DefaultCtpAlgorithm {

	public Result solve(DefaultCtp ctp, Agent agent) {
		agent.senseAction(ctp.g);
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
    	do {
    		// return to source vertex (first run is zero cost)
    		// returnig back by SP -> mby not the same path
    		// -> mby discover new blocked edges 
    		agent.traversePath(dsp.getPath(agent.getCurrentVertex(), ctp.s));
    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
    		shortestPath = dsp.getPath(agent.getCurrentVertex(), ctp.t);
    	}while (!agent.traversePathWithSensing(shortestPath));
		return new Result(agent, "reposition");
	}
}
