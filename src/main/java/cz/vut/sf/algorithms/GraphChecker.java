package cz.vut.sf.algorithms;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.parsers.ParsedDTO;

public class GraphChecker{
	public static boolean isGraphConnected(StochasticWeightedGraph g){
		Set<Vertex> visitedVertexes = new HashSet<Vertex>();
		
		Stack <Vertex> open = new Stack<Vertex>();
		open.push(g.getSourceVtx());
		boolean result = false;
		do{
			Vertex currentVtx = open.pop();
			if(currentVtx.equals(g.getTerminalVtx())){
				result = true;
				break;
			}
			visitedVertexes.add(currentVtx);
			Set<StochasticWeightedEdge> edgesOfCurrentVtx = g.edgesOf(currentVtx);
			for(StochasticWeightedEdge edge : edgesOfCurrentVtx){
				if(edge.getActualState()!=State.BLOCKED){
					if(!g.getEdgeTarget(edge).equals(edge)
							&& !visitedVertexes.contains(g.getEdgeTarget(edge)) 
							&& !open.contains(g.getEdgeTarget(edge))){
					//push edge's target to open list (if target was not already visited)
					open.push(g.getEdgeTarget(edge));
					}
				}
			}
		}while(!open.empty());
//		System.out.println("is connected: "+result);
		return result;
	}
	
	public static int countFailedRollouts(ParsedDTO parsedDto, int totalTries){
		StochasticWeightedGraph rolloutedGraph = null;
		int failedTimes = 0;
		for(int i = 0; i < totalTries; i++){
			rolloutedGraph = new StochasticWeightedGraph(StochasticWeightedEdge.class, parsedDto);
			rolloutedGraph.removeAllBlockedEdges();
			DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(rolloutedGraph);
	    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(rolloutedGraph.getSourceVtx(), rolloutedGraph.getTerminalVtx());
			if(shortestPath == null){
				failedTimes++;
			}
		}
		return failedTimes;
	}
}
