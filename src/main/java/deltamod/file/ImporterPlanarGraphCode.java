package deltamod.file;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import deltamod.geom.Face3D;
import deltamod.geom.Model3D;
import deltamod.geom.Vertex3D;

/**
 * Importer for Planar code by Plantri
 * 
 * Planar code is in binary format which begins with ">>planar_code<<".
 * A number of vertices in the graph comes next after the header.
 * Graph structure is written as follows:
 * A list of vertices connected to the FIRST vertex in clockwise order,
 * separator '0',
 * A list of vertices connected to the SECOND vertex in clockwise order,
 * separator '0' and
 * repeat this for all vertices.
 * （ex: 2 3 4 0 1 3 4...） 
 * 
 * @author tsuruta
 *
 */

public class ImporterPlanarGraphCode {

	public static Model3D load(String filepath, int line) {
		Model3D m = new Model3D();
		InputStream is;

		try  {
			// create filestream and tokenizer
			is = new FileInputStream( filepath );
			int ch;
			final char[] PCODE = ">>planar_code<<".toCharArray();
			
			// empty loop for header
			for (int i=0, n=PCODE.length; i<n; i++) {
				if ((ch = is.read()) != -1)
					System.out.print((char) ch);
			}
			System.out.println();
			// read a graph data
			for (int i=0; i<line; i++) {
				int n = is.read();
				for (int j=0; j<n; )
					if (is.read() == 0)
						j++;
			}
			if ((ch = is.read()) != -1) {
				int n = ch;   // n vertices in a graph
				
				for (int i=0; i<n; i++) {
					// random
					/*double x = Math.random() - 0.5;
					double y = Math.random() - 0.5;
					double z = Math.random() - 0.5;*/
					
					
					Vertex3D v = new Vertex3D(0, 0, 0, i);
					m.vertices.add(v);
				}
				//Adjacency List
				int[][] mat = new int[n][];  //2d variable array
				
				ArrayList<Integer> adj = new ArrayList<Integer>();
				for (int i=0; i<n; i++) {
					adj.clear();
					// 0 means separator
					while ((ch = is.read()) > 0) {
						adj.add(ch-1);
					}
					// keep the order of vertices
					mat[i] = new int[adj.size()];
					for (int j=0, size=adj.size(); j<size; j++)
						mat[i][j] = adj.get(j);
				}
				// create a face
				for (int i=0; i<n; i++) {
					double x, y, z;
					Vertex3D v0 = m.vertices.get(i);
					if (i==0) {
						x = z = 0;
						y = 0.5;
						v0.p.set(x, y, z);
						v0.checkFlg = true;
					} 
					for (int j=0, size=mat[i].length; j<size; j++) {
						Face3D f = new Face3D();
						int x0 = mat[i][j];
						int x1 = mat[i][((j+1)%size)];
						Vertex3D v1 = m.vertices.get(x0);
						Vertex3D v2 = m.vertices.get(x1);
						f.addVertex(v0);
						f.addVertex(v1);
						f.addVertex(v2);
						f.setHalfedgeLoopRelations();
						if (!isDuplicateFace(m.faces, f))
							m.faces.add(f);
						
						if (i == 0) {
							double angle = (2 * Math.PI / size * j);
							x = Math.sin(angle) * 0.5;
							z = Math.cos(angle) * 0.5;
							y = 0.0;
							v1.p.set(x, y, z);
							v1.checkFlg = true;
						}
					}
				}
				for (Vertex3D v: m.vertices) {
					if (!v.checkFlg) {
						double x, y, z;
						x = Math.random() - 0.5;
						z = Math.random() - 0.5;
						y = -1.0;
						v.p.set(x, y, z);
					} else
						v.checkFlg = false;
				}
			}
			is.close();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		m.makeEdges();
		m.setNormals();
		return m;
	}
	
	// duplication check
	private static boolean isDuplicateFace(ArrayList<Face3D> faces, Face3D fin) {
		for (Face3D f: faces)
			if (f.equals(fin))
				return true;
		return false;
	}
}
