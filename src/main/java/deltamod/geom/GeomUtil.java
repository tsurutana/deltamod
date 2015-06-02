package deltamod.geom;

import java.awt.geom.Point2D;

public class GeomUtil {

	public static final double PI = 3.14159265358979323846;
	public static final double EPS = 1.e-6;

	public static double distance(Vec3D a, Vec3D b) {
		return distance(a.x, a.y, a.z, b.x, b.y, b.z);
	}
	public static double distance(double x0, double y0, double z0, double x1, double y1, double z1) {
		return Math.sqrt( (x0 - x1)*(x0 - x1) + (y0 - y1)*(y0 - y1)  + (z0 - z1)*(z0 - z1) );
	}

	public static Vec3D midpoint(Vec3D a, Vec3D b) {
		return new Vec3D( (a.x+b.x)/2, (a.y+b.y)/2, (a.z+b.z)/2 ); 
	}

	public static Vec3D getDir(Halfedge3D he) {
		return getDir(he.vertex.p, he.next.vertex.p);
	}
	public static Vec3D getDir(Vec3D a, Vec3D b) {
		return new Vec3D(b.x - a.x, b.y - a.y, b.z - a.z);
	}
	public static void setDir(Halfedge3D he, Vec3D vec) {
		setDir(he.vertex.p, he.next.vertex.p, vec);
	}
	public static void setDir(Vec3D a, Vec3D b, Vec3D vec) {
		vec.set(b.x - a.x, b.y - a.y, b.z - a.z);
	}

	public static Vec3D getIncenter(Face3D f) {
		Vec3D incenter = new Vec3D();
		int n = f.halfedges.size();
		for (int i=0; i<n; i++)
			incenter.add(f.halfedges.get(i).vertex.p);
		incenter.scale(1.0/n);
		return incenter;
	}

	public static Vec3D getNormal(Face3D f) {
		Vertex3D v0 = f.halfedges.get(0).vertex;
		Vertex3D v1 = f.halfedges.get(1).vertex;
		Vertex3D v2 = f.halfedges.get(2).vertex;
		Vec3D norm = new Vec3D(v1.p.x - v0.p.x, v1.p.y - v0.p.y, v1.p.z - v0.p.z);
		Vec3D v0v2 = new Vec3D(v2.p.x - v0.p.x, v2.p.y - v0.p.y, v2.p.z - v0.p.z);
		norm.cross(v0v2);
		norm.normalize();
		return norm;
	}

	public static boolean isInnerPoint(Halfedge3D he){
		Halfedge3D start = he;
		for (Halfedge3D p=he.next; p==start; p = p.pair.next)
			if (!p.hasPair()) return false;
		return true;
	}

	// Counter-clockwise check
	public static boolean CCWcheck(Vertex3D p0, Vertex3D p1, Point2D q) {
		double dx1, dx2, dy1, dy2, s1, s2;

		dx1 = p1.screen.x - p0.screen.x;
		dy1 = p1.screen.y - p0.screen.y;
		dx2 =  q.getX() - p0.screen.x;
		dy2 =  q.getY() - p0.screen.y;
		s1 = dx1 * dy2;
		s2 = dy1 * dx2;
		if ( s1 > s2 ) return true;    
		return false;
	}

	// if p3 between p1 and p2 then true
	public static boolean isRange( double q, double p0, double p1 ) {
		return Math.min(p0, p1) <= q && q <= Math.max(p0,p1);
	}

	// if point is on segment then true
	public static boolean isPointOnSegment(Point2D p, Vertex3D sv, Vertex3D ev)
	{
		double dx1, dx2, dy1, dy2, s1, s2;

		if (!(isRange(p.getX(), sv.screen.x, ev.screen.x) && isRange(p.getY(), sv.screen.y, ev.screen.y)))
			return false;

		//外積
		dx1 = sv.screen.x - p.getX();
		dy1 = sv.screen.y - p.getY();
		dx2 =  ev.screen.x - p.getX();
		dy2 =  ev.screen.y - p.getY();
		s1 = dx1 * dy2;
		s2 = dy1 * dx2;
		if ( Math.abs(s1 - s2) < EPS ) return true;
		return false;
	}

	// if point is inside face then true
	public static boolean isFaceIncludesPoint(Face3D f, Point2D p){
		// if point is on the edges of face then true
		for (int i=0, n=f.halfedges.size(); i<n; i++) {
			Halfedge3D he = f.halfedges.get(i);
			if(isPointOnSegment(p, he.vertex, he.next.vertex))
				return true;
		}
		Halfedge3D baseHe = f.halfedges.get(0);
		boolean baseFlg = GeomUtil.CCWcheck(baseHe.vertex, baseHe.next.vertex, p);

		for(int i=1, n=f.halfedges.size(); i < n; i++) {
			Halfedge3D he = f.halfedges.get(i);    
			if(GeomUtil.CCWcheck(he.vertex, he.next.vertex, p) != baseFlg) {
				return false;
			}
		}
		return true;
	}

	// get vertex nearest to p among the face f 
	public static Vertex3D getNearestVertex (Point2D p, Face3D f) {
		double min = Double.MAX_VALUE;
		Vertex3D near = null;
		for (int i=0,n=f.halfedges.size(); i<n; i++) {
			Vertex3D v = f.halfedges.get(i).vertex;
			double dist = p.distance(v.screen.x, v.screen.y);
			if (dist < min) {
				min = dist;
				near = v;
			}
		}
		return near;
		/*if (min < 10.)
			return n;
		else
			return null;
		 */
	}

}
