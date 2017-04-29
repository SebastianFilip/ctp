package cz.vut.sf.algorithms;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cz.vut.sf.ctp.AbstractMonteCarloTreeSearchNew;
import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public abstract class AbstractUctAlgorithmNew extends AbstractMonteCarloTreeSearchNew implements UctAlgorithm{
	protected int numberOfAdditionalRollouts = 1;
	protected int numberOfRollouts = 1000;
	private Set<Vertex> expandedHistory = new HashSet<Vertex>(); 
	
	public Result solve(DefaultCtp ctp, Agent agent){
		int blockedEdgesRevealed = 0;
		
		while(agent.getCurrentVertex()!=ctp.t){
			//If graph changes all of the children of current vertex should be expanded (explored)
			boolean isGraphChanged = false;
			agent.senseAction(ctp.g);
			
			if(blockedEdgesRevealed != ctp.g.getBlockedEdgesRevealed()){
				blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
				isGraphChanged = true;
				expandedHistory = new HashSet<Vertex>(); 
			}
			
			this.setRoot(agent.getCurrentVertex());
			this.setGraph(ctp.g);
			if(this.getRoot().isLeafNode()){
				LOG.debug("Expanding root = "+ this.getRoot().getData().vtx +", except for parent = " + agent.getPreviousVertex(isGraphChanged));
				this.expandNode(this.getRoot(), agent.getPreviousVertex(isGraphChanged));
			}
			
			if(this.getRoot().getChildren().isEmpty()){
				//this means that algorithm choose to travel to vtx about which knew that is dead end
				LOG.error("This should never happen!! Chosen vtx(dead end) = " + agent.getPreviousVertex(true));
				LOG.error("is graph changed =" + isGraphChanged);
				LOG.error("Agent's history = " + agent.getTraversalHistory().toString());
				LOG.error(ctp.g.toString());
				throw new CtpException("Uct choose to go to dead end vtx");
			}
			if(this.getRoot().getChildren().size() == 1){
				//there is only one possible child -> go through it
				LOG.debug("Chosen vtx(only child) = " + this.getRoot().getChildren().get(0).getData().vtx);
				expandedHistory.add(agent.getCurrentVertex());
				agent.traverseToAdjancetVtx(ctp.g, this.getRoot().getChildren().get(0).getData().vtx);
				continue;
			}
			
			this.doSearch(numberOfRollouts);
			Vertex chosenVtx = this.getBestAction(root.getChildren());
			expandedHistory.add(agent.getCurrentVertex());
			agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			if(LOG.isDebugEnabled()){
				LOG.debug("Chosen vtx = " + chosenVtx);
			}
		}
		return new Result(agent, "Rollout Based Algorithm");
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
	
	protected Vertex getBestAction(final List<TreeNode<VtxDTO>> children){
		List<TreeNode<VtxDTO>> nodesList = children;
		Vertex result = null;
		double expectedMinCost = Double.MAX_VALUE;
		for(Iterator<TreeNode<VtxDTO>> i = nodesList.iterator(); i.hasNext();){
			//if proposed vtx is not terminal the length of its edge muse be added
			TreeNode<VtxDTO> child = i.next();
	    	double totalCost = child.getData().totalExpectedCost;
	    	int totalIteration = child.getData().visitsMade;
			double averageCost = totalCost/totalIteration;
			
			if(LOG.isDebugEnabled()){
				LOG.debug("average cost for [" + child.getData().vtx 
						+"] is : "+ averageCost + 
						" visits made:" + totalIteration);
			}
			if(averageCost < expectedMinCost){
				expectedMinCost = averageCost;
				result = child.getData().vtx;
			}
		}
//		LOG.info("");
		for(Iterator<TreeNode<VtxDTO>> i = nodesList.iterator(); i.hasNext();){
			//removing chosen child from children list
			TreeNode<VtxDTO> child = i.next();
	    	if(child.getData().vtx.equals(result)){
	    		i.remove();
	    	}
		}
		if(expandedHistory.contains(result)){
			if(nodesList.isEmpty()){
				return result;
			}
			if(LOG.isDebugEnabled()){
				LOG.debug("Best vtx already visited and graph has not changed since then, picking another vtx..");
			}
			return getBestAction(nodesList);
		}else if(StochasticWeightedGraph.getAdjacentVertexes(result, graph).size() == 1){
			LOG.error("Dead end is chosen!!! Picking another...");
			if(nodesList.isEmpty()){
				LOG.error("Dead end is chosen!!! No other choice left");
				return result;
			}
			return getBestAction(nodesList);
		}
		if(result == null){
			return null;
		}
		return result;
	}
	
	@Override
	public TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> node, Set<Vertex> forbiddenList) {
		if(node.isLeafNode()){
			throw new CtpException("pickNode method called with leaf node. Expand node first.");
		}
		
		List<TreeNode<VtxDTO>> children = node.getChildren();
		List<Integer> unexploredIndexes = getUnexploredChildrenIndexes(children, forbiddenList);
		if(unexploredIndexes!=null && !unexploredIndexes.isEmpty()){
			if(unexploredIndexes.size() == 1){
				return children.get(unexploredIndexes.get(0));
			}
			int chosenIndex = findFromUnexplored(children, unexploredIndexes);
			return children.get(chosenIndex);
		}
		double maxUctValue = -1*Double.MAX_VALUE;
		int maxValueIndex = -1;
		for(int i = 0; i < node.getChildren().size();i++){
			TreeNode<VtxDTO> child = node.getChildren().get(i);
			double uctValue = evaluateUctFormula(child);
			if(!forbiddenList.contains(child.getData().vtx)){
				if(uctValue == Double.MAX_VALUE){
					return child;
				}else if(uctValue > maxUctValue){
					maxValueIndex = i;
					maxUctValue = uctValue;
				}
			}
		}
		if(maxValueIndex == -1){
			maxUctValue = 0;
		}
		//TODO find why sometimes maxValueIndex = -1 ArrayOutOfBounds Exception
		return node.getChildren().get(maxValueIndex);
	}

	protected int findFromUnexplored(List<TreeNode<VtxDTO>> children, List<Integer> unexploredIndexes){
		return 0;
	}
	
	protected abstract List<Integer> getUnexploredChildrenIndexes(List<TreeNode<VtxDTO>> children, Set<Vertex> forbiddenList);
	
	protected abstract double evaluateUctFormula(TreeNode<VtxDTO> node);
	
	public int getNumberOfAdditionalRollouts() {
		return numberOfAdditionalRollouts;
	}

	public int getNumberOfRollouts() {
		return numberOfRollouts;
	}

	public void setNumberOfAdditionalRollouts(int n) {
		this.numberOfAdditionalRollouts = n;
	}

	public void setNumberOfRollouts(int i) {
		this.numberOfRollouts = i;
	}
}
