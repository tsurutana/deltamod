package deltamod.comp;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;

import deltamod.DeltaMod;
import deltamod.file.ExporterGraphML;
import deltamod.file.ExporterLiveGraphics3D;
import deltamod.file.ExporterSVG;
import deltamod.file.FileFilterEx;
import deltamod.file.ImporterPlanarGraphCode;
import deltamod.file.OBJStream;
import deltamod.geom.Embedding;
import deltamod.geom.ModelOps;

public class MainFrame extends JFrame implements ActionListener {

	private JMenu menuFile = new JMenu(DeltaMod.res.getString("File"));
	private JMenu menuNew = new JMenu(DeltaMod.res.getString("New"));
	private JMenuItem menuItemNewC4 = new JMenuItem(DeltaMod.res.getString("C4"));
	private JMenuItem menuItemNewC6 = new JMenuItem(DeltaMod.res.getString("C6"));
	private JMenuItem menuItemNewC8 = new JMenuItem(DeltaMod.res.getString("C8"));
	private JMenuItem menuItemNewC10 = new JMenuItem(DeltaMod.res.getString("C10"));
	private JMenuItem menuItemNewC12 = new JMenuItem(DeltaMod.res.getString("C12"));
	private JMenuItem menuItemNewC14 = new JMenuItem(DeltaMod.res.getString("C14"));
	private JMenuItem menuItemNewC16 = new JMenuItem(DeltaMod.res.getString("C16"));
	private JMenuItem menuItemNewC20 = new JMenuItem(DeltaMod.res.getString("C20"));
	private JMenuItem menuItemOpen = new JMenuItem(DeltaMod.res.getString("Open"));
	private JMenuItem menuItemSave = new JMenuItem(DeltaMod.res.getString("Save"));
	private JMenu menuView = new JMenu(DeltaMod.res.getString("View"));
	public JRadioButtonMenuItem menuItemViewFlat = new JRadioButtonMenuItem( DeltaMod.res.getString("Flat") );
	public JRadioButtonMenuItem menuItemViewFlatLines = new JRadioButtonMenuItem( DeltaMod.res.getString("FlatLines"), true );
	public JRadioButtonMenuItem menuItemViewWireframe = new JRadioButtonMenuItem( DeltaMod.res.getString("Wireframe") );

	private JToolBar toolbar = new JToolBar();
	
	public JToggleButton[] buttonOps = new JToggleButton[6];
	public JToggleButton buttonOpConnect = new JToggleButton(DeltaMod.imgres.getImage("add"), true);
	public JToggleButton buttonOpElongate = new JToggleButton(DeltaMod.imgres.getImage("elongate"));
	public JToggleButton buttonOpTuck = new JToggleButton(DeltaMod.imgres.getImage("tuck"));
	public JToggleButton buttonOpFill = new JToggleButton(DeltaMod.imgres.getImage("fill"));
	public JToggleButton buttonOpDivide = new JToggleButton(DeltaMod.imgres.getImage("divide"));
	public JToggleButton buttonOpOptimize = new JToggleButton(DeltaMod.imgres.getImage("optimize"));

	public MainScreen mainscreen;
	public OpsPanel opspanel;
	public StatusBar statusBar;
	public JSplitPane splitpane;

	public MainFrame() {
		
		// load initial model
		DeltaMod.doc.setModel(OBJStream.load(getClass().getResourceAsStream("/obj/01.obj")));
		ModelOps.setConstants(DeltaMod.doc.getModel());
		
		getContentPane().setLayout(new BorderLayout());
		
		initMenu();
		initToolBar();
		
		opspanel = new OpsPanel();
		opspanel.setPreferredSize(new Dimension(250,480));
		
		statusBar = new StatusBar(3);
		statusBar.setMessage(0, DeltaMod.res.getString("Manual"));
		statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		mainscreen = new MainScreen();
		mainscreen.setModel(DeltaMod.doc.getModel());

		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, opspanel, mainscreen);
		getContentPane().add(splitpane, BorderLayout.CENTER);
		
		updateStatusBar();
	}
	
	public void initMenu() {
		
		JMenuBar menuBar = new JMenuBar();
		
		ActionListener initAction = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == menuItemNewC4) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/01.obj")));
							//OBJStream.load(getClass().getResourceAsStream("resources/obj/01.obj")));
				} else if (e.getSource() == menuItemNewC6) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/02.obj")));
				} else if (e.getSource() == menuItemNewC8) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/03.obj")));
				} else if (e.getSource() == menuItemNewC10) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/04.obj")));
				} else if (e.getSource() == menuItemNewC12) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/05.obj")));
				} else if (e.getSource() == menuItemNewC14) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/06.obj")));
				} else if (e.getSource() == menuItemNewC16) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/07.obj")));
				} else if (e.getSource() == menuItemNewC20) {
					DeltaMod.doc.setModel(
							OBJStream.load(getClass().getResourceAsStream("/obj/08.obj")));
				}
				ModelOps.setConstants(DeltaMod.doc.getModel());
				mainscreen.setModel(DeltaMod.doc.getModel());
				mainscreen.repaint();
				updateStatusBar();
			}
		};
		
		menuBar.add(menuFile);
		menuFile.setMnemonic('f');
		menuFile.add(menuNew);
		menuNew.add(menuItemNewC4);
		menuItemNewC4.addActionListener(initAction);
		menuNew.add(menuItemNewC6);
		menuItemNewC6.addActionListener(initAction);
		menuNew.add(menuItemNewC8);
		menuItemNewC8.addActionListener(initAction);
		menuNew.add(menuItemNewC10);
		menuItemNewC10.addActionListener(initAction);
		menuNew.add(menuItemNewC12);
		menuItemNewC12.addActionListener(initAction);
		menuNew.add(menuItemNewC14);
		menuItemNewC14.addActionListener(initAction);
		menuNew.add(menuItemNewC16);
		menuItemNewC16.addActionListener(initAction);
		menuNew.add(menuItemNewC20);
		menuItemNewC20.addActionListener(initAction);
		
		
		menuFile.add(menuItemOpen);
		menuItemOpen.addActionListener(this);
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		menuFile.add(menuItemSave);
		menuItemSave.addActionListener(this);
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));

		menuBar.add(menuView);
		// rendering
		ButtonGroup fillGroup = new ButtonGroup();
		fillGroup.add(menuItemViewFlat);
		menuView.add(menuItemViewFlat);
		menuItemViewFlat.addActionListener(this);
		fillGroup.add(menuItemViewFlatLines);
		menuView.add(menuItemViewFlatLines);
		menuItemViewFlatLines.addActionListener(this);
		fillGroup.add(menuItemViewWireframe);
		menuView.add(menuItemViewWireframe);
		menuItemViewWireframe.addActionListener(this);
		
		setJMenuBar(menuBar);
	}
	
	public void initToolBar() {
		ActionListener opsAction = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (buttonOpConnect.isSelected()) {
					opspanel.show("Connect");
				} else if (buttonOpElongate.isSelected()) {
					opspanel.show("Elongate");
				} else if (buttonOpTuck.isSelected()) {
					opspanel.show("Tuck");
				} else if (buttonOpDivide.isSelected()) {
					opspanel.show("Divide");
				} else if (buttonOpFill.isSelected()) {
					opspanel.show("Fill");
				} else if (buttonOpOptimize.isSelected()) {
					opspanel.show("Optimize");
				}
			}
		};

		ButtonGroup opsGroup = new ButtonGroup();
		opsGroup.add(buttonOpConnect);
		toolbar.add(buttonOpConnect);
		buttonOpConnect.addActionListener(opsAction);
		opsGroup.add(buttonOpElongate);
		toolbar.add(buttonOpElongate);
		buttonOpElongate.addActionListener(opsAction);
		opsGroup.add(buttonOpTuck);
		toolbar.add(buttonOpTuck);
		buttonOpTuck.addActionListener(opsAction);
		opsGroup.add(buttonOpFill);
		toolbar.add(buttonOpFill);
		buttonOpFill.addActionListener(opsAction);
		opsGroup.add(buttonOpDivide);
		toolbar.add(buttonOpDivide);
		buttonOpDivide.addActionListener(opsAction);
		opsGroup.add(buttonOpOptimize);
		toolbar.add(buttonOpOptimize);
		buttonOpOptimize.addActionListener(opsAction);
		toolbar.addSeparator();

		buttonOps[0] = buttonOpConnect;
		buttonOps[1] = buttonOpElongate;
		buttonOps[2] = buttonOpTuck;
		buttonOps[3] = buttonOpFill;
		buttonOps[4] = buttonOpDivide;
		buttonOps[5] = buttonOpOptimize;
		
		getContentPane().add(toolbar, BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItemOpen) {
			open();
		} else if (e.getSource() == menuItemSave) {
			save();
		}
		mainscreen.repaint();
	}

	private void open() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(FileFilterEx.OBJ);
		fileChooser.addChoosableFileFilter(FileFilterEx.PCODE);
		fileChooser.setFileFilter(FileFilterEx.OBJ);
		//fileChooser.setCurrentDirectory(new File("./enumerate/"));
		int selected = fileChooser.showOpenDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			String suffix = getSuffix(file.getName());
			System.out.println(suffix);

			// obj
			if (fileChooser.getFileFilter() == FileFilterEx.OBJ || suffix.compareTo("obj") == 0) {
				try {
					DeltaMod.doc.setModel(OBJStream.load(file.getAbsolutePath()));
					this.setTitle(DeltaMod.res.getString("Title") + ": " + file.getName());
					ModelOps.setConstants(DeltaMod.doc.getModel());
					mainscreen.setModel(DeltaMod.doc.getModel());
					//ModelOps.printDihedralAngles(DeltaMod.doc.getModel());
					ModelOps.hasDihedralAngle180(DeltaMod.doc.getModel());
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			//Planar graph
			} else if (fileChooser.getFileFilter() == FileFilterEx.PCODE || suffix.compareTo("pcode") == 0) {
				String input;
				if ((input = JOptionPane.showInputDialog(this, "Input the index of graph to reconstruct.")) != null) {
					int index = Integer.parseInt(input);
					DeltaMod.doc.setModel(
							ImporterPlanarGraphCode.load(file.getAbsolutePath(), index-1));
					mainscreen.setModel(DeltaMod.doc.getModel());
					this.setTitle(DeltaMod.res.getString("Title") + ": " + file.getName() + " - " + index);
					Embedding.embed(DeltaMod.doc.getModel());
					//Embedding.liftupCentrality2(DeltaMod.doc.getModel());
					//Embedding.barycentric(DeltaMod.doc.getModel());
				}

			}
		}else if (selected == JFileChooser.CANCEL_OPTION){
			System.out.println("file select canceled");
		}else if (selected == JFileChooser.ERROR_OPTION){
			System.out.println("file open error");
		}
	}

	/*
	private void autoload() {
		ArrayList<Double> angles = new ArrayList<>();
		for (int i=1; i<=233; i++) {
			String path = getCountFilePath(i, "./enumerate/10/10-");
			path = path + "S2.obj";
			File f = new File(path);
			if (f.exists()) {
				DeltaMod.doc.setModel(OBJStream.load(path));
				ModelOps.setConstants(DeltaMod.doc.getModel());
				mainscreen.setModel(DeltaMod.doc.getModel());
				//System.out.print(i + ", ");
				//ModelOps.hasDihedralAngle180(DeltaMod.doc.getModel());
				if (!ModelOps.hasDihedralAngle180(DeltaMod.doc.getModel()))
					angles.addAll(ModelOps.getDihedralAngles(DeltaMod.doc.getModel()));
			}
		}
		for (double x : angles) {
			System.out.println(x);
		}
	}*/

	/*
	private String getCountFilePath(int l, String filePath){
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);
		nf.setGroupingUsed(false);
		return filePath + nf.format(l);
	}*/

	private void save() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./enumerate/"));
		fileChooser.addChoosableFileFilter(FileFilterEx.OBJ);
		fileChooser.addChoosableFileFilter(FileFilterEx.SVG);
		fileChooser.addChoosableFileFilter(FileFilterEx.GraphML);
		fileChooser.addChoosableFileFilter(FileFilterEx.M);
		fileChooser.setFileFilter(FileFilterEx.OBJ);
		if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(this)) {
			try {
				String ext = null;
				if (fileChooser.getFileFilter() == FileFilterEx.OBJ)
					ext = "obj";
				else if (fileChooser.getFileFilter() == FileFilterEx.SVG)
					ext = "svg";
				else if (fileChooser.getFileFilter() == FileFilterEx.GraphML)
					ext = "graphml";
				else if (fileChooser.getFileFilter() == FileFilterEx.M)
					ext = "m";
				String filePath = fileChooser.getSelectedFile().getPath();
				File file = new File(filePath);
				
				if(file.exists()) {
					if(JOptionPane.showConfirmDialog(
							null, "Warning SameNameFileExist", "Save as...",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
						return;
					}
				}

				if (ext == null) {
					ext = getSuffix(file.getName());
					if (ext == null)
						throw new Exception();
				}

				if (!filePath.endsWith("." + ext)) {
					filePath += "." + ext;
				}

				if (ext.compareTo("obj") == 0)
					OBJStream.save(filePath, DeltaMod.doc.getModel());
				else if (ext.compareTo("svg") == 0)
					ExporterSVG.export(DeltaMod.doc.getModel(), filePath);
				else if (ext.compareTo("graphml") == 0)
					ExporterGraphML.exportVertexGraph(DeltaMod.doc.getModel(), filePath);
				else if (ext.compareTo("m") == 0)
					ExporterLiveGraphics3D.export(DeltaMod.doc.getModel(), filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateStatusBar() {
		statusBar.setMessage(1, DeltaMod.doc.getModel().getGeometricInformation());
		statusBar.setMessage(2, DeltaMod.doc.getModel().getDeltahedronInformation());
	}
	
	private static String getSuffix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(point + 1);
		}
		return fileName;
	}
}
