package deltamod.comp;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import deltamod.DeltaMod;
import deltamod.geom.ModelOps;


public class OpsPanelTuck extends AbstractOpsPanel {
	
	JLabel lblDescription = new JLabel(DeltaMod.res.getString("TuckDescription"));
	
	public OpsPanelTuck() {
		setLayout(new BorderLayout());
		add(lblDescription, BorderLayout.NORTH);
	}
	
	@Override
	public void perform() {
		ModelOps.tuckCone(DeltaMod.doc.getModel());
	}

}
