package deltamod.comp;

import javax.swing.JPanel;

public abstract class AbstractOpsPanel extends JPanel {

	public abstract void perform();
	public void panelShown() {
	}
}
