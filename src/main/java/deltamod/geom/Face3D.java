package deltamod.geom;

import java.util.ArrayList;

public class Face3D {
	public ArrayList<Halfedge3D> halfedges = new ArrayList<Halfedge3D>(); // 面を構成する3つの頂点へのHalf-edge
	public double z;  // depth
	public Vec3D n = new Vec3D(); // normal
	public Vec3D incenter = new Vec3D();  // center of mass
	
	public int color = 0; // variable for coloring
	
	public boolean isSelected = false;
	public Halfedge3D halfedgeRefersToSelectedVertex = null;
	
	public Face3D(){
		
	}
	public Face3D(Vertex3D v0,Vertex3D v1,Vertex3D v2) {
		halfedges.add(new Halfedge3D(this, v0));
		halfedges.add(new Halfedge3D(this, v1));
		halfedges.add(new Halfedge3D(this, v2));
		
		setHalfedgeLoopRelations();
	}
	public Face3D(Face3D f) {
		z = f.z;
		n.set(f.n);
		incenter.set(f.incenter);
		isSelected = f.isSelected;
	}
	
	public void setNormal(){
		Halfedge3D he = halfedges.get(0);
		Vec3D v0v1 = GeomUtil.getDir(he.vertex.p, he.next.vertex.p);
		Vec3D v0v2 = GeomUtil.getDir(he.vertex.p, he.prev.vertex.p);
		n.cross(v0v1, v0v2);
		n.normalize();
	}
	
	public void reverse() {
		for (int i=0, j=halfedges.size()-1; i<j; i++, j--) {
			Halfedge3D tmp = halfedges.get(i);
			halfedges.set(i, halfedges.get(j));
			halfedges.set(j, tmp);
		}
		n.negate();
	}
	
	public void setHalfedgeLoopRelations() {
		
		for (int i=0, n=halfedges.size(); i<n; i++) {
			Halfedge3D a = halfedges.get(i);
			Halfedge3D b = halfedges.get((i+1)%n);
			a.next = b;
			b.prev = a;
		}
		setIncenter();
		
	}
	
	public void addVertex(Vertex3D v) {
		halfedges.add(new Halfedge3D(this, v));
	}
	
	public void setIncenter() {
		incenter = GeomUtil.getIncenter(this);
	}
	
	public boolean equals(Face3D f) {
		for (Halfedge3D he0: halfedges) {
			Vertex3D v = he0.vertex;
			boolean contains = false;
			for (Halfedge3D he1: f.halfedges) {
				if (he1.vertex == v) {
					contains = true;
					break;
				}
			}
			if (!contains)
				return false;
		}
		return true;
		
	}
}