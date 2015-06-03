package deltamod.comp;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import deltamod.DeltaMod;
import deltamod.geom.Model3D;
import deltamod.geom.Optimization;

public class OpsPanelOptimize extends AbstractOpsPanel {

	JLabel lblDescription = new JLabel(DeltaMod.res.getString("OptimizeDescription"));
	
	JLabel lblNumOfIterationInput = new JLabel(DeltaMod.res.getString("NumOfIteration"));
	JComboBox<Integer> cbxNumOfIterationInput;
	//JLabel lblCapableErrorInput = new JLabel(DeltaMod.res.getString("AcceptableError"));
	//JComboBox<Double> cbxCapableErrorInput;
	
	public OpsPanelOptimize() {
		
		cbxNumOfIterationInput = createJComboBox(100, 1000);
		//cbxCapableErrorInput = createJComboBox(1.0e-8, 1.0e-4);
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(lblNumOfIterationInput));
				//.addComponent(lblCapableErrorInput));
		//hGroup.addGap(50);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(cbxNumOfIterationInput));
				//.addComponent(cbxCapableErrorInput));
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblNumOfIterationInput)
				.addComponent(cbxNumOfIterationInput));
		//vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				//.addComponent(lblCapableErrorInput)
				//.addComponent(cbxCapableErrorInput));
		
		// register to layout manager
		layout.setVerticalGroup(vGroup);
		
		setLayout(new BorderLayout());
		add(lblDescription, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
	}
	
	@Override
	public void perform() {

		Optimization optv = new Optimization(DeltaMod.doc.getModel(), 1.0e-12);
		//Double.parseDouble(cbxCapableErrorInput.getSelectedItem().toString()) );
		Model3D m = optv.optimize(Integer.parseInt(cbxNumOfIterationInput.getSelectedItem().toString()));
		//m = opt.optimize( );
		
		if (m != null) {
			DeltaMod.doc.setModel(m);
			DeltaMod.mainFrame.mainScreen.setModel(m);
			DeltaMod.mainFrame.updateStatusBar();
		} else {
			System.out.println("null");
		}
	}
	
	private JComboBox<Integer> createJComboBox(int min, int max) {
		JComboBox<Integer> combo = new JComboBox<Integer>();
		combo.setEditable(true);
		for (int i=min; i<=max; i+=100) {
			combo.addItem(i);
		}
		return combo;
	}
	/*
	private JComboBox<Double> createJComboBox(double min, double max) {
		JComboBox<Double> combo = new JComboBox<Double>();
		combo.setEditable(true);
		for (double i=min; i<=max; i*=10) {
			combo.addItem(i);
		}
		return combo;
	}*/

}
