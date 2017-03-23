package cz.vut.sf.graph;

import java.text.DecimalFormat;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class StochasticWeightedEdge extends DefaultWeightedEdge {
	private static final long serialVersionUID = 1L;
	static DecimalFormat df = new DecimalFormat(".##");
	
	protected double probability;
	protected State actualState;
	public State beliefState;
	
	public static enum State
    {
      UNKNOWN, TRAVESABLE, BLOCKED
    }

	public State getActualState(){
		return this.actualState;
	}
	
	public StochasticWeightedEdge(){
		super();
		beliefState = State.UNKNOWN;
	}
	
	public double getProbability() {
		return probability;
	}

	public void setProbability(double prob) {
		this.probability = prob;
		actualState = Math.random() <= this.probability ? State.BLOCKED : State.TRAVESABLE;
	}
	
	public void reEvaluateState(Graph<Vertex, StochasticWeightedEdge> graph){
		actualState = Math.random() <= this.probability ? State.BLOCKED : State.TRAVESABLE;
		StochasticWeightedEdge oppositeEdge = getOppositeEdge(graph);
		//this must be done to be sure no dead ends are in graph
		//sadly this means different probabilities for opposite edges make no sense
		//only one of them is used (the last one called)
		if(!(oppositeEdge == null)){
			oppositeEdge.actualState = this.getActualState();
		}
		
	}
	
	public StochasticWeightedEdge getOppositeEdge(Graph<Vertex, StochasticWeightedEdge> graph){
		return graph.getEdge((Vertex)this.getTarget(), (Vertex)this.getSource());
	}
	
	@Override
	public String toString() {
		//return "[w,p]=[" + this.getWeight() + ", " + df.format(probability).replace(',','.') +"]";
		if(actualState == State.BLOCKED){
			return "BLOCKED";
		}
		return "T";
	}
	
	@Override
	public Object clone(){
		StochasticWeightedEdge clone;
		clone = (StochasticWeightedEdge) super.clone();
		return clone;
	}
	
}
