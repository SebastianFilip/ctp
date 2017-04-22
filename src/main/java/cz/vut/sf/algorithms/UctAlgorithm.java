package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.graph.Vertex;

public interface UctAlgorithm extends DefaultCtpAlgorithm{
	public boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int additionalSimulation);
	public int getNumberOfRollouts();
	public int getNumberOfIterations();
	public void setNumberOfRollouts(int n);
	public void setNumberOfIterations(int i);
}
