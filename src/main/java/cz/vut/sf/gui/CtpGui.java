package cz.vut.sf.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.parsers.BasicCtpParser;
import cz.vut.sf.parsers.ParsedDTO;
import cz.vut.sf.runner.CtpRunner;
import cz.vut.sf.runner.GraphSwing;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.log4j.LogManager;

import java.awt.Font;

import javax.swing.JTable;

import java.awt.SystemColor;
import java.awt.Color;

public class CtpGui extends CtpAppConstants{
	private JFrame frmCanadianTravellerProblem;
	private JTextField filePath;
	private JTextField txtVisualiserWidth;
	private JTextField txtVisualiserHeight;
	private JTextField algorithmsRun;
	private static JRadioButton rdbtnYes;
	public static JRadioButton rdbtnInfo;
	public static JRadioButton rdbtnSPP;
	public static JRadioButton rdbtnGA;
	public static JRadioButton rdbtnRA;
	public static JRadioButton rdbtnCA;
	public static JRadioButton rdbtnHOP;
	public static JRadioButton rdbtnORO;
	public static JRadioButton rdbtnUCTO2;
	public static JRadioButton rdbtnUCTO;
	public static JRadioButton rdbtnUCTP;
	public static JLabel lblStatus;
	private JTextField textFieldHOP;
	private JTextField textFieldORO;
	private JTextField textFieldUCTO2;
	private JTextField textFieldUCTO;
	private JTextField textFieldUCTP;
	private JTextField textFieldUCTO_M;
	private JTextField textFieldUCTP_Nr;
	private JTextField textField_UCTO2_Nr;
	private JTextArea textAreaConsole;
	private JScrollPane scrollConsole;
	private JScrollPane scrollTable;
	private JTable table;
	private JTextField txtDefaultExportFolder;
	private JTextField txtDefaultSourceFolder;
	private JTabbedPane tabbedPane;
	private JPanel panelStart;
	private JPanel panelAlgorithms;
	private JPanel panelResults;
	private JPanel panelSettings;
	private JTextField txtFieldTimeToUctDecision;



	/**
	 * Launch the application.
	 */
	public static void startGui() {
		LOG.info("starting gui");
		FileOutputStream fos = null;
		try{
			loadProperties();
			if(!checkProperties()){
				fos = new FileOutputStream(configFile);
				fillDefaultProperties();
				prop.store(fos, "Application Settings");
				fos.close();
				LOG.info("created new config.properties file.");
			}
		}catch(Exception e){
			LOG.error(e);
			e.printStackTrace();
			
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CtpGui window = new CtpGui();
					window.frmCanadianTravellerProblem.setVisible(true);
					window.frmCanadianTravellerProblem.pack();
				} catch (Exception e) {
					LOG.error(e);
					e.printStackTrace();
				}finally{
				}
			}
		});
		
	}
	public static void saveProp() throws Throwable{
		FileOutputStream fos = null;
		fos = new FileOutputStream(configFile);
		prop.store(fos, "Application Settings");
		fos.close();
	}
	
	public static boolean loadProperties() throws IOException{
		//have to escape \ on windows not sure how it would be on other systems
		String[] parts = resourcePath.split("SEPARATOR+SEPARATOR");
		String root = "";
		for (int i = 0; i < parts.length - 1; i++){
			root += parts[i] + SEPARATOR;
		}
		configFile  = new File(root + "config.properties");
		LOG.info("reading .properties file from path:" + root + "config.properties");
		try{
			FileInputStream fin = new FileInputStream(configFile);
			prop.load(fin);
		}catch (Exception e){
			LOG.info("Error reading .properties file", e);
			return false;
		}
		return true;
	}
	private class LabelSwingWorker extends SwingWorker<Void, String>{
		private final JLabel label;
        @Override
        protected Void doInBackground() throws Exception {
        	publish("busy");
			List<AlgNames> algorithmsToBeMade = getAlgList();
			CtpRunner.run(algorithmsToBeMade, Integer.parseInt((String) prop.get(PropKeys.ALGORITHMS_RUN.name())));
            return null;
        }
        @Override
        protected void process(List<String> statuses){
        	String status = statuses.get(statuses.size()-1);
        	label.setText("Status: " + status);
        }
        
        @Override
        public void done(){
        	label.setText("Status: idle");
        	JOptionPane.showMessageDialog(null, "Calculation finished");
    		ResultsTable.initTable();
    		table = new JTable(ResultsTable.model);
    		
        }
        
        public LabelSwingWorker(JLabel lbl){
        	label = lbl;
        }
    }

	private List<AlgNames> getAlgList() {
		List<AlgNames> result = new ArrayList<AlgNames>();
		if(rdbtnSPP.isSelected()){result.add(AlgNames.DIJKSTRA);}
		if(rdbtnGA.isSelected()){result.add(AlgNames.GA);}
		if(rdbtnRA.isSelected()){result.add(AlgNames.RA);}
		if(rdbtnCA.isSelected()){result.add(AlgNames.CA);}
		if(rdbtnHOP.isSelected()){result.add(AlgNames.HOP);}
		if(rdbtnORO.isSelected()){result.add(AlgNames.ORO);}
		if(rdbtnUCTO.isSelected()){result.add(AlgNames.UCTO);}
		if(rdbtnUCTO2.isSelected()){result.add(AlgNames.UCTO2);}
		if(rdbtnUCTP.isSelected()){result.add(AlgNames.UCTP);}
		return result;
	}
	/**
	 * Create the application.
	 */
	public CtpGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GroupLayout groupLayout = initForm();
		
		initStartPanel();
		
		initAlgorithmsPanel();
		
		initResultsPanel();
		
		initSettingsPanel();
		
		frmCanadianTravellerProblem.getContentPane().setLayout(groupLayout);
	}
	private void initSettingsPanel() {
		ButtonGroup bgLogLevel = new ButtonGroup(); 
		ButtonGroup bgDefaultXlsName = new ButtonGroup(); 
		ButtonGroup bgTimeForUct = new ButtonGroup(); 
		panelSettings = new JPanel();
		tabbedPane.addTab("App Settings", null, panelSettings, null);
		GridBagLayout gbl_panelSettings = new GridBagLayout();
		gbl_panelSettings.columnWidths = new int[]{5, 124, 74, 86, 181, 0};
		gbl_panelSettings.rowHeights = new int[]{32, 27, 27, 27, 0, 27, 27, 27, 27, 0, 0};
		gbl_panelSettings.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSettings.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelSettings.setLayout(gbl_panelSettings);
		
		JLabel lblCommonSettings = new JLabel("Common settings");
		lblCommonSettings.setVerticalAlignment(SwingConstants.BOTTOM);
		lblCommonSettings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblCommonSettings.setBackground(SystemColor.controlShadow);
		GridBagConstraints gbc_lblCommonSettings = new GridBagConstraints();
		gbc_lblCommonSettings.anchor = GridBagConstraints.WEST;
		gbc_lblCommonSettings.insets = new Insets(0, 0, 5, 5);
		gbc_lblCommonSettings.gridx = 1;
		gbc_lblCommonSettings.gridy = 0;
		panelSettings.add(lblCommonSettings, gbc_lblCommonSettings);
		
		JLabel lblDefaultGraphSource = new JLabel("Default graph source folder:");
		lblDefaultGraphSource.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_lblDefaultGraphSource = new GridBagConstraints();
		gbc_lblDefaultGraphSource.anchor = GridBagConstraints.WEST;
		gbc_lblDefaultGraphSource.insets = new Insets(0, 0, 5, 5);
		gbc_lblDefaultGraphSource.gridx = 1;
		gbc_lblDefaultGraphSource.gridy = 1;
		panelSettings.add(lblDefaultGraphSource, gbc_lblDefaultGraphSource);
		
		txtDefaultSourceFolder = new JTextField();
		txtDefaultSourceFolder.setText(prop.getProperty(PropKeys.DEFAULT_SOURCE_FOLDER.name()));
		txtDefaultSourceFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.DEFAULT_SOURCE_FOLDER.name(),txtDefaultSourceFolder.getText());
				try {
					saveProp();
				} catch (Throwable e1) {
					LOG.error(e1);
					e1.printStackTrace();
				}
			}
		});
		txtDefaultSourceFolder.setColumns(10);
		GridBagConstraints gbc_txtDefaultSourceFolder = new GridBagConstraints();
		gbc_txtDefaultSourceFolder.fill = GridBagConstraints.BOTH;
		gbc_txtDefaultSourceFolder.anchor = GridBagConstraints.WEST;
		gbc_txtDefaultSourceFolder.gridwidth = 3;
		gbc_txtDefaultSourceFolder.insets = new Insets(0, 0, 5, 0);
		gbc_txtDefaultSourceFolder.gridx = 2;
		gbc_txtDefaultSourceFolder.gridy = 1;
		panelSettings.add(txtDefaultSourceFolder, gbc_txtDefaultSourceFolder);
		
		JLabel lblDefaultExportFolder = new JLabel("Default export folder:");
		GridBagConstraints gbc_lblDefaultExportFolder = new GridBagConstraints();
		gbc_lblDefaultExportFolder.anchor = GridBagConstraints.WEST;
		gbc_lblDefaultExportFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblDefaultExportFolder.gridx = 1;
		gbc_lblDefaultExportFolder.gridy = 2;
		panelSettings.add(lblDefaultExportFolder, gbc_lblDefaultExportFolder);
		
		txtDefaultExportFolder = new JTextField();
		txtDefaultExportFolder.setText(prop.getProperty(PropKeys.DEFAULT_EXPORT_FOLDER.name()));
		txtDefaultExportFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.DEFAULT_EXPORT_FOLDER.name(),txtDefaultExportFolder.getText());
				try {
					saveProp();
				} catch (Throwable e1) {
					LOG.error(e1);
					e1.printStackTrace();
				}
			}
		});
		txtDefaultExportFolder.setColumns(10);
		GridBagConstraints gbc_txtDefaultExportFolder = new GridBagConstraints();
		gbc_txtDefaultExportFolder.fill = GridBagConstraints.BOTH;
		gbc_txtDefaultExportFolder.anchor = GridBagConstraints.WEST;
		gbc_txtDefaultExportFolder.gridwidth = 3;
		gbc_txtDefaultExportFolder.insets = new Insets(0, 0, 5, 0);
		gbc_txtDefaultExportFolder.gridx = 2;
		gbc_txtDefaultExportFolder.gridy = 2;
		panelSettings.add(txtDefaultExportFolder, gbc_txtDefaultExportFolder);
		
		JLabel lblDefaultxlsName = new JLabel("Default .xls name");
		GridBagConstraints gbc_lblDefaultxlsName = new GridBagConstraints();
		gbc_lblDefaultxlsName.anchor = GridBagConstraints.WEST;
		gbc_lblDefaultxlsName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDefaultxlsName.gridx = 1;
		gbc_lblDefaultxlsName.gridy = 3;
		panelSettings.add(lblDefaultxlsName, gbc_lblDefaultxlsName);
		
		rdbtnYes = new JRadioButton("Yes", true);
		rdbtnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.DEFAULT_XLS_NAME_ON.name(), "1");
				try {
					saveProp();
				} catch (Throwable e1) {
					LOG.error(e1);
					e1.printStackTrace();
				}
			}
		});
		bgDefaultXlsName.add(rdbtnYes);
		GridBagConstraints gbc_rdbtnYes = new GridBagConstraints();
		gbc_rdbtnYes.anchor = GridBagConstraints.WEST;
		gbc_rdbtnYes.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnYes.gridx = 2;
		gbc_rdbtnYes.gridy = 3;
		panelSettings.add(rdbtnYes, gbc_rdbtnYes);
		
		JRadioButton rdbtnNo = new JRadioButton("No", false);
		rdbtnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.DEFAULT_XLS_NAME_ON.name(), "0");
				try {
					saveProp();
				} catch (Throwable e1) {
					LOG.error(e1);
					e1.printStackTrace();
				}
			}
		});
		bgDefaultXlsName.add(rdbtnNo);
		GridBagConstraints gbc_rdbtnNo = new GridBagConstraints();
		gbc_rdbtnNo.anchor = GridBagConstraints.WEST;
		gbc_rdbtnNo.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnNo.gridx = 3;
		gbc_rdbtnNo.gridy = 3;
		panelSettings.add(rdbtnNo, gbc_rdbtnNo);
		
		if(prop.getProperty(PropKeys.DEFAULT_XLS_NAME_ON.name()).contains("0")){
			rdbtnNo.setSelected(true);
		}
		String timeToDecisionValue = prop.getProperty(PropKeys.TIME_TO_DECISION_FOR_UCT.name());
		final JLabel lblUctTimeToDecision = new JLabel("UCT time to decision (" + timeToDecisionValue + " ms)");
		lblUctTimeToDecision.setToolTipText("value of default time can be changed in config.properties setting TIME_TO_DECISION_FOR_UCT in [ms]");
		lblUctTimeToDecision.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblUctTimeToDecision = new GridBagConstraints();
		gbc_lblUctTimeToDecision.anchor = GridBagConstraints.WEST;
		gbc_lblUctTimeToDecision.insets = new Insets(0, 0, 5, 5);
		gbc_lblUctTimeToDecision.gridx = 1;
		gbc_lblUctTimeToDecision.gridy = 4;
		panelSettings.add(lblUctTimeToDecision, gbc_lblUctTimeToDecision);
		
		JRadioButton rdbtnOn = new JRadioButton("On", true);
		rdbtnOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.TIME_LIMITATION_ON.name(), "True");
				try {
					saveProp();
				} catch (Throwable e1) {
					LOG.error(e1);
					e1.printStackTrace();
				}
			}
		});
		rdbtnOn.setSelected(Boolean.parseBoolean(prop.getProperty(PropKeys.TIME_LIMITATION_ON.name())));
		GridBagConstraints gbc_rdbtnOn = new GridBagConstraints();
		gbc_rdbtnOn.anchor = GridBagConstraints.WEST;
		gbc_rdbtnOn.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnOn.gridx = 2;
		gbc_rdbtnOn.gridy = 4;
		bgTimeForUct.add(rdbtnOn);
		panelSettings.add(rdbtnOn, gbc_rdbtnOn);
		
		JRadioButton rdbtnOff = new JRadioButton("Off", false);
		rdbtnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.TIME_LIMITATION_ON.name(), "False");
				try {
					saveProp();
				} catch (Throwable e1) {
					LOG.error(e1);
					e1.printStackTrace();
				}
			}
		});
		rdbtnOff.setSelected(!Boolean.parseBoolean(prop.getProperty(PropKeys.TIME_LIMITATION_ON.name())));
		GridBagConstraints gbc_rdbtnOff = new GridBagConstraints();
		gbc_rdbtnOff.anchor = GridBagConstraints.WEST;
		gbc_rdbtnOff.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnOff.gridx = 3;
		gbc_rdbtnOff.gridy = 4;
		bgTimeForUct.add(rdbtnOff);
		panelSettings.add(rdbtnOff, gbc_rdbtnOff);
		
		txtFieldTimeToUctDecision = new JTextField();
		txtFieldTimeToUctDecision.setText(prop.getProperty(PropKeys.TIME_TO_DECISION_FOR_UCT.name()));
		txtFieldTimeToUctDecision.setToolTipText("Enter positive integer (time in miliseconds)");
		txtFieldTimeToUctDecision.addActionListener(new TextFieldIntegerListener(PropKeys.TIME_TO_DECISION_FOR_UCT, txtFieldTimeToUctDecision){
			@Override
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				String timeToDecisionValue = prop.getProperty(PropKeys.TIME_TO_DECISION_FOR_UCT.name());
				lblUctTimeToDecision.setText("UCT time to decision (" + timeToDecisionValue + " ms)");
			}
		});
		txtFieldTimeToUctDecision.setText((String) null);
		txtFieldTimeToUctDecision.setColumns(10);
		GridBagConstraints gbc_txtFieldTimeToUctDecision = new GridBagConstraints();
		gbc_txtFieldTimeToUctDecision.insets = new Insets(0, 0, 5, 0);
		gbc_txtFieldTimeToUctDecision.fill = GridBagConstraints.BOTH;
		gbc_txtFieldTimeToUctDecision.gridx = 4;
		gbc_txtFieldTimeToUctDecision.gridy = 4;
		panelSettings.add(txtFieldTimeToUctDecision, gbc_txtFieldTimeToUctDecision);
		
		JLabel lblLogLevel = new JLabel("Log level");
		GridBagConstraints gbc_lblLogLevel = new GridBagConstraints();
		gbc_lblLogLevel.anchor = GridBagConstraints.WEST;
		gbc_lblLogLevel.insets = new Insets(0, 0, 5, 5);
		gbc_lblLogLevel.gridx = 1;
		gbc_lblLogLevel.gridy = 5;
		panelSettings.add(lblLogLevel, gbc_lblLogLevel);
		
		rdbtnInfo = new JRadioButton("Info",true);
		bgLogLevel.add(rdbtnInfo);
		GridBagConstraints gbc_rdbtnInfo = new GridBagConstraints();
		gbc_rdbtnInfo.anchor = GridBagConstraints.WEST;
		gbc_rdbtnInfo.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnInfo.gridx = 2;
		gbc_rdbtnInfo.gridy = 5;
		panelSettings.add(rdbtnInfo, gbc_rdbtnInfo);
		JRadioButton rdbtnDebug = new JRadioButton("Debug", false);
		bgLogLevel.add(rdbtnDebug);
		GridBagConstraints gbc_rdbtnDebug = new GridBagConstraints();
		gbc_rdbtnDebug.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnDebug.anchor = GridBagConstraints.WEST;
		gbc_rdbtnDebug.gridx = 3;
		gbc_rdbtnDebug.gridy = 5;
		panelSettings.add(rdbtnDebug, gbc_rdbtnDebug);
		
		JLabel lblVisualiserSettings = new JLabel("Visualiser settings");
		lblVisualiserSettings.setVerticalAlignment(SwingConstants.BOTTOM);
		lblVisualiserSettings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblVisualiserSettings.setBackground(SystemColor.controlShadow);
		GridBagConstraints gbc_lblVisualiserSettings = new GridBagConstraints();
		gbc_lblVisualiserSettings.anchor = GridBagConstraints.WEST;
		gbc_lblVisualiserSettings.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisualiserSettings.gridx = 1;
		gbc_lblVisualiserSettings.gridy = 6;
		panelSettings.add(lblVisualiserSettings, gbc_lblVisualiserSettings);
		
		JLabel lblVisualiserWidth = new JLabel("Visualiser width:");
		GridBagConstraints gbc_lblVisualiserWidth = new GridBagConstraints();
		gbc_lblVisualiserWidth.anchor = GridBagConstraints.WEST;
		gbc_lblVisualiserWidth.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisualiserWidth.gridx = 1;
		gbc_lblVisualiserWidth.gridy = 7;
		panelSettings.add(lblVisualiserWidth, gbc_lblVisualiserWidth);
		
		txtVisualiserWidth = new JTextField();
		txtVisualiserWidth.addActionListener(new TextFieldIntegerListener(PropKeys.VISUALISER_WIDTH, txtVisualiserWidth));
		txtVisualiserWidth.setText(prop.getProperty(PropKeys.VISUALISER_WIDTH.name()));
		txtVisualiserWidth.setColumns(10);
		GridBagConstraints gbc_txtVisualiserWidth = new GridBagConstraints();
		gbc_txtVisualiserWidth.fill = GridBagConstraints.BOTH;
		gbc_txtVisualiserWidth.anchor = GridBagConstraints.NORTHWEST;
		gbc_txtVisualiserWidth.insets = new Insets(0, 0, 5, 5);
		gbc_txtVisualiserWidth.gridx = 2;
		gbc_txtVisualiserWidth.gridy = 7;
		panelSettings.add(txtVisualiserWidth, gbc_txtVisualiserWidth);
		
		JLabel lblVisualiserHeight = new JLabel("Visualiser height:");
		GridBagConstraints gbc_lblVisualiserHeight = new GridBagConstraints();
		gbc_lblVisualiserHeight.anchor = GridBagConstraints.WEST;
		gbc_lblVisualiserHeight.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisualiserHeight.gridx = 1;
		gbc_lblVisualiserHeight.gridy = 8;
		panelSettings.add(lblVisualiserHeight, gbc_lblVisualiserHeight);
		
		txtVisualiserHeight = new JTextField();
		txtVisualiserHeight.addActionListener(new TextFieldIntegerListener(PropKeys.VISUALISER_HEIGHT, txtVisualiserHeight));
		txtVisualiserHeight.setText(prop.getProperty(PropKeys.VISUALISER_HEIGHT.name()));
		txtVisualiserHeight.setColumns(10);
		GridBagConstraints gbc_txtVisualiserHeight = new GridBagConstraints();
		gbc_txtVisualiserHeight.fill = GridBagConstraints.BOTH;
		gbc_txtVisualiserHeight.anchor = GridBagConstraints.NORTHWEST;
		gbc_txtVisualiserHeight.insets = new Insets(0, 0, 5, 5);
		gbc_txtVisualiserHeight.gridx = 2;
		gbc_txtVisualiserHeight.gridy = 8;
		panelSettings.add(txtVisualiserHeight, gbc_txtVisualiserHeight);
	}
	private void initResultsPanel() {
		panelResults = new JPanel();
		tabbedPane.addTab("Results", null, panelResults, null);
		
		table = new JTable(ResultsTable.model);
		scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JLabel lblTable = new JLabel("  Results");
		lblTable.setVerticalAlignment(SwingConstants.BOTTOM);
		lblTable.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblTable.setBackground(SystemColor.controlShadow);
		
		JButton btnNewButton = new JButton("Export to xls");
		btnNewButton.setToolTipText("Exports data to .xls file in chosen location.");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton save = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Save File");
				fc.setCurrentDirectory(new File(prop.getProperty(PropKeys.DEFAULT_EXPORT_FOLDER.name())));
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(rdbtnYes.isSelected()){
					String [] sourceParts = prop.getProperty(PropKeys.SOURCE_FILE.name()).split(SEPARATOR + SEPARATOR);
					String sourceFile = sourceParts[sourceParts.length - 1];
					fc.setSelectedFile(new File(fc.getCurrentDirectory().toString() + SEPARATOR + sourceFile + "_" + String.valueOf(runsMade)));
				}
				if(fc.showSaveDialog(save) == JFileChooser.APPROVE_OPTION){
					String fileName = fc.getSelectedFile().getName();
					String path = fc.getSelectedFile().getParentFile().getPath();

					String ext = "";
					String file = "";

					if(fileName.length() > 4){
						ext = fileName.substring(fileName.length()-4, fileName.length());
					}

					if(ext.equals(".xls")){
						file = path + SEPARATOR + fileName; 
					}else{
						file = path + SEPARATOR + fileName + ".xls"; 
					}
					ResultsTable.exportToXls(new File(file));
				}
			}
		});
		
		GroupLayout gl_panelResults = new GroupLayout(panelResults);
		gl_panelResults.setHorizontalGroup(
			gl_panelResults.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelResults.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelResults.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollTable, GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
						.addGroup(gl_panelResults.createSequentialGroup()
							.addComponent(lblTable, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton)))
					.addContainerGap())
		);
		gl_panelResults.setVerticalGroup(
			gl_panelResults.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panelResults.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelResults.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTable, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(scrollTable, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
					.addGap(28))
		);
		panelResults.setLayout(gl_panelResults);
	}
	private void initAlgorithmsPanel() {
		final String lblAlgorithmsRunText = "   Agorithms are going to be run x time(s).";
		
		panelAlgorithms = new JPanel();
		tabbedPane.addTab("Algorithms", null, panelAlgorithms, null);
		GridBagLayout gbl_panelAlgorithms = new GridBagLayout();
		gbl_panelAlgorithms.columnWidths = new int[]{175, 44, 79, 105, 20, 138, 105, 0};
		gbl_panelAlgorithms.rowHeights = new int[]{33, 38, 38, 38, 38, 38, 38, 38, 38, 38, 0, 38, 12, 0};
		gbl_panelAlgorithms.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelAlgorithms.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelAlgorithms.setLayout(gbl_panelAlgorithms);
		
		JLabel lblAlgorithms = new JLabel("Algorithms");
		lblAlgorithms.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblAlgorithms.setBackground(UIManager.getColor("Button.shadow"));
		lblAlgorithms.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_lblAlgorithms = new GridBagConstraints();
		gbc_lblAlgorithms.fill = GridBagConstraints.VERTICAL;
		gbc_lblAlgorithms.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlgorithms.gridx = 0;
		gbc_lblAlgorithms.gridy = 0;
		panelAlgorithms.add(lblAlgorithms, gbc_lblAlgorithms);
		
		JLabel lblDijkstraShortestPath = new JLabel("   Dijkstra shortest path");
		GridBagConstraints gbc_lblDijkstraShortestPath = new GridBagConstraints();
		gbc_lblDijkstraShortestPath.fill = GridBagConstraints.BOTH;
		gbc_lblDijkstraShortestPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblDijkstraShortestPath.gridx = 0;
		gbc_lblDijkstraShortestPath.gridy = 1;
		panelAlgorithms.add(lblDijkstraShortestPath, gbc_lblDijkstraShortestPath);
		
		rdbtnSPP = new JRadioButton("");
		rdbtnSPP.setSelected(true);
		GridBagConstraints gbc_rdbtnSPP = new GridBagConstraints();
		gbc_rdbtnSPP.fill = GridBagConstraints.BOTH;
		gbc_rdbtnSPP.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnSPP.gridx = 1;
		gbc_rdbtnSPP.gridy = 1;
		panelAlgorithms.add(rdbtnSPP, gbc_rdbtnSPP);
		
		JLabel label = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 1;
		panelAlgorithms.add(label, gbc_label);
		
		JLabel label_1 = new JLabel("");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.fill = GridBagConstraints.BOTH;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 3;
		gbc_label_1.gridy = 1;
		panelAlgorithms.add(label_1, gbc_label_1);
		
		JLabel label_2 = new JLabel("");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.fill = GridBagConstraints.BOTH;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 4;
		gbc_label_2.gridy = 1;
		panelAlgorithms.add(label_2, gbc_label_2);
		
		JLabel label_3 = new JLabel("");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.fill = GridBagConstraints.BOTH;
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 5;
		gbc_label_3.gridy = 1;
		panelAlgorithms.add(label_3, gbc_label_3);
		
		JLabel label_4 = new JLabel("");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.fill = GridBagConstraints.BOTH;
		gbc_label_4.insets = new Insets(0, 0, 5, 0);
		gbc_label_4.gridx = 6;
		gbc_label_4.gridy = 1;
		panelAlgorithms.add(label_4, gbc_label_4);
		
		JLabel lblGreedyAlgorithm = new JLabel("   Greedy algorithm");
		GridBagConstraints gbc_lblGreedyAlgorithm = new GridBagConstraints();
		gbc_lblGreedyAlgorithm.fill = GridBagConstraints.BOTH;
		gbc_lblGreedyAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblGreedyAlgorithm.gridx = 0;
		gbc_lblGreedyAlgorithm.gridy = 2;
		panelAlgorithms.add(lblGreedyAlgorithm, gbc_lblGreedyAlgorithm);
		
		rdbtnGA = new JRadioButton("");
		GridBagConstraints gbc_rdbtnGA = new GridBagConstraints();
		gbc_rdbtnGA.fill = GridBagConstraints.BOTH;
		gbc_rdbtnGA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnGA.gridx = 1;
		gbc_rdbtnGA.gridy = 2;
		panelAlgorithms.add(rdbtnGA, gbc_rdbtnGA);
		
		JLabel label_8 = new JLabel("");
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.fill = GridBagConstraints.BOTH;
		gbc_label_8.insets = new Insets(0, 0, 5, 5);
		gbc_label_8.gridx = 2;
		gbc_label_8.gridy = 2;
		panelAlgorithms.add(label_8, gbc_label_8);
		
		JLabel label_9 = new JLabel("");
		GridBagConstraints gbc_label_9 = new GridBagConstraints();
		gbc_label_9.fill = GridBagConstraints.BOTH;
		gbc_label_9.insets = new Insets(0, 0, 5, 5);
		gbc_label_9.gridx = 3;
		gbc_label_9.gridy = 2;
		panelAlgorithms.add(label_9, gbc_label_9);
		
		JLabel label_10 = new JLabel("");
		GridBagConstraints gbc_label_10 = new GridBagConstraints();
		gbc_label_10.fill = GridBagConstraints.BOTH;
		gbc_label_10.insets = new Insets(0, 0, 5, 5);
		gbc_label_10.gridx = 4;
		gbc_label_10.gridy = 2;
		panelAlgorithms.add(label_10, gbc_label_10);
		
		JLabel label_11 = new JLabel("");
		GridBagConstraints gbc_label_11 = new GridBagConstraints();
		gbc_label_11.fill = GridBagConstraints.BOTH;
		gbc_label_11.insets = new Insets(0, 0, 5, 5);
		gbc_label_11.gridx = 5;
		gbc_label_11.gridy = 2;
		panelAlgorithms.add(label_11, gbc_label_11);
		
		JLabel label_12 = new JLabel("");
		GridBagConstraints gbc_label_12 = new GridBagConstraints();
		gbc_label_12.fill = GridBagConstraints.BOTH;
		gbc_label_12.insets = new Insets(0, 0, 5, 0);
		gbc_label_12.gridx = 6;
		gbc_label_12.gridy = 2;
		panelAlgorithms.add(label_12, gbc_label_12);
		
		JLabel lblRepo = new JLabel("   Reposition algorithm");
		GridBagConstraints gbc_lblRepo = new GridBagConstraints();
		gbc_lblRepo.fill = GridBagConstraints.BOTH;
		gbc_lblRepo.insets = new Insets(0, 0, 5, 5);
		gbc_lblRepo.gridx = 0;
		gbc_lblRepo.gridy = 3;
		panelAlgorithms.add(lblRepo, gbc_lblRepo);
		
		rdbtnRA = new JRadioButton("");
		GridBagConstraints gbc_rdbtnRA = new GridBagConstraints();
		gbc_rdbtnRA.fill = GridBagConstraints.BOTH;
		gbc_rdbtnRA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnRA.gridx = 1;
		gbc_rdbtnRA.gridy = 3;
		panelAlgorithms.add(rdbtnRA, gbc_rdbtnRA);
		
		JLabel label_16 = new JLabel("");
		GridBagConstraints gbc_label_16 = new GridBagConstraints();
		gbc_label_16.fill = GridBagConstraints.BOTH;
		gbc_label_16.insets = new Insets(0, 0, 5, 5);
		gbc_label_16.gridx = 2;
		gbc_label_16.gridy = 3;
		panelAlgorithms.add(label_16, gbc_label_16);
		
		JLabel label_17 = new JLabel("");
		GridBagConstraints gbc_label_17 = new GridBagConstraints();
		gbc_label_17.fill = GridBagConstraints.BOTH;
		gbc_label_17.insets = new Insets(0, 0, 5, 5);
		gbc_label_17.gridx = 3;
		gbc_label_17.gridy = 3;
		panelAlgorithms.add(label_17, gbc_label_17);
		
		JLabel label_18 = new JLabel("");
		GridBagConstraints gbc_label_18 = new GridBagConstraints();
		gbc_label_18.fill = GridBagConstraints.BOTH;
		gbc_label_18.insets = new Insets(0, 0, 5, 5);
		gbc_label_18.gridx = 4;
		gbc_label_18.gridy = 3;
		panelAlgorithms.add(label_18, gbc_label_18);
		
		JLabel label_19 = new JLabel("");
		GridBagConstraints gbc_label_19 = new GridBagConstraints();
		gbc_label_19.fill = GridBagConstraints.BOTH;
		gbc_label_19.insets = new Insets(0, 0, 5, 5);
		gbc_label_19.gridx = 5;
		gbc_label_19.gridy = 3;
		panelAlgorithms.add(label_19, gbc_label_19);
		
		JLabel label_20 = new JLabel("");
		GridBagConstraints gbc_label_20 = new GridBagConstraints();
		gbc_label_20.fill = GridBagConstraints.BOTH;
		gbc_label_20.insets = new Insets(0, 0, 5, 0);
		gbc_label_20.gridx = 6;
		gbc_label_20.gridy = 3;
		panelAlgorithms.add(label_20, gbc_label_20);
		
		JLabel lblComparisonAlgorithm = new JLabel("   Comparison algorithm");
		GridBagConstraints gbc_lblComparisonAlgorithm = new GridBagConstraints();
		gbc_lblComparisonAlgorithm.fill = GridBagConstraints.BOTH;
		gbc_lblComparisonAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblComparisonAlgorithm.gridx = 0;
		gbc_lblComparisonAlgorithm.gridy = 4;
		panelAlgorithms.add(lblComparisonAlgorithm, gbc_lblComparisonAlgorithm);
		
		rdbtnCA = new JRadioButton("");
		GridBagConstraints gbc_rdbtnCA = new GridBagConstraints();
		gbc_rdbtnCA.fill = GridBagConstraints.BOTH;
		gbc_rdbtnCA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnCA.gridx = 1;
		gbc_rdbtnCA.gridy = 4;
		panelAlgorithms.add(rdbtnCA, gbc_rdbtnCA);
		
		JLabel label_24 = new JLabel("");
		GridBagConstraints gbc_label_24 = new GridBagConstraints();
		gbc_label_24.fill = GridBagConstraints.BOTH;
		gbc_label_24.insets = new Insets(0, 0, 5, 5);
		gbc_label_24.gridx = 2;
		gbc_label_24.gridy = 4;
		panelAlgorithms.add(label_24, gbc_label_24);
		
		JLabel label_25 = new JLabel("");
		GridBagConstraints gbc_label_25 = new GridBagConstraints();
		gbc_label_25.fill = GridBagConstraints.BOTH;
		gbc_label_25.insets = new Insets(0, 0, 5, 5);
		gbc_label_25.gridx = 3;
		gbc_label_25.gridy = 4;
		panelAlgorithms.add(label_25, gbc_label_25);
		
		JLabel label_26 = new JLabel("");
		GridBagConstraints gbc_label_26 = new GridBagConstraints();
		gbc_label_26.fill = GridBagConstraints.BOTH;
		gbc_label_26.insets = new Insets(0, 0, 5, 5);
		gbc_label_26.gridx = 4;
		gbc_label_26.gridy = 4;
		panelAlgorithms.add(label_26, gbc_label_26);
		
		JLabel label_27 = new JLabel("");
		GridBagConstraints gbc_label_27 = new GridBagConstraints();
		gbc_label_27.fill = GridBagConstraints.BOTH;
		gbc_label_27.insets = new Insets(0, 0, 5, 5);
		gbc_label_27.gridx = 5;
		gbc_label_27.gridy = 4;
		panelAlgorithms.add(label_27, gbc_label_27);
		
		JLabel label_28 = new JLabel("");
		GridBagConstraints gbc_label_28 = new GridBagConstraints();
		gbc_label_28.fill = GridBagConstraints.BOTH;
		gbc_label_28.insets = new Insets(0, 0, 5, 0);
		gbc_label_28.gridx = 6;
		gbc_label_28.gridy = 4;
		panelAlgorithms.add(label_28, gbc_label_28);
		
		JLabel lblHindsightOptimizationhop = new JLabel("   Hindsight Optimization (HOP)");
		GridBagConstraints gbc_lblHindsightOptimizationhop = new GridBagConstraints();
		gbc_lblHindsightOptimizationhop.fill = GridBagConstraints.BOTH;
		gbc_lblHindsightOptimizationhop.insets = new Insets(0, 0, 5, 5);
		gbc_lblHindsightOptimizationhop.gridx = 0;
		gbc_lblHindsightOptimizationhop.gridy = 5;
		panelAlgorithms.add(lblHindsightOptimizationhop, gbc_lblHindsightOptimizationhop);
		
		rdbtnHOP = new JRadioButton("");
		GridBagConstraints gbc_rdbtnHOP = new GridBagConstraints();
		gbc_rdbtnHOP.fill = GridBagConstraints.BOTH;
		gbc_rdbtnHOP.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnHOP.gridx = 1;
		gbc_rdbtnHOP.gridy = 5;
		panelAlgorithms.add(rdbtnHOP, gbc_rdbtnHOP);
		
		JLabel lblRollouts = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts = new GridBagConstraints();
		gbc_lblRollouts.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts.gridx = 2;
		gbc_lblRollouts.gridy = 5;
		panelAlgorithms.add(lblRollouts, gbc_lblRollouts);
		
		textFieldHOP = new JTextField();
		textFieldHOP.setToolTipText("Pls enter positive integer value.");
		textFieldHOP.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_HOP, textFieldHOP));
		
		textFieldHOP.setText(prop.getProperty(PropKeys.ROLLOUTS_HOP.name()));
		textFieldHOP.setColumns(10);
		GridBagConstraints gbc_textFieldHOP = new GridBagConstraints();
		gbc_textFieldHOP.fill = GridBagConstraints.BOTH;
		gbc_textFieldHOP.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldHOP.gridx = 3;
		gbc_textFieldHOP.gridy = 5;
		panelAlgorithms.add(textFieldHOP, gbc_textFieldHOP);
		
		JLabel label_32 = new JLabel("");
		GridBagConstraints gbc_label_32 = new GridBagConstraints();
		gbc_label_32.fill = GridBagConstraints.BOTH;
		gbc_label_32.insets = new Insets(0, 0, 5, 5);
		gbc_label_32.gridx = 4;
		gbc_label_32.gridy = 5;
		panelAlgorithms.add(label_32, gbc_label_32);
		
		JLabel label_33 = new JLabel("");
		GridBagConstraints gbc_label_33 = new GridBagConstraints();
		gbc_label_33.fill = GridBagConstraints.BOTH;
		gbc_label_33.insets = new Insets(0, 0, 5, 5);
		gbc_label_33.gridx = 5;
		gbc_label_33.gridy = 5;
		panelAlgorithms.add(label_33, gbc_label_33);
		
		JLabel label_34 = new JLabel("");
		GridBagConstraints gbc_label_34 = new GridBagConstraints();
		gbc_label_34.fill = GridBagConstraints.BOTH;
		gbc_label_34.insets = new Insets(0, 0, 5, 0);
		gbc_label_34.gridx = 6;
		gbc_label_34.gridy = 5;
		panelAlgorithms.add(label_34, gbc_label_34);
		
		JLabel lblOptimisticRolloutoro = new JLabel("   Optimistic Rollout (ORO)");
		GridBagConstraints gbc_lblOptimisticRolloutoro = new GridBagConstraints();
		gbc_lblOptimisticRolloutoro.fill = GridBagConstraints.BOTH;
		gbc_lblOptimisticRolloutoro.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptimisticRolloutoro.gridx = 0;
		gbc_lblOptimisticRolloutoro.gridy = 6;
		panelAlgorithms.add(lblOptimisticRolloutoro, gbc_lblOptimisticRolloutoro);
		
		rdbtnORO = new JRadioButton("");
		GridBagConstraints gbc_rdbtnORO = new GridBagConstraints();
		gbc_rdbtnORO.fill = GridBagConstraints.BOTH;
		gbc_rdbtnORO.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnORO.gridx = 1;
		gbc_rdbtnORO.gridy = 6;
		panelAlgorithms.add(rdbtnORO, gbc_rdbtnORO);
		
		JLabel lblRollouts1 = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts1 = new GridBagConstraints();
		gbc_lblRollouts1.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts1.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts1.gridx = 2;
		gbc_lblRollouts1.gridy = 6;
		panelAlgorithms.add(lblRollouts1, gbc_lblRollouts1);
		
		textFieldORO = new JTextField();
		textFieldORO.setToolTipText("Pls enter positive integer value.");
		textFieldORO.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_ORO, textFieldORO));
		textFieldORO.setText(prop.getProperty(PropKeys.ROLLOUTS_ORO.name()));
		textFieldORO.setColumns(10);
		GridBagConstraints gbc_textFieldORO = new GridBagConstraints();
		gbc_textFieldORO.fill = GridBagConstraints.BOTH;
		gbc_textFieldORO.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldORO.gridx = 3;
		gbc_textFieldORO.gridy = 6;
		panelAlgorithms.add(textFieldORO, gbc_textFieldORO);
		
		JLabel label_38 = new JLabel("");
		GridBagConstraints gbc_label_38 = new GridBagConstraints();
		gbc_label_38.fill = GridBagConstraints.BOTH;
		gbc_label_38.insets = new Insets(0, 0, 5, 5);
		gbc_label_38.gridx = 4;
		gbc_label_38.gridy = 6;
		panelAlgorithms.add(label_38, gbc_label_38);
		
		JLabel label_39 = new JLabel("");
		GridBagConstraints gbc_label_39 = new GridBagConstraints();
		gbc_label_39.fill = GridBagConstraints.BOTH;
		gbc_label_39.insets = new Insets(0, 0, 5, 5);
		gbc_label_39.gridx = 5;
		gbc_label_39.gridy = 6;
		panelAlgorithms.add(label_39, gbc_label_39);
		
		JLabel label_40 = new JLabel("");
		GridBagConstraints gbc_label_40 = new GridBagConstraints();
		gbc_label_40.fill = GridBagConstraints.BOTH;
		gbc_label_40.insets = new Insets(0, 0, 5, 0);
		gbc_label_40.gridx = 6;
		gbc_label_40.gridy = 6;
		panelAlgorithms.add(label_40, gbc_label_40);
		
		JLabel lblOptimisticUctucto = new JLabel("   Optimistic UCT (UCTO)");
		GridBagConstraints gbc_lblOptimisticUctucto = new GridBagConstraints();
		gbc_lblOptimisticUctucto.fill = GridBagConstraints.BOTH;
		gbc_lblOptimisticUctucto.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptimisticUctucto.gridx = 0;
		gbc_lblOptimisticUctucto.gridy = 7;
		panelAlgorithms.add(lblOptimisticUctucto, gbc_lblOptimisticUctucto);
		
		rdbtnUCTO = new JRadioButton("");
		GridBagConstraints gbc_rdbtnUCTO = new GridBagConstraints();
		gbc_rdbtnUCTO.fill = GridBagConstraints.BOTH;
		gbc_rdbtnUCTO.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnUCTO.gridx = 1;
		gbc_rdbtnUCTO.gridy = 7;
		panelAlgorithms.add(rdbtnUCTO, gbc_rdbtnUCTO);
		
		JLabel lblRollouts3 = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts3 = new GridBagConstraints();
		gbc_lblRollouts3.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts3.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts3.gridx = 2;
		gbc_lblRollouts3.gridy = 7;
		panelAlgorithms.add(lblRollouts3, gbc_lblRollouts3);
		
		textFieldUCTO = new JTextField();
		textFieldUCTO.setToolTipText("Pls enter positive integer value.");
		textFieldUCTO.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_UCTO, textFieldUCTO));
		textFieldUCTO.setText(prop.getProperty(PropKeys.ROLLOUTS_UCTO.name()));;
		textFieldUCTO.setColumns(10);
		GridBagConstraints gbc_textFieldUCTO = new GridBagConstraints();
		gbc_textFieldUCTO.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTO.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUCTO.gridx = 3;
		gbc_textFieldUCTO.gridy = 7;
		panelAlgorithms.add(textFieldUCTO, gbc_textFieldUCTO);
		
		JLabel label_50 = new JLabel("");
		GridBagConstraints gbc_label_50 = new GridBagConstraints();
		gbc_label_50.fill = GridBagConstraints.BOTH;
		gbc_label_50.insets = new Insets(0, 0, 5, 5);
		gbc_label_50.gridx = 4;
		gbc_label_50.gridy = 7;
		panelAlgorithms.add(label_50, gbc_label_50);
		
		JLabel lblAdditionalRolloutsm = new JLabel("   Additional Rollouts (M):");
		GridBagConstraints gbc_lblAdditionalRolloutsm = new GridBagConstraints();
		gbc_lblAdditionalRolloutsm.fill = GridBagConstraints.BOTH;
		gbc_lblAdditionalRolloutsm.insets = new Insets(0, 0, 5, 5);
		gbc_lblAdditionalRolloutsm.gridx = 5;
		gbc_lblAdditionalRolloutsm.gridy = 7;
		panelAlgorithms.add(lblAdditionalRolloutsm, gbc_lblAdditionalRolloutsm);
		
		textFieldUCTO_M = new JTextField();
		textFieldUCTO_M.setToolTipText("Pls enter positive integer value.");
		textFieldUCTO_M.addActionListener(new TextFieldIntegerListener(PropKeys.ADDITIONAL_ROLLOUTS_UCTO, textFieldUCTO_M));
		textFieldUCTO_M.setText(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO.name()));
		textFieldUCTO_M.setColumns(10);
		GridBagConstraints gbc_textFieldUCTO_M = new GridBagConstraints();
		gbc_textFieldUCTO_M.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTO_M.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUCTO_M.gridx = 6;
		gbc_textFieldUCTO_M.gridy = 7;
		panelAlgorithms.add(textFieldUCTO_M, gbc_textFieldUCTO_M);
		
		JLabel lblBlindUctuctb = new JLabel("   Optimistic UCT2 (UCTO2)");
		GridBagConstraints gbc_lblBlindUctuctb = new GridBagConstraints();
		gbc_lblBlindUctuctb.fill = GridBagConstraints.BOTH;
		gbc_lblBlindUctuctb.insets = new Insets(0, 0, 5, 5);
		gbc_lblBlindUctuctb.gridx = 0;
		gbc_lblBlindUctuctb.gridy = 8;
		panelAlgorithms.add(lblBlindUctuctb, gbc_lblBlindUctuctb);
		
		rdbtnUCTO2 = new JRadioButton("");
		GridBagConstraints gbc_rdbtnUCTB = new GridBagConstraints();
		gbc_rdbtnUCTB.fill = GridBagConstraints.BOTH;
		gbc_rdbtnUCTB.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnUCTB.gridx = 1;
		gbc_rdbtnUCTB.gridy = 8;
		panelAlgorithms.add(rdbtnUCTO2, gbc_rdbtnUCTB);
		
		JLabel lblRollouts2 = new JLabel("   Iterations:");
		GridBagConstraints gbc_lblRollouts2 = new GridBagConstraints();
		gbc_lblRollouts2.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts2.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts2.gridx = 2;
		gbc_lblRollouts2.gridy = 8;
		panelAlgorithms.add(lblRollouts2, gbc_lblRollouts2);
		
		textFieldUCTO2 = new JTextField();
		textFieldUCTO2.setToolTipText("Pls enter positive integer value.");
		textFieldUCTO2.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_UCTO2, textFieldUCTO2));
		textFieldUCTO2.setText(prop.getProperty(PropKeys.ROLLOUTS_UCTO2.name()));
		textFieldUCTO2.setColumns(10);
		GridBagConstraints gbc_textFieldUCTB = new GridBagConstraints();
		gbc_textFieldUCTB.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTB.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUCTB.gridx = 3;
		gbc_textFieldUCTB.gridy = 8;
		panelAlgorithms.add(textFieldUCTO2, gbc_textFieldUCTB);
		
		JLabel label_44 = new JLabel("");
		GridBagConstraints gbc_label_44 = new GridBagConstraints();
		gbc_label_44.fill = GridBagConstraints.BOTH;
		gbc_label_44.insets = new Insets(0, 0, 5, 5);
		gbc_label_44.gridx = 4;
		gbc_label_44.gridy = 8;
		panelAlgorithms.add(label_44, gbc_label_44);
		
		JLabel lblRolloutsm = new JLabel("   Rollouts (Nr):");
		GridBagConstraints gbc_lblRolloutsm = new GridBagConstraints();
		gbc_lblRolloutsm.anchor = GridBagConstraints.WEST;
		gbc_lblRolloutsm.fill = GridBagConstraints.VERTICAL;
		gbc_lblRolloutsm.insets = new Insets(0, 0, 5, 5);
		gbc_lblRolloutsm.gridx = 5;
		gbc_lblRolloutsm.gridy = 8;
		panelAlgorithms.add(lblRolloutsm, gbc_lblRolloutsm);
		
		textField_UCTO2_Nr = new JTextField();
		textField_UCTO2_Nr.addActionListener(new TextFieldIntegerListener(PropKeys.ADDITIONAL_ROLLOUTS_UCTO2, textField_UCTO2_Nr));
		textField_UCTO2_Nr.setToolTipText("Pls enter positive integer value.");
		textField_UCTO2_Nr.setText(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO2.name()));
		textField_UCTO2_Nr.setColumns(10);
		GridBagConstraints gbc_textField_UCTO2_Nr = new GridBagConstraints();
		gbc_textField_UCTO2_Nr.insets = new Insets(0, 0, 5, 0);
		gbc_textField_UCTO2_Nr.fill = GridBagConstraints.BOTH;
		gbc_textField_UCTO2_Nr.gridx = 6;
		gbc_textField_UCTO2_Nr.gridy = 8;
		panelAlgorithms.add(textField_UCTO2_Nr, gbc_textField_UCTO2_Nr);
		
		JLabel lblPruningUctuctp = new JLabel("   Pruning UCT (UCTP)");
		GridBagConstraints gbc_lblPruningUctuctp = new GridBagConstraints();
		gbc_lblPruningUctuctp.fill = GridBagConstraints.BOTH;
		gbc_lblPruningUctuctp.insets = new Insets(0, 0, 5, 5);
		gbc_lblPruningUctuctp.gridx = 0;
		gbc_lblPruningUctuctp.gridy = 9;
		panelAlgorithms.add(lblPruningUctuctp, gbc_lblPruningUctuctp);
		
		rdbtnUCTP = new JRadioButton("");
		GridBagConstraints gbc_rdbtnUCTP = new GridBagConstraints();
		gbc_rdbtnUCTP.fill = GridBagConstraints.BOTH;
		gbc_rdbtnUCTP.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnUCTP.gridx = 1;
		gbc_rdbtnUCTP.gridy = 9;
		panelAlgorithms.add(rdbtnUCTP, gbc_rdbtnUCTP);
		
		JLabel lblRollouts4 = new JLabel("   Iterations:");
		GridBagConstraints gbc_lblRollouts4 = new GridBagConstraints();
		gbc_lblRollouts4.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts4.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts4.gridx = 2;
		gbc_lblRollouts4.gridy = 9;
		panelAlgorithms.add(lblRollouts4, gbc_lblRollouts4);
		
		textFieldUCTP = new JTextField();
		textFieldUCTP.setToolTipText("Pls enter positive integer value.");
		textFieldUCTP.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_UCTP, textFieldUCTP));
		textFieldUCTP.setText(prop.getProperty(PropKeys.ROLLOUTS_UCTP.name()));
		textFieldUCTP.setColumns(10);
		GridBagConstraints gbc_textFieldUCTP = new GridBagConstraints();
		gbc_textFieldUCTP.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTP.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUCTP.gridx = 3;
		gbc_textFieldUCTP.gridy = 9;
		panelAlgorithms.add(textFieldUCTP, gbc_textFieldUCTP);
		
		JLabel label_54 = new JLabel("");
		GridBagConstraints gbc_label_54 = new GridBagConstraints();
		gbc_label_54.fill = GridBagConstraints.BOTH;
		gbc_label_54.insets = new Insets(0, 0, 5, 5);
		gbc_label_54.gridx = 4;
		gbc_label_54.gridy = 9;
		panelAlgorithms.add(label_54, gbc_label_54);
		
		JLabel lbAdditionalRolloutsUCTP = new JLabel("   Rollouts(Nr):");
		GridBagConstraints gbc_lbAdditionalRolloutsUCTP = new GridBagConstraints();
		gbc_lbAdditionalRolloutsUCTP.fill = GridBagConstraints.BOTH;
		gbc_lbAdditionalRolloutsUCTP.insets = new Insets(0, 0, 5, 5);
		gbc_lbAdditionalRolloutsUCTP.gridx = 5;
		gbc_lbAdditionalRolloutsUCTP.gridy = 9;
		panelAlgorithms.add(lbAdditionalRolloutsUCTP, gbc_lbAdditionalRolloutsUCTP);
		
		textFieldUCTP_Nr = new JTextField();
		textFieldUCTP_Nr.setToolTipText("Pls enter positive integer value.");
		textFieldUCTP_Nr.addActionListener(new TextFieldIntegerListener(PropKeys.ADDITIONAL_ROLLOUTS_UCTP, textFieldUCTP_Nr));
		textFieldUCTP_Nr.setText(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTP.name()));
		textFieldUCTP_Nr.setColumns(10);
		GridBagConstraints gbc_textFieldUCTP_Nr = new GridBagConstraints();
		gbc_textFieldUCTP_Nr.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTP_Nr.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUCTP_Nr.gridx = 6;
		gbc_textFieldUCTP_Nr.gridy = 9;
		panelAlgorithms.add(textFieldUCTP_Nr, gbc_textFieldUCTP_Nr);
		
		JButton btnSelectAll = new JButton("All");
		btnSelectAll.addActionListener(new ActionListener(){
			int counter = 0;
			public void actionPerformed(ActionEvent e) {
				boolean b = counter%2==0 ? true:false;
				selectAllAlgorithms(b);
				counter++;
			}
		});
		btnSelectAll.setHorizontalAlignment(SwingConstants.LEADING);
		btnSelectAll.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_btnSelectAll = new GridBagConstraints();
		gbc_btnSelectAll.insets = new Insets(0, 0, 5, 5);
		gbc_btnSelectAll.gridx = 1;
		gbc_btnSelectAll.gridy = 10;
		panelAlgorithms.add(btnSelectAll, gbc_btnSelectAll);
		final JLabel lblAgorithmsRun = new JLabel((lblAlgorithmsRunText).replace("x", prop.getProperty(PropKeys.ALGORITHMS_RUN.name())));
		GridBagConstraints gbc_lblAgorithmsRun = new GridBagConstraints();
		gbc_lblAgorithmsRun.fill = GridBagConstraints.BOTH;
		gbc_lblAgorithmsRun.insets = new Insets(0, 0, 5, 5);
		gbc_lblAgorithmsRun.gridx = 0;
		gbc_lblAgorithmsRun.gridy = 11;
		panelAlgorithms.add(lblAgorithmsRun, gbc_lblAgorithmsRun);
		
		JLabel label_58 = new JLabel("");
		GridBagConstraints gbc_label_58 = new GridBagConstraints();
		gbc_label_58.fill = GridBagConstraints.BOTH;
		gbc_label_58.insets = new Insets(0, 0, 5, 5);
		gbc_label_58.gridx = 1;
		gbc_label_58.gridy = 11;
		panelAlgorithms.add(label_58, gbc_label_58);
		
		algorithmsRun = new JTextField();
		algorithmsRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					String strValue = algorithmsRun.getText();
					int value = Integer.parseInt(strValue);
					if(value < 1){throw new NumberFormatException("negative values are not allowed");}
					prop.setProperty(PropKeys.ALGORITHMS_RUN.name(), strValue);
					if(value != 1){
						String lblText = "   Agorithms are going to be run x times.";
						lblAgorithmsRun.setText(lblText.replace("x", strValue));
					}else{
						lblAgorithmsRun.setText("   Agorithms are going to be run 1 time.");
					}
					saveProp();
				} catch(Exception e1){
					if(e1 instanceof NumberFormatException){
						lblAgorithmsRun.setText("Number of runs. Please enter positive integer.");
					}
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		});
		algorithmsRun.setToolTipText("Pls enter positive integer value.\r\nAfter click on Set button to confirm.");
		algorithmsRun.setText(prop.getProperty(PropKeys.ALGORITHMS_RUN.name()));
		algorithmsRun.setColumns(10);
		GridBagConstraints gbc_algorithmsRun = new GridBagConstraints();
		gbc_algorithmsRun.fill = GridBagConstraints.BOTH;
		gbc_algorithmsRun.insets = new Insets(0, 0, 5, 5);
		gbc_algorithmsRun.gridx = 2;
		gbc_algorithmsRun.gridy = 11;
		panelAlgorithms.add(algorithmsRun, gbc_algorithmsRun);
		
		JButton btnSet = new JButton("Set");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					String strValue = algorithmsRun.getText();
					int value = Integer.parseInt(strValue);
					if(value < 1){throw new NumberFormatException("negative values are not allowed");}
					prop.setProperty(PropKeys.ALGORITHMS_RUN.name(), strValue);
					if(value != 1){
						String lblText = "   Agorithms are going to be run x times.";
						lblAgorithmsRun.setText(lblText.replace("x", strValue));
					}else{
						lblAgorithmsRun.setText("   Agorithms are going to be run 1 time.");
					}
					saveProp();
				} catch(Exception e1){
					if(e1 instanceof NumberFormatException){
						lblAgorithmsRun.setText("Number of runs. Please enter positive integer.");
					}
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnSet = new GridBagConstraints();
		gbc_btnSet.fill = GridBagConstraints.BOTH;
		gbc_btnSet.insets = new Insets(0, 0, 5, 5);
		gbc_btnSet.gridx = 3;
		gbc_btnSet.gridy = 11;
		panelAlgorithms.add(btnSet, gbc_btnSet);
		
		JLabel label_61 = new JLabel("");
		GridBagConstraints gbc_label_61 = new GridBagConstraints();
		gbc_label_61.fill = GridBagConstraints.BOTH;
		gbc_label_61.insets = new Insets(0, 0, 5, 5);
		gbc_label_61.gridx = 5;
		gbc_label_61.gridy = 11;
		panelAlgorithms.add(label_61, gbc_label_61);
		
		JLabel label_62 = new JLabel("");
		GridBagConstraints gbc_label_62 = new GridBagConstraints();
		gbc_label_62.insets = new Insets(0, 0, 5, 0);
		gbc_label_62.fill = GridBagConstraints.BOTH;
		gbc_label_62.gridx = 6;
		gbc_label_62.gridy = 11;
		panelAlgorithms.add(label_62, gbc_label_62);
	}
	private void initStartPanel() {
		JButton openFilePathBtn = new JButton("Open File");
		openFilePathBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton open = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Open File");
				fc.setCurrentDirectory(new File(prop.getProperty(PropKeys.DEFAULT_SOURCE_FOLDER.name())));
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
					filePath.setText(fc.getSelectedFile().toString());
					prop.setProperty(PropKeys.SOURCE_FILE.name(), fc.getSelectedFile().toString());
					try {
						saveProp();
					} catch (Throwable err) {
						err.printStackTrace();
					}
				}
			}
		});
		
		JLabel lblNewLabel = new JLabel("Graph source path:");
		
		filePath = new JTextField();
		filePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.SOURCE_FILE.name(), filePath.getText());
				try {
					saveProp();
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
			
		});
		filePath.setText(prop.getProperty(PropKeys.SOURCE_FILE.name()));
		filePath.setColumns(10);
		
		JButton showGraphBtn = new JButton("Show Graph");
		showGraphBtn.setToolTipText("Show image of graph, if it do not show anything, file is invalid.\r\nVertexes are randomly placed if NODE_COORD_SECTION is missing\r\nin source file.");
		showGraphBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	try{
			    	ParsedDTO graphData = new BasicCtpParser().parseFile(prop.getProperty(PropKeys.SOURCE_FILE.name()));
			    	StochasticWeightedGraph g;
			    	g = new StochasticWeightedGraph(StochasticWeightedEdge.class, graphData);
			    	int xFrameSize = Integer.parseInt(prop.getProperty(PropKeys.VISUALISER_WIDTH.name()));
			    	int yFrameSize = Integer.parseInt(prop.getProperty(PropKeys.VISUALISER_HEIGHT.name()));
		    		GraphSwing.displayGraph(g, graphData.pointList, xFrameSize, yFrameSize);
		    		graphData.validateSize();
		    	}catch (Throwable e1){
		    		LOG.info("Loaded graph data are invalid");
		    		LOG.info(e1);
		    	}
		    	
			}
		});
		lblStatus = new JLabel("Status: idle");
		JButton btnRun = new JButton("Run");
		btnRun.setToolTipText("Starts calculation specified in Algorithms tab.\r\nMake sure valid graph data are available.");
		btnRun.setForeground(new Color(0, 128, 0));
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop = false;
	        	try{
	        		if(lblStatus.getText().contains("idle")){
	        			textAreaConsole.setText(null);
		        		LabelSwingWorker workerThread = new LabelSwingWorker(lblStatus);
						workerThread.execute();
	        		}else{
	        			LOG.info("There is already run in progress!");
	        		}
	        	}catch(Exception err){
	        		LOG.error(err);
	        	}
			}
		});
		
		textAreaConsole = new JTextArea();
		textAreaConsole.setFont(new Font("Courier New", Font.PLAIN, 12));
		StatusMessageAppender appender = new StatusMessageAppender(textAreaConsole);
		LogManager.getRootLogger().addAppender(appender);
		
		scrollConsole = new JScrollPane(textAreaConsole, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JLabel lblNewLabel_1 = new JLabel("Console Output");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		JButton btnStop = new JButton("Stop");
		btnStop.setToolTipText("Stops calculation, if at least one run was finished \r\nthen results will be available in Results tab.");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	    		String logMsg = "--- Calculations will be stopped before starting next run---";
	    		String logMsgSeparator = new String(new char[logMsg.length()]).replace('\0', '-');
	    		LOG.info(logMsgSeparator);
				LOG.info(logMsg);
				LOG.info(logMsgSeparator);
				stop = true;
				lblStatus.setText("Status: stopping in progress");
	    		ResultsTable.initTable();
	    		table = new JTable(ResultsTable.model);
			}
		});
		btnStop.setForeground(new Color(255, 0, 0));
		
		GroupLayout gl_panelStart = new GroupLayout(panelStart);
		gl_panelStart.setHorizontalGroup(
			gl_panelStart.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStart.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelStart.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollConsole, GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
						.addGroup(gl_panelStart.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(filePath, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(openFilePathBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(showGraphBtn)
							.addGap(16))
						.addGroup(gl_panelStart.createSequentialGroup()
							.addComponent(btnRun, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
							.addGap(160)
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panelStart.setVerticalGroup(
			gl_panelStart.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStart.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelStart.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(filePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(openFilePathBtn)
						.addComponent(showGraphBtn))
					.addGap(7)
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelStart.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelStart.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnRun, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollConsole, GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
					.addGap(28))
		);
		panelStart.setLayout(gl_panelStart);
	}
	private GroupLayout initForm() {
		frmCanadianTravellerProblem = new JFrame();
		trySetIcon();
		frmCanadianTravellerProblem.setTitle("Canadian Traveller Problem Application");
		frmCanadianTravellerProblem.setBounds(100, 100, 800, 522);
		frmCanadianTravellerProblem.setMinimumSize(new Dimension(800,522));
		frmCanadianTravellerProblem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(frmCanadianTravellerProblem.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
		);
		
		panelStart = new JPanel();
		tabbedPane.addTab("Start", null, panelStart, null);
		return groupLayout;
	}
	
	private void trySetIcon() {
		try{
		}catch(Exception e){
			LOG.info("loading icon from: " + CtpGui.class.getResource("/cz/vut/sf/resources/CanadianIcon.png") + " failed");
		}
		
	}
	protected static void selectAllAlgorithms(boolean b) {
		rdbtnSPP.setSelected(b);
		rdbtnGA.setSelected(b);
		rdbtnRA.setSelected(b);
		rdbtnCA.setSelected(b);
		rdbtnHOP.setSelected(b);
		rdbtnORO.setSelected(b);
		rdbtnUCTO2.setSelected(b);
		rdbtnUCTO.setSelected(b);
		rdbtnUCTP.setSelected(b);
	}
	public class TextFieldIntegerListener implements ActionListener{
		private final PropKeys property;
		private final JTextField textField;
		public TextFieldIntegerListener(PropKeys p, JTextField t){
			property = p;
			textField= t;
		}
		
		public void actionPerformed(ActionEvent e) {
			try {
				String strValue = textField.getText();
				int value = Integer.parseInt(strValue);
				if(value < 1){
					throw new NumberFormatException();
				}
				prop.setProperty(property.name(), textField.getText());
				textField.setText(strValue); 
				
				saveProp();
			} catch (Throwable err) {
				if(err instanceof NumberFormatException){
					textField.setText("Enter positive int");
					return;
				}
				err.printStackTrace();
			}
		}
		
	}
}
