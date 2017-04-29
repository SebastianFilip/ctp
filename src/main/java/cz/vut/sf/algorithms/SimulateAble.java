package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.TreeNode;

public interface SimulateAble {
	public Simulator simulateTravelsal(TreeNode<VtxDTO> node, int numberOfRollouts);
}
