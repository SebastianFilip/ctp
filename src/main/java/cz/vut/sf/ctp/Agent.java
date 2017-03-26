package cz.vut.sf.ctp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;

import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedGraph;
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
	/**
	 * 
	 * @return previously visited vertex, null if isGraphChanged == true
	 * null is also returned if there is no previous vertex visited
	 */
	public Vertex getPreviousVertex(boolean isGraphChanged){
		if(isGraphChanged)return null;
		int index = traversalHistory.size()-2;
		if(index < 0)return null;
		return traversalHistory.get(index);
	}
	
	public double getTotalCost(){
		return totalCost;
	}
	
	public List<Vertex> getTraversalHistory(){
		return traversalHistory;
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
			senseAction((StochasticWeightedGraph)path.getGraph());
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
	
	public Agent(Agent source){
		this.currentVertex = new Vertex(source.currentVertex);
		this.penalizedEdges = new HashSet<StochasticDoubleWeightedEdge>(source.penalizedEdges);
		this.traversalHistory = new ArrayList<Vertex>(source.traversalHistory);
		this.totalCost = source.totalCost;
		this.penalizedEdges = new HashSet<StochasticDoubleWeightedEdge>(source.penalizedEdges);
	}
	
	public void senseAction(StochasticWeightedGraph g){
		Set<StochasticWeightedEdge> adjacentEdges = g.edgesOf(currentVertex);
		for (StochasticWeightedEdge edge : adjacentEdges){
			edge.beliefState = edge.getActualState();
			if(edge.beliefState == State.BLOCKED){
				g.removeEdge(edge);
			}
		}
	}

	public void traverseToAdjancetVtx(StochasticWeightedGraph g,
			Vertex target) {
		StochasticWeightedEdge chosenEdge = g.getEdge(this.currentVertex, target);
		if(chosenEdge == null){
			throw new CtpException("Specified vtx: " + target + " is not adjancent to cur_ctx: " + this.currentVertex);
		}
		if(chosenEdge.beliefState == State.UNKNOWN){
			throw new CtpException("Agent's method 'traverseToAdjancetVtx' called for edge which belief state: UNKNOWN");
		}
		setCurrentVertex(target);
		this.totalCost += g.getEdgeWeight(chosenEdge);
	}
	//-----------methods for recoverable graph-----------
	
	// iterates agent through path in a graph if blocked edge appears return false
	// return true if there are no more vertexes to visit -> path is empty
	public boolean traverseRecPath(GraphPath<Vertex, StochasticWeightedEdge> path){
		List<StochasticWeightedEdge> pathEdges = path.getEdgeList();	
		validateInputForRecTraverse(path, pathEdges);
			
		for (StochasticWeightedEdge e : pathEdges){
			senseAction((StochasticWeightedGraph)path.getGraph());
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
	
	public void tryRecoverPenalizedEdges(StochasticWeightedGraph g){
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
