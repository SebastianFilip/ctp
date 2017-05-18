package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.graph.Vertex;

public interface UctAlgorithm{
	public boolean doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int additionalSimulation);
	
	public int getNumberOfIterations();
	public void setNumberOfIterations(int n);
	
	public int getNumberOfAdditionalRollouts();
	public void setNumberOfAdditionalRollouts(int n);
	
	public void setTimeToDecision(long miliseconds);
	public long getTimeToDecision();
}
