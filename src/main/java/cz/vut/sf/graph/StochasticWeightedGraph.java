package cz.vut.sf.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cz.vut.sf.algorithms.GraphChecker;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.parsers.ParsedDTO;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class StochasticWeightedGraph extends
		SimpleDirectedWeightedGraph<Vertex, StochasticWeightedEdge> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	
	//each vertex should have its own uniq id
	private final Set<Vertex> allVertexes;
	private int blockedEdgesRevealed = 0;
	
	public int getBlockedEdgesRevealed(){
		return blockedEdgesRevealed;
	}
	
	public StochasticWeightedGraph(Class<? extends StochasticWeightedEdge> edgeClass) {
		super(edgeClass);
		allVertexes = new HashSet<Vertex>();
	}
	
	public Set<Vertex> getAllVertexes(){
		return allVertexes;
	}
	
	public StochasticWeightedGraph(Class<? extends StochasticWeightedEdge> edgeClass,ParsedDTO dto) {
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
	
	public Vertex getTerminalVtx(){
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
    								//if there is opposite edge x->y set it same probability and state as y->x
    								temp.setProbability(this.getEdge(target, vtx).getProbability());
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

	
	public static Set<Vertex> getAdjacentVertexes(Vertex source, 
			SimpleDirectedWeightedGraph<Vertex, StochasticWeightedEdge> graph){
		Set<StochasticWeightedEdge> edges = graph.edgesOf(source);
		if(edges.isEmpty()){
			throw new CtpException("Vertex: " + source + " has no adjancent vertexes!");
		}
		Set<Vertex> result = new HashSet<Vertex>();
		for (StochasticWeightedEdge edge : edges) {
			if (!graph.getEdgeTarget(edge).equals(source)){
				result.add(graph.getEdgeTarget(edge));
			}
		}
		return result;
	}
	
	
	/**
	 * Method for simulation purpose, return one possible outcome of stochastic graph
	 * according to its probabilities. The check that source and target vtx is
	 * 
	 * @ new connected Instance of graph that calls it
	 */
	public StochasticWeightedGraph doRollout() {
		return doRollout(0);
	}

	public StochasticWeightedGraph doRollout(int recursiveChecker) {
		if(recursiveChecker >= 100){
			throw new CtpException("Rollout method called recursivly hundred times perhaps "
					+ "probabilites are so high so it cant easily return connected graph");
		}
		StochasticWeightedGraph graphClone = (StochasticWeightedGraph) this.clone();
		
		Set<StochasticWeightedEdge> edges = graphClone.edgeSet();
		for (StochasticWeightedEdge edge : edges) {
			if(edge.beliefState == State.UNKNOWN){
				edge.reEvaluateState(graphClone);
			}
		}
		
		GraphChecker checker = new GraphChecker();
		if(!checker.isGraphConnected(graphClone)){
			graphClone = doRollout(recursiveChecker++);
		}
		return graphClone;
	}

	public void removeAllBlockedEdges() {
		Set<StochasticWeightedEdge> edges = this.edgeSet();
		Set<StochasticWeightedEdge> edgesToRemove = new HashSet<StochasticWeightedEdge>();
		for (Iterator<StochasticWeightedEdge> iterator = edges.iterator(); iterator.hasNext();) {
			StochasticWeightedEdge edge = iterator.next();
			if(edge.getActualState() == State.BLOCKED){
				edgesToRemove.add(edge);
			}
		}
		this.removeAllEdges(edgesToRemove);
	}
	//deep copy the edges
	@Override
	public Object clone(){
		StochasticWeightedGraph clone;
		clone = (StochasticWeightedGraph) super.clone();
		Set<StochasticWeightedEdge> edgesOfThisInstance = this.edgeSet();
		clone.removeAllEdges(edgesOfThisInstance);
		for (Iterator<StochasticWeightedEdge> iterator = edgesOfThisInstance.iterator(); iterator.hasNext();) {
			StochasticWeightedEdge edge = iterator.next();
			clone.addEdge(this.getEdgeSource(edge), this.getEdgeTarget(edge), (StochasticWeightedEdge) edge.clone());
		}
		return clone;
	}
}
