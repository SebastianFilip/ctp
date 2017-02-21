package cz.vut.sf.ctp;

import java.util.Set;

import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;

public class DefaultCtp {

	private final Set<Vertex> V;
	private final Set<StochasticWeightedEdge> E;
	public final Vertex s;
	public final Vertex t;
	public final int k;
	
	public Set<Vertex> getV() {
		return V;
	}

	public Set<StochasticWeightedEdge> getE() {
		return E;
	}
	
	public DefaultCtp(Set<Vertex> v, Set<StochasticWeightedEdge> e, Vertex s,
			Vertex t, int k) {
		super();
		this.V = v;
		this.E = e;
		this.s = s;
		this.t = t;
		this.k = k;
	}

}
