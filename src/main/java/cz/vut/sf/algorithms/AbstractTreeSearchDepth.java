package cz.vut.sf.algorithms;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;





import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public abstract class AbstractTreeSearchDepth extends DefaultCtpAlgorithm {
	public AbstractTreeSearchDepth(DefaultCtp ctp, Agent agent) {
		super(ctp, agent);
	}

	protected StochasticWeightedGraph graph;
	protected TreeNode<VtxDTO> root;
	private double penalizationPercent = 100;
	Set<Vertex> pathToRoot= null;

	public StochasticWeightedGraph getGraph() {
		return graph;
	}

	public void setGraph(StochasticWeightedGraph graph) {
		this.graph = graph;
	}

	public TreeNode<VtxDTO> getRoot() {
		return root;
	}

	public void setRoot(Vertex root) {
		if(this.root != null){
			this.root.cleanSubTree();
		}
		this.root = new TreeNode<VtxDTO>(null);
		this.root.setData(new VtxDTO(root));
	}
	/**
	 * Should return one of the argument's child or itself if it has no children
	 * @param nodeToExpand
	 */
	public abstract TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> parent, Set<Vertex> forbiddenSet);
	
	public int doSearch(final int numberOfRollouts){
		return doSearch(numberOfRollouts, 120000);
	}
	
	public int doSearch(final int numberOfRollouts, final long timeToDecision){
		int iterationsMade = 0;
		long startingTime = (long) (System.nanoTime()/1E6);
		int k = 0;
		//better expand children in algorithm, not all if blockage is not discovered
		while(k < numberOfRollouts){
			iterationsMade++;
			long timeMakingDecision = (long) (System.nanoTime()/1E6) - startingTime;
			if(timeMakingDecision > timeToDecision){
				return iterationsMade;
			}
			
			if(root.getChildren().isEmpty()){
				expandNode(root, null);
			}
			pathToRoot = new HashSet<Vertex>();
			TreeNode<VtxDTO> currentNode = pickNode(root, new HashSet<Vertex>());
			pathToRoot.add(root.getData().vtx);
			pathToRoot.add(currentNode.getData().vtx);
			StochasticWeightedGraph rolloutedGraph = graph.doRollout();
			StochasticWeightedGraph rolloutedGraphWithoutBlockedEdges = (StochasticWeightedGraph) rolloutedGraph.clone();
			rolloutedGraphWithoutBlockedEdges.removeAllBlockedEdges();
			boolean continueCondition = true;
			while(!checkTerminalNode(currentNode) && continueCondition){
				TreeNode<VtxDTO> previousNode = currentNode;
				currentNode = addOneChild(currentNode, rolloutedGraphWithoutBlockedEdges);
				if(previousNode.equals(currentNode)){
					continueCondition = false;
				}
			}
			final TreeNode<VtxDTO> chosenTerminalNode = currentNode;
			//get cost of chosen path
			Stack<Vertex> moveToBeMade = new Stack<Vertex>();
			while(currentNode.getParent()!=null){
				moveToBeMade.add(currentNode.getData().vtx);
				currentNode = currentNode.getParent();
			}
			Agent simAgent = new Agent(root.getData().vtx);
			boolean panalizationNeeded = false;
			do{
				Vertex destination = moveToBeMade.pop();
				simAgent.senseAction(rolloutedGraph);
				simAgent.traverseToAdjancetVtx(rolloutedGraph, destination);
			}while(!moveToBeMade.isEmpty());
			
			if(!simAgent.getCurrentVertex().equals(graph.getTerminalVtx())){
				simAgent.senseAction(rolloutedGraph);
				if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, rolloutedGraph.getTerminalVtx(), simAgent)){
					LOG.debug("Rollouted graph did not have connected " + simAgent.getCurrentVertex() 
						+ ", with termination vtx ... skiping rollout");
				continue;
				}
			}

			logChosenPath(chosenTerminalNode, simAgent, panalizationNeeded);
			backPropagation(chosenTerminalNode, simAgent, panalizationNeeded);
			k++;
		}
		return iterationsMade;
	}

	private TreeNode<VtxDTO> addOneChild(TreeNode<VtxDTO> currentNode,
			StochasticWeightedGraph rolloutedGraphWithoutBlockedEdges) {
		
		if(currentNode.isLeafNode()){
			expandNode(currentNode, currentNode.getParent().getData().vtx);
		}
		Set<Vertex> forbiddenNodes = new HashSet<Vertex>();
		forbiddenNodes.addAll(getBlockedEdgesTerminals(rolloutedGraphWithoutBlockedEdges ,currentNode.getData().vtx));
		
		if(currentNode.getChildren().isEmpty()){
			//dead end return parent
			LOG.debug("Dead end, returning parent");
			return currentNode.getParent();
		}else if(forbiddenNodes.size() == currentNode.getChildren().size()){
			//all nodes are forbidden return itself
			LOG.debug("No more option to add to sequence, finish by GA");
			return currentNode;
		}else{
			do{
				TreeNode<VtxDTO> previousCurrent = currentNode;
				currentNode = pickNode(currentNode, forbiddenNodes);
				if(pathToRoot.add(currentNode.getData().vtx)){
					return currentNode;
				}else{
					// chosen vtx was already on path from root
					forbiddenNodes.add(currentNode.getData().vtx);
					currentNode = previousCurrent;
					if(forbiddenNodes.size() == currentNode.getChildren().size()){
						if(LOG.isDebugEnabled()){
							LOG.debug("No more option to add to sequence, finish by GA");
						}
						return currentNode;
					}
				}
			}while(true);
		}
	}

	private Set<Vertex> getBlockedEdgesTerminals(StochasticWeightedGraph rolloutedGraph, Vertex source) {
		Set<Vertex> result = new HashSet<Vertex>();
		Set<Vertex> neighbours = StochasticWeightedGraph.getAdjacentVertexes(source, graph);
		for(Vertex vtx:neighbours){
			if(rolloutedGraph.getEdge(source, vtx)==null){
				result.add(vtx);
			}
		}
		return result;
	}

	private void logChosenPath(TreeNode<VtxDTO> fromNode, Agent simAgent, boolean penalizationOn) {
		if(LOG.isDebugEnabled()){
			if(penalizationOn){
				LOG.debug("Penalization on, since unpromising search space, penalization = " + (penalizationPercent-100) +"%");
			}
			LOG.debug("Chosen path is:" + simAgent.getTraversalHistory() + ", total cost=" + simAgent.getTotalCost());
			LOG.debug("Back propagation from node:" + fromNode.getData().vtx + ", with parent:" + fromNode.getParent().getData().vtx);
		}
	}

	private void backPropagation(TreeNode<VtxDTO> fromNode, Agent simAgent, boolean penalizationOn) {
		double propagationValue = simAgent.getTotalCost();
		propagationValue = penalizationOn ? propagationValue*(penalizationPercent/100) : propagationValue;
 		do{
			fromNode.getData().totalExpectedCost += propagationValue;
			fromNode.getData().visitsMade ++;
			fromNode = fromNode.getParent();
		}while(fromNode.getParent()!=null);
		//giving data to root as well
		fromNode.getData().totalExpectedCost += propagationValue;
		fromNode.getData().visitsMade ++;
	}

	protected boolean checkTerminalNode(TreeNode<VtxDTO> node) {
		if(node.getData().vtx.equals(graph.getTerminalVtx()))
			return true;
		return false;
	}
	
	/**
	 * Expands children of currentNode, Vertex parent will not be new child of currentNode
	 * set parent = null if you want to expand all of the children
	 * @param currentNode
	 * @param parent
	 */
	protected void expandNode(TreeNode<VtxDTO> currentNode, Vertex parent) {
		//find all children of parent (parent should not be child)
		Vertex currentVtx = currentNode.getData().vtx;
		Set<Vertex> children = StochasticWeightedGraph.getAdjacentVertexes(currentVtx, graph);
		for(Vertex child: children){
			if(parent!=null && parent.equals(child))continue;
			TreeNode<VtxDTO> temp = new TreeNode<VtxDTO>(currentNode);
			temp.setData(new VtxDTO(child));
			currentNode.addChild(temp);
		}
	}

}
