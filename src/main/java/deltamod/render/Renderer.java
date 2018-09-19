package deltamod.render;

import static com.jogamp.opengl.GL2.*;

import java.awt.Dimension;
import java.util.Collections;
import java.util.Comparator;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.gl2.GLUgl2;

import deltamod.DeltaMod;
import deltamod.comp.MainFrame;
import deltamod.geom.Edge3D;
import deltamod.geom.Face3D;
import deltamod.geom.Halfedge3D;
import deltamod.geom.Model3D;
import deltamod.geom.Vec3D;
import deltamod.geom.Vertex3D;

public class Renderer {

	private GLUgl2 glu;
	
	private Camera camera;
	
	private float[] light_ambient = { 0.4F, 0.4F, 0.4F, 1.0F };
	private float[] light_diffuse = { 0.7F, 0.7F, 0.7F, 1.0F };
	private float[] light_specular = { 1.0F, 1.0F, 1.0F, 1.0F };
	
	private float[] current = new float[16];
	private double[] modelview = new double[16];
	private double[] projection = new double[16];
	private int[] viewport = new int[4];
	private double[] window = new double[3];  //window coordinate
	private Dimension size;
	
	private Model3D m;
	private Vec3D c0 = new Vec3D();
	private Vec3D c1 = new Vec3D();
	private Vec3D c2 = new Vec3D();
	private Vec3D c01 = new Vec3D();
	private Vec3D c02 = new Vec3D();
	
	public Renderer(Camera c) {
		glu = new GLUgl2();
		camera = c;
	}
	
	public void init(GL2 gl) {
		GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
		caps.setSampleBuffers(true);
		caps.setNumSamples(2);
		
		// background color
		//gl.glClearColor(0.262F, 0.267F, 0.345F, 0.0F);
		gl.glClearColor(1F, 1F, 1F, 0.0F);
		
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, this.light_ambient, 0);
		gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, this.light_diffuse, 0);
		gl.glLightfv(GL_LIGHT0, GL_SPECULAR, this.light_specular, 0);

		gl.glLightfv(GL_LIGHT1, GL_AMBIENT, this.light_ambient, 0);
		gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, this.light_diffuse, 0);
		gl.glLightfv(GL_LIGHT1, GL_SPECULAR, this.light_specular, 0);

		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_LIGHT0);
		gl.glEnable(GL_LIGHT1);
		gl.glEnable(GL_DEPTH_TEST);

		gl.glEnable(GL_CULL_FACE);
		gl.glCullFace(GL_BACK);

		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL_MULTISAMPLE);
		gl.glEnable(GL_NORMALIZE);
		
		gl.glEnable(GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(0.5F, 1.0F);
		
		Material.generateMaterials();
	}
	
	public void draw(GL2 gl) {
		
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		glu.gluLookAt(camera.eye[0], camera.eye[1], camera.eye[2], 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D);

		gl.glRotatef(camera.rotX, 1.0F, 0.0F, 0.0F);
		gl.glRotatef(camera.rotY, 0.0F, 1.0F, 0.0F);
		gl.glGetFloatv(GL_MODELVIEW_MATRIX, current, 0);
		gl.glLoadIdentity();
		gl.glTranslatef(camera.traX * 0.01F, camera.traY * 0.01F, 0.0F);
		gl.glMultMatrixf(current, 0);
		gl.glScalef(camera.scale, camera.scale, camera.scale);
		
		MainFrame frame = DeltaMod.mainFrame;
		if (!frame.menuItemViewWireframe.isSelected())
			drawModel(gl);
		// wire frame
		if (!frame.menuItemViewFlat.isSelected())
			drawWireFrame(gl);

		//drawSelectedVertex(gl);
		
		setScreenPosition(gl);
		gl.glFlush();
	}

	private void drawModel(GL2 gl) {
		for (Face3D face : m.faces) {
			gl.glNormal3d(face.n.x, face.n.y, face.n.z);
			if (face.color == 1) {
				Material.setMaterial(gl, "red");
			} else if (face.color == 2) {
				Material.setMaterial(gl, "green");
			} else if (face.color == 3) {
				Material.setMaterial(gl, "blue");
			} else {
				Material.setMaterial(gl, "gold");
			}
			if (face.isSelected) {
				setCornerTriangle(face);
				gl.glBegin(GL_POLYGON);
				// draw trapezoid
				Halfedge3D he = face.halfedgeRefersToSelectedVertex;
				for (int i=0, n=face.halfedges.size()-1; i<n; i++) {
					he = he.next;
					gl.glVertex3d(he.vertex.p.x, he.vertex.p.y, he.vertex.p.z);
				}
				gl.glVertex3d(c02.x, c02.y, c02.z);
				gl.glVertex3d(c01.x, c01.y, c01.z);
				gl.glEnd();
				Material.setMaterial(gl, "kupfer");
				gl.glBegin(GL_POLYGON);
				gl.glVertex3d(c0.x, c0.y, c0.z);
				gl.glVertex3d(c01.x, c01.y, c01.z);
				gl.glVertex3d(c02.x, c02.y, c02.z);
				gl.glEnd();
			} else {
				gl.glBegin(GL_POLYGON);
				for (Halfedge3D he : face.halfedges) {
					Vertex3D v = he.vertex;
					gl.glVertex3d(v.p.x, v.p.y, v.p.z);
				}
				gl.glEnd();
			}
			
		}

		Material.setMaterial(gl, "chrom");

		for (Face3D face : m.faces) {
			gl.glNormal3d(-face.n.x, -face.n.y, -face.n.z);
			gl.glBegin(GL_POLYGON);
			for (int i=face.halfedges.size()-1; i>=0; i--) {
				Vertex3D v = face.halfedges.get(i).vertex;
				gl.glVertex3d(v.p.x, v.p.y, v.p.z);
			}
			gl.glEnd();
		}
	}

	private void drawWireFrame(GL2 gl) {
		gl.glDisable(GL_LIGHTING);
		gl.glColor3f(0.0F, 0.0F, 0.0F);
		gl.glBegin(GL_LINES);
		for (Edge3D edge : m.edges) {
			gl.glColor3f(edge.c.getRed(), edge.c.getGreen(), edge.c.getBlue());
			gl.glVertex3d(edge.v1.p.x, edge.v1.p.y, edge.v1.p.z);
			gl.glVertex3d(edge.v2.p.x, edge.v2.p.y, edge.v2.p.z);
		}
		gl.glEnd();
		gl.glEnable(GL_LIGHTING);
	}
	/*
	private void drawSelectedVertex(GL2 gl) {
		gl.glEnable(GL_BLEND);
		Material.setMaterial(gl, "bronze");
		for (Vertex3D v : m.vertices) {
			if (v.isSelected) {
				gl.glTranslatef((float) v.p.x, (float) v.p.y, (float) v.p.z);
				glut.glutSolidSphere(0.1, 16, 16);
				gl.glTranslatef((float) -v.p.x, (float) -v.p.y, (float) -v.p.z);
			}
		}
		gl.glDisable(GL_BLEND);
	}*/
	
	public void reshape(GL2 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);		
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		this.glu.gluPerspective(45.0D, width / (float)height, 1.0D, 100.0D);
		gl.glMatrixMode(GL_MODELVIEW);
		size = new Dimension(width, height);
	}
	
	// update screen coordinates of vertices
	private void setScreenPosition(GL2 gl) {
		gl.glGetDoublev(GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetDoublev(GL_PROJECTION_MATRIX, projection, 0);
		gl.glGetIntegerv(GL_VIEWPORT, viewport, 0);
		
		for (Vertex3D v : m.vertices) {
			glu.gluProject(v.p.x, v.p.y, v.p.z, 
					modelview, 0, projection, 0, viewport, 0, window, 0); //convert coordinate
			v.screen.set(window[0], size.getHeight() - window[1], window[2]);
		}
		// update z value of faces
		for (Face3D f : m.faces) {
			f.z = 0.0;
			for (int j=0,n=f.halfedges.size(); j<n; j++) {
				f.z += f.halfedges.get(j).vertex.screen.z;
			}
			f.z /= f.halfedges.size();
		}
		// sort the faces
		Collections.sort(m.faces, new FaceDepthComparator());
	}
	
	private void setCornerTriangle(Face3D f) {
		Halfedge3D landmark = f.halfedgeRefersToSelectedVertex;
		if (landmark == null) return;
		
		c0.set(landmark.vertex.p);
		c1.set(landmark.next.vertex.p);
		c2.set(landmark.prev.vertex.p);
		c01.set(c1);
		c01.sub(c0);
		c01.scale(0.3);
		c01.add(c0);
		c02.set(c2);
		c02.sub(c0);
		c02.scale(0.3);
		c02.add(c0);
	}
	
	public void setModel(Model3D model) {
		m = model;
	}
}

class FaceDepthComparator implements Comparator<Face3D> {
	public int compare(Face3D f1, Face3D f2) {
		return (f1).z > (f2).z ? 1 : -1;
	}
}