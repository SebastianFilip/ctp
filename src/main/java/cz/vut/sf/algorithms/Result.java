package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Agent;

public class Result {
	public double costPaid;
	public String resultName;
	public String pathTraversed;
	public long timeElapsed = 0;
	public Integer iterationMade = null;
	
	public Result(Agent a){
		costPaid = a.getTotalCost();
		pathTraversed = a.printTraversalHistory().toString();
	}
	
	public Result(Agent a,String msg ,int i){
		this(a, msg);
		this.iterationMade = new Integer(i);
	}
	
	public Result(Agent a, String path){
		this(a);
		this.resultName = path;
	}
	@Override
	public String toString() {
		if(resultName==null){
			return "Result [costPaid=" + costPaid + "]";
		}
		return resultName + " alg " + "[costPaid=" + costPaid  + "] through: " + pathTraversed ;
	}
}
