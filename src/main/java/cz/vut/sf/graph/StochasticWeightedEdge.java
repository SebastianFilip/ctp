package cz.vut.sf.graph;

import java.text.DecimalFormat;

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
	
	@Override
	public String toString() {
		//return "[w,p]=[" + this.getWeight() + ", " + df.format(probability).replace(',','.') +"]";
		return actualState.name();
	}
}
