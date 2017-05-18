package cz.vut.sf.algorithms;

import java.util.HashMap;
import java.util.Map;

import cz.vut.sf.graph.Vertex;


public class HistoryCache {
	private Map<Vertex,Double> expandedHistory = new HashMap<Vertex, Double>();
	private Vertex nextAction = null;
	private double penalization = 0;
	
	public Vertex getNextAction(){
		return nextAction;
	}
	
	public void clearCachedHistory(){
		expandedHistory = new HashMap<Vertex, Double>();
		nextAction = null;
	}
	
	public boolean actualizeExpandedHistory(Map<Vertex,Double> secondChoice, Vertex currentVtx) {
		if(secondChoice == null)
				return false;
		Vertex key = secondChoice.keySet().iterator().next();
		this.nextAction = key;
		expandedHistory.put(currentVtx, secondChoice.get(key));
		return true;
	}
	
	public boolean isPreviouslyVisited(Vertex current){
		return expandedHistory.containsKey(current);
	}
	
	public double getAvgCost(Vertex keyVtx){
		return (double)expandedHistory.get(keyVtx);
	}
	public void increasePenalization(){
		penalization += 2;
	}
	
	public double getPenalizationMultiplyier(){
		return (penalization + 100)/100;
	}
}
