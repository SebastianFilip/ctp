package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.graph.Vertex;

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
    		if(shortestPath.getEdgeList().get(0).beliefState == State.UNKNOWN){
    			Vertex nic = null;
    		}
    		agent.traversePathWithSensing(shortestPath);
    	}while (agent.getCurrentVertex() != ctp.t);
		return new Result(agent, "reposition");
	}
}
