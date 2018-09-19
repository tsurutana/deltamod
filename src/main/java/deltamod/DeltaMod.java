package deltamod;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.UIManager;

import deltamod.comp.MainFrame;

public class DeltaMod {

	public static MainFrame mainFrame;
	public static Doc doc;
	public static ResourceBundle res;
	public static ImageResource imgres;

	// create a main frame
	public static void main(String argv[]) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					res = ResourceBundle.getBundle("deltamod.StringResource",
							Locale.ENGLISH);
				} catch (Exception e) {
					res = ResourceBundle.getBundle("deltamod.StringResource",
							Locale.JAPANESE);
				}
				imgres = ImageResource.getInstance();

				doc = new Doc();

				mainFrame = new MainFrame();
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.setPreferredSize(new Dimension(1600, 900));
				mainFrame.setTitle(res.getString("Title"));
				mainFrame.pack();
				mainFrame.setVisible(true);
			}

		});
	}
}
