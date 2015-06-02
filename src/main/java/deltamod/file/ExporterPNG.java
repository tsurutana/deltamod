package deltamod.file;

import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * RenderedImage->PNG (including alpha)
 * @author tsuruta
 *
 */

public class ExporterPNG {

	public static void export(RenderedImage image, String filepath) throws Exception {
		ImageIO.write(image, "png", new File(filepath));
	}
}
