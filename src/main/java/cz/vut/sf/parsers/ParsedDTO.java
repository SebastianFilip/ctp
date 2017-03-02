package cz.vut.sf.parsers;

import java.util.List;

// all list should have the same size
public class ParsedDTO {
	public List<List<Integer>> adjacencyList;
	public List<List<Double>> weightsList;
	public List<List<Double>> probabilitiesList;
	public List<List<Double>> penaltyList;
	public List<List<Double>> penalizationList;
	
	public boolean validateSize(){
		int rows = adjacencyList.size();
		return(rows ==weightsList.size() && rows == probabilitiesList.size() && rows == penaltyList.size());
	}
	
	public ParsedDTO(List<List<Integer>> adjancyList, List<List<Double>> weightsList,
	List<List<Double>> probabilitiesList, List<List<Double>> penaltyList){
		this.adjacencyList = adjancyList;
		this.weightsList = weightsList;
		this.probabilitiesList = probabilitiesList;
		this.penaltyList = penaltyList;
	}
}
