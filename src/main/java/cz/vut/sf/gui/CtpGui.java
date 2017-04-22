package cz.vut.sf.gui;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.apache.log4j.LogManager;

import java.awt.Font;

import javax.swing.JTable;

import java.awt.ScrollPane;
import java.awt.SystemColor;
import java.awt.Color;

public class CtpGui extends CtpAppConstants{
	private JFrame frmCanadianTravellerProblem;
	private JTextField filePath;
	private JTextField txtVisualiserWidth;
	private JTextField txtVisualiserHeight;
	private JTextField algorithmsRun;
	public static JRadioButton rdbtnInfo;
	public static JRadioButton rdbtnSPP;
	public static JRadioButton rdbtnGA;
	public static JRadioButton rdbtnRA;
	public static JRadioButton rdbtnCA;
	public static JRadioButton rdbtnHOP;
	public static JRadioButton rdbtnORO;
	public static JRadioButton rdbtnUCTB;
	public static JRadioButton rdbtnUCTO;
	public static JRadioButton rdbtnUCTP;
	public static JLabel lblStatus;
	private JTextField textFieldHOP;
	private JTextField textFieldORO;
	private JTextField textFieldUCTB;
	private JTextField textFieldUCTO;
	private JTextField textFieldUCTP;
	private JTextField textFieldUCTO_M;
	private JTextField textFieldUCTP_Ni;
	private JTextArea textAreaConsole;
	private JScrollPane scrollConsole;
	private JScrollPane scrollTable;
	private JTable table;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		FileOutputStream fos = null;
		try{
			loadProperties();
			if(!checkProperties()){
				fos = new FileOutputStream(configFile);
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
				prop.setProperty(PropKeys.ITERATIONS_UCTP.name(), "100");
				prop.setProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO.name(), "20");
				prop.store(fos, "Application Settings");
				fos.close();
			}
		}catch(Exception e){
			
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CtpGui window = new CtpGui();
					window.frmCanadianTravellerProblem.setVisible(true);
					window.frmCanadianTravellerProblem.pack();
				} catch (Exception e) {
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
	
	public static void loadProperties() throws IOException{
		FileInputStream fin = new FileInputStream(configFile);
		prop.load(fin);
		fin.close();
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
		if(rdbtnUCTB.isSelected()){result.add(AlgNames.UCTB);}
		if(rdbtnUCTO.isSelected()){result.add(AlgNames.UCTO);}
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
		frmCanadianTravellerProblem = new JFrame();
		frmCanadianTravellerProblem.setTitle("Canadian Traveller Problem Application");
		frmCanadianTravellerProblem.setBounds(100, 100, 800, 522);
		frmCanadianTravellerProblem.setMinimumSize(new Dimension(800,522));
		frmCanadianTravellerProblem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(frmCanadianTravellerProblem.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
		);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Start", null, panel, null);
		
		JButton openFilePathBtn = new JButton("Open File");
		openFilePathBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton open = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("OpenFile");
				fc.setCurrentDirectory(new File(System.getProperty("user.dir") + SEPARATOR + "src" + SEPARATOR + "main" + SEPARATOR + "resources" ));
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
		filePath.setText(prop.getProperty(PropKeys.SOURCE_FILE.name()));
		filePath.setColumns(10);
		
		JButton showGraphBtn = new JButton("Show Graph");
		showGraphBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	ParsedDTO graphData = new BasicCtpParser().parseFile(prop.getProperty(PropKeys.SOURCE_FILE.name()));
		    	StochasticWeightedGraph g;
		    	g = new StochasticWeightedGraph(StochasticWeightedEdge.class, graphData);
		    	GraphSwing.displayGraph(g, graphData.pointList);
			}
		});
		lblStatus = new JLabel("Status: idle");
		JButton btnRun = new JButton("Run");
		btnRun.setForeground(new Color(0, 128, 0));
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop = false;
	        	try{
	        		textAreaConsole.setText(null);
	        		LabelSwingWorker workerThread = new LabelSwingWorker(lblStatus);
					workerThread.execute();
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
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	    		String logMsg = "--- Calculations will be stopped before starting next run---";
	    		String logMsgSeparator = new String(new char[logMsg.length()]).replace('\0', '-');
	    		LOG.info(logMsgSeparator);
				LOG.info(logMsg);
				LOG.info(logMsgSeparator);
				stop = true;
			}
		});
		btnStop.setForeground(new Color(255, 0, 0));
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollConsole, GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
						.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(filePath, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(openFilePathBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(showGraphBtn)
							.addGap(16))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(btnRun, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
							.addGap(160)
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(filePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(openFilePathBtn)
						.addComponent(showGraphBtn))
					.addGap(7)
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnRun, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnStop, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollConsole, GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
					.addGap(28))
		);
		panel.setLayout(gl_panel);
		
		ButtonGroup bgLogLevel = new ButtonGroup(); 
		
		final String lblAlgorithmsRunText = "   Agorithms are going to be run x time(s).";
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Algorithms", null, panel_1, null);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{175, 44, 79, 105, 20, 138, 105, 0};
		gbl_panel_1.rowHeights = new int[]{33, 38, 38, 38, 38, 38, 38, 38, 38, 38, 0, 38, 12, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblAlgorithms = new JLabel("Algorithms");
		lblAlgorithms.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblAlgorithms.setBackground(UIManager.getColor("Button.shadow"));
		lblAlgorithms.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_lblAlgorithms = new GridBagConstraints();
		gbc_lblAlgorithms.fill = GridBagConstraints.VERTICAL;
		gbc_lblAlgorithms.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlgorithms.gridx = 0;
		gbc_lblAlgorithms.gridy = 0;
		panel_1.add(lblAlgorithms, gbc_lblAlgorithms);
		
		JLabel lblDijkstraShortestPath = new JLabel("   Dijkstra shortest path");
		GridBagConstraints gbc_lblDijkstraShortestPath = new GridBagConstraints();
		gbc_lblDijkstraShortestPath.fill = GridBagConstraints.BOTH;
		gbc_lblDijkstraShortestPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblDijkstraShortestPath.gridx = 0;
		gbc_lblDijkstraShortestPath.gridy = 1;
		panel_1.add(lblDijkstraShortestPath, gbc_lblDijkstraShortestPath);
		
		rdbtnSPP = new JRadioButton("");
		rdbtnSPP.setSelected(true);
		GridBagConstraints gbc_rdbtnSPP = new GridBagConstraints();
		gbc_rdbtnSPP.fill = GridBagConstraints.BOTH;
		gbc_rdbtnSPP.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnSPP.gridx = 1;
		gbc_rdbtnSPP.gridy = 1;
		panel_1.add(rdbtnSPP, gbc_rdbtnSPP);
		
		JLabel label = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 1;
		panel_1.add(label, gbc_label);
		
		JLabel label_1 = new JLabel("");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.fill = GridBagConstraints.BOTH;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 3;
		gbc_label_1.gridy = 1;
		panel_1.add(label_1, gbc_label_1);
		
		JLabel label_2 = new JLabel("");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.fill = GridBagConstraints.BOTH;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 4;
		gbc_label_2.gridy = 1;
		panel_1.add(label_2, gbc_label_2);
		
		JLabel label_3 = new JLabel("");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.fill = GridBagConstraints.BOTH;
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 5;
		gbc_label_3.gridy = 1;
		panel_1.add(label_3, gbc_label_3);
		
		JLabel label_4 = new JLabel("");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.fill = GridBagConstraints.BOTH;
		gbc_label_4.insets = new Insets(0, 0, 5, 0);
		gbc_label_4.gridx = 6;
		gbc_label_4.gridy = 1;
		panel_1.add(label_4, gbc_label_4);
		
		JLabel lblGreedyAlgorithm = new JLabel("   Greedy algorithm");
		GridBagConstraints gbc_lblGreedyAlgorithm = new GridBagConstraints();
		gbc_lblGreedyAlgorithm.fill = GridBagConstraints.BOTH;
		gbc_lblGreedyAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblGreedyAlgorithm.gridx = 0;
		gbc_lblGreedyAlgorithm.gridy = 2;
		panel_1.add(lblGreedyAlgorithm, gbc_lblGreedyAlgorithm);
		
		rdbtnGA = new JRadioButton("");
		GridBagConstraints gbc_rdbtnGA = new GridBagConstraints();
		gbc_rdbtnGA.fill = GridBagConstraints.BOTH;
		gbc_rdbtnGA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnGA.gridx = 1;
		gbc_rdbtnGA.gridy = 2;
		panel_1.add(rdbtnGA, gbc_rdbtnGA);
		
		JLabel label_8 = new JLabel("");
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.fill = GridBagConstraints.BOTH;
		gbc_label_8.insets = new Insets(0, 0, 5, 5);
		gbc_label_8.gridx = 2;
		gbc_label_8.gridy = 2;
		panel_1.add(label_8, gbc_label_8);
		
		JLabel label_9 = new JLabel("");
		GridBagConstraints gbc_label_9 = new GridBagConstraints();
		gbc_label_9.fill = GridBagConstraints.BOTH;
		gbc_label_9.insets = new Insets(0, 0, 5, 5);
		gbc_label_9.gridx = 3;
		gbc_label_9.gridy = 2;
		panel_1.add(label_9, gbc_label_9);
		
		JLabel label_10 = new JLabel("");
		GridBagConstraints gbc_label_10 = new GridBagConstraints();
		gbc_label_10.fill = GridBagConstraints.BOTH;
		gbc_label_10.insets = new Insets(0, 0, 5, 5);
		gbc_label_10.gridx = 4;
		gbc_label_10.gridy = 2;
		panel_1.add(label_10, gbc_label_10);
		
		JLabel label_11 = new JLabel("");
		GridBagConstraints gbc_label_11 = new GridBagConstraints();
		gbc_label_11.fill = GridBagConstraints.BOTH;
		gbc_label_11.insets = new Insets(0, 0, 5, 5);
		gbc_label_11.gridx = 5;
		gbc_label_11.gridy = 2;
		panel_1.add(label_11, gbc_label_11);
		
		JLabel label_12 = new JLabel("");
		GridBagConstraints gbc_label_12 = new GridBagConstraints();
		gbc_label_12.fill = GridBagConstraints.BOTH;
		gbc_label_12.insets = new Insets(0, 0, 5, 0);
		gbc_label_12.gridx = 6;
		gbc_label_12.gridy = 2;
		panel_1.add(label_12, gbc_label_12);
		
		JLabel lblRepo = new JLabel("   Reposition algorithm");
		GridBagConstraints gbc_lblRepo = new GridBagConstraints();
		gbc_lblRepo.fill = GridBagConstraints.BOTH;
		gbc_lblRepo.insets = new Insets(0, 0, 5, 5);
		gbc_lblRepo.gridx = 0;
		gbc_lblRepo.gridy = 3;
		panel_1.add(lblRepo, gbc_lblRepo);
		
		rdbtnRA = new JRadioButton("");
		GridBagConstraints gbc_rdbtnRA = new GridBagConstraints();
		gbc_rdbtnRA.fill = GridBagConstraints.BOTH;
		gbc_rdbtnRA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnRA.gridx = 1;
		gbc_rdbtnRA.gridy = 3;
		panel_1.add(rdbtnRA, gbc_rdbtnRA);
		
		JLabel label_16 = new JLabel("");
		GridBagConstraints gbc_label_16 = new GridBagConstraints();
		gbc_label_16.fill = GridBagConstraints.BOTH;
		gbc_label_16.insets = new Insets(0, 0, 5, 5);
		gbc_label_16.gridx = 2;
		gbc_label_16.gridy = 3;
		panel_1.add(label_16, gbc_label_16);
		
		JLabel label_17 = new JLabel("");
		GridBagConstraints gbc_label_17 = new GridBagConstraints();
		gbc_label_17.fill = GridBagConstraints.BOTH;
		gbc_label_17.insets = new Insets(0, 0, 5, 5);
		gbc_label_17.gridx = 3;
		gbc_label_17.gridy = 3;
		panel_1.add(label_17, gbc_label_17);
		
		JLabel label_18 = new JLabel("");
		GridBagConstraints gbc_label_18 = new GridBagConstraints();
		gbc_label_18.fill = GridBagConstraints.BOTH;
		gbc_label_18.insets = new Insets(0, 0, 5, 5);
		gbc_label_18.gridx = 4;
		gbc_label_18.gridy = 3;
		panel_1.add(label_18, gbc_label_18);
		
		JLabel label_19 = new JLabel("");
		GridBagConstraints gbc_label_19 = new GridBagConstraints();
		gbc_label_19.fill = GridBagConstraints.BOTH;
		gbc_label_19.insets = new Insets(0, 0, 5, 5);
		gbc_label_19.gridx = 5;
		gbc_label_19.gridy = 3;
		panel_1.add(label_19, gbc_label_19);
		
		JLabel label_20 = new JLabel("");
		GridBagConstraints gbc_label_20 = new GridBagConstraints();
		gbc_label_20.fill = GridBagConstraints.BOTH;
		gbc_label_20.insets = new Insets(0, 0, 5, 0);
		gbc_label_20.gridx = 6;
		gbc_label_20.gridy = 3;
		panel_1.add(label_20, gbc_label_20);
		
		JLabel lblComparisionAlgorithm = new JLabel("   Comparision algorithm");
		GridBagConstraints gbc_lblComparisionAlgorithm = new GridBagConstraints();
		gbc_lblComparisionAlgorithm.fill = GridBagConstraints.BOTH;
		gbc_lblComparisionAlgorithm.insets = new Insets(0, 0, 5, 5);
		gbc_lblComparisionAlgorithm.gridx = 0;
		gbc_lblComparisionAlgorithm.gridy = 4;
		panel_1.add(lblComparisionAlgorithm, gbc_lblComparisionAlgorithm);
		
		rdbtnCA = new JRadioButton("");
		GridBagConstraints gbc_rdbtnCA = new GridBagConstraints();
		gbc_rdbtnCA.fill = GridBagConstraints.BOTH;
		gbc_rdbtnCA.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnCA.gridx = 1;
		gbc_rdbtnCA.gridy = 4;
		panel_1.add(rdbtnCA, gbc_rdbtnCA);
		
		JLabel label_24 = new JLabel("");
		GridBagConstraints gbc_label_24 = new GridBagConstraints();
		gbc_label_24.fill = GridBagConstraints.BOTH;
		gbc_label_24.insets = new Insets(0, 0, 5, 5);
		gbc_label_24.gridx = 2;
		gbc_label_24.gridy = 4;
		panel_1.add(label_24, gbc_label_24);
		
		JLabel label_25 = new JLabel("");
		GridBagConstraints gbc_label_25 = new GridBagConstraints();
		gbc_label_25.fill = GridBagConstraints.BOTH;
		gbc_label_25.insets = new Insets(0, 0, 5, 5);
		gbc_label_25.gridx = 3;
		gbc_label_25.gridy = 4;
		panel_1.add(label_25, gbc_label_25);
		
		JLabel label_26 = new JLabel("");
		GridBagConstraints gbc_label_26 = new GridBagConstraints();
		gbc_label_26.fill = GridBagConstraints.BOTH;
		gbc_label_26.insets = new Insets(0, 0, 5, 5);
		gbc_label_26.gridx = 4;
		gbc_label_26.gridy = 4;
		panel_1.add(label_26, gbc_label_26);
		
		JLabel label_27 = new JLabel("");
		GridBagConstraints gbc_label_27 = new GridBagConstraints();
		gbc_label_27.fill = GridBagConstraints.BOTH;
		gbc_label_27.insets = new Insets(0, 0, 5, 5);
		gbc_label_27.gridx = 5;
		gbc_label_27.gridy = 4;
		panel_1.add(label_27, gbc_label_27);
		
		JLabel label_28 = new JLabel("");
		GridBagConstraints gbc_label_28 = new GridBagConstraints();
		gbc_label_28.fill = GridBagConstraints.BOTH;
		gbc_label_28.insets = new Insets(0, 0, 5, 0);
		gbc_label_28.gridx = 6;
		gbc_label_28.gridy = 4;
		panel_1.add(label_28, gbc_label_28);
		
		JLabel lblHindsightOptimizationhop = new JLabel("   Hindsight Optimization (HOP)");
		GridBagConstraints gbc_lblHindsightOptimizationhop = new GridBagConstraints();
		gbc_lblHindsightOptimizationhop.fill = GridBagConstraints.BOTH;
		gbc_lblHindsightOptimizationhop.insets = new Insets(0, 0, 5, 5);
		gbc_lblHindsightOptimizationhop.gridx = 0;
		gbc_lblHindsightOptimizationhop.gridy = 5;
		panel_1.add(lblHindsightOptimizationhop, gbc_lblHindsightOptimizationhop);
		
		rdbtnHOP = new JRadioButton("");
		GridBagConstraints gbc_rdbtnHOP = new GridBagConstraints();
		gbc_rdbtnHOP.fill = GridBagConstraints.BOTH;
		gbc_rdbtnHOP.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnHOP.gridx = 1;
		gbc_rdbtnHOP.gridy = 5;
		panel_1.add(rdbtnHOP, gbc_rdbtnHOP);
		
		JLabel lblRollouts = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts = new GridBagConstraints();
		gbc_lblRollouts.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts.gridx = 2;
		gbc_lblRollouts.gridy = 5;
		panel_1.add(lblRollouts, gbc_lblRollouts);
		
		textFieldHOP = new JTextField();
		textFieldHOP.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_HOP, textFieldHOP));
		
		textFieldHOP.setText(prop.getProperty(PropKeys.ROLLOUTS_HOP.name()));
		textFieldHOP.setColumns(10);
		GridBagConstraints gbc_textFieldHOP = new GridBagConstraints();
		gbc_textFieldHOP.fill = GridBagConstraints.BOTH;
		gbc_textFieldHOP.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldHOP.gridx = 3;
		gbc_textFieldHOP.gridy = 5;
		panel_1.add(textFieldHOP, gbc_textFieldHOP);
		
		JLabel label_32 = new JLabel("");
		GridBagConstraints gbc_label_32 = new GridBagConstraints();
		gbc_label_32.fill = GridBagConstraints.BOTH;
		gbc_label_32.insets = new Insets(0, 0, 5, 5);
		gbc_label_32.gridx = 4;
		gbc_label_32.gridy = 5;
		panel_1.add(label_32, gbc_label_32);
		
		JLabel label_33 = new JLabel("");
		GridBagConstraints gbc_label_33 = new GridBagConstraints();
		gbc_label_33.fill = GridBagConstraints.BOTH;
		gbc_label_33.insets = new Insets(0, 0, 5, 5);
		gbc_label_33.gridx = 5;
		gbc_label_33.gridy = 5;
		panel_1.add(label_33, gbc_label_33);
		
		JLabel label_34 = new JLabel("");
		GridBagConstraints gbc_label_34 = new GridBagConstraints();
		gbc_label_34.fill = GridBagConstraints.BOTH;
		gbc_label_34.insets = new Insets(0, 0, 5, 0);
		gbc_label_34.gridx = 6;
		gbc_label_34.gridy = 5;
		panel_1.add(label_34, gbc_label_34);
		
		JLabel lblOptimisticRolloutoro = new JLabel("   Optimistic Rollout (ORO)");
		GridBagConstraints gbc_lblOptimisticRolloutoro = new GridBagConstraints();
		gbc_lblOptimisticRolloutoro.fill = GridBagConstraints.BOTH;
		gbc_lblOptimisticRolloutoro.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptimisticRolloutoro.gridx = 0;
		gbc_lblOptimisticRolloutoro.gridy = 6;
		panel_1.add(lblOptimisticRolloutoro, gbc_lblOptimisticRolloutoro);
		
		rdbtnORO = new JRadioButton("");
		GridBagConstraints gbc_rdbtnORO = new GridBagConstraints();
		gbc_rdbtnORO.fill = GridBagConstraints.BOTH;
		gbc_rdbtnORO.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnORO.gridx = 1;
		gbc_rdbtnORO.gridy = 6;
		panel_1.add(rdbtnORO, gbc_rdbtnORO);
		
		JLabel lblRollouts1 = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts1 = new GridBagConstraints();
		gbc_lblRollouts1.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts1.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts1.gridx = 2;
		gbc_lblRollouts1.gridy = 6;
		panel_1.add(lblRollouts1, gbc_lblRollouts1);
		
		textFieldORO = new JTextField();
		textFieldORO.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_ORO, textFieldORO));
		textFieldORO.setText(prop.getProperty(PropKeys.ROLLOUTS_ORO.name()));
		textFieldORO.setColumns(10);
		GridBagConstraints gbc_textFieldORO = new GridBagConstraints();
		gbc_textFieldORO.fill = GridBagConstraints.BOTH;
		gbc_textFieldORO.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldORO.gridx = 3;
		gbc_textFieldORO.gridy = 6;
		panel_1.add(textFieldORO, gbc_textFieldORO);
		
		JLabel label_38 = new JLabel("");
		GridBagConstraints gbc_label_38 = new GridBagConstraints();
		gbc_label_38.fill = GridBagConstraints.BOTH;
		gbc_label_38.insets = new Insets(0, 0, 5, 5);
		gbc_label_38.gridx = 4;
		gbc_label_38.gridy = 6;
		panel_1.add(label_38, gbc_label_38);
		
		JLabel label_39 = new JLabel("");
		GridBagConstraints gbc_label_39 = new GridBagConstraints();
		gbc_label_39.fill = GridBagConstraints.BOTH;
		gbc_label_39.insets = new Insets(0, 0, 5, 5);
		gbc_label_39.gridx = 5;
		gbc_label_39.gridy = 6;
		panel_1.add(label_39, gbc_label_39);
		
		JLabel label_40 = new JLabel("");
		GridBagConstraints gbc_label_40 = new GridBagConstraints();
		gbc_label_40.fill = GridBagConstraints.BOTH;
		gbc_label_40.insets = new Insets(0, 0, 5, 0);
		gbc_label_40.gridx = 6;
		gbc_label_40.gridy = 6;
		panel_1.add(label_40, gbc_label_40);
		
		JLabel lblBlindUctuctb = new JLabel("   Blind UCT (UCTB)");
		GridBagConstraints gbc_lblBlindUctuctb = new GridBagConstraints();
		gbc_lblBlindUctuctb.fill = GridBagConstraints.BOTH;
		gbc_lblBlindUctuctb.insets = new Insets(0, 0, 5, 5);
		gbc_lblBlindUctuctb.gridx = 0;
		gbc_lblBlindUctuctb.gridy = 7;
		panel_1.add(lblBlindUctuctb, gbc_lblBlindUctuctb);
		
		rdbtnUCTB = new JRadioButton("");
		GridBagConstraints gbc_rdbtnUCTB = new GridBagConstraints();
		gbc_rdbtnUCTB.fill = GridBagConstraints.BOTH;
		gbc_rdbtnUCTB.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnUCTB.gridx = 1;
		gbc_rdbtnUCTB.gridy = 7;
		panel_1.add(rdbtnUCTB, gbc_rdbtnUCTB);
		
		JLabel lblRollouts2 = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts2 = new GridBagConstraints();
		gbc_lblRollouts2.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts2.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts2.gridx = 2;
		gbc_lblRollouts2.gridy = 7;
		panel_1.add(lblRollouts2, gbc_lblRollouts2);
		
		textFieldUCTB = new JTextField();
		textFieldUCTB.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_UCTB, textFieldUCTB));
		textFieldUCTB.setText(prop.getProperty(PropKeys.ROLLOUTS_UCTB.name()));
		textFieldUCTB.setColumns(10);
		GridBagConstraints gbc_textFieldUCTB = new GridBagConstraints();
		gbc_textFieldUCTB.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTB.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUCTB.gridx = 3;
		gbc_textFieldUCTB.gridy = 7;
		panel_1.add(textFieldUCTB, gbc_textFieldUCTB);
		
		JLabel label_44 = new JLabel("");
		GridBagConstraints gbc_label_44 = new GridBagConstraints();
		gbc_label_44.fill = GridBagConstraints.BOTH;
		gbc_label_44.insets = new Insets(0, 0, 5, 5);
		gbc_label_44.gridx = 4;
		gbc_label_44.gridy = 7;
		panel_1.add(label_44, gbc_label_44);
		
		JLabel label_45 = new JLabel("");
		GridBagConstraints gbc_label_45 = new GridBagConstraints();
		gbc_label_45.fill = GridBagConstraints.BOTH;
		gbc_label_45.insets = new Insets(0, 0, 5, 5);
		gbc_label_45.gridx = 5;
		gbc_label_45.gridy = 7;
		panel_1.add(label_45, gbc_label_45);
		
		JLabel label_46 = new JLabel("");
		GridBagConstraints gbc_label_46 = new GridBagConstraints();
		gbc_label_46.fill = GridBagConstraints.BOTH;
		gbc_label_46.insets = new Insets(0, 0, 5, 0);
		gbc_label_46.gridx = 6;
		gbc_label_46.gridy = 7;
		panel_1.add(label_46, gbc_label_46);
		
		JLabel lblOptimisticUctucto = new JLabel("   Optimistic UCT (UCTO)");
		GridBagConstraints gbc_lblOptimisticUctucto = new GridBagConstraints();
		gbc_lblOptimisticUctucto.fill = GridBagConstraints.BOTH;
		gbc_lblOptimisticUctucto.insets = new Insets(0, 0, 5, 5);
		gbc_lblOptimisticUctucto.gridx = 0;
		gbc_lblOptimisticUctucto.gridy = 8;
		panel_1.add(lblOptimisticUctucto, gbc_lblOptimisticUctucto);
		
		rdbtnUCTO = new JRadioButton("");
		GridBagConstraints gbc_rdbtnUCTO = new GridBagConstraints();
		gbc_rdbtnUCTO.fill = GridBagConstraints.BOTH;
		gbc_rdbtnUCTO.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnUCTO.gridx = 1;
		gbc_rdbtnUCTO.gridy = 8;
		panel_1.add(rdbtnUCTO, gbc_rdbtnUCTO);
		
		JLabel lblRollouts3 = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts3 = new GridBagConstraints();
		gbc_lblRollouts3.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts3.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts3.gridx = 2;
		gbc_lblRollouts3.gridy = 8;
		panel_1.add(lblRollouts3, gbc_lblRollouts3);
		
		textFieldUCTO = new JTextField();
		textFieldUCTO.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_UCTO, textFieldUCTO));
		textFieldUCTO.setText(prop.getProperty(PropKeys.ROLLOUTS_UCTO.name()));;
		textFieldUCTO.setColumns(10);
		GridBagConstraints gbc_textFieldUCTO = new GridBagConstraints();
		gbc_textFieldUCTO.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTO.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUCTO.gridx = 3;
		gbc_textFieldUCTO.gridy = 8;
		panel_1.add(textFieldUCTO, gbc_textFieldUCTO);
		
		JLabel label_50 = new JLabel("");
		GridBagConstraints gbc_label_50 = new GridBagConstraints();
		gbc_label_50.fill = GridBagConstraints.BOTH;
		gbc_label_50.insets = new Insets(0, 0, 5, 5);
		gbc_label_50.gridx = 4;
		gbc_label_50.gridy = 8;
		panel_1.add(label_50, gbc_label_50);
		
		JLabel lblAdditionalRolloutsm = new JLabel("   Additional Rollouts (M):");
		GridBagConstraints gbc_lblAdditionalRolloutsm = new GridBagConstraints();
		gbc_lblAdditionalRolloutsm.fill = GridBagConstraints.BOTH;
		gbc_lblAdditionalRolloutsm.insets = new Insets(0, 0, 5, 5);
		gbc_lblAdditionalRolloutsm.gridx = 5;
		gbc_lblAdditionalRolloutsm.gridy = 8;
		panel_1.add(lblAdditionalRolloutsm, gbc_lblAdditionalRolloutsm);
		
		textFieldUCTO_M = new JTextField();
		textFieldUCTO_M.addActionListener(new TextFieldIntegerListener(PropKeys.ADDITIONAL_ROLLOUTS_UCTO, textFieldUCTO_M));
		textFieldUCTO_M.setText(prop.getProperty(PropKeys.ADDITIONAL_ROLLOUTS_UCTO.name()));
		textFieldUCTO_M.setColumns(10);
		GridBagConstraints gbc_textFieldUCTO_M = new GridBagConstraints();
		gbc_textFieldUCTO_M.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTO_M.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUCTO_M.gridx = 6;
		gbc_textFieldUCTO_M.gridy = 8;
		panel_1.add(textFieldUCTO_M, gbc_textFieldUCTO_M);
		
		JLabel lblPruningUctuctp = new JLabel("   Pruning UCT (UCTP)");
		GridBagConstraints gbc_lblPruningUctuctp = new GridBagConstraints();
		gbc_lblPruningUctuctp.fill = GridBagConstraints.BOTH;
		gbc_lblPruningUctuctp.insets = new Insets(0, 0, 5, 5);
		gbc_lblPruningUctuctp.gridx = 0;
		gbc_lblPruningUctuctp.gridy = 9;
		panel_1.add(lblPruningUctuctp, gbc_lblPruningUctuctp);
		
		rdbtnUCTP = new JRadioButton("");
		GridBagConstraints gbc_rdbtnUCTP = new GridBagConstraints();
		gbc_rdbtnUCTP.fill = GridBagConstraints.BOTH;
		gbc_rdbtnUCTP.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnUCTP.gridx = 1;
		gbc_rdbtnUCTP.gridy = 9;
		panel_1.add(rdbtnUCTP, gbc_rdbtnUCTP);
		
		JLabel lblRollouts4 = new JLabel("   Rollouts:");
		GridBagConstraints gbc_lblRollouts4 = new GridBagConstraints();
		gbc_lblRollouts4.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts4.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts4.gridx = 2;
		gbc_lblRollouts4.gridy = 9;
		panel_1.add(lblRollouts4, gbc_lblRollouts4);
		
		textFieldUCTP = new JTextField();
		textFieldUCTP.addActionListener(new TextFieldIntegerListener(PropKeys.ROLLOUTS_UCTP, textFieldUCTP));
		textFieldUCTP.setText(prop.getProperty(PropKeys.ROLLOUTS_UCTP.name()));
		textFieldUCTP.setColumns(10);
		GridBagConstraints gbc_textFieldUCTP = new GridBagConstraints();
		gbc_textFieldUCTP.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTP.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUCTP.gridx = 3;
		gbc_textFieldUCTP.gridy = 9;
		panel_1.add(textFieldUCTP, gbc_textFieldUCTP);
		
		JLabel label_54 = new JLabel("");
		GridBagConstraints gbc_label_54 = new GridBagConstraints();
		gbc_label_54.fill = GridBagConstraints.BOTH;
		gbc_label_54.insets = new Insets(0, 0, 5, 5);
		gbc_label_54.gridx = 4;
		gbc_label_54.gridy = 9;
		panel_1.add(label_54, gbc_label_54);
		
		JLabel lblIterationsNi = new JLabel("   Iterations (Ni):");
		GridBagConstraints gbc_lblIterationsNi = new GridBagConstraints();
		gbc_lblIterationsNi.fill = GridBagConstraints.BOTH;
		gbc_lblIterationsNi.insets = new Insets(0, 0, 5, 5);
		gbc_lblIterationsNi.gridx = 5;
		gbc_lblIterationsNi.gridy = 9;
		panel_1.add(lblIterationsNi, gbc_lblIterationsNi);
		
		textFieldUCTP_Ni = new JTextField();
		textFieldUCTP_Ni.addActionListener(new TextFieldIntegerListener(PropKeys.ITERATIONS_UCTP, textFieldUCTP_Ni));
		textFieldUCTP_Ni.setText(prop.getProperty(PropKeys.ITERATIONS_UCTP.name()));
		textFieldUCTP_Ni.setColumns(10);
		GridBagConstraints gbc_textFieldUCTP_Ni = new GridBagConstraints();
		gbc_textFieldUCTP_Ni.fill = GridBagConstraints.BOTH;
		gbc_textFieldUCTP_Ni.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldUCTP_Ni.gridx = 6;
		gbc_textFieldUCTP_Ni.gridy = 9;
		panel_1.add(textFieldUCTP_Ni, gbc_textFieldUCTP_Ni);
		
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
		panel_1.add(btnSelectAll, gbc_btnSelectAll);
		final JLabel lblAgorithmsRun = new JLabel((lblAlgorithmsRunText).replace("x", prop.getProperty(PropKeys.ALGORITHMS_RUN.name())));
		GridBagConstraints gbc_lblAgorithmsRun = new GridBagConstraints();
		gbc_lblAgorithmsRun.fill = GridBagConstraints.BOTH;
		gbc_lblAgorithmsRun.insets = new Insets(0, 0, 5, 5);
		gbc_lblAgorithmsRun.gridx = 0;
		gbc_lblAgorithmsRun.gridy = 11;
		panel_1.add(lblAgorithmsRun, gbc_lblAgorithmsRun);
		
		JLabel label_58 = new JLabel("");
		GridBagConstraints gbc_label_58 = new GridBagConstraints();
		gbc_label_58.fill = GridBagConstraints.BOTH;
		gbc_label_58.insets = new Insets(0, 0, 5, 5);
		gbc_label_58.gridx = 1;
		gbc_label_58.gridy = 11;
		panel_1.add(label_58, gbc_label_58);
		
		algorithmsRun = new JTextField();
		algorithmsRun.setText(prop.getProperty(PropKeys.ALGORITHMS_RUN.name()));
		algorithmsRun.setColumns(10);
		GridBagConstraints gbc_algorithmsRun = new GridBagConstraints();
		gbc_algorithmsRun.fill = GridBagConstraints.BOTH;
		gbc_algorithmsRun.insets = new Insets(0, 0, 5, 5);
		gbc_algorithmsRun.gridx = 2;
		gbc_algorithmsRun.gridy = 11;
		panel_1.add(algorithmsRun, gbc_algorithmsRun);
		
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
		panel_1.add(btnSet, gbc_btnSet);
		
		JLabel label_61 = new JLabel("");
		GridBagConstraints gbc_label_61 = new GridBagConstraints();
		gbc_label_61.fill = GridBagConstraints.BOTH;
		gbc_label_61.insets = new Insets(0, 0, 5, 5);
		gbc_label_61.gridx = 5;
		gbc_label_61.gridy = 11;
		panel_1.add(label_61, gbc_label_61);
		
		JLabel label_62 = new JLabel("");
		GridBagConstraints gbc_label_62 = new GridBagConstraints();
		gbc_label_62.insets = new Insets(0, 0, 5, 0);
		gbc_label_62.fill = GridBagConstraints.BOTH;
		gbc_label_62.gridx = 6;
		gbc_label_62.gridy = 11;
		panel_1.add(label_62, gbc_label_62);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Results", null, panel_3, null);
		
		table = new JTable(ResultsTable.model);
		scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JLabel lblTable = new JLabel("  Results");
		lblTable.setVerticalAlignment(SwingConstants.BOTTOM);
		lblTable.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblTable.setBackground(SystemColor.controlShadow);
		
		JButton btnNewButton = new JButton("Export to xls");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton save = new JButton();
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Save File");
				fc.setCurrentDirectory(new File(System.getProperty("user.dir") + SEPARATOR + "results" ));
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(fc.showSaveDialog(save) == JFileChooser.APPROVE_OPTION){
					String fileName = fc.getSelectedFile().getName();
					String path = fc.getSelectedFile().getParentFile().getPath();

					String ext = "";
					String file = "";

					if(fileName.length() > 4){
						ext = fileName.substring(fileName.length()-4, fileName.length());
					}

					if(ext.equals(".xls")){
						file = path + "\\" + fileName; 
					}else{
						file = path + "\\" + fileName + ".xls"; 
					}
					ResultsTable.exportToXls(new File(file));
				}
			}
		});
		
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollTable, GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblTable, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton)))
					.addContainerGap())
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTable, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(scrollTable, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
					.addGap(28))
		);
		panel_3.setLayout(gl_panel_3);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("App Settings", null, panel_2, null);
		
		JLabel lblLogLevel = new JLabel("Log level");
		
		JLabel lblVisualiserWidth = new JLabel("Visualiser width:");
		
		txtVisualiserWidth = new JTextField();
		txtVisualiserWidth.addActionListener(new TextFieldIntegerListener(PropKeys.VISUALISER_WIDTH, txtVisualiserWidth));
		txtVisualiserWidth.setText(prop.getProperty(PropKeys.VISUALISER_WIDTH.name()));
		txtVisualiserWidth.setColumns(10);
		
		JLabel lblVisualiserHeight = new JLabel("Visualiser height:");
		
		txtVisualiserHeight = new JTextField();
		txtVisualiserHeight.addActionListener(new TextFieldIntegerListener(PropKeys.VISUALISER_HEIGHT, txtVisualiserHeight));
		txtVisualiserHeight.setText(prop.getProperty(PropKeys.VISUALISER_HEIGHT.name()));
		txtVisualiserHeight.setColumns(10);
		
		rdbtnInfo = new JRadioButton("INFO",true);
		JRadioButton rdbtnDebug = new JRadioButton("DEBUG", false);
		bgLogLevel.add(rdbtnDebug);
		bgLogLevel.add(rdbtnInfo);
		
		
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(21)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblLogLevel)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(rdbtnInfo)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(rdbtnDebug))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblVisualiserWidth, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblVisualiserHeight, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtVisualiserWidth)
								.addComponent(txtVisualiserHeight))))
					.addContainerGap(533, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGap(27)
							.addComponent(lblVisualiserWidth))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGap(24)
							.addComponent(txtVisualiserWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblVisualiserHeight)
						.addComponent(txtVisualiserHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(11)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLogLevel)
						.addComponent(rdbtnInfo)
						.addComponent(rdbtnDebug))
					.addContainerGap(283, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		frmCanadianTravellerProblem.getContentPane().setLayout(groupLayout);
	}
	
	protected static void selectAllAlgorithms(boolean b) {
		rdbtnSPP.setSelected(b);
		rdbtnGA.setSelected(b);
		rdbtnRA.setSelected(b);
		rdbtnCA.setSelected(b);
		rdbtnHOP.setSelected(b);
		rdbtnORO.setSelected(b);
		rdbtnUCTB.setSelected(b);
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