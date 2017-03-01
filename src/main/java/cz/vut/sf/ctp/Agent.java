package cz.vut.sf.ctp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;

import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticDirectedWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.graph.Vertex;

public class Agent {
	private double totalCost;	
	private Vertex currentVertex;
	private List<Vertex> traversalHistory;
	
	public Vertex getCurrentVertex(){
		return currentVertex;
	}
	
	public double getTotalCost(){
		return totalCost;
	}
	
	public String printTraversalHistory(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0; i < traversalHistory.size(); i++){
			sb.append(traversalHistory.get(i).toString());
			if(i<traversalHistory.size()-1){
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	// iterates agent through path in a graph if blocked edge appears return false
	// return true if there are no more vertexes to visit -> path is empty
	public boolean traversePath(GraphPath<Vertex, StochasticWeightedEdge> path){
		List<StochasticWeightedEdge> pathEdges = path.getEdgeList();	
		validateInputForTraverse(path, pathEdges);
		
		for (StochasticWeightedEdge e : pathEdges){
			if(e.getActualState() == State.BLOCKED){
				return false;
			}
			setCurrentVertex(path.getGraph().getEdgeTarget(e));
			totalCost += path.getGraph().getEdgeWeight(e);
		}
		return true;
	}
	
	// iterates agent through path in a graph if blocked edge appears return false
	// return true if there are no more vertexes to visit -> path is empty
	public boolean traversePathWithSensing(GraphPath<Vertex, StochasticWeightedEdge> path){
		List<StochasticWeightedEdge> pathEdges = path.getEdgeList();
		validateInputForTraverse(path, pathEdges);
		
		for (StochasticWeightedEdge e : pathEdges){
			if(e.getActualState() == State.BLOCKED){
				senseAction((StochasticDirectedWeightedGraph)path.getGraph());
				return false;
			}
			senseAction((StochasticDirectedWeightedGraph)path.getGraph());
			setCurrentVertex(path.getGraph().getEdgeTarget(e));
			totalCost += path.getGraph().getEdgeWeight(e);
		}
		return true;
	}
	
	private void setCurrentVertex(Vertex newVertex){
		currentVertex = newVertex;
		traversalHistory.add(currentVertex);
	}

	private void validateInputForTraverse(GraphPath<Vertex, StochasticWeightedEdge> path,
			List<StochasticWeightedEdge> pathEdges) {
		if(currentVertex != path.getStartVertex()){
			throw new CtpException("Agent's method 'traversPath' called for Vtx != Agent's currentVertex!");
		}
		
		if(!pathEdges.isEmpty() && pathEdges.get(0).beliefState == State.UNKNOWN){
			throw new CtpException("Agent's method 'traversPath' called with first edge belief state:"
				+ pathEdges.get(0).getActualState().name());
		}
	}
	
	public Agent(Vertex initialVertex){
		currentVertex = initialVertex;
		totalCost = 0;
		traversalHistory = new ArrayList<Vertex>();
		traversalHistory.add(currentVertex);
	}
	
	public void senseAction(StochasticDirectedWeightedGraph g){
		Set<StochasticWeightedEdge> adjacentEdges = g.edgesOf(currentVertex);
		for (StochasticWeightedEdge edge : adjacentEdges){
			edge.beliefState = edge.getActualState();
			if(edge.beliefState == State.BLOCKED){
				g.removeEdge(edge);
			}
		}
	}
	public void senseActionIterator(StochasticDirectedWeightedGraph g){
		Set<StochasticWeightedEdge> adjacentEdges = g.edgesOf(currentVertex);
		for (Iterator<StochasticWeightedEdge> iter = adjacentEdges.iterator(); iter.hasNext();){
			iter.next().beliefState = iter.next().getActualState();
			if(iter.next().beliefState == State.BLOCKED){
				g.removeEdge(iter.next());
				iter.remove();
			}
		}
	}
}
