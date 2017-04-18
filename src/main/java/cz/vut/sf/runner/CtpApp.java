package cz.vut.sf.runner;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import cz.vut.sf.algorithms.ComparisionAlgorithm;
import cz.vut.sf.algorithms.GraphChecker;
import cz.vut.sf.algorithms.GreedyAlgorithm;
import cz.vut.sf.algorithms.Hop;
import cz.vut.sf.algorithms.Oro;
import cz.vut.sf.algorithms.RepositionAlgorithm;
import cz.vut.sf.algorithms.Result;
import cz.vut.sf.algorithms.UctPrunning;
import cz.vut.sf.algorithms.Uctb;
import cz.vut.sf.algorithms.Uctb2;
import cz.vut.sf.algorithms.Ucto;
import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.Vertex;
import cz.vut.sf.parsers.BasicCtpParser;
import cz.vut.sf.parsers.ParsedDTO;

public class CtpApp extends CtpAppConstants {
    static Graph<String, DefaultEdge> completeGraph;
    
    public static void run(List<AlgNames> algorithmsToBeMade, int numberOfRuns){
    	ParsedDTO graphData = new BasicCtpParser().parseFile(prop.getProperty(PropKeys.SOURCE_FILE.name()));
    	//Instantiate CTP
    	StochasticWeightedGraph g;
    	g = new StochasticWeightedGraph(StochasticWeightedEdge.class, graphData);
    	
    	Vertex s = g.getSourceVtx();
    	Vertex t = g.getTerminalVtx();
    	DefaultCtp ctp = new DefaultCtp(g, s, t);
    	if(!new GraphChecker().isGraphConnected(g)){
    		System.out.println("is connected: false");
    		g.removeAllBlockedEdges();
    		System.out.println(g.toString());
    		return;
    	}
    	List<Result> results = algorithmRunner(ctp, algorithmsToBeMade, numberOfRuns);
    	
    	//Print results
    	printResult(results);
//    	//Optimal solution print s->t
//    	LOG.info("starting DIJKSTRA");
//    	ctp.g.removeAllBlockedEdges();
//    	DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
//    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(s, t);
//    	LOG.info("DIJKSTRA cost: "+ shortestPath.getWeight() +" through: "+shortestPath.getVertexList().toString());
    }
    
    private static void printResult(List<Result> results){
    	if(results==null)return;
    	for(Result result : results){
    		if(result!=null){
    		System.out.println(result.toString());
    		}
    	}
    }
    
    private static List<Result> algorithmRunner(DefaultCtp ctp, List<AlgNames> algorithms, int repeater){
    	List<Result> results = new ArrayList<Result>();
    	while(repeater>0){
    		results.addAll(runAlgorithms(ctp, algorithms));
    		repeater--;
    		// for each repetition new instance of graph
    		ctp.g = ctp.g.doRollout();
    	}
    	return results;
    }

	private static List<Result> runAlgorithms(DefaultCtp ctp, List<AlgNames> algorithms) {
		List<Result> result = new ArrayList<Result>();
		try {
			for(AlgNames algorithm : algorithms){
				//make clone of graph instance so for upcoming algorithms same instance is used
				StochasticWeightedGraph graphClone = (StochasticWeightedGraph) ctp.g.clone();
				
				//TEST
//				ctp.g = ctp.g.doRollout();
				
				Result r = null;
				switch (algorithm){
					case DIJKSTRA:
						LOG.info("starting DIJKSTRA");
				    	ctp.g.removeAllBlockedEdges();
				    	DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
				    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(ctp.s, ctp.t);
				    	Agent a = new Agent(ctp.s);
				    	a.senseAction(ctp.g);
				    	a.traversePath(shortestPath);
				    	r = new Result(a, "DIJKSTRA");
						break;
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
					case UCTB:
						Uctb uctb = new Uctb();
						r = uctb.solve(ctp, new Agent(ctp.s));
						break;
					case UCTO:
						Ucto ucto = new Ucto();
						r = ucto.solve(ctp, new Agent(ctp.s));
						break;
					case UCTB2:
						Uctb2 uctb2 = new Uctb2();
						r = uctb2.solve(ctp, new Agent(ctp.s));
						break;
					case UCTP:
						UctPrunning uctp = new UctPrunning();
						r = uctp.solve(ctp, new Agent(ctp.s));
						break;
					}
				//set graphClone to ctp
				ctp.g = graphClone;
				result.add(r);
				LOG.info(r.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
