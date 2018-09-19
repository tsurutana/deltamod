package deltamod.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.text.DecimalFormat;

import deltamod.geom.*;
import deltamod.render.Material;

/**
 * OBJ importer & exporter
 * 
 * @author tsuruta
 *
 */

public class OBJStream {

	public static Model3D load(String filepath) {
		Model3D m = new Model3D(); // initialize the model
		InputStream is;
		Vertex3D minV = new Vertex3D(Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE);
		Vertex3D maxV = new Vertex3D(-Double.MAX_VALUE, -Double.MAX_VALUE,
				-Double.MAX_VALUE);

		try {
			// create filestream and tokenizer
			is = new FileInputStream(filepath);
			Reader r = new BufferedReader(new InputStreamReader(is));
			StreamTokenizer st = new StreamTokenizer(r);

			// read a token
			int token, count = 0;
			token = st.nextToken();
			while (token != StreamTokenizer.TT_EOF) {
				if (st.ttype == StreamTokenizer.TT_WORD) {
					if (st.sval.equals("v")) { // vertex
						st.nextToken();
						double x = st.nval; // x
						st.nextToken();
						double y = st.nval; // y
						st.nextToken();
						double z = st.nval; // z

						// update model size
						minV.p.x = Math.min(minV.p.x, x);
						minV.p.y = Math.min(minV.p.y, y);
						minV.p.z = Math.min(minV.p.z, z);
						maxV.p.x = Math.max(maxV.p.x, x);
						maxV.p.y = Math.max(maxV.p.y, y);
						maxV.p.z = Math.max(maxV.p.z, z);

						// add new vertex to the vertex list
						m.vertices.add(new Vertex3D(x, y, z, count++));
						st.nextToken();
					} else if (st.sval.equals("f")) { // face
						Face3D f = new Face3D();
						while ((token = st.nextToken()) == StreamTokenizer.TT_NUMBER) {
							// add vertex to the face
							f.addVertex(m.vertices.get((int) st.nval - 1));
						}
						f.setHalfedgeLoopRelations();
						m.faces.add(f);
					} else {
						st.nextToken();
					}
				} else {
					st.nextToken();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		m.makeEdges();
		m.normalize();

		m.setNormals();
		return m;
	}

	public static Model3D load(InputStream is) {
		Model3D m = new Model3D(); // initialize the model

		Vertex3D minV = new Vertex3D(Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE);
		Vertex3D maxV = new Vertex3D(-Double.MAX_VALUE, -Double.MAX_VALUE,
				-Double.MAX_VALUE);

		try {
			// create tokenizer

			Reader r = new BufferedReader(new InputStreamReader(is));
			StreamTokenizer st = new StreamTokenizer(r);

			// read a token
			int token, count = 0;
			token = st.nextToken();
			while (token != StreamTokenizer.TT_EOF) {
				if (st.ttype == StreamTokenizer.TT_WORD) {
					if (st.sval.equals("v")) { // vertex
						st.nextToken();
						double x = st.nval; // x
						st.nextToken();
						double y = st.nval; // y
						st.nextToken();
						double z = st.nval; // z

						// update model size
						minV.p.x = Math.min(minV.p.x, x);
						minV.p.y = Math.min(minV.p.y, y);
						minV.p.z = Math.min(minV.p.z, z);
						maxV.p.x = Math.max(maxV.p.x, x);
						maxV.p.y = Math.max(maxV.p.y, y);
						maxV.p.z = Math.max(maxV.p.z, z);

						// add new vertex to the vertex list
						m.vertices.add(new Vertex3D(x, y, z, count++));
						st.nextToken();
					} else if (st.sval.equals("f")) { // face
						Face3D f = new Face3D();
						while ((token = st.nextToken()) == StreamTokenizer.TT_NUMBER) {
							// add vertex to the face
							f.addVertex(m.vertices.get((int) st.nval - 1));
						}
						f.setHalfedgeLoopRelations();
						m.faces.add(f);
					} else {
						st.nextToken();
					}
				} else {
					st.nextToken();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		m.makeEdges();
		m.normalize();

		m.setNormals();
		return m;
	}

	public static Model3D load(URL url) {
		System.out.println(url.toString());
		System.out.println(url.getPath());
		return load(url.toString());
	}

	public static boolean save(String filepath, Model3D model) {

		Model3D m = new Model3D(model);
		m.normalize();
		FileWriter fw;
		try {
			fw = new FileWriter(filepath);
			BufferedWriter bw = new BufferedWriter(fw);
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(8);
			df.setMinimumFractionDigits(1);
			df.setGroupingUsed(false);

			int i = 1;
			for (Vertex3D v : m.vertices) {
				v.index = i++;
				bw.write("v " + df.format(v.p.x) + " " + df.format(v.p.y) + " "
						+ df.format(v.p.z));
				bw.newLine();
			}
			for (Face3D f : m.faces) {
				bw.write("f");
				for (Halfedge3D he : f.halfedges) {
					bw.write(" " + he.vertex.index);
				}
				bw.newLine();
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean saveWithMaterial(String filepath, Model3D model) {
		try {
			FileWriter fw = new FileWriter(filepath);
			BufferedWriter bw = new BufferedWriter(fw);
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(8);
			df.setMinimumFractionDigits(1);
			df.setGroupingUsed(false);

			// export Material File
			String matpath = getMaterialFilePath(filepath);
			Material.export(matpath);
			String matname = new File(matpath).getName();
			bw.write("matlib " + matname);
			bw.newLine();

			int i = 1;
			for (Vertex3D v : model.vertices) {
				v.index = i++;
				bw.write("v " + df.format(v.p.x) + " " + df.format(v.p.y) + " "
						+ df.format(v.p.z));
				bw.newLine();
			}
			for (Face3D f : model.faces) {
				// マテリアルの設定
				bw.write("usemtl ");
				if (f.color == 1)
					bw.write("red");
				else if (f.color == 2)
					bw.write("green");
				else if (f.color == 3)
					bw.write("blue");
				bw.newLine();
				bw.write("f");
				for (Halfedge3D he : f.halfedges) {
					bw.write(" " + he.vertex.index);
				}
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static String getMaterialFilePath(String filepath) {
		int point = filepath.lastIndexOf(".");
		if (point != -1) {
			return filepath.substring(0, point) + ".mtl";
		}
		return filepath + ".mtl";
	}
}
