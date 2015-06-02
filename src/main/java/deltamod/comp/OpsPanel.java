package deltamod.comp;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import deltamod.DeltaMod;
import deltamod.geom.Model3D;

public class OpsPanel extends JPanel {

	JPanel center = new JPanel();
	public CardLayout layout;

	public OpsPanel() {

		layout = new CardLayout();
		layout.setHgap(10);
		layout.setVgap(10);

		center.setLayout(layout);
		center.add(new OpsPanelConnect(), "Connect");
		center.add(new OpsPanelElongate(), "Elongate");
		center.add(new OpsPanelTuck(), "Tuck");
		center.add(new OpsPanelFill(), "Fill");
		center.add(new OpsPanelDivide(), "Divide");
		center.add(new OpsPanelOptimize(), "Optimize");
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		add(center, BorderLayout.CENTER);
	}

	public void show(String name) {
		layout.show(center, name);
		//this.setTitle(DeltaMod.res.getString(name) + " " + DeltaMod.res.getString("Operation"));
	}

	public void perform() {
		// get the current card
		Component[] c = center.getComponents();
		for (int i=0, n=c.length; i<n; i++) {
			if (c[i].isVisible()) {
				AbstractOpsPanel op = (AbstractOpsPanel) c[i];
				op.perform();
				Model3D m = DeltaMod.doc.getModel();
				//m.removeOverlappedFaces();
				m.removeIsolatedVertices();
				DeltaMod.mainFrame.statusBar.setMessage(1, m.getGeometricInformation());
				DeltaMod.mainFrame.statusBar.setMessage(2, m.getDeltahedronInformation());
				break;
			}
		}
	}
}
