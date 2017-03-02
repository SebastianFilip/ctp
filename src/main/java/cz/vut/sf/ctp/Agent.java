package cz.vut.sf.ctp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;

import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticDirectedWeightedGraph;
import cz.vut.sf.graph.StochasticDoubleWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.graph.Vertex;

public class Agent {
	private double totalCost;	
	private Vertex currentVertex;
	private List<Vertex> traversalHistory;
	
	private Set<StochasticDoubleWeightedEdge> penalizedEdges;
	
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
			senseAction((StochasticDirectedWeightedGraph)path.getGraph());
			if(e.beliefState == State.BLOCKED){
				return false;
			}
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
			throw new CtpException("Agent's method 'traversPath' called with first edge belief state: UNKNOWN");
		}
	}
	
	public Agent(Vertex initialVertex){
		currentVertex = initialVertex;
		totalCost = 0;
		traversalHistory = new ArrayList<Vertex>();
		traversalHistory.add(currentVertex);
		penalizedEdges = new HashSet<StochasticDoubleWeightedEdge>();
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
	
	//-----------methods for recoverable graph-----------
	
	// iterates agent through path in a graph if blocked edge appears return false
	// return true if there are no more vertexes to visit -> path is empty
	public boolean traverseRecPath(GraphPath<Vertex, StochasticWeightedEdge> path){
		List<StochasticWeightedEdge> pathEdges = path.getEdgeList();	
		validateInputForRecTraverse(path, pathEdges);
			
		for (StochasticWeightedEdge e : pathEdges){
			senseAction((StochasticDirectedWeightedGraph)path.getGraph());
			if(e.getActualState() == State.BLOCKED){
				StochasticWeightedEdge oppositeDirEdge = e.getOppositeEdge(path.getGraph());
				WeightedGraph<Vertex, StochasticWeightedEdge> g = (WeightedGraph<Vertex, StochasticWeightedEdge>)path.getGraph();
				((StochasticDoubleWeightedEdge)e).applyPenalization(g, totalCost);
				((StochasticDoubleWeightedEdge)oppositeDirEdge).applyPenalization(g, totalCost);
				penalizedEdges.add((StochasticDoubleWeightedEdge) e);
				penalizedEdges.add((StochasticDoubleWeightedEdge) oppositeDirEdge);
				if(oppositeDirEdge != null && oppositeDirEdge.beliefState == State.UNKNOWN){
					//this means that opposite edge has already been re-evaluated
				}
				return false;
			}
			setCurrentVertex(path.getGraph().getEdgeTarget(e));
			totalCost += path.getGraph().getEdgeWeight(e);
		}
		return true;
	}
	
	public void tryRecoverPenalizedEdges(StochasticDirectedWeightedGraph g){
		for (StochasticDoubleWeightedEdge e :penalizedEdges){
			//recover will be successful for edges blocked for penalization cost 
			e.recoverWeight(g, totalCost);
		}
	}
	
	private void validateInputForRecTraverse(GraphPath<Vertex, StochasticWeightedEdge> path,
			List<StochasticWeightedEdge> pathEdges) {
		validateInputForTraverse(path, pathEdges);
		
		if(!pathEdges.isEmpty() && !(pathEdges.get(0) instanceof StochasticDoubleWeightedEdge)){
			throw new CtpException("Agent's method 'traversRecPath' called with non recoverable edge instance");
		}
	}
}
