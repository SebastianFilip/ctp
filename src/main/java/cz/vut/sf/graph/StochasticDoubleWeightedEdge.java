package cz.vut.sf.graph;

import org.jgrapht.WeightedGraph;

public class StochasticDoubleWeightedEdge extends StochasticWeightedEdge {
	private static final long serialVersionUID = 1L;
	private double penalization;
	
	// Value cost that is required to get rid of penalization
	private double recoveryThresholdValue;
	
	public StochasticDoubleWeightedEdge(double p){
		super();
		setPenalization(p);
	}
	
	public double getPenalization() {
		return penalization;
	}
	
	public void setPenalization(double penalization) {
		this.penalization = penalization;
	}
	
	public void applyPenalization(WeightedGraph<Vertex, StochasticWeightedEdge> g, double currentValue){
		if(this.actualState == State.BLOCKED){
			g.setEdgeWeight(this, this.getWeight() + penalization);
			recoveryThresholdValue = currentValue + penalization;
			this.actualState = State.TRAVESABLE;
		}
		else{
			//log error, throw exception?
			System.out.println("setPenalizedWeight called for non blocked edge");
		}
	}
	public void recoverWeight(WeightedGraph<Vertex, StochasticWeightedEdge> g, double currentValue){
		if(recoveryThresholdValue <= currentValue){
			g.setEdgeWeight(this, this.getWeight() - penalization);
			// the question is: should once recovered edge be able to be penalized (blocked) again?
			// by belief state UNKNOWN is meant that it should be able to.
			// thus re-evaluate probability assign to edge must be done
			// to know its actual state
			this.beliefState = State.UNKNOWN;
			StochasticWeightedEdge oppositeDirEdge = getOppositeEdge(g);
			if(oppositeDirEdge != null && oppositeDirEdge.beliefState == State.UNKNOWN){
				//this means that opposite edge has already been re-evaluated
				return;
			}
			this.setProbability(this.getProbability());
		}
	}
}
