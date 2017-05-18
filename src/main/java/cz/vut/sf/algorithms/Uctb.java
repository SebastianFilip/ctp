package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.TreeNode;
import cz.vut.sf.graph.Vertex;

public class Uctb extends AbstractUctDepthAlgorithm{

	public Uctb(DefaultCtp ctp, Agent agent) {
		super(ctp, agent);
	}

	@Override
	public Result solve() {
		LOG.info("Starting UCTB new, total rollouts = " + numberOfIterations );
		Result result = super.solve();
		result.resultName = "UCTB new";
		return result;
	}
	
	public boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,
			int additionalSimulation) {
		return false;
	}

	@Override
	protected double evaluateUctFormula(TreeNode<VtxDTO> node) {
		double result = 0;
		double totalExpectedCost = node.getData().totalExpectedCost;
		double totalVisits = node.getData().visitsMade;
		double bias = (node.getParent().getData().totalExpectedCost / node.getParent().getData().visitsMade);
		result -= this.getGraph().getEdgeWeight(this.getGraph().getEdge(node.getParent().getData().vtx, node.getData().vtx));
		result -= totalExpectedCost/totalVisits;
		result += bias*Math.sqrt(Math.log(node.getParent().getData().visitsMade)/(node.getData().visitsMade));
		return result;
	}

}
