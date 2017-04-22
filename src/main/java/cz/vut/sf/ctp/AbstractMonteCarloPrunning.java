package cz.vut.sf.ctp;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public abstract class AbstractMonteCarloPrunning extends AbstractMonteCarloTreeSearch {
	private static Map<Vertex, ExpandedNodeDTO> expandedVtx = null;
	Set<Vertex> pathToRoot= null;
	
	@Override
	public abstract TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> parent);
	
	@Override
	public abstract Simulator rollout(TreeNode<VtxDTO> node, int numberOfRollouts);
	
	@Override
	public void doSearch(final int numberOfIteration, final int numberOfRollouts){
		expandedVtx = new HashMap<Vertex, ExpandedNodeDTO>();
		Simulator rolloutData = null;
		for(int i=0; i < numberOfIteration; i++){
			if(root.getChildren().size() == 1){return;}
			boolean innerCycle = false;
			TreeNode<VtxDTO> currentNode = root;
			pathToRoot = new HashSet<Vertex>();
			//pick node to explore
			while(!currentNode.isLeafNode()){
				currentNode = pickNode(currentNode);
				if(!pathToRoot.add(currentNode.getData().vtx)){
					LOG.debug("Exploring vtx:" + currentNode.toString() + ", is already on path to root!");
					innerCycle = true;
				}
			}
			
			if(currentNode.getData().visitsMade == 0){
				rolloutData = rollout(currentNode, numberOfRollouts);
				// for the first visit save data to expandedVtx map, except for terminal vtx

			}else{
				if(!checkTerminalNode(currentNode)){
					expandNode(currentNode, currentNode.getParent().getData().vtx, root.getData().vtx);
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
					if(!pathToRoot.add(currentNode.getData().vtx)){
						LOG.debug("Exploring vtx:" + currentNode.toString() + ", is already on path to root!");
						innerCycle = true;
					}
				}
				rolloutData = rollout(currentNode, numberOfRollouts);
			}
			// if rolloutData == null then, rollout was unsuccessful (bad weather)
			if(rolloutData != null){
				backPropagation(currentNode, rolloutData);
				checkExpandedVtx(rolloutData, currentNode, innerCycle);
			}
		}
	}
	
	private void checkExpandedVtx(Simulator rolloutData, TreeNode<VtxDTO> currentNode, boolean innerCycle) {
		//!checkTerminalNode(currentNode)
		double expectedCost = rolloutData.totalCost/rolloutData.totalIterations;
		double avgExpectedCost = getAvgExpectdCost(currentNode, expectedCost);
		ExpandedNodeDTO dto = new ExpandedNodeDTO(currentNode.getParent().getData().vtx,expectedCost ,avgExpectedCost);
		if(expandedVtx.get(currentNode.getData().vtx) == null){
			expandedVtx.put(currentNode.getData().vtx, dto);
		}else{
			//do pruning but only for different dto's -> for terminal vtx it happens 
			//that the same dto's can occur
			if(!expandedVtx.get(currentNode.getData().vtx).parent.equals(dto.parent)){
				if(!innerCycle){
					doPrunning(expandedVtx.get(currentNode.getData().vtx),dto, currentNode.getData().vtx);
				}else{
					LOG.debug("Inner cycle! Pruning vtx: " + currentNode.getData().vtx + ", with parent: " + dto.parent);
					prune(dto, expandedVtx.get(currentNode.getData().vtx), currentNode.getData().vtx);
				}
			}else{
				if(!currentNode.getData().vtx.equals(graph.getTerminalVtx())){
					LOG.error("THIS SHOULD NOT HAPPEN!!!!!!!!!!!!!!!!!");
				}
			}
		}
	}

	private void doPrunning(ExpandedNodeDTO oldDto, ExpandedNodeDTO newDto, Vertex doubler) {
		ExpandedNodeDTO pruneDto = null;
		ExpandedNodeDTO dtoToKeep = null;
		if(oldDto.avgExpectedCost > newDto.avgExpectedCost){
			pruneDto = oldDto;
			dtoToKeep = newDto;
			
		}else{
			pruneDto = newDto;
			dtoToKeep = oldDto;
		}
		LOG.debug("Prunning vtx: " + doubler + ", with parent: " + pruneDto.parent);
		LOG.debug("Keeping vtx: " + doubler + ", with parent: " + dtoToKeep.parent);

		prune(pruneDto, dtoToKeep, doubler);
		expandedVtx.put(doubler, dtoToKeep);
	}

	private void prune(final ExpandedNodeDTO dtoToPrune, final ExpandedNodeDTO dtoToKeep,final Vertex doubler) {
		// searching for doublers's parent
		final TreeNode<VtxDTO> pruneNode = getFromTreeByBFS(doubler, dtoToPrune.parent);
		
		if(pruneNode == null){
			throw new CtpException("Prune node was not found in tree!");
		}
		List<TreeNode<VtxDTO>> pruneChildren = removeVtxNodeFromParentNodeTree(doubler, pruneNode.getParent());
		
		//check if pruneChildren are not null or empty then they must be
		//transfered to dtoToKeep, also avgExpCost must be recalculated
		if(pruneChildren != null && !pruneChildren.isEmpty()){
			TreeNode<VtxDTO> nodeToKeep = getFromTreeByBFS(doubler, dtoToKeep.parent);
			for(int i=0; i < pruneChildren.size(); i++){
				TreeNode<VtxDTO> childToMove = pruneChildren.get(i);
				if(childToMove.getData().visitsMade == 0){continue;}
				// if prune child has visits there should not be the same child in nodeToKeep
				if(!nodeToKeep.getChildren().isEmpty()){
					for(int j=0; j < nodeToKeep.getChildren().size(); j++){
						if(nodeToKeep.getChildren().get(j).getData().equals(childToMove.getData())
								&& nodeToKeep.getChildren().get(j).getData().visitsMade != 0){
							throw new CtpException("In tree it should never happen that there are two same already visited vtx:" + childToMove.getData().vtx);
						}
					}
				}
				childToMove.setParent(nodeToKeep);
				nodeToKeep.getChildren().add(childToMove);
				reBackPropagateBranch(childToMove);
			}
		}
		
		//check if pruned vtx was the only child then prune parent as well
		TreeNode<VtxDTO> parentOfCurrent = pruneNode.getParent();
		while(parentOfCurrent.getChildren().isEmpty()){
			if(parentOfCurrent.getParent() == null){LOG.info("null parent mby root?!!");}
			removeVtxNodeFromParentNodeTree(parentOfCurrent.getData().vtx, parentOfCurrent.getParent());
			LOG.debug("Removing also vtx: " + parentOfCurrent.getData().vtx + ", since it was childless");
			expandedVtx.remove(parentOfCurrent.getData().vtx);
			parentOfCurrent = parentOfCurrent.getParent();
		}
	}

	private void reBackPropagateBranch(TreeNode<VtxDTO> childToMove) {
		final double valueToPropagate = childToMove.getData().totalExpectedCost;
		final int visitsToPropagate = childToMove.getData().visitsMade;
		
		StringBuilder sb = new StringBuilder();
		sb.append(childToMove.getData().vtx );
		TreeNode<VtxDTO> propagationNode = childToMove;
		while(propagationNode.getParent()!=null) {
			sb.append("<-" + propagationNode.getParent().getData().vtx);
			
			propagationNode.getParent().getData().totalExpectedCost += valueToPropagate;
			propagationNode.getParent().getData().visitsMade += visitsToPropagate;
			propagationNode = propagationNode.getParent();
		}
		LOG.debug("re-propagation for "+ sb.toString() + ": " + valueToPropagate + "/" + visitsToPropagate +" = " + valueToPropagate/visitsToPropagate);
	}

	private List<TreeNode<VtxDTO>>  removeVtxNodeFromParentNodeTree(final Vertex vtx, TreeNode<VtxDTO> parentNode) {
		List<TreeNode<VtxDTO>> result= null;
		boolean wasRemoved = false;
		for(Iterator<TreeNode<VtxDTO>> iterator= parentNode.getChildren().iterator(); iterator.hasNext();){
			TreeNode<VtxDTO> child = iterator.next();
			if(child.getData().vtx.equals(vtx)){
				// prune it
				
				if(child.getChildren() != null && !child.getChildren().isEmpty()){
					result = child.getChildren();
					LOG.debug("Vtx to remove: " + vtx + ", has " + result.size() +" child(ren)!");
				}
				iterator.remove();
				wasRemoved = true;
				break;
			}
		}
		LOG.debug(vtx + " removed=" + wasRemoved);
		return result;
	}

	private double getAvgExpectdCost(final TreeNode<VtxDTO> currentNode, final double avgCostOfCurrenNode) {
		double result = avgCostOfCurrenNode;
		int devidor = 1;
		TreeNode<VtxDTO> parent = currentNode.getParent();
		ExpandedNodeDTO parentDTO = null;
		// for root, no cost estimation is needed
		while(parent != null && !parent.getData().equals(this.getRoot().getData())){
			parentDTO = expandedVtx.get(parent.getData().vtx);
			result += parentDTO.avgExpectedCost;
			devidor ++;
			parent = parent.getParent();
		}
		return result/devidor;
	}

	protected void expandNode(TreeNode<VtxDTO> currentNode, Vertex parent, Vertex currentPosition) {
		//find all children of parent (parent should not be child)
		Vertex currentVtx = currentNode.getData().vtx;
		Set<Vertex> children = StochasticWeightedGraph.getAdjacentVertexes(currentVtx, graph);
		for(Vertex child: children){
			if(parent!=null && parent.equals(child))continue;
			if(parent!=null && currentPosition.equals(child))continue;
			TreeNode<VtxDTO> temp = new TreeNode<VtxDTO>(currentNode);
			temp.setData(new VtxDTO(child));
			currentNode.addChild(temp);
		}
	}

	public class ExpandedNodeDTO{
		final double expectedCost;
		double avgExpectedCost = 0;
		final Vertex parent;
		public ExpandedNodeDTO(Vertex parent,double expectedCost ,double avgExpectedCost){
			this.parent = parent;
			this.avgExpectedCost = avgExpectedCost;
			this.expectedCost = expectedCost;
		}
	}
	
	private TreeNode<VtxDTO> getFromTreeByBFS(final Vertex required, final Vertex parent){
		Queue <TreeNode<VtxDTO>> open = new ArrayDeque<TreeNode<VtxDTO>>();
		TreeNode<VtxDTO> current = this.getRoot();
		open.add(current);
		boolean parentFound= false;
		// search for t's parent in whole tree
		while(!open.isEmpty()){
			current = open.poll();
			List<TreeNode<VtxDTO>> children = current.getChildren();
			if(children == null){
				//should not happen since this is breadth first search
				throw new CtpException("there are no children for vtx");
			}
			if(current.getData().vtx.equals(parent)){
				parentFound = true;
				for(int i=0; i < children.size(); i++){
					if(children.get(i).getData().vtx.equals(required)){
						return children.get(i);
					}
				}
			}
			for(int i = 0; i < children.size(); i++){
				open.add(children.get(i)); 
			}
		}
		LOG.info("There were no element:" + required + " with parent:"+ parent + " found = " + parentFound + " within tree (by Breadth First Search)");
		return null;
	}	
	private TreeNode<VtxDTO> getFromTreeByDFS(final Vertex required, final Vertex parent){
		Stack <TreeNode<VtxDTO>> open = new Stack<TreeNode<VtxDTO>>();
		TreeNode<VtxDTO> current = this.getRoot();
		open.push(current);
		// search for t's parent in whole tree
		while(!open.isEmpty()){
			current = open.pop();
			List<TreeNode<VtxDTO>> children = current.getChildren();
			if(current.getData().vtx.equals(parent)){
				for(int i=0; i < children.size(); i++){
					if(children.get(i).getData().vtx.equals(required)){
						return children.get(i);
					}
				}
			}
			for(int i = 0; i < children.size(); i++){
				open.push(children.get(i)); 
			}
		}
		LOG.warn("There were no element:" + required + " with parent:"+ parent +" within tree (by Depth First Search)");
		return null;
	}
	
	protected void printTerminationFromExpanded(){
		final Vertex t = graph.getTerminalVtx();
		if(!expandedVtx.containsKey(t)){
			LOG.info("Termination vertex has not been expanded yet");
			return;
		}
		final Vertex parentOfT = expandedVtx.get(t).parent;

		TreeNode<VtxDTO> vtxNode = getFromTreeByDFS(t, parentOfT);
		
		StringBuilder sb = new StringBuilder();
		double avgSumFromExpanded = expandedVtx.get(t).avgExpectedCost;
		double iteration = 1;
		sb.append("t");
		while(vtxNode.getParent()!=null){
			// dont want to add value from root
			avgSumFromExpanded += expandedVtx.get(vtxNode.getData().vtx).avgExpectedCost;
			iteration++;
			sb.append("<-");
			vtxNode = vtxNode.getParent();
			sb.append(vtxNode.getData().vtx);
		}
		LOG.info("trace form t <- s : "+ sb.toString() + ", total avgExpectedCost " + avgSumFromExpanded/iteration);
	}
}
