package cz.vut.sf.algorithms;

import junit.framework.Assert;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.parsers.ParsedDTO;
import cz.vut.sf.parsers.TestData;
 
public class GraphCheckerTest {
	
	public void testIsGraphConnected_returnFalse(){
		ParsedDTO dto = TestData.getDisconnectedGraphData();
		StochasticWeightedGraph g = new StochasticWeightedGraph(StochasticWeightedEdge.class, dto);
		Assert.assertEquals(false, new GraphChecker().isGraphConnected(g));
	}
	public void testIsGraophConnectes_returnTrue(){
		ParsedDTO dto =  TestData.getConnectedGraphData();
		StochasticWeightedGraph g = new StochasticWeightedGraph(StochasticWeightedEdge.class, dto);
		Assert.assertEquals(true, new GraphChecker().isGraphConnected(g));
	}
}
