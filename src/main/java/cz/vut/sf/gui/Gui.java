package cz.vut.sf.gui;

import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTree;
import javax.swing.JToggleButton;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import cz.vut.sf.graph.StochasticWeightedEdge;
import cz.vut.sf.graph.StochasticWeightedGraph;
import cz.vut.sf.parsers.BasicCtpParser;
import cz.vut.sf.parsers.ParsedDTO;
import cz.vut.sf.runner.CtpApp;
import cz.vut.sf.runner.CtpAppConstants;
import cz.vut.sf.runner.GraphSwing;
import cz.vut.sf.runner.CtpAppConstants.AlgNames;
import cz.vut.sf.runner.CtpAppConstants.PropKeys;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class Gui extends CtpAppConstants{
	private JFrame frmCanadianTravellerProblem;
	private JTextField filePath;
	private JTextField txtVisualiserWidth;
	private JTextField txtVisualiserHeight;
	private JTextField algorithmsRun;
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
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;

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
				prop.store(fos, "Application Settings");
				fos.close();
			}
		}catch(Exception e){
			
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
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
			CtpApp.run(algorithmsToBeMade, Integer.parseInt((String) prop.get(PropKeys.ALGORITHMS_RUN.name())));
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
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCanadianTravellerProblem = new JFrame();
		frmCanadianTravellerProblem.setTitle("Canadian Traveller Problem Application");
		frmCanadianTravellerProblem.setBounds(100, 100, 781, 492);
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
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	        	try{
	        		LabelSwingWorker workerThread = new LabelSwingWorker(lblStatus);
					workerThread.execute();
	        	}catch(Exception err){
	        		LOG.error(err);
	        	}
			}
		});
		
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(filePath, GroupLayout.PREFERRED_SIZE, 283, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(openFilePathBtn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(showGraphBtn))
						.addComponent(btnRun)
						.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(196, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
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
					.addComponent(btnRun)
					.addContainerGap(298, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		ButtonGroup bgLogLevel = new ButtonGroup(); 
		
		final String lblAlgorithmsRunText = "Agorithms are going to be run x time(s).";
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("App Settings", null, panel_2, null);
		
		JLabel lblLogLevel = new JLabel("Log level");
		
		JLabel lblVisualiserWidth = new JLabel("Visualiser width:");
		
		txtVisualiserWidth = new JTextField();
		txtVisualiserWidth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.VISUALISER_WIDTH.name(), txtVisualiserWidth.getText());
				try {
					saveProp();
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
		});
		txtVisualiserWidth.setColumns(10);
		txtVisualiserWidth.setText(prop.getProperty(PropKeys.VISUALISER_WIDTH.name()));
		
		JLabel lblVisualiserHeight = new JLabel("Visualiser height:");
		
		txtVisualiserHeight = new JTextField();
		txtVisualiserHeight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prop.setProperty(PropKeys.VISUALISER_HEIGHT.name(), txtVisualiserHeight.getText());
				try {
					saveProp();
				} catch (Throwable err) {
					err.printStackTrace();
				}
			}
		});
		txtVisualiserHeight.setColumns(10);
		txtVisualiserHeight.setText(prop.getProperty(PropKeys.VISUALISER_HEIGHT.name()));
		
		JRadioButton rdbtnInfo = new JRadioButton("INFO",true);
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
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Algorithms", null, panel_1, null);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{175, 44, 79, 138, 20, 138, 138, 138, 138, 138, 0};
		gbl_panel_1.rowHeights = new int[]{19, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
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
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		gbc_label_4.gridx = 6;
		gbc_label_4.gridy = 1;
		panel_1.add(label_4, gbc_label_4);
		
		JLabel label_5 = new JLabel("");
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.fill = GridBagConstraints.BOTH;
		gbc_label_5.insets = new Insets(0, 0, 5, 5);
		gbc_label_5.gridx = 7;
		gbc_label_5.gridy = 1;
		panel_1.add(label_5, gbc_label_5);
		
		JLabel label_6 = new JLabel("");
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.fill = GridBagConstraints.BOTH;
		gbc_label_6.insets = new Insets(0, 0, 5, 5);
		gbc_label_6.gridx = 8;
		gbc_label_6.gridy = 1;
		panel_1.add(label_6, gbc_label_6);
		
		JLabel label_7 = new JLabel("");
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.fill = GridBagConstraints.BOTH;
		gbc_label_7.insets = new Insets(0, 0, 5, 0);
		gbc_label_7.gridx = 9;
		gbc_label_7.gridy = 1;
		panel_1.add(label_7, gbc_label_7);
		
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
		gbc_label_12.insets = new Insets(0, 0, 5, 5);
		gbc_label_12.gridx = 6;
		gbc_label_12.gridy = 2;
		panel_1.add(label_12, gbc_label_12);
		
		JLabel label_13 = new JLabel("");
		GridBagConstraints gbc_label_13 = new GridBagConstraints();
		gbc_label_13.fill = GridBagConstraints.BOTH;
		gbc_label_13.insets = new Insets(0, 0, 5, 5);
		gbc_label_13.gridx = 7;
		gbc_label_13.gridy = 2;
		panel_1.add(label_13, gbc_label_13);
		
		JLabel label_14 = new JLabel("");
		GridBagConstraints gbc_label_14 = new GridBagConstraints();
		gbc_label_14.fill = GridBagConstraints.BOTH;
		gbc_label_14.insets = new Insets(0, 0, 5, 5);
		gbc_label_14.gridx = 8;
		gbc_label_14.gridy = 2;
		panel_1.add(label_14, gbc_label_14);
		
		JLabel label_15 = new JLabel("");
		GridBagConstraints gbc_label_15 = new GridBagConstraints();
		gbc_label_15.fill = GridBagConstraints.BOTH;
		gbc_label_15.insets = new Insets(0, 0, 5, 0);
		gbc_label_15.gridx = 9;
		gbc_label_15.gridy = 2;
		panel_1.add(label_15, gbc_label_15);
		
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
		gbc_label_20.insets = new Insets(0, 0, 5, 5);
		gbc_label_20.gridx = 6;
		gbc_label_20.gridy = 3;
		panel_1.add(label_20, gbc_label_20);
		
		JLabel label_21 = new JLabel("");
		GridBagConstraints gbc_label_21 = new GridBagConstraints();
		gbc_label_21.fill = GridBagConstraints.BOTH;
		gbc_label_21.insets = new Insets(0, 0, 5, 5);
		gbc_label_21.gridx = 7;
		gbc_label_21.gridy = 3;
		panel_1.add(label_21, gbc_label_21);
		
		JLabel label_22 = new JLabel("");
		GridBagConstraints gbc_label_22 = new GridBagConstraints();
		gbc_label_22.fill = GridBagConstraints.BOTH;
		gbc_label_22.insets = new Insets(0, 0, 5, 5);
		gbc_label_22.gridx = 8;
		gbc_label_22.gridy = 3;
		panel_1.add(label_22, gbc_label_22);
		
		JLabel label_23 = new JLabel("");
		GridBagConstraints gbc_label_23 = new GridBagConstraints();
		gbc_label_23.fill = GridBagConstraints.BOTH;
		gbc_label_23.insets = new Insets(0, 0, 5, 0);
		gbc_label_23.gridx = 9;
		gbc_label_23.gridy = 3;
		panel_1.add(label_23, gbc_label_23);
		
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
		gbc_label_28.insets = new Insets(0, 0, 5, 5);
		gbc_label_28.gridx = 6;
		gbc_label_28.gridy = 4;
		panel_1.add(label_28, gbc_label_28);
		
		JLabel label_29 = new JLabel("");
		GridBagConstraints gbc_label_29 = new GridBagConstraints();
		gbc_label_29.fill = GridBagConstraints.BOTH;
		gbc_label_29.insets = new Insets(0, 0, 5, 5);
		gbc_label_29.gridx = 7;
		gbc_label_29.gridy = 4;
		panel_1.add(label_29, gbc_label_29);
		
		JLabel label_30 = new JLabel("");
		GridBagConstraints gbc_label_30 = new GridBagConstraints();
		gbc_label_30.fill = GridBagConstraints.BOTH;
		gbc_label_30.insets = new Insets(0, 0, 5, 5);
		gbc_label_30.gridx = 8;
		gbc_label_30.gridy = 4;
		panel_1.add(label_30, gbc_label_30);
		
		JLabel label_31 = new JLabel("");
		GridBagConstraints gbc_label_31 = new GridBagConstraints();
		gbc_label_31.fill = GridBagConstraints.BOTH;
		gbc_label_31.insets = new Insets(0, 0, 5, 0);
		gbc_label_31.gridx = 9;
		gbc_label_31.gridy = 4;
		panel_1.add(label_31, gbc_label_31);
		
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
		
		JLabel lblRollouts = new JLabel("Rollouts:");
		GridBagConstraints gbc_lblRollouts = new GridBagConstraints();
		gbc_lblRollouts.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts.gridx = 2;
		gbc_lblRollouts.gridy = 5;
		panel_1.add(lblRollouts, gbc_lblRollouts);
		
		textField = new JTextField();
		textField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 5;
		panel_1.add(textField, gbc_textField);
		
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
		gbc_label_34.insets = new Insets(0, 0, 5, 5);
		gbc_label_34.gridx = 6;
		gbc_label_34.gridy = 5;
		panel_1.add(label_34, gbc_label_34);
		
		JLabel label_35 = new JLabel("");
		GridBagConstraints gbc_label_35 = new GridBagConstraints();
		gbc_label_35.fill = GridBagConstraints.BOTH;
		gbc_label_35.insets = new Insets(0, 0, 5, 5);
		gbc_label_35.gridx = 7;
		gbc_label_35.gridy = 5;
		panel_1.add(label_35, gbc_label_35);
		
		JLabel label_36 = new JLabel("");
		GridBagConstraints gbc_label_36 = new GridBagConstraints();
		gbc_label_36.fill = GridBagConstraints.BOTH;
		gbc_label_36.insets = new Insets(0, 0, 5, 5);
		gbc_label_36.gridx = 8;
		gbc_label_36.gridy = 5;
		panel_1.add(label_36, gbc_label_36);
		
		JLabel label_37 = new JLabel("");
		GridBagConstraints gbc_label_37 = new GridBagConstraints();
		gbc_label_37.fill = GridBagConstraints.BOTH;
		gbc_label_37.insets = new Insets(0, 0, 5, 0);
		gbc_label_37.gridx = 9;
		gbc_label_37.gridy = 5;
		panel_1.add(label_37, gbc_label_37);
		
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
		
		JLabel lblRollouts1 = new JLabel("Rollouts:");
		GridBagConstraints gbc_lblRollouts1 = new GridBagConstraints();
		gbc_lblRollouts1.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts1.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts1.gridx = 2;
		gbc_lblRollouts1.gridy = 6;
		panel_1.add(lblRollouts1, gbc_lblRollouts1);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.BOTH;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = 6;
		panel_1.add(textField_1, gbc_textField_1);
		
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
		gbc_label_40.insets = new Insets(0, 0, 5, 5);
		gbc_label_40.gridx = 6;
		gbc_label_40.gridy = 6;
		panel_1.add(label_40, gbc_label_40);
		
		JLabel label_41 = new JLabel("");
		GridBagConstraints gbc_label_41 = new GridBagConstraints();
		gbc_label_41.fill = GridBagConstraints.BOTH;
		gbc_label_41.insets = new Insets(0, 0, 5, 5);
		gbc_label_41.gridx = 7;
		gbc_label_41.gridy = 6;
		panel_1.add(label_41, gbc_label_41);
		
		JLabel label_42 = new JLabel("");
		GridBagConstraints gbc_label_42 = new GridBagConstraints();
		gbc_label_42.fill = GridBagConstraints.BOTH;
		gbc_label_42.insets = new Insets(0, 0, 5, 5);
		gbc_label_42.gridx = 8;
		gbc_label_42.gridy = 6;
		panel_1.add(label_42, gbc_label_42);
		
		JLabel label_43 = new JLabel("");
		GridBagConstraints gbc_label_43 = new GridBagConstraints();
		gbc_label_43.fill = GridBagConstraints.BOTH;
		gbc_label_43.insets = new Insets(0, 0, 5, 0);
		gbc_label_43.gridx = 9;
		gbc_label_43.gridy = 6;
		panel_1.add(label_43, gbc_label_43);
		
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
		
		JLabel lblRollouts2 = new JLabel("Rollouts:");
		GridBagConstraints gbc_lblRollouts2 = new GridBagConstraints();
		gbc_lblRollouts2.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts2.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts2.gridx = 2;
		gbc_lblRollouts2.gridy = 7;
		panel_1.add(lblRollouts2, gbc_lblRollouts2);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.fill = GridBagConstraints.BOTH;
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.gridx = 3;
		gbc_textField_2.gridy = 7;
		panel_1.add(textField_2, gbc_textField_2);
		
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
		gbc_label_46.insets = new Insets(0, 0, 5, 5);
		gbc_label_46.gridx = 6;
		gbc_label_46.gridy = 7;
		panel_1.add(label_46, gbc_label_46);
		
		JLabel label_47 = new JLabel("");
		GridBagConstraints gbc_label_47 = new GridBagConstraints();
		gbc_label_47.fill = GridBagConstraints.BOTH;
		gbc_label_47.insets = new Insets(0, 0, 5, 5);
		gbc_label_47.gridx = 7;
		gbc_label_47.gridy = 7;
		panel_1.add(label_47, gbc_label_47);
		
		JLabel label_48 = new JLabel("");
		GridBagConstraints gbc_label_48 = new GridBagConstraints();
		gbc_label_48.fill = GridBagConstraints.BOTH;
		gbc_label_48.insets = new Insets(0, 0, 5, 5);
		gbc_label_48.gridx = 8;
		gbc_label_48.gridy = 7;
		panel_1.add(label_48, gbc_label_48);
		
		JLabel label_49 = new JLabel("");
		GridBagConstraints gbc_label_49 = new GridBagConstraints();
		gbc_label_49.fill = GridBagConstraints.BOTH;
		gbc_label_49.insets = new Insets(0, 0, 5, 0);
		gbc_label_49.gridx = 9;
		gbc_label_49.gridy = 7;
		panel_1.add(label_49, gbc_label_49);
		
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
		
		JLabel lblRollouts3 = new JLabel("Rollouts:");
		GridBagConstraints gbc_lblRollouts3 = new GridBagConstraints();
		gbc_lblRollouts3.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts3.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts3.gridx = 2;
		gbc_lblRollouts3.gridy = 8;
		panel_1.add(lblRollouts3, gbc_lblRollouts3);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.fill = GridBagConstraints.BOTH;
		gbc_textField_3.insets = new Insets(0, 0, 5, 5);
		gbc_textField_3.gridx = 3;
		gbc_textField_3.gridy = 8;
		panel_1.add(textField_3, gbc_textField_3);
		
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
		
		textField_5 = new JTextField();
		textField_5.setColumns(10);
		GridBagConstraints gbc_textField_5 = new GridBagConstraints();
		gbc_textField_5.fill = GridBagConstraints.BOTH;
		gbc_textField_5.insets = new Insets(0, 0, 5, 5);
		gbc_textField_5.gridx = 6;
		gbc_textField_5.gridy = 8;
		panel_1.add(textField_5, gbc_textField_5);
		
		JLabel label_52 = new JLabel("");
		GridBagConstraints gbc_label_52 = new GridBagConstraints();
		gbc_label_52.fill = GridBagConstraints.BOTH;
		gbc_label_52.insets = new Insets(0, 0, 5, 5);
		gbc_label_52.gridx = 7;
		gbc_label_52.gridy = 8;
		panel_1.add(label_52, gbc_label_52);
		
		JLabel label_53 = new JLabel("");
		GridBagConstraints gbc_label_53 = new GridBagConstraints();
		gbc_label_53.fill = GridBagConstraints.BOTH;
		gbc_label_53.insets = new Insets(0, 0, 5, 5);
		gbc_label_53.gridx = 8;
		gbc_label_53.gridy = 8;
		panel_1.add(label_53, gbc_label_53);
		
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
		
		JLabel lblRollouts4 = new JLabel("Rollouts:");
		GridBagConstraints gbc_lblRollouts4 = new GridBagConstraints();
		gbc_lblRollouts4.fill = GridBagConstraints.BOTH;
		gbc_lblRollouts4.insets = new Insets(0, 0, 5, 5);
		gbc_lblRollouts4.gridx = 2;
		gbc_lblRollouts4.gridy = 9;
		panel_1.add(lblRollouts4, gbc_lblRollouts4);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.fill = GridBagConstraints.BOTH;
		gbc_textField_4.insets = new Insets(0, 0, 5, 5);
		gbc_textField_4.gridx = 3;
		gbc_textField_4.gridy = 9;
		panel_1.add(textField_4, gbc_textField_4);
		
		JLabel label_54 = new JLabel("");
		GridBagConstraints gbc_label_54 = new GridBagConstraints();
		gbc_label_54.fill = GridBagConstraints.BOTH;
		gbc_label_54.insets = new Insets(0, 0, 5, 5);
		gbc_label_54.gridx = 4;
		gbc_label_54.gridy = 9;
		panel_1.add(label_54, gbc_label_54);
		
		JLabel lblIterationsri = new JLabel("   Iterations (Ni):");
		GridBagConstraints gbc_lblIterationsri = new GridBagConstraints();
		gbc_lblIterationsri.fill = GridBagConstraints.BOTH;
		gbc_lblIterationsri.insets = new Insets(0, 0, 5, 5);
		gbc_lblIterationsri.gridx = 5;
		gbc_lblIterationsri.gridy = 9;
		panel_1.add(lblIterationsri, gbc_lblIterationsri);
		
		textField_6 = new JTextField();
		textField_6.setColumns(10);
		GridBagConstraints gbc_textField_6 = new GridBagConstraints();
		gbc_textField_6.fill = GridBagConstraints.BOTH;
		gbc_textField_6.insets = new Insets(0, 0, 5, 5);
		gbc_textField_6.gridx = 6;
		gbc_textField_6.gridy = 9;
		panel_1.add(textField_6, gbc_textField_6);
		
		JLabel label_56 = new JLabel("");
		GridBagConstraints gbc_label_56 = new GridBagConstraints();
		gbc_label_56.fill = GridBagConstraints.BOTH;
		gbc_label_56.insets = new Insets(0, 0, 5, 5);
		gbc_label_56.gridx = 7;
		gbc_label_56.gridy = 9;
		panel_1.add(label_56, gbc_label_56);
		
		JLabel label_57 = new JLabel("");
		GridBagConstraints gbc_label_57 = new GridBagConstraints();
		gbc_label_57.fill = GridBagConstraints.BOTH;
		gbc_label_57.insets = new Insets(0, 0, 5, 0);
		gbc_label_57.gridx = 9;
		gbc_label_57.gridy = 9;
		panel_1.add(label_57, gbc_label_57);
		final JLabel lblAgorithmsRun = new JLabel((lblAlgorithmsRunText).replace("x", prop.getProperty(PropKeys.ALGORITHMS_RUN.name())));
		GridBagConstraints gbc_lblAgorithmsRun = new GridBagConstraints();
		gbc_lblAgorithmsRun.fill = GridBagConstraints.BOTH;
		gbc_lblAgorithmsRun.insets = new Insets(0, 0, 0, 5);
		gbc_lblAgorithmsRun.gridx = 0;
		gbc_lblAgorithmsRun.gridy = 10;
		panel_1.add(lblAgorithmsRun, gbc_lblAgorithmsRun);
		
		JLabel label_58 = new JLabel("");
		GridBagConstraints gbc_label_58 = new GridBagConstraints();
		gbc_label_58.fill = GridBagConstraints.BOTH;
		gbc_label_58.insets = new Insets(0, 0, 0, 5);
		gbc_label_58.gridx = 1;
		gbc_label_58.gridy = 10;
		panel_1.add(label_58, gbc_label_58);
		
		algorithmsRun = new JTextField();
		algorithmsRun.setColumns(10);
		GridBagConstraints gbc_algorithmsRun = new GridBagConstraints();
		gbc_algorithmsRun.fill = GridBagConstraints.BOTH;
		gbc_algorithmsRun.insets = new Insets(0, 0, 0, 5);
		gbc_algorithmsRun.gridx = 2;
		gbc_algorithmsRun.gridy = 10;
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
						String lblText = "Agorithms are going to be run x times.";
						lblAgorithmsRun.setText(lblText.replace("x", strValue));
					}else{
						lblAgorithmsRun.setText("Agorithms are going to be run 1 time.");
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
		gbc_btnSet.insets = new Insets(0, 0, 0, 5);
		gbc_btnSet.gridx = 3;
		gbc_btnSet.gridy = 10;
		panel_1.add(btnSet, gbc_btnSet);
		
		JLabel label_61 = new JLabel("");
		GridBagConstraints gbc_label_61 = new GridBagConstraints();
		gbc_label_61.fill = GridBagConstraints.BOTH;
		gbc_label_61.insets = new Insets(0, 0, 0, 5);
		gbc_label_61.gridx = 5;
		gbc_label_61.gridy = 10;
		panel_1.add(label_61, gbc_label_61);
		
		JLabel label_62 = new JLabel("");
		GridBagConstraints gbc_label_62 = new GridBagConstraints();
		gbc_label_62.fill = GridBagConstraints.BOTH;
		gbc_label_62.insets = new Insets(0, 0, 0, 5);
		gbc_label_62.gridx = 6;
		gbc_label_62.gridy = 10;
		panel_1.add(label_62, gbc_label_62);
		
		JLabel label_63 = new JLabel("");
		GridBagConstraints gbc_label_63 = new GridBagConstraints();
		gbc_label_63.fill = GridBagConstraints.BOTH;
		gbc_label_63.insets = new Insets(0, 0, 0, 5);
		gbc_label_63.gridx = 8;
		gbc_label_63.gridy = 10;
		panel_1.add(label_63, gbc_label_63);
		
		JLabel label_64 = new JLabel("");
		GridBagConstraints gbc_label_64 = new GridBagConstraints();
		gbc_label_64.fill = GridBagConstraints.BOTH;
		gbc_label_64.gridx = 9;
		gbc_label_64.gridy = 10;
		panel_1.add(label_64, gbc_label_64);
		frmCanadianTravellerProblem.getContentPane().setLayout(groupLayout);
	}
}
