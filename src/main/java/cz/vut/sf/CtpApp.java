package cz.vut.sf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;

public class CtpApp {
    static Graph<String, DefaultEdge> completeGraph;

    public static void main(String[] args){
//    	EDGE_DIRECTIONS_SECTION
//    	2 4 
//    	1 3 5 
//    	2 5 6 
//    	1 3 5
//		2 4 6
//    	3 5 
    	List<List<Integer>> adjancyList = new ArrayList<List<Integer>>();
    	List<Integer> _1 = new ArrayList<Integer>(); _1.add(2); _1.add(4);
    	List<Integer> _2 = new ArrayList<Integer>(); _2.add(1); _2.add(3); _2.add(5);
    	List<Integer> _3 = new ArrayList<Integer>(); _3.add(2); _3.add(4); _3.add(6);
    	List<Integer> _4 = new ArrayList<Integer>(); _4.add(1); _4.add(3); _4.add(5);
    	List<Integer> _5 = new ArrayList<Integer>(); _5.add(2); _5.add(4); _5.add(6);
    	List<Integer> _6 = new ArrayList<Integer>(); _6.add(3); _6.add(5);
    	
    	adjancyList.add(_1);adjancyList.add(_2);adjancyList.add(_3);
    	adjancyList.add(_4);adjancyList.add(_5);adjancyList.add(_6);
    	
    	Graph<Vertex, StochasticWeightedEdge> g = new DefaultDirectedGraph<Vertex, StochasticWeightedEdge>(StochasticWeightedEdge.class);
    	Vertex s = null;
    	Vertex t = null;
    	
    	initGraph(adjancyList, g, s, t);
    	System.out.println(g.toString());
        
    }

	private static void initGraph(List<List<Integer>> adjancyList, Graph<Vertex, StochasticWeightedEdge> g, Vertex s, Vertex t) {
    	// add vertexs
    	Set<Vertex> vtx = new HashSet<Vertex>();
    	for(int i = 1; i <= adjancyList.size() ;i++){
    		Vertex temp = new Vertex(i);
    		if(i==1){
    			s = temp;
    		};
    		if(i == adjancyList.size()){
    			t = temp;
    		}
    		vtx.add(temp); 
    		g.addVertex(temp);
    	}
    	
    	//add edges from adjancyList
    	for (Vertex source : vtx) {
    		for(int i = 1; i <= adjancyList.size();i++){
    			if(source.getById(i)== null) continue;
    			else{
    				List<Integer> neighbours = adjancyList.get(i-1);
    				for (Integer n : neighbours) {
    					for (Vertex target : vtx){
    						if(target.getById(n) == null) continue;
    						else{
    							g.addEdge(source, target);
    						}
    					}
					}
    			}
    			
        	}
		}
	}

	private static void completeGraphDemo() {

	    // Number of vertices
	    int size = 10;
		// Create the graph object; it is null at this point
        completeGraph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        
        // Create the CompleteGraphGenerator object
        CompleteGraphGenerator<String, DefaultEdge> completeGenerator =
            new CompleteGraphGenerator<String, DefaultEdge>(size);

        // Create the VertexFactory so the generator can create vertices
        VertexFactory<String> vFactory = new VertexFactory<String>()
        {
            private int id = 0;

            public String createVertex()
            {
                return "v" + id++;
            }
        };

        // Use the CompleteGraphGenerator object to make completeGraph a
        // complete graph with [size] number of vertices
        completeGenerator.generateGraph(completeGraph, vFactory, null);

        // Print out the graph to be sure it's really complete
        Iterator<String> iter = new DepthFirstIterator<String, DefaultEdge>(completeGraph);
        while (iter.hasNext()) {
            String vertex = iter.next();
            System.out.println(
                "Vertex " + vertex + " is connected to: "
                    + completeGraph.edgesOf(vertex).toString());
        }
	}

}
