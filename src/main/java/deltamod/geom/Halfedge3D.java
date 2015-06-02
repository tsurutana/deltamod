package deltamod.geom;

public class Halfedge3D {

	public Halfedge3D next = null;
	public Halfedge3D prev = null;
	public Halfedge3D pair = null;
	public Edge3D edge = null;
	public Vertex3D vertex = null;
	public Face3D face = null;
	
	public Halfedge3D (Face3D f, Vertex3D v){
		face = f;
		vertex = v;
	}
	public boolean hasPair(){
		return pair != null;
	}
}
