package deltamod.file;


import java.io.BufferedWriter;
import java.io.FileWriter;

import deltamod.geom.Edge3D;
import deltamod.geom.Face3D;
import deltamod.geom.Model3D;
import deltamod.geom.Vertex3D;

/**
 * Model->GraphML（Line Graph）
 * @author tsuruta
 *
 */

public class ExporterGraphML {
	
	private static String borderStyle = "<y:BorderStyle hasColor=\"false\" type=\"line\" width=\"1.0\"/>";
	private static String nodeLabelStyle = "<y:NodeLabel alignment=\"center\" " +
			"autoSizePolicy=\"content\" " +
			"fontFamily=\"Dialog\" " +
			"fontSize=\"12\" " +
			"fontStyle=\"plain\" " +
			"hasBackgroundColor=\"false\" " +
			"hasLineColor=\"false\" " +
			"hasText=\"false\" " +
			"height=\"4.0\" " +
			"modelName=\"internal\" " +
			"modelPosition=\"c\" " +
			"textColor=\"#000000\" " +
			"visible=\"true\" " +
			"width=\"4.0\" " +
			"x=\"13.0\" y=\"13.0\"/>";
	private static int size = 30;  // node size
	private static int gap = 60;  // gap between nodes
	private static int row = 10;  // number of nodes per row
	
	public static void exportLineGraph(Model3D m, String filepath) throws Exception {
		
		if (m.edges.size() > 100) {
			size = 20;
			gap = 40;
			row = 15;
		}
		FileWriter fw = new FileWriter(filepath);
		BufferedWriter bw = new BufferedWriter(fw);
		
		writeBegin(bw);
		
		//output edges as nodes
		int count = 0;
		for (Edge3D e : m.edges) {
			e.index = count;
			String c = "#FF0000";
			if (e.color == 1)
				c = "#00FF00";
			else if (e.color == 2)
				c = "#0000FF";
			final int x = (count%row) * gap;
			final int y = (int) Math.floor(count/row) * gap;
			bw.write("    <node id=\"n" + count + "\">"); bw.newLine();
			bw.write("      <data key=\"d6\">"); bw.newLine();
			bw.write("        <y:ShapeNode>"); bw.newLine();
			bw.write("          <y:Geometry height=\"" + size + "\" width=\"" + size +
					"\" x=\"" + x + "\" y=\"" + y + "\"/>"); bw.newLine();
			bw.write("          <y:Fill color=\"" + c + "\" transparent=\"false\"/>"); bw.newLine();
			bw.write("          " + borderStyle ); bw.newLine();
			bw.write("          " + nodeLabelStyle ); bw.newLine();
			bw.write("          <y:Shape type=\"ellipse\"/>"); bw.newLine();
			bw.write("        </y:ShapeNode>"); bw.newLine();
			bw.write("      </data>"); bw.newLine();
			bw.write("    </node>"); bw.newLine();
			count++;
		}
		
		//output edge-relations as edges
		count = 0;
		for (Face3D f : m.faces) {
			int source = f.halfedges.get(0).edge.index;
			int target = f.halfedges.get(1).edge.index;
			writeEdge(bw, source, target, count++);
			target =  f.halfedges.get(2).edge.index;
			writeEdge(bw, source, target, count++);
			source = f.halfedges.get(1).edge.index;
			writeEdge(bw, source, target, count++);
		}
		
		writeEnd(bw);
		bw.close();
	}
	
public static void exportVertexGraph(Model3D m, String filepath) throws Exception {
		
		int size = 30, grid = 60, row = 10;
		if (m.edges.size() > 100) {
			size = 20;
			grid = 40;
			row = 15;
		}
		FileWriter fw = new FileWriter(filepath);
		BufferedWriter bw = new BufferedWriter(fw);
		
		writeBegin(bw);
		
		//output edges as nodes
		int count = 0;
		for (Vertex3D v : m.vertices) {
			v.index = count;
			String c = "#FF0000";
			final int x = (count%row) * grid;
			final int y = (int) Math.floor(count/row) * grid;
			bw.write("    <node id=\"n" + count + "\">"); bw.newLine();
			bw.write("      <data key=\"d6\">"); bw.newLine();
			bw.write("        <y:ShapeNode>"); bw.newLine();
			bw.write("          <y:Geometry height=\"" + size + "\" width=\"" + size +
					"\" x=\"" + x + "\" y=\"" + y + "\"/>"); bw.newLine();
			bw.write("          <y:Fill color=\"" + c + "\" transparent=\"false\"/>"); bw.newLine();
			bw.write("          " + borderStyle ); bw.newLine();
			bw.write("          " + nodeLabelStyle ); bw.newLine();
			bw.write("          <y:Shape type=\"ellipse\"/>"); bw.newLine();
			bw.write("        </y:ShapeNode>"); bw.newLine();
			bw.write("      </data>"); bw.newLine();
			bw.write("    </node>"); bw.newLine();
			count++;
		}
		
		//output edge-relations as edges
		count = 0;
		for (Edge3D e : m.edges) {
			int source = e.sv.index;
			int target = e.ev.index;
			writeEdge(bw, source, target, count++);
		}
		
		writeEnd(bw);
		bw.close();
	}
	
	private static void writeBegin(BufferedWriter bw)  throws Exception {
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"); bw.newLine();	
		bw.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"xmlns:y=\"http://www.yworks.com/xml/graphml\" " +
				"xmlns:yed=\"http://www.yworks.com/xml/yed/3\" " +
				"xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns " +
				"http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">"); bw.newLine();
		bw.write("  <key for=\"graphml\" id=\"d0\" yfiles.type=\"resources\"/>"); bw.newLine();
		bw.write("  <key for=\"port\" id=\"d1\" yfiles.type=\"portgraphics\"/>"); bw.newLine();
		bw.write("  <key for=\"port\" id=\"d2\" yfiles.type=\"portgeometry\"/>"); bw.newLine();
		bw.write("  <key for=\"port\" id=\"d3\" yfiles.type=\"portuserdata\"/>"); bw.newLine();
		bw.write("  <key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d4\"/>"); bw.newLine();
		bw.write("  <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d5\"/>"); bw.newLine();
		bw.write("  <key for=\"node\" id=\"d6\" yfiles.type=\"nodegraphics\"/>"); bw.newLine();
		bw.write("  <key attr.name=\"Description\" attr.type=\"string\" for=\"graph\" id=\"d7\"/>"); bw.newLine();
		bw.write("  <key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d8\"/>"); bw.newLine();
		bw.write("  <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d9\"/>"); bw.newLine();
		bw.write("  <key for=\"edge\" id=\"d10\" yfiles.type=\"edgegraphics\"/>"); bw.newLine();
		bw.write("  <graph edgedefault=\"directed\" id=\"G\">"); bw.newLine();
		bw.write("    <data key=\"d7\"/>"); bw.newLine();
	}
	
	private static void writeEnd(BufferedWriter bw)  throws Exception {
		bw.write("  </graph>"); bw.newLine();
		bw.write("  <data key=\"d0\">"); bw.newLine();
		bw.write("  <y:Resources/>"); bw.newLine();
		bw.write("  </data>"); bw.newLine();
		bw.write("</graphml>"); bw.newLine();
	}

	private static void writeEdge(BufferedWriter bw, int source, int target, int id) throws Exception {
		bw.write("    <edge id=\"e" + id + "\" source=\"n" + source + "\" target=\"n" + target + "\">"); bw.newLine();
		bw.write("      <data key=\"d10\">"); bw.newLine();
		bw.write("        <y:PolyLineEdge>"); bw.newLine();
		bw.write("          <y:Path sx=\"0.0\" sy=\"0.0\" tx=\"0.0\" ty=\"0.0\"/>"); bw.newLine();
		bw.write("          <y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>"); bw.newLine();
		bw.write("          <y:Arrows source=\"none\" target=\"none\"/>"); bw.newLine();
		bw.write("          <y:BendStyle smoothed=\"false\"/>"); bw.newLine();
		bw.write("        </y:PolyLineEdge>"); bw.newLine();
		bw.write("      </data>"); bw.newLine();
		bw.write("    </edge>"); bw.newLine();
	}
}
