package cz.vut.sf.algorithms;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.graph.Vertex;

public class GraphChecker{
	public boolean isGraphConnected(StochasticWeightedGraph g){
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
}
