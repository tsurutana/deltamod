package deltamod.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;

import deltamod.DeltaMod;
import deltamod.geom.*;
import deltamod.render.Camera;
import deltamod.render.Renderer;


public class MainScreen extends GLJPanel implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener,ActionListener {

	private Renderer renderer;
	public Camera camera;
	
	private Point2D preMousePosition = new Point2D.Float();
	private Point2D mousePosition = new Point2D.Float();
	
	private JPopupMenu popup;
	private JMenuItem menuItemSelectAll = new JMenuItem(DeltaMod.res.getString("SelectAll"));
	private JMenuItem menuItemUnselectAll = new JMenuItem(DeltaMod.res.getString("UnselectAll"));
	private JMenuItem menuItemSetGeometryCenter = new JMenuItem(DeltaMod.res.getString("GeomToOrig"));

	private boolean faceSelectionMode = false;
	private int keycode;
	
	private Model3D m;
	
	public MainScreen(){
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		addGLEventListener(this);

		// popup menu
		popup = new JPopupMenu();
		popup.add(menuItemSelectAll);
		popup.add(menuItemUnselectAll);
		popup.add(menuItemSetGeometryCenter);

		menuItemSelectAll.addActionListener(this);
		menuItemUnselectAll.addActionListener(this);
		menuItemSetGeometryCenter.addActionListener(this);
		
		// disable hi-resolution on retina
		setSurfaceScale(new float[]{ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE});
		camera = new Camera();
		renderer = new Renderer(camera);
	}
	
	public void init(GLAutoDrawable drawable) {
		renderer.init(drawable.getGL().getGL2());
	}

	public void display(GLAutoDrawable drawable) {
		renderer.draw(drawable.getGL().getGL2());
	}
	
	public void dispose(GLAutoDrawable drawable) {	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		renderer.reshape(drawable.getGL().getGL2(), width, height);
	}

	public void mouseClicked(MouseEvent e) {
		mousePosition.setLocation(e.getX(), e.getY());
		if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
			for (Face3D f : m.faces) {
				if (GeomUtil.isFaceIncludesPoint(f, mousePosition)) {
					f.isSelected = faceSelectionMode;
					for ( Halfedge3D he : f.halfedges) {
						he.vertex.isSelected = false;
					}
					for (Vertex3D v : m.vertices)
						v.isSelected = false;
					Vertex3D c = GeomUtil.getNearestVertex(mousePosition, f);
					c.isSelected = faceSelectionMode;
					break;
				}
			}
		} else {
			popup.show(e.getComponent(), (int) mousePosition.getX(), (int) mousePosition.getY());
		}

		repaint();
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		preMousePosition.setLocation(e.getX(), e.getY());;
		for (Face3D f : m.faces) {
			if (GeomUtil.isFaceIncludesPoint(f, preMousePosition)) {
				if (f.isSelected)
					faceSelectionMode = false;
				else
					faceSelectionMode = true;
				break;
			}
		}
		requestFocusInWindow();
	}

	public void mouseReleased(MouseEvent e) {
		repaint();
	}


	public void mouseDragged(MouseEvent e) {

		mousePosition.setLocation(e.getX(), e.getY());
		
		if (e.isControlDown())  {
			camera.rotateX((float) (mousePosition.getY() - preMousePosition.getY()));
			camera.rotateY((float) (mousePosition.getX() - preMousePosition.getX()));
		} else if (e.isShiftDown()) {
			camera.translateX((float) (mousePosition.getX() - preMousePosition.getX()));
			camera.translateY((float) (mousePosition.getY() - preMousePosition.getY()));
		} else {
			for (Face3D f : m.faces) {
				if (GeomUtil.isFaceIncludesPoint(f, mousePosition)) {
					f.isSelected = faceSelectionMode;
					break;
				}
			}
		}
		preMousePosition.setLocation(mousePosition);
		repaint();
	}

	public void mouseMoved(MouseEvent e) {	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		camera.scale(e.getWheelRotation());
		repaint();
	}

	public void keyPressed(KeyEvent e) {

		keycode = e.getKeyCode();
		//System.out.println(keycode);
		if (e.isControlDown()) {
			if (keycode == 65){
				ModelOps.selectAllFaces(m);
			} else if (keycode == 88 && e.isControlDown()) {
				System.out.println("perform");
				DeltaMod.mainFrame.opspanel.perform();
			}
		} else {
			double angle = 1.0 * Math.PI / 180.0;
			if (keycode == 27){
				ModelOps.unselectAllFaces(m);
				ModelOps.unselectAllVertices(m);
				ModelOps.uncheckAllVertices(m);
			}
			// rotate geometry
			else if (keycode == 37){
				Vec3D yaxis = new Vec3D(0.0F, 1.0F, 0.0F);
				for (Vertex3D v : m.vertices) { 
					v.p.rotate(yaxis, -angle);
				}
				m.setNormals();
			}
			else if (keycode == 38){
				Vec3D xaxis = new Vec3D(1.0F, 0.0F, 0.0F);
				for (Vertex3D v : m.vertices) { 
					v.p.rotate(xaxis, -angle);
				}
				m.setNormals();
			}
			else if (keycode == 39){
				Vec3D yaxis = new Vec3D(0.0F, 1.0F, 0.0F);
				for (Vertex3D v : m.vertices) { 
					v.p.rotate(yaxis, angle);
				}
				m.setNormals();
			}
			else if (keycode == 40){
				Vec3D xaxis = new Vec3D(1.0F, 0.0F, 0.0F);
				for (Vertex3D v : m.vertices) { 
					v.p.rotate(xaxis, angle);
				}
				m.setNormals();
			}
		}
		//m.removeIsolatedVertices();
		//m.testEdges();
		repaint();

	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItemSelectAll) {
			ModelOps.selectAllFaces(m);
		} else if (e.getSource() == menuItemUnselectAll) {
			ModelOps.unselectAllFaces(m);
			ModelOps.unselectAllVertices(m);
			//System.out.println(m.getVolume());
		} else if (e.getSource() == menuItemSetGeometryCenter) {
			ModelOps.setOriginToCenterOfGeometry(m);
		}
		repaint();
	}

	public void setModel(Model3D model) {
		m = model;
		renderer.setModel(model);
	}
	
	public Model3D getModel() {
		return m;
	}

}
