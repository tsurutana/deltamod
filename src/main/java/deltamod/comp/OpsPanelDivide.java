package deltamod.comp;

import java.awt.BorderLayout;


import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import deltamod.DeltaMod;
import deltamod.geom.ModelOps;

public class OpsPanelDivide extends AbstractOpsPanel {

	JLabel lblDescription = new JLabel(DeltaMod.res.getString("DivDescription"));
	JLabel lblNumOfDivInput = new JLabel(DeltaMod.res.getString("NumOfDivision"));
	JComboBox<Integer> cbxNumOfDivInput;
	
	public OpsPanelDivide() {
		
		cbxNumOfDivInput = createJComboBox(2, 10);
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(lblNumOfDivInput));
		hGroup.addGap(50);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(cbxNumOfDivInput));
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblNumOfDivInput)
				.addComponent(cbxNumOfDivInput));
		
		
		layout.setVerticalGroup(vGroup);
		
		setLayout(new BorderLayout());
		add(lblDescription, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
	}
	
	@Override
	public void perform() {
		if (ModelOps.divideFaces(
				Integer.parseInt(cbxNumOfDivInput.getSelectedItem().toString()), 
				DeltaMod.doc.getModel()))
			DeltaMod.mainFrame.mainscreen.camera.scale *= 0.5;
		
	}
	
	private JComboBox<Integer> createJComboBox(int min, int max) {
		JComboBox<Integer> combo = new JComboBox<Integer>();
		combo.setEditable(true);
		for (int i=min; i<=max; i++) {
			combo.addItem(i);
		}
		return combo;
	}

}
