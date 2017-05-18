package cz.vut.sf.algorithms;


import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public class Uctb2 extends AbstractUctWidthAlgorithm{
	public Uctb2(DefaultCtp ctp, Agent agent) {
		super(ctp, agent);
	}

	@Override
	public Result solve() {
		LOG.info("Starting UCTB2, total iterations = " + numberOfIterations);
		Result result = super.solve();
		result.resultName = "UCTB2";
		return result;
	}

	@Override
	public TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> node) {
		if(node.isLeafNode()){
			throw new CtpException("pickNode method called with leaf node: "+ node.toString() +". Expand node first.");
		}
		double maxUctValue = -1*Double.MAX_VALUE;
		int maxValueIndex = -1;
		for(int i = 0; i < node.getChildren().size();i++){
			TreeNode<VtxDTO> child = node.getChildren().get(i);
			double uctValue = evaluateUctFormula(child);
			if(uctValue == Double.MAX_VALUE){
				return child;
			}else if(uctValue > maxUctValue){
				maxValueIndex = i;
				maxUctValue = uctValue;
			}
		}
		if(maxValueIndex == -1){
			maxValueIndex = 0;
		}
		return node.getChildren().get(maxValueIndex);
	}
	
	protected double evaluateUctFormula(TreeNode<VtxDTO> child) {
		if(child.getData().visitsMade == 0){
			return Double.MAX_VALUE;
		}
		double result = 0;
		double bias = child.getParent().getData().totalExpectedCost / child.getParent().getData().visitsMade;
		result -= this.getGraph().getEdgeWeight(this.getGraph().getEdge(child.getParent().getData().vtx, child.getData().vtx));
		result -= child.getData().totalExpectedCost/child.getData().visitsMade;
		result += bias*Math.sqrt(Math.log10(child.getParent().getData().visitsMade)/child.getData().visitsMade);
		return result;
	}
	
	public boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int additionalSimulation) {
		int currentRollout = 0;
		do{
			currentRollout ++;
			StochasticWeightedGraph rolloutedGraph = this.getGraph().doRollout();
			// I want my simulate agent to be always in same position and 
			// I am using him only for creating traveling agent
			Agent travellingAgent = new Agent(simulator.agent);
			travellingAgent.senseAction(rolloutedGraph);
			StochasticWeightedEdge edgeFromParentToChild = rolloutedGraph.getEdge(travellingAgent.getCurrentVertex(), vtxWhichIsExplored);
			if(edgeFromParentToChild==null){
				//edge is blocked go there by GA
				//mby later it will be needed to check if graph is connected
				if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, vtxWhichIsExplored, travellingAgent)){
					//there is no path from current travellingAgent position to vertex which is about to be explored
					continue;
				}
			}else{
				travellingAgent.traverseToAdjancetVtx(rolloutedGraph, vtxWhichIsExplored);
			}
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
