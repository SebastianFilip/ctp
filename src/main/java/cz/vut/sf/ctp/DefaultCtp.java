package cz.vut.sf.ctp;


import cz.vut.sf.graph.StochasticDirectedWeightedGraph;
import cz.vut.sf.graph.Vertex;

public class DefaultCtp {

	public StochasticDirectedWeightedGraph g;
	public final Vertex s;
	public final Vertex t;
	public final int k;
	
	public DefaultCtp(StochasticDirectedWeightedGraph sdwg, Vertex s,
			Vertex t, int k) {
		super();
		this.g = sdwg;
		this.s = s;
		this.t = t;
		this.k = k;
	}
	
	public DefaultCtp(StochasticDirectedWeightedGraph sdwg, Vertex s,
			Vertex t) {
		super();
		this.g = sdwg;
		this.s = s;
		this.t = t;
		this.k = sdwg.getAllVertexes().size();
	}

}
