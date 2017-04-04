package cz.vut.sf.ctp;

import java.util.Set;

import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.ctp.VtxDTO;

public abstract class MonteCarloTreeSearch implements RolloutAble{
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
		final double propagatedValue = fromNode.getData().totalExpectedCost += data.totalCost / data.totalIterations;
		final int totalVisitsOfExplored  = ++fromNode.getData().visitsMade;
		
//		StringBuilder sb = new StringBuilder();
//		sb.append(fromNode.getData().vtx );
		do {
//			sb.append("->" + fromNode.getParent().getData().vtx);
			
			fromNode.getParent().getData().totalExpectedCost += propagatedValue/totalVisitsOfExplored;
			fromNode.getParent().getData().visitsMade ++;
			fromNode = fromNode.getParent();
		}while(fromNode.getParent()!=null);
//		System.out.println("back propagation for "+ sb.toString() + " = " + propagatedValue/totalVisitsOfExplored);
//		getBestAction();
	}
	
	public void backPropagationForTerminal(TreeNode<VtxDTO> fromNode){
		updateTerminalData(fromNode);
		double valueToPropagate = fromNode.getData().totalExpectedCost/fromNode.getData().visitsMade;
		do {
			fromNode.getParent().getData().visitsMade ++;
			fromNode.getParent().getData().totalExpectedCost += valueToPropagate;
			fromNode = fromNode.getParent();
		}while(fromNode.getParent()!=null);
	}
	
	public void doSearch(final int numberOfIteration, final int numberOfRollouts){
		Simulator rolloutData = null;
		for(int i=0; i < numberOfIteration; i++){
			TreeNode<VtxDTO> currentNode = root;
			//pick node to explore
			while(!currentNode.isLeafNode()){
				currentNode = pickNode(currentNode);
			}
			
			//rollout
//			if(checkTerminalNode(currentNode)){
//				backPropagationForTerminal(currentNode);
//				continue;
//			}
			
			if(currentNode.getData().visitsMade == 0){
				rolloutData = rollout(currentNode, numberOfRollouts);
			}else{
				if(!checkTerminalNode(currentNode)){
					expandNode(currentNode);
					currentNode = pickNode(currentNode);
				}
				rolloutData = rollout(currentNode, numberOfRollouts);
			}
			backPropagation(currentNode, rolloutData);
		}
	}
	
	private void updateTerminalData(TreeNode<VtxDTO> node){
		StochasticWeightedEdge edgeFromParentToChild = this.getGraph().getEdge(node.getParent().getData().vtx, node.getData().vtx);
		node.getData().totalExpectedCost += this.getGraph().getEdgeWeight(edgeFromParentToChild);
		node.getData().visitsMade ++;
	}
	
	private boolean checkTerminalNode(TreeNode<VtxDTO> node) {
		if(node.getData().vtx.equals(graph.getTerminalVtx()))
			return true;
		return false;
	}

	protected void expandNode(TreeNode<VtxDTO> currentNode) {
		//find all children of parent (parent should not be child)
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
	 * Expands passes currentNode, Vertex parent will not be new child of currentNode
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