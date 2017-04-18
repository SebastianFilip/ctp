package cz.vut.sf.algorithms;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import cz.vut.sf.ctp.Agent;
import cz.vut.sf.ctp.DefaultCtp;
import cz.vut.sf.ctp.Simulator;
import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.Vertex;

public class Uctb2 extends Uctb {
	
	@Override
	public Result solve(DefaultCtp ctp, Agent agent) {
		LOG.info("Starting UCTB2, total rollouts = " + numberOfRollouts + ", total iteration = " + numberOfIteration);
		int blockedEdgesRevealed = 0;
		
		while(agent.getCurrentVertex()!=ctp.t){
			//If graph changes all of the children of current vertex should be expanded (explored)
			boolean isGraphChanged = false;
			agent.senseAction(ctp.g);
			
			if(blockedEdgesRevealed != ctp.g.getBlockedEdgesRevealed()){
				blockedEdgesRevealed = ctp.g.getBlockedEdgesRevealed();
				isGraphChanged = true;
			}
			
			this.setRoot(agent.getCurrentVertex());
			this.setGraph(ctp.g);
			if(this.getRoot().isLeafNode()){
				LOG.debug("Expanding root = "+ this.getRoot().getData().vtx +", except for parent = " + agent.getPreviousVertex(isGraphChanged));
				this.expandNode(this.getRoot(), agent.getPreviousVertex(isGraphChanged));
			}
			
			if(this.getRoot().getChildren().isEmpty()){
				//this means that algorithm choose to travel to vtx about which knew that is dead end
				LOG.error("This should never happen!! Chosen vtx(dead end) = " + agent.getPreviousVertex(true));
				throw new CtpException("Uct choose to go to dead end vtx");
			}
			if(this.getRoot().getChildren().size() == 1){
				//there is only one possible child -> go through it
				LOG.debug("Chosen vtx(only child) = " + this.getRoot().getChildren().get(0).getData().vtx);
				agent.traverseToAdjancetVtx(ctp.g, this.getRoot().getChildren().get(0).getData().vtx);
				continue;
				}
			
			this.doSearch(numberOfIteration, numberOfRollouts);
			Vertex chosenVtx = this.getBestAction();
			LOG.debug("Chosen vtx = " + chosenVtx);
			agent.traverseToAdjancetVtx(ctp.g, chosenVtx);
			
		}
		return new Result(agent, "UCTB2");
	}
	
	@Override
	public void doSimulation(Simulator simulator, Vertex vtxWhichIsExplored,int numberOfRollouts) {
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp;
		GraphPath<Vertex, StochasticWeightedEdge> shortestPath;
		int currentRollout = 0;
		do{
			currentRollout ++;
			StochasticWeightedGraph rolloutedGraph = this.getGraph().doRollout();
			// I want my simulate agent to be always in same position and 
			// I am using him only for creating traveling agent
			Agent travellingAgent = new Agent(simulator.agent);
			travellingAgent.senseAction(rolloutedGraph);
			StochasticWeightedEdge edgeFromParentToChild = rolloutedGraph.getEdge(travellingAgent.getCurrentVertex(), vtxWhichIsExplored);
			if(edgeFromParentToChild==null){
				//edge is blocked go there by GA
				//mby later it will be needed to check if graph is connected
				if(!GreedyAlgorithm.traverseByGa(rolloutedGraph, vtxWhichIsExplored, travellingAgent)){
					//there is no path from current travellingAgent position to vertex which is about to be explored
					continue;
				}
			}else{
				travellingAgent.traverseToAdjancetVtx(rolloutedGraph, vtxWhichIsExplored);
			}
			//finish route by Dijkstra
			rolloutedGraph.removeAllBlockedEdges();
			dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(rolloutedGraph);
			dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(rolloutedGraph);
			shortestPath = dsp.getPath(travellingAgent.getCurrentVertex(), rolloutedGraph.getTerminalVtx());

			simulator.totalCost += travellingAgent.getTotalCost() + shortestPath.getWeight();
			simulator.totalIterations ++;
		}while(currentRollout < numberOfRollouts);
	}
}
