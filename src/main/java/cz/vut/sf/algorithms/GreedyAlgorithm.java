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
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp; 
    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
    	int counter = 0;
    	do {
    		counter++;
    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
    		shortestPath = dsp.getPath(agent.getCurrentVertex(), ctp.t);
    		if(counter > 3){
    			counter = 3;
    		}
    		System.out.println(agent.getCurrentVertex().toString());
    	}while (!agent.traversePathWithSensing(shortestPath));
		return new Result(agent, "greedy");
	}
}
