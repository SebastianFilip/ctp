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

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import cz.vut.sf.graph.CtpException;
import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedEdge.State;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.graph.Vertex;
public class GraphSwing extends JFrame{
	private static final long serialVersionUID = 1L;
	static int screenX;
	static int screenY;
	static int spaces = 50;
	public GraphSwing(StochasticWeightedGraph gSource, List<Point> pointList,int xFrameSize,int yFrameSize){
		super();
		screenX = xFrameSize;
		screenY = yFrameSize;
		try {
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
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	private void displayGraph(StochasticWeightedGraph gSource, mxGraph g,final List<Point> pointList){
		List<Point> multiplyedPointList = multiplyPointList(pointList, 100);
		int x = 50; int y = 50;
		final int HEIGHT = 40; final int WIDTH = 40;
		Object parent = g.getDefaultParent();
		g.getModel().beginUpdate();
		
		List<Integer> blockedEdgesIndexes = new ArrayList<Integer>();
		List<Integer> sortestPathIndexes = new ArrayList<Integer>();
		Object v[] = new Object[gSource.getAllVertexes().size()];
		Object e[] = new Object[gSource.edgeSet().size()];

		try{
			boolean isMovingAllowed = true;
			boolean isScalingAllowed = true;
			Point movement = new Point(0,0);
			double[] scalingConstant = {1,1};
			for (int i = 0; i < v.length;i++){
				if(multiplyedPointList != null && !multiplyedPointList.isEmpty()){
					//if NODE_COORD_SECTION is present
					
					if(isMovingAllowed){
						movement = getMovement(multiplyedPointList);
						applyMovement(multiplyedPointList, movement);
						//want this to be calculated only once
						isMovingAllowed = false;
					}
					
					if(isScalingAllowed){
						scalingConstant = getScaling(multiplyedPointList);
						//want this to be calculated only once
						isScalingAllowed = false;
					}
					x = (int) (scalingConstant[0] * (multiplyedPointList.get(i).x)); 
					y = (int) (scalingConstant[1] * (multiplyedPointList.get(i).y)); 
				}
				Vertex vtx = gSource.getVtxById(i+1);
				v[i] = g.insertVertex(parent, vtx.toString(), vtx.toString(), x%screenX, y%screenY,
						HEIGHT, WIDTH, "shape=ellipse;perimeter=ellipsePerimeter");
				x = getNewX(x); y= getNewY(y);
			}
			
	    	paintEdges(gSource, g, parent, blockedEdgesIndexes, sortestPathIndexes, v, e);
			
			
		}catch(RuntimeException e1){
			throw e1;
		}finally{
			g.getModel().endUpdate();
		}
		setEdgeStyle(g);
		g.refresh();
	}

	private List<Point> multiplyPointList(List<Point> pointList, int k) {
		if(pointList == null || pointList.isEmpty()){
			return pointList;
		}
		List<Point> result = new ArrayList<Point>();
		for (Point point : pointList) {
			result.add(new Point(point.x * k, point.y * k));
		}
		return result;
	}

	private void applyMovement(List<Point> pointList,Point movement) {
		for (Point p:pointList){
			p.x -= movement.x;
			p.y -= movement.y;
		}
	}

	private Point getMovement(List<Point> pointList) {
		final int xDef = spaces;
		final int yDef = spaces;
		
		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;
		for (Point p:pointList){
			if(p.x < xMin){
				xMin = p.x;
			}
			if(p.y < yMin){
				yMin = p.y;
			}
		}
		return new Point(xMin - xDef, yMin - yDef);
	}

	private double[] getScaling(List<Point> pointList) {
		int xDefSpaceToEdge = screenX - 2*spaces;
		int yDefSpaceToEdge = screenY - 2*spaces;
		int xMax = Integer.MIN_VALUE;
		int yMax = Integer.MIN_VALUE;
		for (Point p:pointList){
			if(p.x > xMax){
				xMax = p.x;
			}
			if(p.y > yMax){
				yMax = p.y;
			}
		}
		return new double[]{((double)xDefSpaceToEdge)/((double)xMax),((double)yDefSpaceToEdge)/((double)yMax)};
	}

	private void setEdgeStyle(mxGraph g) {
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
	}

	private void paintEdges(StochasticWeightedGraph gSource, mxGraph g,
			Object parent, List<Integer> blockedEdgesIndexes,
			List<Integer> sortestPathIndexes, Object[] v, Object[] e) {
		Set<StochasticWeightedEdge> edgeSet = gSource.edgeSet();
		StochasticWeightedGraph clone = (StochasticWeightedGraph) gSource.clone();
		clone.removeAllBlockedEdges();
		DijkstraShortestPath<Vertex, StochasticWeightedEdge> dsp = new DijkstraShortestPath<Vertex, StochasticWeightedEdge>(clone);
    	GraphPath<Vertex, StochasticWeightedEdge> shortestPath = dsp.getPath(clone.getSourceVtx(), clone.getTerminalVtx());
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
			
			if(shortestPath == null){
				throw new CtpException("Generated instance was not connected from start vtx to termination vtx");
			}
			List<StochasticWeightedEdge> spEdgeList = shortestPath.getEdgeList();
			for(StochasticWeightedEdge spEdge : spEdgeList){
				if(gSource.getEdgeSource(spEdge).equals(source) && gSource.getEdgeTarget(spEdge).equals(target) ||
						gSource.getEdgeSource(spEdge).equals(target) && gSource.getEdgeTarget(spEdge).equals(source)){
					g.setCellStyles(mxConstants.STYLE_STROKECOLOR, "green", new Object[]{e[i]});
					sortestPathIndexes.add(i);
				}
			}
			i++;
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
	
	public static void displayGraph(StochasticWeightedGraph gSource, List<Point> pointList, int xFrameSize, int yFrameSize){
		try {
			GraphSwing frame = new GraphSwing(gSource, pointList, xFrameSize, yFrameSize);
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			frame.setSize(screenX,screenY);
			frame.setVisible(true);
		} catch (RuntimeException e) {
			throw e;
		}
	}
}
