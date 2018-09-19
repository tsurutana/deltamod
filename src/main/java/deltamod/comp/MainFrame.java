package deltamod.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
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
	private JMenuItem[] menuItemDeltahedra = new JMenuItem[8];
	private JMenuItem menuItemOpen = new JMenuItem(DeltaMod.res.getString("Open"));
	private JMenuItem menuItemSave = new JMenuItem(DeltaMod.res.getString("Save"));
	private JMenu menuView = new JMenu(DeltaMod.res.getString("View"));
	private JMenuItem menuItemViewReset = new JMenuItem(DeltaMod.res.getString("Reset"));
	public JRadioButtonMenuItem menuItemViewFlat = new JRadioButtonMenuItem( DeltaMod.res.getString("Flat") );
	public JRadioButtonMenuItem menuItemViewFlatLines = new JRadioButtonMenuItem( DeltaMod.res.getString("FlatLines"), true );
	public JRadioButtonMenuItem menuItemViewWireframe = new JRadioButtonMenuItem( DeltaMod.res.getString("Wireframe") );
	
	private JToolBar toolbar = new JToolBar();
	public JToggleButton[] oprButtons = new JToggleButton[7];
	public MainScreen mainScreen;
	public OpsPanel opspanel;
	private StatusBar statusBar;
	public JSplitPane splitpane;

	private String lastDirectory;
	
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
		
		mainScreen = new MainScreen();
		mainScreen.setModel(DeltaMod.doc.getModel());

		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, opspanel, mainScreen);
		getContentPane().add(splitpane, BorderLayout.CENTER);
		
		updateStatusBar();
	}
	
	public void initMenu() {
		
		JMenuBar menuBar = new JMenuBar();
		
		menuItemDeltahedra[0] = new JMenuItem(DeltaMod.res.getString("C4"));
		menuItemDeltahedra[1] = new JMenuItem(DeltaMod.res.getString("C6"));
		menuItemDeltahedra[2] = new JMenuItem(DeltaMod.res.getString("C8"));
		menuItemDeltahedra[3] = new JMenuItem(DeltaMod.res.getString("C10"));
		menuItemDeltahedra[4] = new JMenuItem(DeltaMod.res.getString("C12"));
		menuItemDeltahedra[5] = new JMenuItem(DeltaMod.res.getString("C14"));
		menuItemDeltahedra[6] = new JMenuItem(DeltaMod.res.getString("C16"));
		menuItemDeltahedra[7] = new JMenuItem(DeltaMod.res.getString("C20"));
		
		ActionListener initAction = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for (int i=0; i<menuItemDeltahedra.length; i++) {
					if (e.getSource() == menuItemDeltahedra[i]) {
						DeltaMod.doc.setModel(OBJStream.load(getClass().getResourceAsStream("/obj/0"+ (i+1) +".obj")));
					}
				}
				ModelOps.setConstants(DeltaMod.doc.getModel());
				mainScreen.setModel(DeltaMod.doc.getModel());
				updateStatusBar();
			}
		};
		
		menuBar.add(menuFile);
		menuFile.setMnemonic('f');
		menuFile.add(menuNew);
		for (int i=0; i<menuItemDeltahedra.length; i++) {
			menuNew.add(menuItemDeltahedra[i]);
			menuItemDeltahedra[i].addActionListener(initAction);
		}
		
		menuFile.add(menuItemOpen);
		menuItemOpen.addActionListener(this);
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		menuFile.add(menuItemSave);
		menuItemSave.addActionListener(this);
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));

		menuBar.add(menuView);
		menuView.add(menuItemViewReset);
		menuItemViewReset.addActionListener(this);
		menuView.addSeparator();
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
				
		ItemListener listener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JToggleButton tb = (JToggleButton) e.getSource();
				if (tb.isSelected()) {
					if (tb == oprButtons[0])
						opspanel.show("Connect");
					else if (tb == oprButtons[1])
						opspanel.show("Elongate");
					else if (tb == oprButtons[2])
						opspanel.show("Tuck");
					else if (tb == oprButtons[3])
						opspanel.show("Fill");
					else if (tb == oprButtons[4])
						opspanel.show("Divide");
					else if (tb == oprButtons[5])
						opspanel.show("Optimize");
					else if (tb == oprButtons[6])
						opspanel.show("Assemble");
				}
			}
		};
		
		oprButtons[0] = new JToggleButton(DeltaMod.imgres.getImage("add"), true);
		oprButtons[1] = new JToggleButton(DeltaMod.imgres.getImage("elongate"));
		oprButtons[2] = new JToggleButton(DeltaMod.imgres.getImage("tuck"));
		oprButtons[3] = new JToggleButton(DeltaMod.imgres.getImage("fill"));
		oprButtons[4] = new JToggleButton(DeltaMod.imgres.getImage("divide"));
		oprButtons[5] = new JToggleButton(DeltaMod.imgres.getImage("optimize"));
		oprButtons[6] = new JToggleButton(DeltaMod.imgres.getImage("assemble"));

		ButtonGroup oprButtonGroup = new ButtonGroup();
		for (int i=0; i<oprButtons.length; i++) {
			oprButtonGroup.add(oprButtons[i]);
			toolbar.add(oprButtons[i]);
			//buttonOps[i].addChangeListener(opsChange);
			oprButtons[i].addItemListener(listener);
		}
		
		getContentPane().add(toolbar, BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItemOpen) {
			open();
		} else if (e.getSource() == menuItemSave) {
			save();
		} else if (e.getSource() == menuItemViewReset) {
			mainScreen.camera.reset();
			mainScreen.repaint();
		} 
		mainScreen.repaint();
	}

	private void open() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(FileFilterEx.OBJ);
		fileChooser.addChoosableFileFilter(FileFilterEx.PCODE);
		fileChooser.setFileFilter(FileFilterEx.OBJ);
		if (lastDirectory != null)
			fileChooser.setCurrentDirectory(new File(lastDirectory));
		
		int selected = fileChooser.showOpenDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			String suffix = getSuffix(file.getName());
			System.out.println(suffix);

			lastDirectory = file.getPath();
			
			// obj
			if (fileChooser.getFileFilter() == FileFilterEx.OBJ || suffix.compareTo("obj") == 0) {
				try {
					DeltaMod.doc.setModel(OBJStream.load(file.getAbsolutePath()));
					this.setTitle(DeltaMod.res.getString("Title") + ": " + file.getName());
					ModelOps.setConstants(DeltaMod.doc.getModel());
					mainScreen.setModel(DeltaMod.doc.getModel());
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
					mainScreen.setModel(DeltaMod.doc.getModel());
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
		JCheckBox cbxMaterial = new JCheckBox("Export Material");
		fileChooser.setAccessory(cbxMaterial);
		fileChooser.addChoosableFileFilter(FileFilterEx.OBJ);
		fileChooser.addChoosableFileFilter(FileFilterEx.SVG);
		fileChooser.addChoosableFileFilter(FileFilterEx.GraphML);
		fileChooser.addChoosableFileFilter(FileFilterEx.M);
		fileChooser.setFileFilter(FileFilterEx.OBJ);
		if (lastDirectory != null)
			fileChooser.setCurrentDirectory(new File(lastDirectory));
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
					if (cbxMaterial.isSelected())
						OBJStream.saveWithMaterial(filePath, DeltaMod.doc.getModel());
					else
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
		mainScreen.repaint();
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
