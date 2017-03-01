package cz.vut.sf.parsers;

import java.util.ArrayList;
import java.util.List;

public class TestData {
	/* This method returns data defining graph
	 * by adjacency lists
	 * 		  2--3
	 * 		 /\	 /\
	 * 		1  \/  6
	 * 		|\ /\ /|
	 *		| 4--5 |
	 *      |______|
	 */
	public static ParsedDTO getData(){
		//    	EDGE_DIRECTIONS_SECTION
    	List<List<Integer>> adjacencyList = new ArrayList<List<Integer>>();
    	fillAdjencyList(adjacencyList);
    	
    	//		WEIGHTS_SECTION
    	List<List<Double>> weightsList = new ArrayList<List<Double>>();
    	fillWeightsList(weightsList);
    	
    	//		PROBABLITIES_SECTION
    	List<List<Double>> probabilitiesList = new ArrayList<List<Double>>();
    	fillProbabilityList(adjacencyList, probabilitiesList, 2);
    	
    	//		PENALTIES_SECTION
    	List<List<Double>> penaltyList = new ArrayList<List<Double>>();
    	
		return new ParsedDTO(adjacencyList, weightsList, probabilitiesList, penaltyList);
	}
	
	/*		 
	 * 		  2--3
	 * 		 /\	 /
	 * 		1  \/  6
	 * 		 \ /\ 
	 *		  4--5 
	 */
	public static ParsedDTO getDisconnectedGraphData(){
		//    	EDGE_DIRECTIONS_SECTION
    	List<List<Integer>> adjacencyList = new ArrayList<List<Integer>>();
    	fillDisconnectedAdjencyList(adjacencyList);
    	
    	//		WEIGHTS_SECTION
    	List<List<Double>> weightsList = new ArrayList<List<Double>>();
    	fillWeightsList(weightsList);
    	
    	//		PROBABLITIES_SECTION
    	List<List<Double>> probabilitiesList = new ArrayList<List<Double>>();
    	fillProbabilityListWith(adjacencyList, probabilitiesList, 0);
    	
    	//		PENALTIES_SECTION
    	List<List<Double>> penaltyList = new ArrayList<List<Double>>();
    	
		return new ParsedDTO(adjacencyList, weightsList, probabilitiesList, penaltyList);
	}
	/*
	 * 		  2--3
	 * 		 /\	 /\
	 * 		1  \/  6
	 * 		 \ /\ /
	 *		  4--5 
	 */
	public static ParsedDTO getConnectedGraphData(){
		//    	EDGE_DIRECTIONS_SECTION
    	List<List<Integer>> adjacencyList = new ArrayList<List<Integer>>();
    	fillAdjencyList(adjacencyList);
    	
    	//		WEIGHTS_SECTION
    	List<List<Double>> weightsList = new ArrayList<List<Double>>();
    	fillWeightsList(weightsList);
    	
    	//		PROBABLITIES_SECTION
    	List<List<Double>> probabilitiesList = new ArrayList<List<Double>>();
    	fillProbabilityListWith(adjacencyList, probabilitiesList, 0);
    	
    	//		PENALTIES_SECTION
    	List<List<Double>> penaltyList = new ArrayList<List<Double>>();
    	
		return new ParsedDTO(adjacencyList, weightsList, probabilitiesList, penaltyList);
	}

	//    	2 4 6
	//    	1 3 5 
	//    	2 5 6 
	//    	1 3 5
	//		2 4 6
	//    	1 3 5 
	private static void fillAdjencyList(List<List<Integer>> adjacencyList) {
		List<Integer> _1 = new ArrayList<Integer>(); _1.add(2); _1.add(4); _1.add(6);
    	List<Integer> _2 = new ArrayList<Integer>(); _2.add(1); _2.add(3); _2.add(5);
    	List<Integer> _3 = new ArrayList<Integer>(); _3.add(2); _3.add(4); _3.add(6);
    	List<Integer> _4 = new ArrayList<Integer>(); _4.add(1); _4.add(3); _4.add(5);
    	List<Integer> _5 = new ArrayList<Integer>(); _5.add(2); _5.add(4); _5.add(6);
    	List<Integer> _6 = new ArrayList<Integer>(); _6.add(1); _6.add(3); _6.add(5);    	
    	adjacencyList.add(_1);adjacencyList.add(_2);adjacencyList.add(_3);
    	adjacencyList.add(_4);adjacencyList.add(_5);adjacencyList.add(_6);
	}
	
	private static void fillProbabilityList(List<List<Integer>> adjacencyList,
			List<List<Double>> probabilitiesList, double probabilityModifier) {
		for(int i = 0; i < adjacencyList.size(); i++){
    		List<Double> row = new ArrayList<Double>();
    		for(int j = 0; j < adjacencyList.get(i).size(); j++){
    			row.add(Math.random()/probabilityModifier);
    		}
    		probabilitiesList.add(row);
    	}
	}
	
	private static void fillProbabilityListWith(List<List<Integer>> adjacencyList,
			List<List<Double>> probabilitiesList, double probability) {
		for(int i = 0; i < adjacencyList.size(); i++){
    		List<Double> row = new ArrayList<Double>();
    		for(int j = 0; j < adjacencyList.get(i).size(); j++){
    			row.add(probability);
    		}
    		probabilitiesList.add(row);
    	}
	}

	private static void fillWeightsList(List<List<Double>> weightsList) {
		List<Double> w1 = new ArrayList<Double>(); w1.add(100d); w1.add(1d); w1.add(101d);
    	List<Double> w2 = new ArrayList<Double>(); w2.add(10d); w2.add(1d); w2.add(1d);
    	List<Double> w3 = new ArrayList<Double>(); w3.add(10d); w3.add(10d); w3.add(1d);
    	List<Double> w4 = new ArrayList<Double>(); w4.add(10d); w4.add(100d); w4.add(1d);
    	List<Double> w5 = new ArrayList<Double>(); w5.add(0.5d); w5.add(10d); w5.add(100d);
    	List<Double> w6 = new ArrayList<Double>(); w6.add(101d); w6.add(10d); w6.add(10d);
    	weightsList.add(w1);weightsList.add(w2);weightsList.add(w3);
    	weightsList.add(w4);weightsList.add(w5);weightsList.add(w6);
	}
	
	private static void fillDisconnectedAdjencyList(List<List<Integer>> adjacencyList) {
		List<Integer> _1 = new ArrayList<Integer>(); _1.add(2); _1.add(4);
    	List<Integer> _2 = new ArrayList<Integer>(); _2.add(1); _2.add(3); _2.add(5);
    	List<Integer> _3 = new ArrayList<Integer>(); _3.add(2); _3.add(4);
    	List<Integer> _4 = new ArrayList<Integer>(); _4.add(1); _4.add(3); _4.add(5);
    	List<Integer> _5 = new ArrayList<Integer>(); _5.add(2); _5.add(4);
    	List<Integer> _6 = new ArrayList<Integer>();     	
    	adjacencyList.add(_1);adjacencyList.add(_2);adjacencyList.add(_3);
    	adjacencyList.add(_4);adjacencyList.add(_5);adjacencyList.add(_6);
	}
}
