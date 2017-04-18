package cz.vut.sf.algorithms;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.AbstractMonteCarloPrunning;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public class UctPrunning extends AbstractMonteCarloPrunning implements UctAlgorithm{
	protected int numberOfRollouts = 10;
	protected int numberOfIteration = 100000;
	public Result solve(DefaultCtp ctp, Agent agent) {
		LOG.info("Starting UCTP, total rollouts = " + numberOfRollouts + ", total iteration = " + numberOfIteration);
		int blockedEdgesRevealed = 0;
		
		while(agent.getCurrentVertex()!=ctp.t){
			//If graph changes all of the children of current vertex should be expanded (explored)
			boolean isGraphChanged = false;
			agent.senseAction(ctp.g);
			
			if(blockedEdgesRevealed != ctp.g.getBlockedEdgesRevealed()){
				blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
				isGraphChanged = true;
			}
			
			this.setRoot(agent.getCurrentVertex());
			this.setGraph(ctp.g);
			if(this.getRoot().isLeafNode()){
				LOG.debug("Expanding root = "+ this.getRoot().getData().vtx +", except for parent = " + agent.getPreviousVertex(isGraphChanged));
				this.expandNode(this.getRoot(), agent.getPreviousVertex(isGraphChanged), agent.getCurrentVertex());
			}
			
			if(this.getRoot().getChildren().isEmpty()){
				//this means that algorithm choose to travel to vtx about which knew that is dead end
				LOG.error("This should never happen!! Chosen vtx(dead end) = " + agent.getPreviousVertex(true));
				throw new CtpException("Uct choose to go to dead end vtx");
			}
			if(this.getRoot().getChildren().size() == 1){
				//there is only one possible child -> go through it
				LOG.debug("Chosen vtx(only child) = " + this.getRoot().getChildren().get(0).getData().vtx);
				agent.traverseToAdjancetVtx(ctp.g, this.getRoot().getChildren().get(0).getData().vtx);
				continue;
				}
			
			this.doSearch(numberOfIteration, numberOfRollouts);
			Vertex chosenVtx = this.getBestAction();
			agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			LOG.debug("Chosen vtx = " + chosenVtx + "\n");
//			printTerminationFromExpanded();
		}
		return new Result(agent, "UCTP");
	}

	@Override
	public Simulator rollout(TreeNode<VtxDTO> node, int numberOfRollouts) {		
		Simulator simulator = new Simulator(node.getParent().getData().vtx);
		doSimulation(simulator,node.getData().vtx ,numberOfRollouts);
		return simulator;
	}
	
	public void doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int numberOfRollouts) {
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
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
					LOG.debug("In rollouted graph there were no path to adjancent vtx. Skipping rollout...");
					continue;
				}
			}else{
				travellingAgent.traverseToAdjancetVtx(rolloutedGraph, vtxWhichIsExplored);
			}
			//finish route by Dijkstra
			rolloutedGraph.removeAllBlockedEdges();
			dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(rolloutedGraph);
			shortestPath = dsp.getPath(travellingAgent.getCurrentVertex(), rolloutedGraph.getTerminalVtx());
			if(shortestPath == null){
				LOG.debug("In rollouted graph there were no path to terminal vtx. Skipping rollout...");
				continue;
			}
			simulator.totalCost += travellingAgent.getTotalCost() + shortestPath.getWeight();
			simulator.totalIterations ++;
		}while(currentRollout < numberOfRollouts);
	}
	
	protected Vertex getBestAction(){
		List<TreeNode<VtxDTO>> children = root.getChildren();
		Vertex result = null;
		double expectedMinCost = Double.MAX_VALUE;
		for(int i = 0; i < children.size(); i++){
			//if proposed vtx is not terminal the length of its edge muse be added
	    	double totalCost = children.get(i).getData().totalExpectedCost;
	    	int totalIteration = children.get(i).getData().visitsMade;
			double averageCost = totalCost/totalIteration;
			LOG.debug("average cost for [" + children.get(i).getData().vtx 
					+"] is : "+ averageCost + 
					" visits made:" + totalIteration);
		
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = children.get(i).getData().vtx;
			}
		}
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
		return node.getChildren().get(maxValueIndex);
	}
	
//	protected double evaluateUctFormula(TreeNode<VtxDTO> child) {
//		if(child.getData().visitsMade == 0){
//			return Double.MAX_VALUE;
//		}
//		double result = 0;
//		double bias = child.getParent().getData().totalExpectedCost / child.getParent().getData().visitsMade;
////		bias /= 5;
////		result -= this.getGraph().getEdgeWeight(this.getGraph().getEdge(child.getParent().getData().vtx, child.getData().vtx));
//		result -= child.getData().totalExpectedCost/child.getData().visitsMade;
//		result += bias*Math.sqrt(Math.log10(child.getParent().getData().visitsMade)/child.getData().visitsMade);
//		return result;
//	}
	
	private double evaluateUctFormula(TreeNode<VtxDTO> child) {
		if(child.getData().visitsMade == 0){
			return Double.MAX_VALUE;
		}
		double result = 0;
		double totalExpectedCost = child.getData().totalExpectedCost 
				+ 20 * getDijkstraPathWeight(child.getData().vtx);
		double totalVisits = child.getData().visitsMade + 20;
		double bias = (child.getParent().getData().totalExpectedCost / child.getParent().getData().visitsMade)*10;
//		result -= this.getGraph().getEdgeWeight(this.getGraph().getEdge(child.getParent().getData().vtx, child.getData().vtx));
		result -= totalExpectedCost/totalVisits;
		result += bias*Math.sqrt(Math.log(child.getParent().getData().visitsMade)/child.getData().visitsMade);
//		System.out.println(child.getData().vtx + ", avg=" + totalExpectedCost/totalVisits +" ,UCT value=" + result );
		return result;
	}
	private double getDijkstraPathWeight(Vertex start){
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(this.getGraph());
		shortestPath = dsp.getPath(start, this.getGraph().getTerminalVtx());
		return shortestPath.getWeight();
	}
	
	public int getNumberOfRollouts() {
		return numberOfRollouts;
	}

	public int getNumberOfIterations() {
		return numberOfIteration;
	}

	public void setNumberOfRollouts(int n) {
		this.numberOfRollouts = n;
	}

	public void setNumberOfIterations(int i) {
		this.numberOfIteration = i;
	}

}
