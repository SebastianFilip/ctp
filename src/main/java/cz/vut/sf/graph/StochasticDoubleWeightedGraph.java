package cz.vut.sf.graph;

import java.util.Set;

public class StochasticDoubleWeightedGraph extends
		StochasticWeightedGraph {
	//TODO
	public StochasticDoubleWeightedGraph(
			Class<? extends StochasticWeightedEdge> edgeClass) {
		super(edgeClass);
	}
	
	//makes a new sdwg from swg
	public StochasticDoubleWeightedGraph(StochasticWeightedGraph swg) {
		super(StochasticDoubleWeightedEdge.class);
		
		castEdges(swg);
	}

	private void castEdges(StochasticWeightedGraph swg) {
		EdgeConvertor ec = new EdgeConvertor();
		// promyslet si návrh doubleWeighted grafu!!!
		Set<StochasticWeightedEdge> edges = swg.edgeSet();
		for(StochasticWeightedEdge edge : edges){
//			swg.addEdge(sourceVertex, targetVertex)
		}
	}

}
