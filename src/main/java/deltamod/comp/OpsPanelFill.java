package deltamod.comp;


import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import deltamod.DeltaMod;
import deltamod.geom.ModelOps;

public class OpsPanelFill extends AbstractOpsPanel {

	JLabel lblDescription = new JLabel(DeltaMod.res.getString("FillDescription"));
	
	JCheckBox ckbQuads = new JCheckBox(DeltaMod.res.getString("FillQuads"), true);
	JCheckBox ckbPentagons = new JCheckBox(DeltaMod.res.getString("FillPentagons"), true);
	JCheckBox ckbHexagons = new JCheckBox(DeltaMod.res.getString("FillHexagons"), true);
	
	JCheckBox ckbInside = new JCheckBox(DeltaMod.res.getString("FillInside"));
	
	public OpsPanelFill() {
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(ckbQuads)
				.addComponent(ckbPentagons)
				.addComponent(ckbHexagons)
				.addComponent(ckbInside));
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(ckbQuads));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(ckbPentagons));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(ckbHexagons));
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(ckbInside));
		
		layout.setVerticalGroup(vGroup);
		
		setLayout(new BorderLayout());
		add(lblDescription, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
	}
	
	@Override
	public void perform() {
		int d = 1;
		if (ckbInside.isSelected())
			d = -1;
		if (ckbQuads.isSelected()) {
			ModelOps.fillQuadsWithTriangles(DeltaMod.doc.getModel(), d);
		}
		if (ckbPentagons.isSelected()) {
			ModelOps.fillPentagonsWithTriangles(DeltaMod.doc.getModel(), d);
		}
		if (ckbHexagons.isSelected()) {
			ModelOps.fillHexagonsWithTriangles(DeltaMod.doc.getModel());
		}
	}

}
