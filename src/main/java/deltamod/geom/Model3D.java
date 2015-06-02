

package deltamod.geom;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * 3D model
 * @author tsuruta
 *
 */

public class Model3D {

	public double length;

	public ArrayList<Vertex3D> vertices = new ArrayList<Vertex3D>();
	public ArrayList<Face3D> faces = new ArrayList<Face3D>();
	public ArrayList<Edge3D> edges = new ArrayList<Edge3D>() {
		public boolean add(Edge3D e) {
			for (int i=0, n=size(); i<n; i++) {
				Edge3D kn = get(i);
				if (e.equals(kn)) {
					kn.setHalfedge(e.he[0]);
					return false;
				}
			}
			return super.add(e);
		}
	};

	public Model3D(){
	}

	public Model3D(Model3D m){
		int index = 0;
		for (Vertex3D v : m.vertices) {
			v.index = index++;
			vertices.add(new Vertex3D(v));
		}
		for (Face3D f : m.faces) {
			Face3D f_copy = new Face3D(f);
			for (int i=0, n=f.halfedges.size(); i<n; i++)
				f_copy.halfedges.add( new Halfedge3D(f_copy, vertices.get(f.halfedges.get(i).vertex.index)) );
			f_copy.setHalfedgeLoopRelations();
			faces.add(f_copy);
		}
		makeEdges();
		setMeanEdgeLength();
	}

	public void makeEdges(){
		edges.clear();
		for (Vertex3D v : vertices)
			v.halfedges.clear();
		for (Face3D f : faces) {
			for (Halfedge3D he : f.halfedges) {
				Edge3D e0 = new Edge3D(he);
				edges.add(e0);
				he.vertex.halfedges.add(he);
			}
		}
		setMeanEdgeLength();
	}
	
	public void setMeanEdgeLength() {
		length = 0.0;
		for (Edge3D e: edges){
			length += e.getLength();
		}
		length /= edges.size();
	}

	/// get maximum difference of edge lengths
	public double getMaximumError() {
		double max_error = 0.0;
		for (Edge3D e : edges) {
			double d = GeomUtil.distance(e.sv.p, e.ev.p) - length;
			d = (d < 0) ? -d : d;
			if (d > max_error)
				max_error = d;
		}
		return max_error;
	}
	/// has only triangle face
	public boolean isTriangleMesh() {
		for (Face3D f : faces) {
			if (f.halfedges.size() != 3)
				return false;
		}
		return true;
	}
	
	public void setNormals() {
		for (Face3D f : faces)
			f.setNormal();
		for (Vertex3D v : vertices)
			v.setNormal();
	}
	
	/// normalize
	public void normalize(){
		setMeanEdgeLength();
		final double s = Math.sqrt(1.0 / length);
		for (Vertex3D v : vertices) {
			v.p.scale(s);
		}
		length = 1.0;
	}
	/// scaling
	public void scale(double s){
		for (Vertex3D v : vertices) {
			v.p.scale(s);
		}
		length *= s;
	}

	/// remove overlapped face (when incenters are at the same position)
	public void removeOverlappedFaces () {
		int i=faces.size()-1;
		for (Face3D f : faces)
			f.setIncenter();
		while (i > 0) {
			Face3D f1 = faces.get(i);
			int j=i-1;
			while (j > 0) {
				Face3D f2 = faces.get(j);
				if (GeomUtil.distance(f1.incenter, f2.incenter) < GeomUtil.EPS) {
					faces.remove(f1);
					faces.remove(f2);
					i -= 2;
					j = i;
				}
				j--;
			}
			i--;
		}
	}
	
	
	/// remove isolated vertex
	public void removeIsolatedVertices (){
		for (Face3D f : faces) {
			for (int i=0, n=f.halfedges.size(); i<n; i++)
				f.halfedges.get(i).vertex.checkFlg = true;
		}
		ListIterator<Vertex3D> ite = vertices.listIterator();
		while (ite.hasNext()) {
			Vertex3D v = ite.next();
			if (!v.checkFlg)
				ite.remove();
			else
				v.checkFlg = false;
		}
	}

	/// remove selected faces
	public void removeSelectedFaces (){
		ListIterator<Face3D> ite = faces.listIterator();
		while (ite.hasNext()) {
			Face3D f = ite.next();
			if (f.isSelected)
				ite.remove();
		}
	}
	
	public String getGeometricInformation() {
		String str = "";
		str += "Face : " + faces.size() + 
		"  Edge : " + edges.size() + 
		"  Vertex : " + vertices.size();
		return str;
	}
	public String getDeltahedronInformation() {
		if (isTriangleMesh()) {
			return "Mesh: triangle, Error: " + getMaximumError();
		}
		return "Mesh: polygonal, Error: " + getMaximumError();
	}
	
	public void write() {
		for (Face3D f : faces) {
			System.out.println(f + "  size: " + f.halfedges.size() );
		}
		System.out.println("------");
		for (Face3D f : faces) {
			for (Halfedge3D he : f.halfedges) {
				System.out.println(f + "  : " + he.prev + " " + he + " " + he.next );
			}
		}
		System.out.println("------");
		for (Edge3D e : edges) {
			System.out.println(e.he[0] + " " + e + " " + e.he[1] );
		}
	}
	
	public double getArea(){
		double area = length * length * Math.sqrt(3) / 4;
		area *= faces.size();
		return area;
	}
	public double getVolume(){
		double volume = 0;
		for (Face3D f : faces) {
			Vec3D p1 = f.halfedges.get(0).vertex.p;
			Vec3D p2 = f.halfedges.get(1).vertex.p;
			Vec3D p3 = f.halfedges.get(2).vertex.p;

			final double v321 = p3.x * p2.y * p1.z;
			final double v231 = p2.x * p3.y * p1.z;
			final double v312 = p3.x * p1.y * p2.z;
			final double v132 = p1.x * p3.y * p2.z;
			final double v213 = p2.x * p1.y * p3.z;
			final double v123 = p1.x * p2.y * p3.z;
			volume += (1.0/6.0) * (-v321 + v231 + v312 - v132 - v213 + v123);
		}
		return volume;
	}
	// surface ratio (compared to a sphere which has the same volume)
	public double getSphericity(){
		final double vp = getVolume();
		final double ap = getArea();
		double tmp = Math.cbrt(6 * vp);
		final double as = Math.cbrt(Math.PI) * tmp * tmp;
		return as/ap;
	}

	/**
	 * coloring functions
	 */
	// backtracking algorithm
	public void coloring(){
		// init
		long start = System.currentTimeMillis();
		for (Edge3D e: edges) {
			e.color = 0;
		}
		for (int i=0; i<3; i++){
			edges.get(i).color = i+1;
		}
		recursiveColoring(3);
		long end = System.currentTimeMillis();
		System.out.println("processing time [ms]："+ (end - start));
	}
	// get unused color by calculating XOR with adjacent edges 
	private boolean recursiveColoring(int n){
		if (n >= edges.size()) {
			return true;
		}

		Edge3D e = edges.get(n);

		int adcolor[] = new int[4];
		int oldColor;
		int c = 0;

		adcolor[0] = e.he[0].prev.edge.color;
		adcolor[1] = e.he[0].next.edge.color;
		adcolor[2] = e.he[1].prev.edge.color;
		adcolor[3] = e.he[1].next.edge.color;

		for (int i=0; i<4; i++){
			if (adcolor[i] < 3)
				c |= adcolor[i];
			else
				c |= 4;
		}
		System.out.println(n+ "  "+c);
		oldColor = e.color;
		if (c == 0) {
			for (int i=1; i<4; i++) {
				e.color = i;
				if (recursiveColoring(n+1))
					return true;
				e.color = oldColor;
			}
		} else if (c == 1) {
			for (int i=2; i<4; i++) {
				e.color = i;
				if (recursiveColoring(n+1))
					return true;
				e.color = oldColor;
			}
		} else if (c == 2) {
			for (int i=1; i<4; i+=2) {
				e.color = i;
				if (recursiveColoring(n+1))
					return true;
				e.color = oldColor;
			}
		} else if (c == 4) {
			for (int i=1; i<3; i++) {
				e.color = i;
				if (recursiveColoring(n+1))
					return true;
				e.color = oldColor;
			}
		} else if (c == 7) {
		} else {
			if (c == 3)
				e.color = 3;
			else if (c == 5)
				e.color = 2;
			else if (c == 6)
				e.color = 1;
			if (recursiveColoring(n+1))
				return true;
			e.color = oldColor;
		}
		return false;
	}
}