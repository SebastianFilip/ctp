package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.gui.LoggerClass;

public class RepositionAlgorithm extends LoggerClass implements DefaultCtpAlgorithm {

	public Result solve(DefaultCtp ctp, Agent agent) {
		LOG.info("Starting Reposition Algorithm");
		agent.senseAction(ctp.g);
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
    	do {
    		// return to source vertex (first run is zero cost)
    		// returnig back by SP -> mby not the same path
    		// -> mby discover new blocked edges 
    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
    		agent.traversePath(dsp.getPath(agent.getCurrentVertex(), ctp.s));
    		shortestPath = dsp.getPath(agent.getCurrentVertex(), ctp.t);
    		agent.traversePathWithSensing(shortestPath);
    	}while (agent.getCurrentVertex() != ctp.t);
		return new Result(agent, "Reposition");
	}
}
