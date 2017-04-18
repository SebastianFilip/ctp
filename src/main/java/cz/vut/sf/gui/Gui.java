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
		frmCanadianTravellerProblem.setBounds(100, 100, 784, 454);
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
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Algorithms", null, panel_1, null);
		
		JLabel lblDijkstraShortestPath = new JLabel("Dijkstra shortest path");
		
		JLabel lblGreedyAlgorithm = new JLabel("Greedy algorithm");
		
		JLabel lblRepo = new JLabel("Reposition algorithm");
		
		JLabel lblComparisionAlgorithm = new JLabel("Comparision algorithm");
		
		JLabel lblHindsightOptimizationhop = new JLabel("Hindsight Optimization (HOP)");
		
		JLabel lblOptimisticRolloutoro = new JLabel("Optimistic Rollout (ORO)");
		
		JLabel lblBlindUctuctb = new JLabel("Blind UCT (UCTB)");
		
		JLabel lblOptimisticUctucto = new JLabel("Optimistic UCT (UCTO)");
		
		JLabel lblPruningUctuctp = new JLabel("Pruning UCT (UCTP)");
		
		rdbtnSPP = new JRadioButton("");
		rdbtnSPP.setSelected(true);
		
		rdbtnGA = new JRadioButton("");
		
		rdbtnRA = new JRadioButton("");
		
		rdbtnCA = new JRadioButton("");
		
		rdbtnHOP = new JRadioButton("");
		
		rdbtnORO = new JRadioButton("");
		
		rdbtnUCTB = new JRadioButton("");
		
		rdbtnUCTO = new JRadioButton("");
		
		rdbtnUCTP = new JRadioButton("");
		
		final String lblAlgorithmsRunText = "Agorithms are going to be run x time(s).";
		final JLabel lblAgorithmsRun = new JLabel((lblAlgorithmsRunText).replace("x", prop.getProperty(PropKeys.ALGORITHMS_RUN.name())));
		
		algorithmsRun = new JTextField();
		algorithmsRun.setColumns(10);
		
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
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblAgorithmsRun, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(algorithmsRun, 0, 0, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSet)
							.addGap(382))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblHindsightOptimizationhop, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblComparisionAlgorithm, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblRepo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblGreedyAlgorithm, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblDijkstraShortestPath, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
									.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblPruningUctuctp, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
										.addComponent(lblOptimisticUctucto, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
										.addComponent(lblBlindUctuctb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
										.addComponent(lblOptimisticRolloutoro, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.UNRELATED)))
							.addGap(6)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(rdbtnUCTP)
								.addComponent(rdbtnSPP)
								.addComponent(rdbtnGA)
								.addComponent(rdbtnRA)
								.addComponent(rdbtnCA)
								.addComponent(rdbtnHOP)
								.addComponent(rdbtnUCTO)
								.addComponent(rdbtnUCTB)
								.addComponent(rdbtnORO))
							.addContainerGap(559, Short.MAX_VALUE))))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDijkstraShortestPath)
						.addComponent(rdbtnSPP))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGreedyAlgorithm)
						.addComponent(rdbtnGA))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRepo)
						.addComponent(rdbtnRA))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComparisionAlgorithm)
						.addComponent(rdbtnCA))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHindsightOptimizationhop)
						.addComponent(rdbtnHOP))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOptimisticRolloutoro)
						.addComponent(rdbtnORO))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBlindUctuctb)
						.addComponent(rdbtnUCTB))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOptimisticUctucto)
						.addComponent(rdbtnUCTO))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPruningUctuctp)
						.addComponent(rdbtnUCTP))
					.addGap(28)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAgorithmsRun)
						.addComponent(btnSet)
						.addComponent(algorithmsRun, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(120, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
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
		frmCanadianTravellerProblem.getContentPane().setLayout(groupLayout);
	}
}
