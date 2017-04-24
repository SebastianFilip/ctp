package cz.vut.sf.parsers;

import java.awt.Point;
import java.util.List;

import cz.vut.sf.graph.CtpException;


// all list should have the same size
public class ParsedDTO {
	public List<List<Integer>> adjacencyList;
	public List<List<Double>> weightsList;
	public List<List<Double>> probabilitiesList;
	public List<List<Double>> penaltyList;
	public List<Point> pointList;
	
	public void validateSize(){
		int rows = adjacencyList.size();
		if(!(rows == weightsList.size() && rows == probabilitiesList.size())){
			throw new CtpException("Source file has inconsistent data. EDGE_DIRECTIONS_SECTION size is not equal with EDGE_WEIGHT_SECTION or PROBABILITY_SECTION size.");
		}
		for(int i = 0; i < adjacencyList.size(); i++){
			if(adjacencyList.get(i).size() != weightsList.get(i).size()){
				throw new CtpException("Source file has inconsistent data. EDGE_DIRECTIONS_SECTION element #"
						+ (i+1) + " size is not equal with EDGE_WEIGHT_SECTION size.");
			}
		}
		
		for(int i = 0; i < adjacencyList.size(); i++){
			if(adjacencyList.get(i).size() != probabilitiesList.get(i).size()){
				throw new CtpException("Source file has inconsistent data. EDGE_DIRECTIONS_SECTION element #"
						+ (i+1) + " size is not equal with PROBABILITY_SECTION size.");
			}
		}
	}
	
	public ParsedDTO(List<List<Integer>> adjancyList, List<List<Double>> weightsList,
	List<List<Double>> probabilitiesList, List<List<Double>> penaltyList, List<Point> pointList){
		this.adjacencyList = adjancyList;
		this.weightsList = weightsList;
		this.probabilitiesList = probabilitiesList;
		this.penaltyList = penaltyList;
		this.pointList = pointList;
	}
	
	public ParsedDTO(List<List<Integer>> adjancyList, List<List<Double>> weightsList,
	List<List<Double>> probabilitiesList, List<List<Double>> penaltyList){
		this.adjacencyList = adjancyList;
		this.weightsList = weightsList;
		this.probabilitiesList = probabilitiesList;
		this.penaltyList = penaltyList;
		this.pointList = null;
	}
}
