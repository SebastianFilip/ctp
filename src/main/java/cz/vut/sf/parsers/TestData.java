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
    	fillProbabilityList(adjacencyList, probabilitiesList, 1);
    	
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
	//--------------------------------------------------------------SCTP------------------------------------------------------------
	/* This method returns data defining graph
	 * by adjacency lists (VERTEX 8	is target)
	 * 		  2--3
	 * 		 / \  \
	 * 		/   \--8
	 * 		1-------------8  	 
	 *		 \		
	 *		  \ /--5----8
	 *		   4---6----8 
	 *          \--7----8 
	 */
	public static ParsedDTO getData2(){
		//    	EDGE_DIRECTIONS_SECTION
    	List<List<Integer>> adjacencyList = new ArrayList<List<Integer>>();
    	fillAdjencyList2(adjacencyList);
    	
    	//		WEIGHTS_SECTION
    	List<List<Double>> weightsList = new ArrayList<List<Double>>();
    	fillWeightsList2(weightsList);
    	
    	//		PROBABLITIES_SECTION
    	List<List<Double>> probabilitiesList = new ArrayList<List<Double>>();
    	fillProbabilityList2(probabilitiesList);
    	
		return new ParsedDTO(adjacencyList, weightsList, probabilitiesList, null);
	}
	//    	2 4 8
	//    	1 3 8  
	//    	2 8 
	//    	1 5 6 7
	//		4 8
	//    	4 8
	//		4 8
	// 		1 2 3 5 6 7
	private static void fillAdjencyList2(List<List<Integer>> adjacencyList) {
		List<Integer> _1 = new ArrayList<Integer>();
    	List<Integer> _2 = new ArrayList<Integer>();
    	List<Integer> _3 = new ArrayList<Integer>(); 
    	List<Integer> _4 = new ArrayList<Integer>();
    	List<Integer> _5 = new ArrayList<Integer>();
    	List<Integer> _6 = new ArrayList<Integer>();
    	List<Integer> _7 = new ArrayList<Integer>();
    	List<Integer> _8 = new ArrayList<Integer>();
    	_1.add(2); _1.add(4); _1.add(8);
    	_2.add(1); _2.add(3); _2.add(8);
    	_3.add(2); _3.add(8);
    	_4.add(1); _4.add(5);_4.add(6); _4.add(7);
    	_5.add(4); _5.add(8); 
    	_6.add(4); _6.add(8);
    	_7.add(4); _7.add(8); 
    	_8.add(1);_8.add(2); _8.add(3);_8.add(5); _8.add(6);_8.add(7);
    	adjacencyList.add(_1);adjacencyList.add(_2);adjacencyList.add(_3);adjacencyList.add(_4);
    	adjacencyList.add(_5);adjacencyList.add(_6);adjacencyList.add(_7);adjacencyList.add(_8);
	}
	private static void fillWeightsList2(List<List<Double>> weightsList) {
		List<Double> w1 = new ArrayList<Double>(); w1.add(20d); w1.add(10d); w1.add(100d);
    	List<Double> w2 = new ArrayList<Double>(); w2.add(20d); w2.add(40d); w2.add(70d);
    	List<Double> w3 = new ArrayList<Double>(); w3.add(40d); w3.add(0d);
    	List<Double> w4 = new ArrayList<Double>(); w4.add(10d); w4.add(60d); w4.add(60d); w4.add(60d);
    	List<Double> w5 = new ArrayList<Double>(); w5.add(60d); w5.add(0d);
    	List<Double> w6 = new ArrayList<Double>(); w6.add(60d); w6.add(0d);
    	List<Double> w7 = new ArrayList<Double>(); w7.add(60d); w7.add(0d);
    	List<Double> w8 = new ArrayList<Double>(); w8.add(100d); w8.add(70d); w8.add(0d);w8.add(0d); w8.add(0d); w8.add(0d);
    	weightsList.add(w1);weightsList.add(w2);weightsList.add(w3);
    	weightsList.add(w4);weightsList.add(w5);weightsList.add(w6);
    	weightsList.add(w7);weightsList.add(w8);
	}
	private static void fillProbabilityList2(List<List<Double>> probabilitiesList) {
		List<Double> p1 = new ArrayList<Double>(); p1.add(-1d); p1.add(-1d); p1.add(-1d);
    	List<Double> p2 = new ArrayList<Double>(); p2.add(-1d); p2.add(-1d); p2.add(-1d);
    	List<Double> p3 = new ArrayList<Double>(); p3.add(-1d); p3.add(1d);
    	List<Double> p4 = new ArrayList<Double>(); p4.add(-1d); p4.add(-1d); p4.add(-1d); p4.add(-1d);
    	List<Double> p5 = new ArrayList<Double>(); p5.add(-1d); p5.add(0.5d);
    	List<Double> p6 = new ArrayList<Double>(); p6.add(-1d); p6.add(0.5d);
    	List<Double> p7 = new ArrayList<Double>(); p7.add(-1d); p7.add(0.5d);
    	List<Double> p8 = new ArrayList<Double>(); p8.add(-1d); p8.add(-1d); p8.add(1d);p8.add(0.5d); p8.add(0.5d); p8.add(0.5d);
    	probabilitiesList.add(p1);probabilitiesList.add(p2);probabilitiesList.add(p3);
    	probabilitiesList.add(p4);probabilitiesList.add(p5);probabilitiesList.add(p6);
    	probabilitiesList.add(p7);probabilitiesList.add(p8);
	}

}
