package deltamod.comp;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import deltamod.DeltaMod;
import deltamod.file.FileFilterEx;
import deltamod.file.OBJStream;
import deltamod.geom.Model3D;
import deltamod.geom.ModelOps;

public class OpsPanelConnect extends AbstractOpsPanel implements ActionListener {
	
	JLabel lblDescription = new JLabel(DeltaMod.res.getString("ConnectDescription"));
	
	JRadioButton rdbAddTetrahedron = new JRadioButton(DeltaMod.res.getString("AddTetra"), true);
	JRadioButton rdbAddOctahedron = new JRadioButton(DeltaMod.res.getString("AddOcta"));
	JRadioButton rdbAddIcosahedron = new JRadioButton(DeltaMod.res.getString("AddIcosa"));
	JRadioButton rdbAddModel = new JRadioButton(DeltaMod.res.getString("AddModel"));
	
	JButton buttonLoad = new JButton(DeltaMod.res.getString("Load"));
	
	JCheckBox chkboxSubtract = new JCheckBox(DeltaMod.res.getString("Subtract"));
	
	MainScreen subScreen;
	
	public OpsPanelConnect() {
		
		ButtonGroup objGroup = new ButtonGroup();
		objGroup.add(rdbAddTetrahedron);
		objGroup.add(rdbAddOctahedron);
		objGroup.add(rdbAddIcosahedron);
		objGroup.add(rdbAddModel);
		
		rdbAddTetrahedron.addActionListener(this);
		rdbAddOctahedron.addActionListener(this);
		rdbAddIcosahedron.addActionListener(this);
		rdbAddModel.addActionListener(this);
		
		buttonLoad.addActionListener(this);
		buttonLoad.setEnabled(false);
		
		subScreen = new MainScreen();
		subScreen.setModel(OBJStream.load(getClass().getResourceAsStream("/obj/01.obj")));
		
		// create group layout
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		// gap settings
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// horizontal group
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		//hGroup.addGap(100);
		// add components
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(rdbAddTetrahedron)
				.addComponent(rdbAddOctahedron)
				.addComponent(rdbAddIcosahedron)
				.addComponent(rdbAddModel));
				//.addComponent(chkboxSubtract));
		hGroup.addGap(40);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(buttonLoad));
		
		// register to layout manager
		layout.setHorizontalGroup(hGroup);

		// vertical group
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(rdbAddTetrahedron));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(rdbAddOctahedron));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(rdbAddIcosahedron));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(rdbAddModel)
				.addComponent(buttonLoad));
		vGroup.addGap(10);
		//vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		//		.addComponent(chkboxSubtract));
		
		
		layout.setVerticalGroup(vGroup);
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		north.add(lblDescription, BorderLayout.NORTH);
		north.add(panel, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(north, BorderLayout.NORTH);
		add(subScreen, BorderLayout.CENTER);
	}
	
	private void open() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./"));
		fileChooser.addChoosableFileFilter(FileFilterEx.OBJ);
		int selected = fileChooser.showOpenDialog(this);
		if (selected == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try {
				subScreen.setModel(OBJStream.load(file.getAbsolutePath()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if (selected == JFileChooser.CANCEL_OPTION){
			System.out.println("file select canceled");
		}else if (selected == JFileChooser.ERROR_OPTION){
			System.out.println("file open error");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonLoad) {
			open();
		} else if (e.getSource() == rdbAddModel) {
			buttonLoad.setEnabled(true);
		} else {
			if (e.getSource() == rdbAddTetrahedron) {
				subScreen.setModel(OBJStream.load(getClass().getResourceAsStream("/obj/01.obj")));
			} else if (e.getSource() == rdbAddOctahedron) {
				subScreen.setModel(OBJStream.load(getClass().getResourceAsStream("/obj/03.obj")));
			} else if (e.getSource() == rdbAddIcosahedron) {
				subScreen.setModel(OBJStream.load(getClass().getResourceAsStream("/obj/08.obj")));
			}
			buttonLoad.setEnabled(false);
			
		}
		subScreen.repaint();
	}

	@Override
	public void perform() {
		Model3D m = DeltaMod.doc.getModel();
		if (rdbAddTetrahedron.isSelected()) {
			ModelOps.addTetrahedron(m);
		} else if (rdbAddOctahedron.isSelected()) {
			ModelOps.addOctahedron(m);
		} else if (rdbAddIcosahedron.isSelected()) {
			ModelOps.addIcosahedron(m);
		} else if (rdbAddModel.isSelected()) {
			if (chkboxSubtract.isSelected())
				ModelOps.subModel(m, subScreen.getModel());
			else
				ModelOps.addModel(m, subScreen.getModel());
		}
	}
}
