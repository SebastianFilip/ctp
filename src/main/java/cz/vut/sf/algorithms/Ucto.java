package cz.vut.sf.algorithms;

import java.util.List;
import java.util.Map;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public class Ucto extends AbstractUctDepthAlgorithm {

	public Ucto(DefaultCtp ctp, Agent agent) {
		super(ctp, agent);
	}

	@Override
	public Result solve() {
		LOG.info("Starting UCTO, total rollouts = " + numberOfIterations + ", additional fake rollouts = " + numberOfAdditionalRollouts);
		Result result = super.solve();
		result.resultName = "UCTO";
		return result;
	}
	
	public boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,
			int additionalSimulation) {
		int currentRollout = 0;
		do{
			currentRollout ++;
			StochasticWeightedGraph rolloutedGraph = this.getGraph().doRollout();
			// I want my simulate agent to be always in same position and 
			// I am using him only for creating traveling agent
			Agent travellingAgent = new Agent(simulator.agent);
			travellingAgent.senseAction(rolloutedGraph);
//			StochasticWeightedEdge edgeFromParentToChild = rolloutedGraph.getEdge(travellingAgent.getCurrentVertex(), vtxWhichIsExplored);
//			if(edgeFromParentToChild==null){
//				//edge is blocked go there by GA
//				//mby later it will be needed to check if graph is connected
//				if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, vtxWhichIsExplored, travellingAgent)){
//					//there is no path from current travellingAgent position to vertex which is about to be explored
//					continue;
//				}
//			}else{
//				travellingAgent.traverseToAdjancetVtx(rolloutedGraph, vtxWhichIsExplored);
//			}
			if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, rolloutedGraph.getTerminalVtx(), travellingAgent)){
				continue;
			}
			simulator.totalCost += travellingAgent.getTotalCost();
			simulator.totalIterations ++;
		}while(currentRollout < additionalSimulation);
		boolean result = simulator.totalIterations == 0 ? false:true;
		return result;	
	}
	
	@Override
	protected int findFromUnexplored(List<TreeNode<VtxDTO>> children, List<Integer> unexploredIndexes) {
		Map<Vertex, Double[]> additionalRolloutsData = uctFormula.getCachedData();
		int result = -1;
		TreeNode<VtxDTO> parent = children.get(0).getParent();
		TreeNode<VtxDTO> child = null;
		double shortestPathValue = Double.MAX_VALUE;
		double estimatedCost = Double.MAX_VALUE/10;
		for(int i=0; i < unexploredIndexes.size();i++){
			child = children.get(unexploredIndexes.get(i));
//			Simulator gaData = simulateTravelsal(child, 1);
//			double estimatedCost = 0;
//			if(gaData == null){
//				//rollout was unsuccessful assign it high value
//				estimatedCost = Double.MAX_VALUE/10;
//			}else{
//				double edgeCost = graph.getEdgeWeight(graph.getEdge(parent.getData().vtx, child.getData().vtx));
//				estimatedCost = gaData.totalCost + edgeCost;
//			}

			Double[] data = null;
			if(!additionalRolloutsData.containsKey(child.getData().vtx)){
				Simulator gaData = simulateTravelsal(child, numberOfAdditionalRollouts);
				if(gaData == null){
					// simulation did not succeeded
//					LOG.debug("Caching data for evaluating UCT fromula, simulation returned null!!!!!!!!!!!!!!!!");
					data = new Double[] {Double.MAX_VALUE / numberOfAdditionalRollouts, 1d};
				}else{
					data = new Double[] {gaData.totalCost / gaData.totalIterations, (double) gaData.totalIterations};
				}
//				LOG.setLevel(org.apache.log4j.Level.DEBUG);
				if(LOG.isDebugEnabled()){
					LOG.debug("Caching data for evaluating UCT fromula. " + child.getData().vtx + ", avg cost:" + data[0]);
				}
				LOG.setLevel(org.apache.log4j.Level.INFO);
				additionalRolloutsData.put(child.getData().vtx, data);
			}else{
				data = additionalRolloutsData.get(child.getData().vtx);
			}
			double edgeCost = graph.getEdgeWeight(graph.getEdge(parent.getData().vtx, child.getData().vtx));
			estimatedCost = data[0] + edgeCost;
			if(estimatedCost < shortestPathValue){
				result = i;
				shortestPathValue = estimatedCost;
			}
		}
		if(result == -1){
			result = 0;
		}
		return result;
	}

	@Override
	protected double evaluateUctFormula(TreeNode<VtxDTO> node) {
		return uctFormula.evaluateUctFormulaWithEdge(node, numberOfAdditionalRollouts, 5);
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
}
