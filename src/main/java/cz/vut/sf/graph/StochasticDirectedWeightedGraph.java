package cz.vut.sf.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cz.vut.sf.parsers.ParsedDTO;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class StochasticDirectedWeightedGraph extends
		SimpleDirectedWeightedGraph<Vertex, StochasticWeightedEdge> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	
	//each vertex should have its own uniq id
	private final Set<Vertex> allVertexes;
	private double blockedEdgesRevealed = 0;
	
	public StochasticDirectedWeightedGraph(Class<? extends StochasticWeightedEdge> edgeClass) {
		super(edgeClass);
		allVertexes = new HashSet<Vertex>();
	}
	
	public Set<Vertex> getAllVertexes(){
		return allVertexes;
	}
	
	public StochasticDirectedWeightedGraph(Class<? extends StochasticWeightedEdge> edgeClass,ParsedDTO dto) {
		super(edgeClass);
		allVertexes = new HashSet<Vertex>();
		initGraph(dto);
	}
	
	public Vertex getSourceVtx(){
		for(Vertex s: allVertexes){
			if (s.getById(1) != null){
				return s;
			}
		}
		return null;
	}
	
	public Vertex getTargetVtx(){
		for(Vertex s: allVertexes){
			if (s.getById(allVertexes.size()) != null){
				return s;
			}
		}
		return null;
	}
	
	public Vertex getVtxById(int id){
		for(Vertex s: allVertexes){
			if (s.getById(id) != null){
				return s;
			}
		}
		return null;
	}
	
	public void initGraph(ParsedDTO dto) {
    	// add vertexes (each one of them must have unique id)
    	for(int id = 1; id <= dto.adjacencyList.size() ;id++){
    		Vertex temp = new Vertex(id);
    		allVertexes.add(temp); 
    		this.addVertex(temp);
    	}
    	
    	//add edges from adjancyList
    	for (Vertex vtx : allVertexes) {
    		//iterate whole adjancy list to find current vtx (since it is hash set it have to be found out)
    		for(int i = 1; i <= dto.adjacencyList.size();i++){
    			if(vtx.getById(i)== null) continue;
    			else{
    				//vtx is found add its adjacent vertexes from adj.list
    				// get i-1 since adj. list indexes starts with 0
    				List<Integer> adjacentVtxs = dto.adjacencyList.get(i-1);
    				for (int adjacentIndex = 0; adjacentIndex < adjacentVtxs.size(); adjacentIndex++)
    				{
    					//iterate through all vtxs again to find defined adjacent vtx (target)
    					for (Vertex target : allVertexes){
    						if(target.getById(adjacentVtxs.get(adjacentIndex)) == null) continue;
    						else{
    							StochasticWeightedEdge temp = this.addEdge(vtx, target);
    							this.setEdgeWeight(temp, dto.weightsList.get(i-1).get(adjacentIndex));
    							// probability and state determination is only one even
    							// for oriented graphs (both x->y y->x has the same actual state)
    							if(this.getEdge(target, vtx) == null){
    								temp.setProbability(dto.probabilitiesList.get(i-1).get(adjacentIndex));
    							}
    							else{
    								temp.actualState = this.getEdge(target, vtx).actualState;
    							}
    						}
    					}
    				}
    			}
        	}
		}
	}// end of initGraph
	
	@Override
	public boolean removeEdge(StochasticWeightedEdge e){
		blockedEdgesRevealed ++;
		return super.removeEdge(e);
	}


}
