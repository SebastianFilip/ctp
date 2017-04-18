package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.graph.Vertex;

public interface UctAlgorithm extends DefaultCtpAlgorithm{
	public void doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int numberOfRollouts);
	public int getNumberOfRollouts();
	public int getNumberOfIterations();
	public void setNumberOfRollouts(int n);
	public void setNumberOfIterations(int i);
}
