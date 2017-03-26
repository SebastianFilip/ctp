package cz.vut.sf.ctp;

import cz.vut.sf.graph.Vertex;

public class VtxDTO{
	public double totalExpectedCost = 0;
	public int visitsMade = 0;
	public final Vertex vtx;
	public VtxDTO(Vertex vtx){
		this.vtx = vtx;
	}
	
	@Override
	public int hashCode() {
		return vtx.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return vtx.equals(obj);
	}

	@Override
	public String toString() {
		return "VtxDTO [vtx=" + vtx + ", totalExpectedCost=" + totalExpectedCost
				+ ", visitsMade=" + visitsMade + "]";
	}
	
	
	
//	public void setData(List<Simulator> sims, int chosenOne){
//		for(int i = 0; i < sims.size(); i++){
//			if(i == chosenOne){continue;}
//			this.totalExpectedCost += sims.get(i).totalCost;
//			this.visitsMade += sims.get(i).totalIterations;
//		}
//	}
}