package cz.vut.sf.algorithms;


import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.MonteCarloTreeSearch;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public class Uctb extends MonteCarloTreeSearch implements DefaultCtpAlgorithm {

	public Result solve(DefaultCtp ctp, Agent agent) {
		int blockedEdgesRevealed = 0;
		
		while(agent.getCurrentVertex()!=ctp.t){
			//TODO pokud se zmeni graf melo by byt mozno expandovat i predchudce
			boolean isGraphChanged = false;
			agent.senseAction(ctp.g);
			
			if(blockedEdgesRevealed != ctp.g.getBlockedEdgesRevealed()){
				blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
				isGraphChanged = true;
			}
			
			this.setRoot(agent.getCurrentVertex());
			this.setGraph(ctp.g);
			if(this.getRoot().isLeafNode()){
				this.expandNode(this.getRoot(), agent.getPreviousVertex(isGraphChanged));
			}
			
			if(this.getRoot().getChildren().isEmpty()){
				//dead end -> go back
				agent.traverseToAdjancetVtx(ctp.g, agent.getPreviousVertex(true));
				continue;
			}
			if(this.getRoot().getChildren().size() == 1){
				//there is only one possible child -> go through it
				agent.traverseToAdjancetVtx(ctp.g, this.getRoot().getChildren().get(0).getData().vtx);
				continue;
			}
			
			this.doSearch(150, 50);
			Vertex chosenOne = this.getBestAction();
			agent.traverseToAdjancetVtx(ctp.g, chosenOne);
			System.out.println("agent goes through: "+chosenOne);
		}
		return new Result(agent, "UCTB");
	}

	@Override
	public TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> parent) {
		if(parent.isLeafNode()){
			return parent;
		}
		double maxUctValue = -1*Double.MAX_VALUE;
		int maxValueIndex = -1;
		for(int i = 0; i < parent.getChildren().size();i++){
			TreeNode<VtxDTO> child = parent.getChildren().get(i);
			double uctValue = evaluateUctFormula(child);
			if(uctValue == Double.MAX_VALUE){
				return child;
			}else if(uctValue > maxUctValue){
				maxValueIndex = i;
				maxUctValue = uctValue;
			}
		}
		return parent.getChildren().get(maxValueIndex);
	}
	
	private double evaluateUctFormula(TreeNode<VtxDTO> child) {
		if(child.getData().visitsMade == 0){
			return Double.MAX_VALUE;
		}
		double result = 0;
		double bias = this.getRoot().getData().totalExpectedCost / this.getRoot().getData().visitsMade;
		result -= this.getGraph().getEdgeWeight(this.getGraph().getEdge(child.getParent().getData().vtx, child.getData().vtx));
		result -= child.getData().totalExpectedCost/child.getData().visitsMade;
		result += bias*Math.sqrt(Math.log10(child.getParent().getData().visitsMade)/child.getData().visitsMade);
		return result;
	}
	
	private void simulateTravelsals(Simulator simulator, int numberOfRollouts) {
		int currentRollout = 0;
		do{
			currentRollout ++;
			StochasticWeightedGraph rolloutedGraph = this.getGraph().doRollout();
			// I want my simulate agent to be always in same position and 
			// I am using him only for creating traveling agent
			Agent travellingAgent = new Agent(simulator.agent);
			travellingAgent.senseAction(rolloutedGraph);
			GreedyAlgorithm.traverseByGa(rolloutedGraph, rolloutedGraph.getTerminalVtx(), travellingAgent, false);
			simulator.totalCost += travellingAgent.getTotalCost();
			simulator.totalIterations ++;
		}while(currentRollout < numberOfRollouts);
	}
	
	private void simulateTravelsals(Simulator simulator, Vertex vtxWhichIsExplored,int numberOfRollouts) {
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
		}while(currentRollout < numberOfRollouts);
	}

	@Override
	public Simulator rollout(TreeNode<VtxDTO> node, int numberOfRollouts) {
//		if(node.getParent().getData().vtx.equals(this.getGraph().getVtxById(3))){
//			if(node.getData().vtx.equals(this.getGraph().getVtxById(8))){
//				int i=0;
//			}
//		}

//		Simulator simulator = new Simulator(node.getData().vtx);
//		simulateTravelsals(simulator,numberOfRollouts);
//		StochasticWeightedEdge edgeFromParentToChild = this.getGraph().getEdge(node.getParent().getData().vtx, node.getData().vtx);
//		simulator.totalCost += numberOfRollouts * this.getGraph().getEdgeWeight(edgeFromParentToChild);
		
		
		Simulator simulator = new Simulator(node.getParent().getData().vtx);
		simulateTravelsals(simulator,node.getData().vtx ,numberOfRollouts);
		return simulator;
	}

}
