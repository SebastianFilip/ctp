package cz.vut.sf.gui;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;




import cz.vut.sf.algorithms.Result;

public class CtpAppConstants extends LoggerClass {
	public static boolean stop = false;
	
    public static enum AlgNames{
    	DIJKSTRA, GA,RA,CA,HOP,ORO,UCTB,UCTO, UCTB2, UCTP;
    }
	public static final String SEPARATOR = System.getProperty("file.separator");
	public static Properties prop = new Properties();
	public static final String resourcePath = System.getProperty("user.dir");
//	public static final String resourcePath = System.getProperty("user.dir") + SEPARATOR + "src" + SEPARATOR + "main" + SEPARATOR + "resources";
	public static File configFile;
	public static InputStream configFileIs = CtpGuiMain.class.getResourceAsStream(SEPARATOR+"config.properties");
	
	
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
		ADDITIONAL_ROLLOUTS_UCTP,
		ADDITIONAL_ROLLOUTS_UCTO,
		DEFAULT_SOURCE_FOLDER,
		DEFAULT_EXPORT_FOLDER,
		DEFAULT_XLS_NAME_ON;
		
	}

	public static boolean checkProperties(){
		for(int i = 0; i < AlgNames.values().length; i++){
			if(!prop.containsKey(PropKeys.values()[i].name())){
				return false;
			}
		}
		return true;
	}
	
	public static void fillDefaultProperties(){
		prop.setProperty(PropKeys.VISUALISER_WIDTH.name(), "1366");
		prop.setProperty(PropKeys.VISUALISER_HEIGHT.name(), "768");
		prop.setProperty(PropKeys.LOG_LEVEL.name(), "INFO");
		prop.setProperty(PropKeys.ALGORITHMS_RUN.name(), "1");
		prop.setProperty(PropKeys.SOURCE_FILE.name(), resourcePath + SEPARATOR + "eyerich.ctp");
		prop.setProperty(PropKeys.ROLLOUTS_HOP.name(), "100");
		prop.setProperty(PropKeys.ROLLOUTS_ORO.name(), "100");
		prop.setProperty(PropKeys.ROLLOUTS_UCTB.name(), "100");
		prop.setProperty(PropKeys.ROLLOUTS_UCTO.name(), "100");
		prop.setProperty(PropKeys.ROLLOUTS_UCTP.name(), "100");
		prop.setProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTP.name(), "100");
		prop.setProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO.name(), "20");
		prop.setProperty(PropKeys.DEFAULT_EXPORT_FOLDER.name(), resourcePath);
		prop.setProperty(PropKeys.DEFAULT_SOURCE_FOLDER.name(), resourcePath);
		prop.setProperty(PropKeys.DEFAULT_XLS_NAME_ON.name(), "1");
	}
	
	// variables for Table
	public static List<Result> ctpResults;
	public static int runsMade = 0;
	public static int columnsToCreate = 0;
	
}
