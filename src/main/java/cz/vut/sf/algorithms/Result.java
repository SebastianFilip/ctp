package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Agent;

public class Result {
	public double costPaid;
	public String msg;
	public String pathTraversed;
	
	public Result(Agent a){
		costPaid = a.getTotalCost();
	}
	
	public Result(Agent a, String path){
		costPaid = a.getTotalCost();
		this.msg = path;
		pathTraversed = a.printTraversalHistory().toString();
	}
	@Override
	public String toString() {
		if(msg==null){
			return "Result [costPaid=" + costPaid + "]";
		}
		return msg + " alg " + "[costPaid=" + costPaid  + "] through: " + pathTraversed ;
	}
}
