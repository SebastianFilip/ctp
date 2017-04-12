package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;

public class ComparisionAlgorithm extends LoggerClass implements DefaultCtpAlgorithm {

	public Result solve(DefaultCtp ctp, Agent agent) {
		LOG.info("Starting Comparision Algorithm");
		agent.senseAction(ctp.g);
    	
    	DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
    	GraphPath<Vertex, StochasticWeightedEdge> dspFromSourceToTerminal;
    	GraphPath<Vertex, StochasticWeightedEdge> dspFromCurrentToTerminal;
    	int counter = 0;
    	
    	do {
    		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
    		dspFromSourceToTerminal = dsp.getPath(ctp.s, ctp.t);
    		dspFromCurrentToTerminal = dsp.getPath(agent.getCurrentVertex(), ctp.t);
    		
    		if(dspFromCurrentToTerminal.getWeight() <= dspFromSourceToTerminal.getWeight()){
    			agent.traversePathWithSensing(dspFromCurrentToTerminal);
    		}
    		else{
    			//return
    			agent.traversePath(dsp.getPath(agent.getCurrentVertex(), ctp.s));
    			agent.traversePathWithSensing(dspFromSourceToTerminal);
    		}
    		counter++;
    		if(counter > 3){
    			counter = 3;
    		}
    	}while (!(agent.getCurrentVertex().equals(ctp.t)));
		return new Result(agent, "comparision");
	}

}
