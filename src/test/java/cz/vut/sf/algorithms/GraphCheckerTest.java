package cz.vut.sf.algorithms;

import junit.framework.Assert;
import cz.vut.sf.graph.StochasticDirectedWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.parsers.ParsedDTO;
import cz.vut.sf.parsers.TestData;
 
public class GraphCheckerTest {
	
	public void testIsGraphConnected_returnFalse(){
		ParsedDTO dto = TestData.getDisconnectedGraphData();
		StochasticDirectedWeightedGraph g = new StochasticDirectedWeightedGraph(StochasticWeightedEdge.class, dto);
		Assert.assertEquals(false, new GraphChecker().isGraphConnected(g));
	}
	public void testIsGraophConnectes_returnTrue(){
		ParsedDTO dto =  TestData.getConnectedGraphData();
		StochasticDirectedWeightedGraph g = new StochasticDirectedWeightedGraph(StochasticWeightedEdge.class, dto);
		Assert.assertEquals(true, new GraphChecker().isGraphConnected(g));
	}
}
