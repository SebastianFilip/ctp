package cz.vut.sf.algorithms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public class UctPrunning extends AbstractTreeSearchWidthPruning implements UctAlgorithm{
	public UctPrunning(DefaultCtp ctp, Agent agent) {
		super(ctp, agent);
	}
	protected int numberOfAdditionalRollouts = 100;
	protected int numberOfIterations = 100;
	protected long timeToDecision = 0;
	private OptimisticUctFormula uctFormula = new OptimisticUctFormula(this.graph, this.root);
	private int iterationsMade = 0;
	//historyCache always contains only one previous vtx with its second choice if there were more than 10 visits
	private HistoryCache historyCache = new HistoryCache();
	
	@Override
	public Result solve() {
		LOG.info("Starting UCTP, total iterations = " + numberOfIterations + ", total rollouts = " + numberOfAdditionalRollouts);
		int blockedEdgesRevealed = 0;
		this.setGraph(ctp.g);
		boolean isPreviousVtxCached = false;
		while(agent.getCurrentVertex()!=ctp.t){
			//If graph changes all of the children of current vertex should be expanded (explored)
			boolean isGraphChanged = false;
			agent.senseAction(ctp.g);
			
			if(blockedEdgesRevealed != ctp.g.getBlockedEdgesRevealed()){
				blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
				isGraphChanged = true;
				uctFormula.setGraph(ctp.g);
				this.setGraph(ctp.g);
				uctFormula.clearCachedData();
			}
			
			this.setRoot(agent.getCurrentVertex());
			uctFormula.setRoot(root);
			
			if(LOG.isDebugEnabled()){
				LOG.debug("Expanding root = "+ this.getRoot().getData().vtx +", except for parent = " + agent.getPreviousVertex(isGraphChanged));
			}
			this.expandNode(this.getRoot(), agent.getPreviousVertex(!isPreviousVtxCached));
			
			if(getTimeToDecision() == 0){
				iterationsMade += this.doSearch(numberOfIterations, numberOfAdditionalRollouts);
			}else{
				iterationsMade += this.doSearch(numberOfIterations, numberOfAdditionalRollouts, getTimeToDecision());
			}
			
			Vertex vtxBeforeTraverse = agent.getCurrentVertex();
			Vertex previousVtx = agent.getPreviousVertex();
			if(previousVtx!= null && historyCache.isPreviouslyVisited(previousVtx)){
				TreeNode<VtxDTO> nodeToAdd = new TreeNode<VtxDTO>(root);
				VtxDTO data = new VtxDTO(previousVtx);
				data.totalExpectedCost = historyCache.getAvgCost(previousVtx) * historyCache.getPenalizationMultiplyier();
				data.visitsMade = 1;
				nodeToAdd.setData(data);
				root.getChildren().add(nodeToAdd);
			}
			Vertex chosenVtx = getNextAction(agent);
			if(chosenVtx == previousVtx && historyCache.isPreviouslyVisited(chosenVtx)){
				if(LOG.isDebugEnabled()){
					LOG.debug("UCTP chosen to go back taking data from cachedHistory next moves:" +agent.getCurrentVertex() + "->"+ chosenVtx + "->" + historyCache.getNextAction());
				}
				historyCache.increasePenalization();
				agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
				agent.traverseToAdjancetVtx(ctp.g, historyCache.getNextAction());
			}else{
				agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			}
			if(LOG.isDebugEnabled()){
				LOG.debug("Chosen vtx = " + chosenVtx + "\n");	
			}
			historyCache.clearCachedHistory();
			if(root.getChildren().size()>1){
				Map<Vertex, Double> secondChoice = getSeconChoiceWithAvgCost(chosenVtx);
				isPreviousVtxCached = historyCache.actualizeExpandedHistory(secondChoice, vtxBeforeTraverse);
			}
//			printTerminationFromExpanded();
		}
		return new Result(agent, "UCTP", iterationsMade/(agent.getTraversalHistory().size()-1));
	}
	
	private Map<Vertex, Double> getSeconChoiceWithAvgCost(Vertex chosenVtx) {
		List<TreeNode<VtxDTO>> children = root.getChildren();
		Vertex secondChoice = null;
		double expectedMinCost = Double.MAX_VALUE;
		int visitsMade = 0;
		for(int i = 0; i < children.size(); i++){
			if(children.get(i).getData().vtx == chosenVtx){continue;}
	    	double totalCost = children.get(i).getData().totalExpectedCost;
	    	int totalIteration = children.get(i).getData().visitsMade;
			double averageCost = totalCost/totalIteration;
			if(LOG.isDebugEnabled()){
				LOG.debug("average cost for [" + children.get(i).getData().vtx 
					+"] is : "+ averageCost + 
					" visits made:" + totalIteration);
			}
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				visitsMade = children.get(i).getData().visitsMade;
				secondChoice = children.get(i).getData().vtx;
			}
		}
		// no sense caching history of visited vtx if it was not visit enough (i choose 10 visits is enough)
		// also making sure that cached vtx will not be 'dead end' there must be at least two edges 
		// from connection (it is oriented graph) and if there are no more edges that means it is dead end
		if(secondChoice == null){
			return null;
		}else if(visitsMade < 10 && StochasticWeightedGraph.getAdjacentVertexes(secondChoice, this.graph).size() <= 2){
			return null;
		}
		Map<Vertex,Double> result = new HashMap<Vertex,Double>();
		result.put(secondChoice, new Double(expectedMinCost));
		return result;
	}

	@Override
	public Simulator simulateTravelsal(TreeNode<VtxDTO> node, int numberOfRollouts) {		
		Simulator simulator = new Simulator(node.getParent().getData().vtx);
		if(doSimulation(simulator,node.getData().vtx ,numberOfRollouts)){
			return simulator;
		}
		return null;
	}

	protected Vertex getNextAction(Agent agent){
		List<TreeNode<VtxDTO>> children = root.getChildren();
		if(root.getChildren().size() == 1){return root.getChildren().get(0).getData().vtx;}
		else if(root.getChildren().size() == 0){
			//'dead end' -> go back
			return agent.getPreviousVertex();
		}
		Vertex result = null;
		double expectedMinCost = Double.MAX_VALUE;
		for(int i = 0; i < children.size(); i++){
			//if proposed vtx is not terminal the length of its edge muse be added
	    	double totalCost = children.get(i).getData().totalExpectedCost;
	    	int totalIteration = children.get(i).getData().visitsMade;
			double averageCost = totalCost/totalIteration;
			if(LOG.isDebugEnabled()){
				LOG.debug("average cost for [" + children.get(i).getData().vtx 
					+"] is : "+ averageCost + 
					" visits made:" + totalIteration);
			}
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = children.get(i).getData().vtx;
			}
		}
		return result;
	}
	
	public boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int additionalSimulation) {
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
				//edge to exploring vtx is blocked use GA from parent instead
				if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, this.getGraph().getTerminalVtx(), travellingAgent)){
					//there is no path from current travellingAgent position to vertex which is about to be explored
					LOG.debug("In rollouted graph there were no path to adjancent vtx. Skipping rollout...");
					continue;
				}
			}else{
				//finish route by Dijkstra, use its length to estimate cost
				travellingAgent.traverseToAdjancetVtx(rolloutedGraph, vtxWhichIsExplored);
				rolloutedGraph.removeAllBlockedEdges();
				dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(rolloutedGraph);
				shortestPath = dsp.getPath(travellingAgent.getCurrentVertex(), rolloutedGraph.getTerminalVtx());if(shortestPath == null){
					LOG.debug("In rollouted graph there were no path to terminal vtx. Skipping rollout...");
					continue;
				}
				simulator.totalCost += shortestPath.getWeight();
			}
			simulator.totalCost += travellingAgent.getTotalCost();
			simulator.totalIterations ++;
		}while(currentRollout < additionalSimulation);
		boolean result = simulator.totalIterations == 0 ? false:true;
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
	
	private double evaluateUctFormula(TreeNode<VtxDTO> child) {
		return uctFormula.evaluateUctFormula(child, numberOfAdditionalRollouts, 10);
	}
	
	public int getNumberOfAdditionalRollouts() {
		return numberOfAdditionalRollouts;
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfAdditionalRollouts(int n) {
		this.numberOfAdditionalRollouts = n;
	}

	public void setNumberOfIterations(int i) {
		this.numberOfIterations = i;
	}
	
	public void setTimeToDecision(long miliseconds){
		timeToDecision = miliseconds;
	}
	public long getTimeToDecision(){
		return timeToDecision;
	}
}
