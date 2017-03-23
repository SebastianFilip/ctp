package cz.vut.sf.graph;


public class EdgeConvertor {
	// to be more sure about overflow /10E100 (it should be ok even without it)
	// info max double is ~1.79e308
	private static final double DEFAULT_PENALIZATION = Double.MAX_VALUE / 10E100;
	
	public StochasticDoubleWeightedEdge toStochasticDoubleWeightedEdge(StochasticWeightedEdge swe){
		return new StochasticDoubleWeightedEdge(DEFAULT_PENALIZATION);
	}
	public StochasticDoubleWeightedEdge toStochasticDoubleWeightedEdge(StochasticWeightedEdge swe, double penalization){
		return new StochasticDoubleWeightedEdge(penalization);
	}
	
	//TODO: move to tests
	public static void testOverFlow(){
		double testDouble = DEFAULT_PENALIZATION;
		for (int i=1; i<100;i++){
			double newTestDouble = (2*i) * testDouble;
			if(newTestDouble < testDouble){
				System.out.println("fail!");
			};
		} 
	}
	
}
