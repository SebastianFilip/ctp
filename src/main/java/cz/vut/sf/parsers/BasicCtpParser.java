package cz.vut.sf.parsers;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.vut.sf.graph.CtpException;

public class BasicCtpParser implements DefaultParser {
	List<List<Integer>> adjacencyList = new ArrayList<List<Integer>>();
	List<NodeCoord> nodeCoordList= new ArrayList<NodeCoord>();
	List<Point> pointList= new ArrayList<Point>();
	List<List<Double>> weightsList= new ArrayList<List<Double>>();
	List<List<Double>> probabilitiesList= new ArrayList<List<Double>>();
	List<List<Double>> penaltyList= new ArrayList<List<Double>>();
	private KeyWords currentSection = KeyWords.DEFAULT;
	private int lineNumber = 1;
	public ParsedDTO parseFile(String pathToSource) {
		try{
			BufferedReader br = new BufferedReader(new FileReader(pathToSource));
			processSections(br);
			if(nodeCoordList.size()>0 && weightsList.isEmpty()){
				initWeightsListFromCoord();
			}
		}catch(Exception e){
			throw new CtpException("Exception cause " + e.getClass().getName() + " msg=" + e.getMessage() + "\n"
					+ "Exception occured while reading line=" + lineNumber + " from file=" + pathToSource);
		}
		
		fillPointList();
		return new ParsedDTO(adjacencyList, weightsList, probabilitiesList, penaltyList,pointList);
	}
	
	private void fillPointList() {
		if(!nodeCoordList.isEmpty()){
			for (NodeCoord node : nodeCoordList){
				pointList.add(new Point((int) node.x,(int) node.y));
			}
		}
	}

	private void initWeightsListFromCoord() {
		for(int i=0; i < adjacencyList.size(); i++){
			List<Double> temp = new ArrayList<Double>();
			for(int j=0; j < adjacencyList.get(i).size(); j++){
				// id of vertexes are from 1 and java indexes list from 0 thus -1
				int neighbourId = adjacencyList.get(i).get(j)-1;
				Double weight = computeEulerDistance(i, neighbourId);
				temp.add(weight);
			}
			weightsList.add(temp);
		}
		
	}

	private Double computeEulerDistance(int i, int neighbourId) {
		double x1 = nodeCoordList.get(i).x;
		double y1 = nodeCoordList.get(i).y;
		double x2 = nodeCoordList.get(neighbourId).x;
		double y2 = nodeCoordList.get(neighbourId).y;
		double result = Math.sqrt(Math.pow((x1-x2), 2)+ Math.pow((y1-y2),2));
		return new Double(result);
	}

	private void processSections(BufferedReader br) throws IOException {
		String line = br.readLine();
		while(KeyWords.EOF != getKeyWordValue(line)){
			//when implementing file info need to split line
			currentSection = getKeyWordValue(line);
			
			switch (currentSection){
			case NODE_COORD_SECTION:
				line = br.readLine();
				lineNumber++;
				if(currentSection != getKeyWordValue(line)){
					continue;
				}
				String[] lineParts = line.split(" ");
				NodeCoord node = processNodeSection(lineParts);
				nodeCoordList.add(node);
				break;
			case EDGE_DIRECTIONS_SECTION:
				line = br.readLine();
				lineNumber++;
				if(currentSection != getKeyWordValue(line)){
					continue;
				}
				List<Integer> adjancetRow = getAdjancetRow(line.split(" "));
				adjacencyList.add(adjancetRow);
				break;
			case PROBABILITY_SECTION:
				line = br.readLine();
				lineNumber++;
				if(currentSection != getKeyWordValue(line)){
					continue;
				}
				List<Double> probabilityRow = getDoubleRow(line.split(" "));
				probabilitiesList.add(probabilityRow);
				break;
			case PENALTY_SECTION:
				line = br.readLine();
				lineNumber++;
				if(currentSection != getKeyWordValue(line)){
					continue;
				}
				List<Double> penaltyRow = getDoubleRow(line.split(" "));
				penaltyList.add(penaltyRow);
				break;
			case EDGE_WEIGHT_SECTION:
				line = br.readLine();
				lineNumber++;
				if(currentSection != getKeyWordValue(line)){
					continue;
				}
				List<Double> weightRow = getDoubleRow(line.split(" "));
				weightsList.add(weightRow);
				break;
			default:
				line = br.readLine();
				lineNumber++;
				break;
			}
		}
	}

	private List<Double> getDoubleRow(String[] split) {
		List<Double> result = new ArrayList<Double>();
		for(int i = 0; i < split.length; i++){
			result.add(Double.parseDouble(split[i]));
		}
		return result;
	}

	private List<Integer> getAdjancetRow(String[] split) {
		List<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < split.length; i++){
			result.add(Integer.parseInt(split[i]));
		}
		return result;
	}

	private NodeCoord processNodeSection(String[] lineParts) {
		if(lineParts.length != 3) throw new IllegalArgumentException("processNodeSection argument[0]="+ lineParts[0]) ;
		return new NodeCoord(Integer.parseInt(lineParts[0]), Double.parseDouble(lineParts[1]), Double.parseDouble(lineParts[2]));
	}

	private KeyWords getKeyWordValue(String word) {
		KeyWords[] keyWords = KeyWords.values();
		for (int i = 0; i < keyWords.length; i++){
			if(word.equals(keyWords[i].name())) return keyWords[i];
		}
		return currentSection;
	}

	public static enum KeyWords{
		NAME, COMMENT, TYPE, GRAPH, DIRECTED, UNDIRECTED, PROBABILITY_COUNT, 
		DIMENSION, EDGE_WEIGHT_TYPE, EUC_2D, NODE_COORD_SECTION, EDGE_DIRECTIONS_SECTION,
		PROBABILITY_SECTION, PENALTY_SECTION, EOF, DEFAULT, EDGE_WEIGHT_SECTION;
	}
	
	private class NodeCoord{
		@SuppressWarnings("unused")
		public int id;
		public double x;
		public double y;
		public NodeCoord(int i,double xValue,double yValue){
			id = i;
			x = xValue;
			y = yValue;
		}
	}

}
