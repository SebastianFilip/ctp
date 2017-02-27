package cz.vut.sf.main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.VertexFactory;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import cz.vut.sf.algorithms.GraphChecker;
import cz.vut.sf.algorithms.GreedyAlgorithm;
import cz.vut.sf.algorithms.RepositionAlgorithm;
import cz.vut.sf.algorithms.Result;
import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticDirectedWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.parsers.TestData;

public class CtpApp {
    static Graph<String, DefaultEdge> completeGraph;

    public static void main(String[] args){

    	StochasticDirectedWeightedGraph g = new StochasticDirectedWeightedGraph(StochasticWeightedEdge.class, TestData.getData());
    	StochasticDirectedWeightedGraph g2 = (StochasticDirectedWeightedGraph) g.clone();
    	Vertex s = g.getSourceVtx();
    	Vertex t = g.getTargetVtx();

    	//GA
    	DefaultCtp ctp = new DefaultCtp(g, s, t);
    	if(!new GraphChecker().isGraphConnected(g)){
    		return;
    	}
    	System.out.println(g.toString());
    	GreedyAlgorithm ga = new GreedyAlgorithm();
    	System.out.println("start");
    	Result r = ga.solve(ctp, new Agent(ctp.s));
    	System.out.println(r.toString());
    	
    	//RA
    	DefaultCtp ctp2 = new DefaultCtp(g2, s, t);
    	if(!new GraphChecker().isGraphConnected(g2)){
    		return;
    	}
    	System.out.println(g2.toString());
    	RepositionAlgorithm ra = new RepositionAlgorithm();
    	System.out.println("start 2");
    	r = ra.solve(ctp2, new Agent(ctp2.s));
    	System.out.println(r.toString());
    	
    	//SSP s->t
    	DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(g);
    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(s, t);
    	System.out.println("SP cost: "+ shortestPath.getWeight() +" through: "+shortestPath.getVertexList().toString());
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
