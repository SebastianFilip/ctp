package cz.vut.sf.runner;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import cz.vut.sf.algorithms.ComparisonAlgorithm;
import cz.vut.sf.algorithms.DefaultCtpAlgorithm;
import cz.vut.sf.algorithms.GraphChecker;
import cz.vut.sf.algorithms.GreedyAlgorithm;
import cz.vut.sf.algorithms.Hop;
import cz.vut.sf.algorithms.Oro;
import cz.vut.sf.algorithms.RepositionAlgorithm;
import cz.vut.sf.algorithms.Result;
import cz.vut.sf.algorithms.UctAlgorithm;
import cz.vut.sf.algorithms.UctPrunning;
import cz.vut.sf.algorithms.Uctb2;
import cz.vut.sf.algorithms.Uctb;
import cz.vut.sf.algorithms.Ucto2;
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
    	ParsedDTO graphData = null;
    	try{
    		graphData = new BasicCtpParser().parseFile(prop.getProperty(PropKeys.SOURCE_FILE.name()));
    	}catch(Exception e){
    		LOG.info("Parsing graph data failed!");
    		LOG.info(e.getClass().getName() + e.getMessage());
    		return; 
    	}
    	 
    	//Instantiate CTP
    	StochasticWeightedGraph g;
    	g = new StochasticWeightedGraph(StochasticWeightedEdge.class, graphData);
    	LOG.info("Out of 1000 times this graph was generated " + GraphChecker.countFailedRollouts(graphData, 1000) + " time(s) not connected");
    	Vertex s = g.getSourceVtx();
    	Vertex t = g.getTerminalVtx();
    	DefaultCtp ctp = new DefaultCtp(g, s, t);
    	if(!GraphChecker.isGraphConnected(g)){
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
    		List<Result> runResult = null;
			try {
				runResult = runAlgorithms(ctp, algorithms);
			} catch (Exception e) {
				LOG.info("Run ended by unexpected exception!");
				LOG.info(e);
				e.printStackTrace();
				return results;
			}
    		if(stop){
    			if(runResult.size() == algorithms.size()){
    	    		runsMade++;
    	    		ctpResults.addAll(runResult);
    			}
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

	private static List<Result> runAlgorithms(DefaultCtp ctp, List<AlgNames> algorithms) throws Exception {
		List<Result> result = new ArrayList<Result>();
		boolean timeLimitationOn = Boolean.parseBoolean(prop.getProperty(PropKeys.TIME_LIMITATION_ON.name()));
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
						DefaultCtpAlgorithm ga = new GreedyAlgorithm(ctp, new Agent(ctp.s));
				    	r = ga.solve();
						break;
					case RA:
						DefaultCtpAlgorithm ra = new RepositionAlgorithm(ctp, new Agent(ctp.s));
				    	r = ra.solve();
						break;
					case CA:
						DefaultCtpAlgorithm ca = new ComparisonAlgorithm(ctp, new Agent(ctp.s));
						r = ca.solve();
						break;
					case HOP:
						Hop hop = new Hop(ctp, new Agent(ctp.s));
						int nHop = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_HOP.name()));
						hop.setTotalRollouts(nHop);
						r = hop.solve();
						break;
					case ORO:
						Oro oro = new Oro(ctp, new Agent(ctp.s));
						int nOro = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_ORO.name()));
						oro.setTotalRollouts(nOro);
						r = oro.solve();
						break;
					case UCTB:
						Uctb uctb = new Uctb(ctp, new Agent(ctp.s));
						setTimeLimitation(timeLimitationOn, uctb);
						uctb.setNumberOfAdditionalRollouts(1);
						int n = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTB.name()));
						uctb.setNumberOfIterations(n);
						r = uctb.solve();						
						break;
					case UCTO:
						Ucto ucto = new Ucto(ctp, new Agent(ctp.s));
						setTimeLimitation(timeLimitationOn, ucto);
						int n1 = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTO.name()));
						int m1 = Integer.parseInt(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO.name()));
						ucto.setNumberOfIterations(n1);
						ucto.setNumberOfAdditionalRollouts(m1);
						r = ucto.solve();
						break;
					case UCTO2:
						Ucto2 ucto2 = new Ucto2(ctp, new Agent(ctp.s));
						setTimeLimitation(timeLimitationOn, ucto2);
						int n2 = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTO2.name()));
						int m2 = Integer.parseInt(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO2.name()));
						ucto2.setNumberOfIterations(n2);
						ucto2.setNumberOfAdditionalRollouts(m2);
						r = ucto2.solve();
						break;
					case UCTB2:
						Uctb2 uctb2 = new Uctb2(ctp, new Agent(ctp.s));
						setTimeLimitation(timeLimitationOn, uctb2);
						uctb2.setNumberOfAdditionalRollouts(1);
						int nUctb2 = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTB.name()));
						uctb2.setNumberOfIterations(nUctb2);
						r = uctb2.solve();
						break;
					case UCTP:
						UctPrunning uctp = new UctPrunning(ctp, new Agent(ctp.s));
						setTimeLimitation(timeLimitationOn, uctp);
						int nr = Integer.parseInt(prop.getProperty(PropKeys.ROLLOUTS_UCTP.name()));
						int na = Integer.parseInt(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTP.name()));
						uctp.setNumberOfIterations(nr);
						uctp.setNumberOfAdditionalRollouts(na);
						r = uctp.solve();
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
			throw e;
		}
		return result;
	}
	
	
	private static void setTimeLimitation(boolean timeLimitationOn, UctAlgorithm uct) {
		if(timeLimitationOn){
			long timeToDecision = Long.parseLong(prop.getProperty(PropKeys.TIME_TO_DECISION_FOR_UCT.name()));
			uct.setTimeToDecision(timeToDecision);
		}
	}

	public static boolean isLogInfo(){
		return CtpGui.rdbtnInfo.isSelected();
	}
}
