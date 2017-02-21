package cz.vut.sf.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

public class StochasticWeightedEdge extends DefaultWeightedEdge {
	private static final long serialVersionUID = 1L;

	private int probability;

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}
	
}
