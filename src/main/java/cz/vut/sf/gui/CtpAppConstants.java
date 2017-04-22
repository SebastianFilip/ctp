package cz.vut.sf.gui;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.swing.JRadioButton;

import cz.vut.sf.algorithms.Result;

public class CtpAppConstants extends LoggerClass {
	public static boolean stop = false;
	
    public static enum AlgNames{
    	DIJKSTRA, GA,RA,CA,HOP,ORO,UCTB,UCTO, UCTB2, UCTP;
    }
	public static final String SEPARATOR = System.getProperty("file.separator");
	public static Properties prop = new Properties();
	public static final String resourcePath = System.getProperty("user.dir") + SEPARATOR + "src" + SEPARATOR + "main" + SEPARATOR + "resources";
	public static final File configFile  = new File(resourcePath + SEPARATOR + "config.properties");
	
	public static enum PropKeys{
		VISUALISER_WIDTH,
		VISUALISER_HEIGHT,
		LOG_LEVEL,
		ALGORITHMS_RUN,
		SOURCE_FILE,
		ROLLOUTS_HOP,
		ROLLOUTS_ORO,
		ROLLOUTS_UCTB,
		ROLLOUTS_UCTO,
		ROLLOUTS_UCTP,
		ITERATIONS_UCTP,
		ADDITIONAL_ROLLOUTS_UCTO,
	}

	public static boolean checkProperties(){
		for(int i = 0; i < AlgNames.values().length; i++){
			if(!prop.containsKey(PropKeys.values()[i].name())){
				return false;
			}
		}
		return true;
	}
	
	// variables for Table
	public static List<Result> ctpResults;
	public static int runsMade = 0;
	public static int columnsToCreate = 0;
	
}
