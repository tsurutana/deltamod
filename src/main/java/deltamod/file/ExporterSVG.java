package deltamod.file;


import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;

import deltamod.geom.*;


public class ExporterSVG {

	public static void export(Model3D m, String filepath) throws Exception{
		int width = 600, height = 600;

		FileWriter fw = new FileWriter(filepath);
		BufferedWriter bw = new BufferedWriter(fw);

		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(6);
		df.setMinimumFractionDigits(1);

		// 
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"); bw.newLine();
		bw.write("<svg"); bw.newLine();
		bw.write("   xmlns:svg=\"http://www.w3.org/2000/svg\"");  bw.newLine();
		bw.write("   xmlns=\"http://www.w3.org/2000/svg\"");  bw.newLine();
		bw.write("   version=\"1.0\"");  bw.newLine();
		bw.write("   width=\"" + width + "\"");  bw.newLine();
		bw.write("   height=\""+ height + "\">");  bw.newLine();
		bw.write("  <g>");  bw.newLine();


		for (int i=m.faces.size()-1; i>=0; i--) {
			Face3D f = m.faces.get(i);
			
			// do not output hidden faces
			if (f.n.z < 0)
				continue;
			
			Halfedge3D he = f.halfedges.get(0);
			bw.write("<path d=\"M ");
			bw.write(df.format(he.vertex.screen.x) + " " + df.format(he.vertex.screen.y));
			bw.write(" L");
			for (int j=1, n=f.halfedges.size(); j<n; j++){
				he = f.halfedges.get(j);
				bw.write(" " + df.format(he.vertex.screen.x) + " " + df.format(he.vertex.screen.y));
			}
			bw.write(" Z\" fill=\"");
			
			// face color
			
			Color c;
			float v;
			//float v = (float)(tmpn.z);
			v = (f.n.z > 0) ? (float)f.n.z : (float)-f.n.z;
			// brighter
			v += (1.0 - v) * 0.5;
			c = Color.getHSBColor(0.58f, 0.08f, v); // blue
			
			bw.write(String.format("#%06x", (c.getRed()<<16)|(c.getGreen()<<8)|c.getBlue()));
			bw.write("\" stroke=\"black\" stroke-width=\"1\" stroke-linejoin=\"round\"");
			//bw.write(" fill-opacity=\"0.8\"");
			bw.write("/>");
			bw.newLine();
		}
		//    fill="none" stroke="red" stroke-width="3"/>

		bw.write(" </g>\n"); // 
		bw.write("</svg>\n"); //
		bw.close();
	}
}
