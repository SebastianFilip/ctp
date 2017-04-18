package cz.vut.sf.runner;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.Vertex;
public class GraphSwing extends JFrame{
	final static int screenX = 1366;
	final static int screenY = 768;
	public GraphSwing(StochasticWeightedGraph gSource, List<Point> pointList){
		super();
		String title = "Graph topology";
		title += pointList.isEmpty() ? "" : ", graph size = " + pointList.size();
		this.setTitle(title);
		final mxGraph g = new mxGraph(){
			@Override
	        public boolean isCellSelectable(Object cell) {
	            if (model.isEdge(cell)) {
	                return false;
	            }
	            return super.isCellSelectable(cell);
	        }
			@Override
			public boolean isCellMovable(Object cell)  {
	            if (model.isEdge(cell)) {
	                return false;
	            }
	            return super.isCellMovable(cell);
	        }
		};
		displayGraph(gSource, g, pointList);
		final mxGraphComponent graphComponent = new mxGraphComponent(g);
		g.setCellsMovable(true);
		g.setKeepEdgesInBackground(true);
		g.setCellsResizable(false);
		g.setEdgeLabelsMovable(false);
		g.setEventsEnabled(false);		
		g.setCellsDisconnectable(false);
		g.setResetEdgesOnMove(true);
		g.setCellsSelectable(true);
		g.setCellsEditable(false);
		graphComponent.setConnectable(false);
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);
		g.refresh();
		getContentPane().add(graphComponent);
		
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter(){
		
			public void mouseReleased(MouseEvent e)
			{
				//this repaint dont work as I wish
				g.getModel().beginUpdate();
				try{
					g.repaint();
					Object cell = graphComponent.getCellAt(e.getX(), e.getY());
					if (cell != null){
						if(g.getModel().isEdge(cell)){
							System.out.println("edge = "+ g.getLabel(cell));
							return;
						}
						System.out.println(g.getLabel(cell));
					}
					g.refresh();
				}finally{
					g.getModel().endUpdate();
				}
			}
		});
	}
	
	private void displayGraph(StochasticWeightedGraph gSource, mxGraph g, List<Point> pointList){
		int x = 50; int y = 50;
		final int HEIGHT = 40; final int WIDTH = 40;
		Object parent = g.getDefaultParent();
		g.getModel().beginUpdate();
		
		List<Integer> blockedEdgesIndexes = new ArrayList<Integer>();
		List<Integer> sortestPathIndexes = new ArrayList<Integer>();
		Object v[] = new Object[gSource.getAllVertexes().size()];
		Object e[] = new Object[gSource.edgeSet().size()];

		try{
			for (int i = 0; i < v.length;i++){
				if(pointList != null && !pointList.isEmpty()){
					x = pointList.get(i).x; y= pointList.get(i).y;
				}
				Vertex vtx = gSource.getVtxById(i+1);
				v[i] = g.insertVertex(parent, vtx.toString(), vtx.toString(), x%screenX, y%screenY,
						HEIGHT, WIDTH, "shape=ellipse;perimeter=ellipsePerimeter");
				x = getNewX(x); y= getNewY(y);
			}
			
			Set<StochasticWeightedEdge> edgeSet = gSource.edgeSet();
			StochasticWeightedGraph clone = (StochasticWeightedGraph) gSource.clone();
			clone.removeAllBlockedEdges();
			DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(clone);
	    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(clone.getSourceVtx(), clone.getTerminalVtx());
	    	List<StochasticWeightedEdge> spEdgeList = shortestPath.getEdgeList();
	    	
	    	int i = 0;
			for (StochasticWeightedEdge edge : edgeSet){
				Vertex source = gSource.getEdgeSource(edge);
				Vertex target = gSource.getEdgeTarget(edge);
				String label = getEdgeLabel(edge,gSource);
				e[i] = g.insertEdge(parent,null, label,v[source.id - 1],v[target.id -1], "endArrow=none;");
				if(edge.getActualState() == State.BLOCKED){
					blockedEdgesIndexes.add(i);
					g.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", new Object[]{e[i]});
				}
				for(StochasticWeightedEdge spEdge : spEdgeList){
					if(gSource.getEdgeSource(spEdge).equals(source) && gSource.getEdgeTarget(spEdge).equals(target) ||
							gSource.getEdgeSource(spEdge).equals(target) && gSource.getEdgeTarget(spEdge).equals(source)){
						g.setCellStyles(mxConstants.STYLE_STROKECOLOR, "green", new Object[]{e[i]});
						sortestPathIndexes.add(i);
					}
				}
				i++;
			}
			
			
		}catch(RuntimeException e1){
			throw e1;
		}finally{
			g.getModel().endUpdate();
		}
		Map<String, Object> edgeStyle = new HashMap<String, Object>();
		//edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ORTHOGONAL);
		edgeStyle.put(mxConstants.STYLE_SHAPE,    mxConstants.SHAPE_CONNECTOR);
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
		edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#ffffff");

		mxStylesheet stylesheet = new mxStylesheet();
		stylesheet.setDefaultEdgeStyle(edgeStyle);
		g.setStylesheet(stylesheet);
		
		
		g.refresh();
	}
	
	private void paintEdges(mxGraph g,Object[] e, List<Integer> indexesToPaint, String color) {
		for(int i=0; i< indexesToPaint.size();i++){
		}
	}

	private String getEdgeLabel(StochasticWeightedEdge edge,StochasticWeightedGraph gSource) {
		int weight = (int)gSource.getEdgeWeight(edge);
		
		return String.valueOf(weight) + " | " + edge.getProbability();
	}

	private int getNewY(int y) {
		int result = y;
		if(y%2 == 0){
			result += 43;
		}
		if(y%2 == 1){
			result -= 50;
		}
		if(y%3 == 0){
			result += 28;
		}
		if(y%5 == 0){
			result -= -20;
		}
		return result+= Math.round(Math.random()*80);
	}

	private int getNewX(int x) {
		int result = x;
		if(x%2 == 0){
			result += 28;
		}
		if(x%2 == 1){
			result -= 60;
		}
		if(x%3 == 0){
			result += 23;
		}
		if(x%5 == 0){
			result -= -20;
		}
		return result+= Math.round(Math.random()*120);
	}

	public GraphSwing(){
		super("Hello, World!");
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		Object v1 = null;
		Object e1 = null;
		Object e2 = null;
		try
		{
			v1 = graph.insertVertex(parent, "1", "Hello,", 10, 10, 30,
					30, "shape=ellipse;perimeter=ellipsePerimeter");
			Object v2 = graph.insertVertex(parent, "1", "World!", 200, 150,
					60, 60, "shape=ellipse;perimeter=ellipsePerimeter");
			Object v3 = graph.insertVertex(parent, null, "Hello,", 200, 20, 60,
					60,"shape=ellipse;perimeter=ellipsePerimeter");
			e1 = graph.insertEdge(parent,null,"",v1,v2, "endArrow=none;");
//			Object e2 = graph.insertEdge(parent,null,"",v2,v1);
//			Object e3 = graph.insertEdge(parent, null, "", v3, v2);
			e2 = graph.insertEdge(parent,null,"",v2,v3);
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graph.setCellsMovable(true);
		graph.setCellsResizable(false);
		graph.setEdgeLabelsMovable(false);
		graph.setEventsEnabled(false);
		graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "red", new Object[]{v1});
		graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "10", new Object[]{e1});
		graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", new Object[]{e1});
		graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "green", new Object[]{e2});
		graph.setCellStyles(mxConstants.STYLE_EDGE, "green", new Object[]{e2});
		
		graph.setCellsDisconnectable(false);
		graph.refresh();
		getContentPane().add(graphComponent);
	}
	
	
	public static void doMagic(){
		GraphSwing frame = new GraphSwing();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1366,768);
		frame.setVisible(true);
	}
	
	public static void displayGraph(StochasticWeightedGraph gSource, List<Point> pointList){
		GraphSwing frame = new GraphSwing(gSource, pointList);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(screenX,screenY);
		frame.setVisible(true);
	}
}
