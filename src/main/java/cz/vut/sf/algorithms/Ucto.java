package cz.vut.sf.algorithms;


import java.util.ArrayList;
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

public class Ucto extends AbstractUctAlgorithm{
//	private int additionalFakeRollouts = 10;
	private Map<RolloutKey, Double[]> additionalRolloutsData = new HashMap<RolloutKey, Double[]>();
	
	@Override
	public Result solve(DefaultCtp ctp, Agent agent) {
		LOG.info("Starting UCTO, total rollouts = " + numberOfRollouts + ", additional rollouts = " + numberOfAdditionalRollouts);
		Result result = super.solve(ctp, agent);
		result.msg = "UCTO";
		return result;
	}

	/**
	 * This method pick and return argument's children node who maximize UCT formula,
	 * for unexplored children UCT formula is infinity, for those cases
	 * logic is added where chosen node is determined by its shortest path to destination vertex.
	 * Shortest dijkstras path wins.
	 *  
	 * @param node - TreeNode from who 'best' child will be return
	 * @return TreeNode
	 */
	@Override
	public TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> node) {
		if(node.isLeafNode()){
			throw new CtpException("pickNode method called with leaf node. Expand node first.");
		}
		
		List<TreeNode<VtxDTO>> children = node.getChildren();
		List<Integer> unexploredIndexes = getUnexploredChildrenIndexes(children);
		if(unexploredIndexes!=null){
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
			if(uctValue == Double.MAX_VALUE){
				return child;
			}else if(uctValue > maxUctValue){
				maxValueIndex = i;
				maxUctValue = uctValue;
			}
		}
		//TODO find why sometimes maxValueIndex = -1 ArrayOutOfBounds Exception
		return node.getChildren().get(maxValueIndex);
	}
	private double getDijkstraPathWeight(Vertex start){
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(this.getGraph());
		shortestPath = dsp.getPath(start, this.getGraph().getTerminalVtx());
		return shortestPath.getWeight();
	}

	private int findFromUnexplored(List<TreeNode<VtxDTO>> children, List<Integer> unexploredIndexes) {
		int result = -1;
		double shortestPathValue = Double.MAX_VALUE;
		for(int i=0; i < unexploredIndexes.size();i++){
			double dijkstraPathWeight = getDijkstraPathWeight(children.get(unexploredIndexes.get(i)).getData().vtx);
			if(dijkstraPathWeight < shortestPathValue){
				result = unexploredIndexes.get(i);
				shortestPathValue = dijkstraPathWeight;
			}
		}
		return result;
	}


	private List<Integer> getUnexploredChildrenIndexes(List<TreeNode<VtxDTO>> children) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getData().visitsMade == 0){
				Integer temp = new Integer(i);
				result.add(temp);
			}
		}
		return result.isEmpty() ? null : result;
	}


	private double evaluateUctFormula(TreeNode<VtxDTO> child) {
		if(child.getData().visitsMade == 0){
			return Double.MAX_VALUE;
		}
		//
		Double[] data = null;
		if(!additionalRolloutsData.containsKey(new RolloutKey(child.getParent().getData().vtx, child.getData().vtx))){
			Simulator gaData = simulateTravelsal(child, numberOfAdditionalRollouts);
			if(gaData == null){
				// simulation did not succeeded
				data = new Double[] {Double.MAX_VALUE / numberOfAdditionalRollouts, 1d};
			}else{
				data = new Double[] {gaData.totalCost / gaData.totalIterations, (double) gaData.totalIterations};
			}
			if(LOG.isDebugEnabled()){
				LOG.debug("Caching data for evaluating UCT fromula. " + child.getParent().getData().vtx + "<-" + child.getData().vtx + ", avg cost:" + data[0]);
			}
			additionalRolloutsData.put(new RolloutKey(child.getParent().getData().vtx, child.getData().vtx), data);
		}else{
			data = additionalRolloutsData.get(new RolloutKey(child.getParent().getData().vtx, child.getData().vtx));
		}
		
		double result = 0;
		double totalExpectedCost = child.getData().totalExpectedCost;
		double totalVisits = child.getData().visitsMade;
		double bias = (child.getParent().getData().totalExpectedCost / child.getParent().getData().visitsMade);
		bias = bias*10;
		result -= this.getGraph().getEdgeWeight(this.getGraph().getEdge(child.getParent().getData().vtx, child.getData().vtx));
		result -= totalExpectedCost/totalVisits;
		result -= data[0] / data[1];
		result += bias*Math.sqrt(Math.log(child.getParent().getData().visitsMade + data[1])/(child.getData().visitsMade + data[1]));
//		System.out.println(child.getData().vtx + ", avg=" + totalExpectedCost/totalVisits +" ,UCT value=" + result );
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
	
	private class RolloutKey{
		public final Vertex parent;
		// child must not be null ever
		public final Vertex child;
		
		public RolloutKey(Vertex p, Vertex ch){
			parent = p;
			child = ch;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			if(parent == null){
				return prime * child.hashCode();
			}
			result = prime * (parent.hashCode() + child.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RolloutKey other = (RolloutKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (child == null) {
				if (other.child != null)
					return false;
			} else if (!child.equals(other.child))
				return false;
			if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (!parent.equals(other.parent))
				return false;
			return true;
		}

		private Ucto getOuterType() {
			return Ucto.this;
		}
		
	}
}
