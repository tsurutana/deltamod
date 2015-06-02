package deltamod.comp;


import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import deltamod.DeltaMod;
import deltamod.geom.ModelOps;

public class OpsPanelElongate extends AbstractOpsPanel {

	JLabel lblDescription = new JLabel(DeltaMod.res.getString("ElongateDescription"));
	JRadioButton rdbElongate = new JRadioButton(DeltaMod.res.getString("Elongate"), true);
	JRadioButton rdbGyroelongate = new JRadioButton(DeltaMod.res.getString("Gyroelongate"));
	
	public OpsPanelElongate() {
		
		ButtonGroup rdbGroup = new ButtonGroup();
		rdbGroup.add(rdbElongate);
		rdbGroup.add(rdbGyroelongate);
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(rdbElongate)
				.addComponent(rdbGyroelongate));
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(rdbElongate));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(rdbGyroelongate));
		
		layout.setVerticalGroup(vGroup);
		
		setLayout(new BorderLayout());
		add(lblDescription, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
	}
	
	@Override
	public void perform() {
		if (rdbElongate.isSelected()) {
			ModelOps.elongate(DeltaMod.doc.getModel());
		} else if (rdbGyroelongate.isSelected()) {
			ModelOps.gyroelongate(DeltaMod.doc.getModel());
		}
	}

}
