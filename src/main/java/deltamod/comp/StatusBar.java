package deltamod.comp;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JToolBar;

public class StatusBar extends JToolBar {

	private JLabel[] labels; 
	
	public StatusBar(int fieldnum) {
		
		setPreferredSize(new Dimension(100, 30));
		setFloatable(false);
		
		labels = new JLabel[fieldnum];
		for (int i=0; i<fieldnum; i++) {
			labels[i] = new JLabel(" ");
			add(labels[i]);
			if (i + 1 < fieldnum)
				addSeparator();
		}
	}
	
	public void setMessage(int index, String text) {
		labels[index].setText(" " + text + "   ");
	}
}
