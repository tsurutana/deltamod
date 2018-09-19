package deltamod.geom;

import java.util.ArrayList;

/**
 * 頂点
 * @author tsuruta
 *
 */

public class Vertex3D {
	public Vec3D p;  // position
	public Vec3D n = new Vec3D();  // normal
	public ArrayList<Halfedge3D> halfedges = new ArrayList<Halfedge3D>();

	public int index = 0;
	public Vec3D screen = new Vec3D(); // screen coordinate

	//public boolean isSelected = false;
	public boolean checkFlg = false;
	
	public Vertex3D(Vec3D v) {
		p = v;
	}
	public Vertex3D(Vertex3D v) {
		p = new Vec3D(v.p);
		n = new Vec3D(v.n);
		screen = new Vec3D(v.screen);
		index = v.index;
		//isSelected = v.isSelected;
		checkFlg = v.checkFlg;
	}
	public Vertex3D(double x,double y,double z) {
		p = new Vec3D(x, y, z);
	}
	public Vertex3D(double x,double y,double z, int i) {
		p = new Vec3D(x, y, z);
		index = i;
	}
	public void setNormal() {
		n.set(0.0, 0.0, 0.0);
		for (Halfedge3D he : halfedges) {
			n.add(he.face.n);
		}
		n.normalize();
	}
}