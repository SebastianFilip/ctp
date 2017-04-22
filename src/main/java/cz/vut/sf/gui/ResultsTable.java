package cz.vut.sf.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.table.DefaultTableModel;

public class ResultsTable extends CtpAppConstants {
	private static String[] header = null;
	private static String[][] data = null;
	private static double [] colCostSums = null;
	private static double [] colTimeSums = null;
	public static DefaultTableModel model = new DefaultTableModel();
	
	private static void setHeader(String[] headers){
		// fill headers
		header[0] = "run #";
		for (int i= 1; i< header.length; i++){
			header[i] = ctpResults.get(i-1).msg;
		}
		
		model.setColumnIdentifiers(header);
	}
	
	private static void setData(){
		colCostSums = new double [header.length - 1];
		colTimeSums = new double [header.length - 1];
		for (int row = 0; row < runsMade; row++){
			data[row][0] = String.valueOf(row + 1);
			int index = (row) * (header.length - 1);
			for(int col = 1; col < header.length; col++){
				double costPaid = ctpResults.get(index + col - 1).costPaid;
				data[row][col] = String.valueOf(costPaid);
				colCostSums[col-1] += costPaid;
				colTimeSums[col-1] += (double) ctpResults.get(index + col - 1).timeElapsed/1000;
			}
		}
		setAvg();
		model.setDataVector(data, header);
	}
	
	private static void setAvg() {
		//Avg travel cost
		int row = data.length -2;
		data[row][0] = "Avg Cost";
		for(int col = 1; col < header.length; col++){
			data[row][col] = String.valueOf(colCostSums[col-1] / runsMade);
		}
		//Avg computational time (s)
		row++;
		data[row][0] = "Avg Time";
		for(int col = 1; col < header.length; col++){
			data[row][col] = String.format("%.2f",colTimeSums[col-1] / runsMade).replace(",", ".");
		}
	}

	public static void initTable(){
		if(runsMade == 0){
			return;
		}
		header = new String[columnsToCreate + 1];
		setHeader(header);
		// +2 columns for avg values
		data = new String[runsMade + 2][columnsToCreate + 1];
		setData();
	}
	
	public static void exportToXls(File file){
		try {
			FileWriter excel = new FileWriter(file);
	        for(int i = 0; i < model.getColumnCount(); i++){
	            excel.write(model.getColumnName(i) + "\t");
	        }
	        excel.write("\n");
	        
	        for(int i=0; i< model.getRowCount(); i++) {
	            for(int j=0; j < model.getColumnCount(); j++) {
	                excel.write(model.getValueAt(i,j).toString()+"\t");
	            }
	            excel.write("\n");
	        }

	        excel.close();
	        
		} catch (IOException e) {
			LOG.error(e);
		}
	}
}
