package cz.vut.sf.ctp;

import java.util.Set;

import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.algorithms.LoggerClass;
import cz.vut.sf.ctp.VtxDTO;

public abstract class MonteCarloTreeSearch extends LoggerClass implements RolloutAble{
	protected StochasticWeightedGraph graph;
	protected TreeNode<VtxDTO> root;

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
	public abstract TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> parent);
	
	public void backPropagation(TreeNode<VtxDTO> fromNode, Simulator data){
		final double totalPropagatedValue = fromNode.getData().totalExpectedCost += data.totalCost / data.totalIterations;
		final int totalVisitsOfExplored  = ++fromNode.getData().visitsMade;
		
		StringBuilder sb = new StringBuilder();
		sb.append(fromNode.getData().vtx );
		do {
			sb.append("<-" + fromNode.getParent().getData().vtx);
			
			fromNode.getParent().getData().totalExpectedCost += totalPropagatedValue/totalVisitsOfExplored;
			fromNode.getParent().getData().visitsMade ++;
			fromNode = fromNode.getParent();
		}while(fromNode.getParent()!=null);
		LOG.debug("back propagation for "+ sb.toString() + " = " + totalPropagatedValue/totalVisitsOfExplored);
	}
		
	public void doSearch(final int numberOfIteration, final int numberOfRollouts){
		Simulator rolloutData = null;
		for(int i=0; i < numberOfIteration; i++){
			TreeNode<VtxDTO> currentNode = root;
			//pick node to explore
			while(!currentNode.isLeafNode()){
				currentNode = pickNode(currentNode);
			}
			
			if(currentNode.getData().visitsMade == 0){
				rolloutData = rollout(currentNode, numberOfRollouts);
			}else{
				if(!checkTerminalNode(currentNode)){
					expandNode(currentNode);
					if(currentNode.isLeafNode()){
						// dead end of tree, set expected cost so high it will not be explored again
						LOG.debug("dead end of search tree for " + currentNode.getData().vtx + ", parent " + currentNode.getParent().getData().vtx);
						currentNode.getData().totalExpectedCost += Double.MAX_VALUE/numberOfIteration;
						if(currentNode.getData().totalExpectedCost < Double.MAX_VALUE/numberOfIteration){
							throw new CtpException("Double overflow!");
						}
						continue;
					}
					currentNode = pickNode(currentNode);
				}
				rolloutData = rollout(currentNode, numberOfRollouts);
			}
			backPropagation(currentNode, rolloutData);
		}
	}
	
	protected boolean checkTerminalNode(TreeNode<VtxDTO> node) {
		if(node.getData().vtx.equals(graph.getTerminalVtx()))
			return true;
		return false;
	}

	protected void expandNode(TreeNode<VtxDTO> currentNode) {
		//find all children of parent (parent will not be child)
		Vertex currentVtx = currentNode.getData().vtx;
		Set<Vertex> children = StochasticWeightedGraph.getAdjacentVertexes(currentVtx, graph);
		for(Vertex child: children){
			if(currentNode.getParent()!=null && currentNode.getParent().getData().vtx.equals(child))continue;
			TreeNode<VtxDTO> temp = new TreeNode<VtxDTO>(currentNode);
			temp.setData(new VtxDTO(child));
			currentNode.addChild(temp);
		}
		
		
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
	
	public abstract Simulator rollout(TreeNode<VtxDTO> node, int numberOfRollouts);
	
}