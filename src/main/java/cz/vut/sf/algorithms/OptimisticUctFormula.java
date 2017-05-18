package cz.vut.sf.algorithms;

import java.util.HashMap;
import java.util.Map;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.gui.LoggerClass;

public class OptimisticUctFormula extends LoggerClass{
	private StochasticWeightedGraph graph;
	private TreeNode<VtxDTO> root;
	private Map<Vertex, Double[]> additionalRolloutsData = new HashMap<Vertex, Double[]>();
	
	Map<Vertex, Double[]> getCachedData(){
		return additionalRolloutsData;
	}
	
	public OptimisticUctFormula(StochasticWeightedGraph graph, TreeNode<VtxDTO> root){
		this.graph = graph;
		this.root = root;
	}
	
	public void clearCachedData(){
		additionalRolloutsData = new HashMap<Vertex, Double[]>();
	}
	
	//every time graph is changed it need to be updated as well
	public void setGraph(StochasticWeightedGraph g){
		graph = g;
	}
	
	//every time root is changed this needs to be updated as well
	public void setRoot(TreeNode<VtxDTO> r){
		root = r;
	}
	
	public double evaluateUctFormula(TreeNode<VtxDTO> node, int additionalRollouts, double biasMultiplier) {
		if(node.getData().visitsMade == 0){
			return Double.MAX_VALUE;
		}
		//
		Double[] data = null;
		if(!additionalRolloutsData.containsKey(node.getData().vtx)){
			Simulator gaData = simulateTravelsal(node, additionalRollouts);
			if(gaData == null){
				// simulation did not succeeded
				LOG.debug("Caching data for evaluating UCT fromula, simulation returned null!!!!!!!!!!!!!!!!");
				data = new Double[] {Double.MAX_VALUE / additionalRollouts, 1d};
			}else{
				data = new Double[] {gaData.totalCost / gaData.totalIterations, (double) gaData.totalIterations};
			}
//			LOG.setLevel(org.apache.log4j.Level.DEBUG);
			if(LOG.isDebugEnabled()){
				LOG.debug("Caching data for evaluating UCT fromula. " + node.getData().vtx + ", avg cost:" + data[0]);
			}
//			LOG.setLevel(org.apache.log4j.Level.INFO);
			additionalRolloutsData.put(node.getData().vtx, data);
		}else{
			data = additionalRolloutsData.get(node.getData().vtx);
		}
		double result = 0;
		double totalExpectedCost = node.getData().totalExpectedCost;
		double totalVisits = node.getData().visitsMade;
		double bias = (root.getData().totalExpectedCost / root.getData().visitsMade);
		bias = bias*biasMultiplier;
		result -= totalExpectedCost/totalVisits;
		result -= data[0];
		result += bias*Math.sqrt(Math.log(node.getParent().getData().visitsMade)/(node.getData().visitsMade + data[1]));
		return result;
	}
	
	public double evaluateUctFormulaWithEdge(TreeNode<VtxDTO> node, int additionalRollouts, double biasMultiplier) {
		double edgeWeight = graph.getEdgeWeight(graph.getEdge(node.getParent().getData().vtx, node.getData().vtx));
		return evaluateUctFormula(node, additionalRollouts, biasMultiplier) - edgeWeight;
	}
	
	public Simulator simulateTravelsal(TreeNode<VtxDTO> node, int additionalSimulation) {		
		Simulator simulator = new Simulator(node.getData().vtx);
		if(doSimulation(simulator,node.getData().vtx ,additionalSimulation)){
			return simulator;
		}
		if(LOG.isDebugEnabled()){
			LOG.error("rollout returns simulator = null");	
		}
		return null;
	}
	
	public boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,
			int additionalSimulation) {
		int currentRollout = 0;
		do{
			currentRollout ++;
			StochasticWeightedGraph rolloutedGraph = graph.doRollout();
			// I want my simulate agent to be always in same position and 
			// I am using him only for creating traveling agent
			Agent travellingAgent = new Agent(simulator.agent);
			travellingAgent.senseAction(rolloutedGraph);
			if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, rolloutedGraph.getTerminalVtx(), travellingAgent)){
				continue;
			}
			simulator.totalCost += travellingAgent.getTotalCost();
			simulator.totalIterations ++;
		}while(currentRollout < additionalSimulation);
		boolean result = simulator.totalIterations == 0 ? false:true;
		return result;	
	}
}
