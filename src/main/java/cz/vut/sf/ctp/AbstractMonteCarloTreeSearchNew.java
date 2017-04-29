package cz.vut.sf.ctp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.swing.plaf.basic.BasicTreeUI.TreeHomeAction;

import cz.vut.sf.algorithms.GreedyAlgorithm;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.gui.LoggerClass;

public abstract class AbstractMonteCarloTreeSearchNew extends LoggerClass {
	protected StochasticWeightedGraph graph;
	protected TreeNode<VtxDTO> root;
	private double penalizationPercent = 105;
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
	
	public void doSearch(final int numberOfRollouts){
		int k = 0;
		//better expand children in algorithm, not all if blockage is not discovered
		while(k < numberOfRollouts){
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
			boolean treeHasAvailableChildren = true;
			boolean innerCycle = false;
			while(!checkTerminalNode(currentNode) && treeHasAvailableChildren && !innerCycle){
				if(currentNode.isLeafNode()){
					expandNode(currentNode, currentNode.getParent().getData().vtx);
					if(currentNode.getChildren().isEmpty()){
						//dead end take GA from there
						break;
					}
				}
				Set<Vertex> forbiddenNodes = new HashSet<Vertex>();
				final TreeNode<VtxDTO> currentsParent = currentNode;
				// checks if edge is in rolloutedGraph if not pick other¨
				boolean isEdgeInRolloutedGraph = true;
				do{
					currentNode = pickNode(currentNode, forbiddenNodes);
					isEdgeInRolloutedGraph = rolloutedGraphWithoutBlockedEdges.getEdge(currentsParent.getData().vtx, currentNode.getData().vtx) != null;
					if(!isEdgeInRolloutedGraph){
						forbiddenNodes.add(currentNode.getData().vtx);
						currentNode = currentsParent;
						if(forbiddenNodes.size() == currentNode.getChildren().size()){
							treeHasAvailableChildren = false;
						}
					}else{

					}
					if(!treeHasAvailableChildren){
						break;
					}
				}while(!(isEdgeInRolloutedGraph));
				
				if(!pathToRoot.add(currentNode.getData().vtx)){
					if(LOG.isDebugEnabled()){
						LOG.debug("Adding to sequence vtx:" + currentNode.getData().vtx 
								+ ", is already on path to root! Rest of path will be determined by GA");
					}
					innerCycle = true;
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
			
			if(!treeHasAvailableChildren){
				LOG.debug("Dead end was encountred, finishing travelsal by GA from: " + simAgent.getCurrentVertex());
				simAgent.senseAction(rolloutedGraph);
				//it takes SPP since rollouted graph has removed blocked edges
				if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, rolloutedGraph.getTerminalVtx(), simAgent)){
					LOG.debug("Rollouted graph did not have connected " + simAgent.getCurrentVertex() 
							+ ", with termination vtx ... skiping rollout");
					continue;
				}
				panalizationNeeded = true;
			}else if(innerCycle){
				LOG.debug("Inner cycle was encountred, finishing travelsal by GA from: " + simAgent.getCurrentVertex());
				simAgent.senseAction(rolloutedGraph);
				//it takes SPP since rollouted graph has removed blocked edges
				if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, rolloutedGraph.getTerminalVtx(), simAgent)){
					LOG.debug("Rollouted graph did not have connected " + simAgent.getCurrentVertex() 
							+ ", with termination vtx ... skiping rollout");
					continue;
				}
				panalizationNeeded = true;
			}
			
			logChosenPath(chosenTerminalNode, simAgent, panalizationNeeded);
			backPropagation(chosenTerminalNode, simAgent, panalizationNeeded);
			k++;
		}
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
