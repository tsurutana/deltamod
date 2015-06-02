package deltamod.geom;

import java.awt.Color;

public class Edge3D {

	public Vertex3D sv = null;
	public Vertex3D ev = null;
	public Halfedge3D[] he = new Halfedge3D[2];

	public int color = 0; // variable for coloring
	public Color c = Color.black;  //color

	public int index = 0;

	public Edge3D(Vertex3D sv, Vertex3D ev) {
		this.sv = sv;
		this.ev = ev;
	}
	public Edge3D(Halfedge3D halfedge) {
		this.sv = halfedge.vertex;
		this.ev = halfedge.next.vertex;
		halfedge.edge = this;
		he[0] = halfedge;
	}

	public boolean contains(Vertex3D v) {
		if (sv == v)
			return true;
		if (ev == v)
			return true;
		return false;
	}

	public void setHalfedge(Halfedge3D halfedge) {
		halfedge.edge = this;
		he[1] = halfedge;

		he[0].pair = he[1];
		he[1].pair = he[0];
	}

	public double getLength() {
		//return sv.p.distanceTo(ev.p);
		return sv.p.squaredDistanceTo(ev.p);
	}
	
	public double getDihedralAngle() {
		// vertex that is not touched to the adjacent face
		Vec3D c0 = this.he[0].prev.vertex.p;
		if (this.he[1] == null)
			return 0;
		Vec3D c1 = this.he[1].prev.vertex.p;
		// perpendicular vector from mid-point of common edge
		Vec3D a0 = new Vec3D(sv.p);
		a0.add(ev.p);
		a0.scale(0.5);
		Vec3D a1 = new Vec3D(a0);
		a0.sub(c0);
		a1.sub(c1);
		return a0.angle(a1) * 180.0 / 3.141592653589793;
	}

	public boolean equals(Edge3D e) {
		if (e.contains(sv) && e.contains(ev))
			return true;
		return false;
	}

}
