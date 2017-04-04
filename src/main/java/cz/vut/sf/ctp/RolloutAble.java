package cz.vut.sf.ctp;

import cz.vut.sf.graph.TreeNode;

public interface RolloutAble {
	public Simulator rollout(TreeNode<VtxDTO> node, int numberOfRollouts);
}
