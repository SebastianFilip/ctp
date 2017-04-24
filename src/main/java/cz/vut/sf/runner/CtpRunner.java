package cz.vut.sf.runner;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
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
import cz.vut.sf.gui.CtpAppConstants;
import cz.vut.sf.gui.CtpGui;
import cz.vut.sf.parsers.BasicCtpParser;
import cz.vut.sf.parsers.ParsedDTO;

public class CtpRunner extends CtpAppConstants {
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
    		LOG.info("Generated graph was not connected, pls try again.");
    		LOG.info(g.toString());
    		return;
    	}
    	algorithmRunner(ctp, algorithmsToBeMade, numberOfRuns);
    }
    
    private static List<Result> algorithmRunner(DefaultCtp ctp, List<AlgNames> algorithms, final int numberOfRuns){
    	List<Result> results = new ArrayList<Result>();
    	Level lvl = isLogInfo() ? Level.INFO : Level.DEBUG;
    	LOG.setLevel(lvl);
    	runsMade = 0;
    	columnsToCreate = algorithms.size();
    	int repeater = numberOfRuns;
    	while(repeater>0){
    		String logMsg = "-------------Starting run #" + (numberOfRuns - repeater + 1) + "-------------";
    		String logMsgSeparator = new String(new char[logMsg.length()]).replace('\0', '-');
    		LOG.info(logMsgSeparator);
    		LOG.info(logMsg);
    		LOG.info(logMsgSeparator);
    		List<Result> runResult = runAlgorithms(ctp, algorithms);
    		if(stop){
    			return results;
    		}
    		ctpResults = results;
    		repeater--;
    		runsMade++;
    		// for each repetition new instance of graph
    		ctp.g = ctp.g.doRollout();
    		results.addAll(runResult);
    	}
    	return results;
    }

	private static List<Result> runAlgorithms(DefaultCtp ctp, List<AlgNames> algorithms) {
		List<Result> result = new ArrayList<Result>();
		try {
			for(AlgNames algorithm : algorithms){
				long startTime = System.nanoTime();
				StochasticWeightedGraph graphClone = (StochasticWeightedGraph) ctp.g.clone();
				Result r = null;
				switch (algorithm){
					case DIJKSTRA:
						LOG.info("starting Dijkstra");
				    	ctp.g.removeAllBlockedEdges();
				    	DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(ctp.g);
				    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(ctp.s, ctp.t);
				    	Agent a = new Agent(ctp.s);
				    	a.senseAction(ctp.g);
				    	a.traversePath(shortestPath);
				    	r = new Result(a, "Dijkstra");
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
						int nHop = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_HOP.name()));
						hop.setTotalRollouts(nHop);
						r = hop.solve(ctp, new Agent(ctp.s));
						break;
					case ORO:
						Oro oro = new Oro();
						int nOro = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_ORO.name()));
						oro.setTotalRollouts(nOro);
						r = oro.solve(ctp, new Agent(ctp.s));
						break;
					case UCTB:
						Uctb uctb = new Uctb();
						uctb.setNumberOfAdditionalRollouts(1);
						int n = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTB.name()));
						uctb.setNumberOfRollouts(n);
						r = uctb.solve(ctp, new Agent(ctp.s));
						break;
					case UCTO:
						Ucto ucto = new Ucto();
						int n1 = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTO.name()));
						int m1 = Integer.parseInt(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO.name()));
						ucto.setNumberOfRollouts(n1);
						ucto.setNumberOfAdditionalRollouts(m1);
						r = ucto.solve(ctp, new Agent(ctp.s));
						break;
					case UCTB2:
						Uctb2 uctb2 = new Uctb2();
						r = uctb2.solve(ctp, new Agent(ctp.s));
						break;
					case UCTP:
						UctPrunning uctp = new UctPrunning();
						int nr = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTP.name()));
						int na = Integer.parseInt(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTP.name()));
						uctp.setNumberOfRollouts(nr);
						uctp.setNumberOfAdditionalRollouts(na);
						r = uctp.solve(ctp, new Agent(ctp.s));
						break;
					}
				r.timeElapsed = (long) ((System.nanoTime() - startTime)/1E6);
				result.add(r);
				LOG.info(r.toString());
				if(stop){
					return result;
				}
				//set graphClone to ctp
				ctp.g = graphClone;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static boolean isLogInfo(){
		return CtpGui.rdbtnInfo.isSelected();
	}
}
