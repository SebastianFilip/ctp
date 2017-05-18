package cz.vut.sf.algorithms;

import java.util.List;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public abstract class AbstractUctWidthAlgorithm extends AbstractTreeSearchWidth implements UctAlgorithm {
	public AbstractUctWidthAlgorithm(DefaultCtp ctp, Agent agent) {
		super(ctp, agent);
	}
	protected int numberOfAdditionalRollouts = 1;
	protected int numberOfIterations = 1000;
	protected long timeToDecision = 0; // default 100s
	private int iterationsMade = 0;
	protected OptimisticUctFormula uctFormula = new OptimisticUctFormula(this.graph, this.root);
	
	@Override
	public Result solve() {
		int blockedEdgesRevealed = 0;
		this.setGraph(ctp.g);
		while(agent.getCurrentVertex()!=ctp.t){
			//If graph changes all of the children of current vertex should be expanded (explored)
			boolean isGraphChanged = false;
			agent.senseAction(ctp.g);
			
			if(blockedEdgesRevealed != ctp.g.getBlockedEdgesRevealed()){
				blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
				uctFormula.setGraph(ctp.g);
				uctFormula.clearCachedData();
				this.setGraph(ctp.g);
				isGraphChanged = true;
			}
			this.setRoot(agent.getCurrentVertex());
			uctFormula.setRoot(root);
			
			if(this.getRoot().isLeafNode()){
				LOG.debug("Expanding root = "+ this.getRoot().getData().vtx +", except for parent = " + agent.getPreviousVertex(isGraphChanged));
				this.expandNode(this.getRoot(), agent.getPreviousVertex(isGraphChanged));
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
			if(getTimeToDecision() == 0){
				iterationsMade += this.doSearch(numberOfIterations, numberOfAdditionalRollouts);	
			}else{
				iterationsMade += this.doSearch(numberOfIterations, numberOfAdditionalRollouts, getTimeToDecision());
			}
			Vertex chosenVtx = this.getBestAction();
			agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			if(LOG.isDebugEnabled()){
				LOG.debug("Chosen vtx = " + chosenVtx);
			}
		}
		return new Result(agent, "Rollout Based Algorithm", iterationsMade/(agent.getTraversalHistory().size()-1));
	}

	public abstract boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int additionalSimulation);
	
	@Override
	public abstract TreeNode<VtxDTO> pickNode(TreeNode<VtxDTO> parent);

	@Override
	public Simulator simulateTravelsal(TreeNode<VtxDTO> node, int additionalSimulation) {		
		Simulator simulator = new Simulator(node.getParent().getData().vtx);
		if(doSimulation(simulator,node.getData().vtx ,additionalSimulation)){
			return simulator;
		}
		LOG.debug("rollout returns simulator = null");
		return null;
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
