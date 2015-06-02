package deltamod.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.NumberFormat;

import deltamod.geom.Face3D;
import deltamod.geom.Halfedge3D;
import deltamod.geom.Model3D;

public class ExporterLiveGraphics3D {

	public static void export(Model3D m, String filepath) throws Exception {
		FileWriter fw = new FileWriter(filepath);
		BufferedWriter bw = new BufferedWriter(fw);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(6);
		
		bw.write("Graphics3D[{"); bw.newLine();
		for (int i=0, fsize=m.faces.size(); i<fsize; i++) {
			Face3D f = m.faces.get(i);
			bw.write("Polygon[{");
			for (int j=0, n=f.halfedges.size(); j<n; j++) {
				Halfedge3D he = f.halfedges.get(j);
				bw.write("{");
				bw.write(nf.format(he.vertex.p.x));
				bw.write(", ");
				bw.write(nf.format(he.vertex.p.y));
				bw.write(", ");
				bw.write(nf.format(he.vertex.p.z));
				bw.write("}");
				if ((j+1)<n)
					bw.write(", ");
			}
			bw.write("}],");
			bw.newLine();
		}
		bw.write("},");
		bw.newLine();
		//Disable bounding box
		bw.write("{ Boxed -> False }]");
		bw.close();
	}
}
