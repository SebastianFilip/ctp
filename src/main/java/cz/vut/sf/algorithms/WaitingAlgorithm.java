package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;

public class WaitingAlgorithm implements DefaultCtpAlgorithm{
	//todo test it works!
	public Result solve(DefaultCtp ctp, Agent agent) {
		agent.senseAction(ctp.g);
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
		shortestPath = dsp.getPath(agent.getCurrentVertex(), ctp.t);
    	do {
    		agent.tryRecoverPenalizedEdges(ctp.g);
    		agent.traverseRecPath(shortestPath);
    	}while (!(agent.getCurrentVertex().equals(ctp.t)));
	
		return new Result(agent, "waiting");
	}

}
