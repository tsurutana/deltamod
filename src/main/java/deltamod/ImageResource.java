package deltamod;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class ImageResource {

	private static ImageResource instance;
	private Map<String, ImageIcon> images = new HashMap<String, ImageIcon>();

	private ImageResource() {
		ClassLoader loader = getClass().getClassLoader();
		ImageIcon img;
		// Operations
		img = new ImageIcon(loader.getResource("icons/add.png"));
		images.put("add", img);
		img = new ImageIcon(loader.getResource("icons/elongate.png"));
		images.put("elongate", img);
		img = new ImageIcon(loader.getResource("icons/tuck.png"));
		images.put("tuck", img);
		img = new ImageIcon(loader.getResource("icons/fill.png"));
		images.put("fill", img);
		img = new ImageIcon(loader.getResource("icons/divide.png"));
		images.put("divide", img);
		img = new ImageIcon(loader.getResource("icons/optimize.png"));
		images.put("optimize", img);
		
	}

	public static ImageResource getInstance() {
		if(instance == null){ 
			instance = new ImageResource();
		}
		return instance;
	}

	public ImageIcon getImage(String str) {
		return images.get(str);
	}
}
