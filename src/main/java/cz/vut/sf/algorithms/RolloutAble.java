package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.ctp.VtxDTO;
import cz.vut.sf.graph.TreeNode;

public interface RolloutAble {
	public Simulator rollout(TreeNode<VtxDTO> node, int numberOfRollouts);
}
