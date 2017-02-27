package cz.vut.sf.algorithms;

import cz.vut.sf.ctp.Agent;

public class Result {
	public double costPaid;
	public String path;
	
	public Result(Agent a){
		costPaid = a.getTotalCost();
	}
	
	public Result(Agent a, String path){
		costPaid = a.getTotalCost();
		this.path = path;
	}
	@Override
	public String toString() {
		if(path==null){
			return "Result [costPaid=" + costPaid + "]";
		}
		return "Result [costPaid=" + costPaid + "] by path:" + path;
	}
}
