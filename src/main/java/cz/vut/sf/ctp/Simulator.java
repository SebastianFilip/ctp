package cz.vut.sf.ctp;

import java.util.List;

import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.gui.LoggerClass;

//holds copies of agent and moves them towards to specified vtx
public class Simulator extends LoggerClass{
	public Agent agent;
	public final Vertex startingVtx;
	public double totalCost = 0;
	public int totalIterations = 0;
	public Simulator(Vertex currentVtx, Vertex startingVtx, StochasticWeightedGraph graph){
		this.agent = new Agent(currentVtx);
		this.startingVtx = startingVtx;
		this.agent.traverseToAdjancetVtx(graph, this.startingVtx);
	}
	
	public Simulator(Vertex startingVtx){
		this.agent = new Agent(startingVtx);
		this.startingVtx = startingVtx;
	}
	
	@Override
	public String toString(){
		return "average cost for Vtx [" + startingVtx +"] is : "+ totalCost/totalIterations;
	}
	
	public static Vertex getBestAction(List<Simulator> simulators) {
		Vertex result = null;
		double expectedMinCost = Double.MAX_VALUE;
		for(int i = 0; i < simulators.size(); i++){
	    	LOG.debug("average cost for Vtx [" + simulators.get(i).startingVtx +"] is : "+ simulators.get(i).totalCost/simulators.get(i).totalIterations);
			double averageCost = simulators.get(i).totalCost/simulators.get(i).totalIterations;
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = simulators.get(i).startingVtx;
			}
		}
		return result;
	}
	
	public static int getBestActionIndex(List<Simulator> simulators) {
		int result = 0;
		double expectedMinCost = Double.MAX_VALUE;
		for(int i = 0; i < simulators.size(); i++){
			LOG.debug("average cost for Vtx [" + simulators.get(i).startingVtx +"] is : "+ simulators.get(i).totalCost/simulators.get(i).totalIterations);
			double averageCost = simulators.get(i).totalCost/simulators.get(i).totalIterations;
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = i;
			}
		}
		return result;
	}
}