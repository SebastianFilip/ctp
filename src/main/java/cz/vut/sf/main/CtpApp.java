package cz.vut.sf.main;

import java.util.ArrayList;
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

import cz.vut.sf.algorithms.ComparisionAlgorithm;
import cz.vut.sf.algorithms.GraphChecker;
import cz.vut.sf.algorithms.GreedyAlgorithm;
import cz.vut.sf.algorithms.Hop;
import cz.vut.sf.algorithms.Oro;
import cz.vut.sf.algorithms.RepositionAlgorithm;
import cz.vut.sf.algorithms.Result;
import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.EdgeConvertor;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.parsers.TestData;

public class CtpApp {
    static Graph<String, DefaultEdge> completeGraph;

    public static void main(String[] args){
    	//Instantiate CTP
    	StochasticWeightedGraph g = new StochasticWeightedGraph(StochasticWeightedEdge.class, TestData.getData2());
    	Vertex s = g.getSourceVtx();
    	Vertex t = g.getTargetVtx();
    	DefaultCtp ctp = new DefaultCtp(g, s, t);
    	System.out.println(g.toString());
    	if(!new GraphChecker().isGraphConnected(g)){
    		System.out.println("is connected: false");
    		g.removeAllBlockedEdges();
    		System.out.println(g.toString());
    		return;
    	}
    	
    	//TEST RCTP
    	
    	
    	
    	//TEST RCTP
    	
    	//Compute
    	List<AlgNames> algorithmsToBeMade = new ArrayList<AlgNames>();
    	algorithmsToBeMade.add(AlgNames.HOP); algorithmsToBeMade.add(AlgNames.ORO);
    	algorithmsToBeMade.add(AlgNames.GA); algorithmsToBeMade.add(AlgNames.RA);
    	algorithmsToBeMade.add(AlgNames.CA);
    	List<Result> results = algorithmRunner(ctp, algorithmsToBeMade);
    	
    	//Print results
    	printResult(results);
    	//Optimal solution print s->t
    	ctp.g.removeAllBlockedEdges();
    	DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(s, t);
    	System.out.println("SP cost: "+ shortestPath.getWeight() +" through: "+shortestPath.getVertexList().toString());
    }
    
    private static void printResult(List<Result> results){
    	if(results==null)return;
    	for(Result result : results){
    		if(result!=null){
    		System.out.println(result.toString());
    		}
    	}
    }
    
    private static enum AlgNames{
    	GA,RA,CA,HOP, ORO;
    }
    
    private static List<Result> algorithmRunner(DefaultCtp ctp, List<AlgNames> algorithms){
    	try {
			List<Result> results = new ArrayList<Result>();
			for(AlgNames algorithm : algorithms){
				//make clone of graph instance so for upcoming algorithms same instance is used
				StochasticWeightedGraph graphClone = (StochasticWeightedGraph) ctp.g.clone();
//				System.out.println(ctp.g.toString());
				
				Result r = null;
				switch (algorithm){
					case GA:
						GreedyAlgorithm ga = new GreedyAlgorithm();
				    	r = ga.solve(ctp, new Agent(ctp.s));
						break;
					case RA:
						RepositionAlgorithm ra = new RepositionAlgorithm();
				    	r = ra.solve(ctp, new Agent(ctp.s));
						break;
					case CA:
						ComparisionAlgorithm ca = new ComparisionAlgorithm();
						r = ca.solve(ctp, new Agent(ctp.s));
						break;
					case HOP:
						Hop hop = new Hop();
						r = hop.solve(ctp, new Agent(ctp.s));
						break;
					case ORO:
						Oro oro = new Oro();
						r = oro.solve(ctp, new Agent(ctp.s));
						break;
					}
				//set graphClone to ctp
				ctp.g = graphClone;
				results.add(r);
			}
			return results;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
