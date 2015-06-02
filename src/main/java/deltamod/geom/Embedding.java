package deltamod.geom;

import java.util.ArrayList;

public class Embedding {

	/**
	 * graph embedding algorithm by Bon Plstenjak
	 * @param m
	 */
	public static void embed(Model3D m) {
		//Pick up a face
		final int ran = (int) Math.floor(Math.random() * 16);
		Face3D outer = m.faces.get(ran);
		/*
		int test = 0;
		for (Face3D f : m.faces) {
			int valence = 0;
			for (int i=0; i<3; i++)
				valence += f.halfedges.get(i).vertex.halfedges.size();
			if (valence == 12)
				outer = f;
		}*/
			
		//Position vertices of outer face in regular triangle inscribed into a circle
		//and put all others in the origin 
		for (Vertex3D v : m.vertices)
			v.p.set(0, 0, 0);
		final double inv_sqrt3 = 1.0 / Math.sqrt(3);
		for (int i=0; i<3; i++) {
			Vertex3D v = outer.halfedges.get(i).vertex;
			final double rad = (i * 120.0 / 180.0) * Math.PI;
			final double x = inv_sqrt3 * Math.cos(rad);
			final double y = inv_sqrt3 * Math.sin(rad);
			v.p.set(x, y, 0);
			v.checkFlg = true;
		}

		//Prepare the variables for the force F_uv on a Edge(u,v)
		Vec3D[] fuv = new Vec3D[m.edges.size()];
		for (int i=0, n=m.edges.size(); i<n; i++)
			fuv[i] = new Vec3D();
		//We use v.n for storing the force F_v on a Vertex(v)
		for (Vertex3D v : m.vertices)
			v.n.set(0, 0, 0);
		/*
		//We use v.index for storing the periphericity per on a Vertex(v)
		//Calculate periphericity for each Vertex
		for (Vertex3D v : m.vertices) 
			v.index = -1;
		ArrayList<Vertex3D> stack0 = new ArrayList<Vertex3D>();
		ArrayList<Vertex3D> stack1 = new ArrayList<Vertex3D>();
		for (Halfedge3D he : outer.halfedges)
			stack0.add(he.vertex);
		int per = 0;
		while (!stack0.isEmpty()) {
			for (Vertex3D v : stack0)
				v.index = per;
			for (Vertex3D v : stack0) {
				for (Halfedge3D he : v.halfedges) {
					Vertex3D vo = (he.edge.sv.equals(v))? he.edge.ev: he.edge.sv;
					if (vo.index < 0)
						stack1.add(vo);
				}
			}
			stack0.clear();
			stack0.addAll(stack1);
			stack1.clear();
			per++;
		}
		final int maxper = per - 1;
		 */

		//Constant
		final double n_pi = Math.sqrt(m.vertices.size() / Math.PI);
		//final double A = 2.5;
		final double avg_area = Math.PI / m.vertices.size();
		final int loop = 500;

		//iterations
		for (int i=0; i<loop; i++) {
			//Set F_v to zero
			for (Vertex3D v : m.vertices)
				v.n.set(0, 0, 0);

			//Calculate F_uv for Edges
			for (int j=0, n=m.edges.size(); j<n; j++) {
				Edge3D e = m.edges.get(j);
				Vertex3D v = e.sv;
				Vertex3D u = e.ev;
				//final double C = n_pi * Math.exp(A * (2.0 * maxper - u.index - v.index) / maxper);
				final double C = n_pi;
				final double x = C * Math.pow(v.p.x - u.p.x, 3);
				final double y = C * Math.pow(v.p.y - u.p.y, 3);
				if (!(Double.isNaN(x) || Double.isNaN(y)))
					if (!(Double.isInfinite(x) || Double.isInfinite(y)))
						fuv[j].set(x, y, 0);
				//Update the forces on v and u
				v.n.sub(fuv[j]);
				u.n.add(fuv[j]);
			}

			//Move Vertices
			final double cool = Math.sqrt(avg_area) / (1.0 + Math.pow(Math.sqrt(avg_area * i), 3));
			for (Vertex3D v : m.vertices) {
				if (!v.checkFlg) {
					final double f = v.n.length();
					final double size = Math.min(f, cool);
					v.n.normalize();
					v.n.scale(size);
					
					v.p.add(v.n);
				}
			}
		}

		periphericity(m, outer);
		liftup(m);
		//SequentialSpringSimulation.calc(m);

		
		for (int i=0; i<3; i++) {
			Vertex3D v = outer.halfedges.get(i).vertex;
			v.p.scale(0.5);
		}
		
		int index = 0;
		for (Vertex3D v : m.vertices)
			v.index = index++;
		m.length = 1.0;
		
		m.normalize();
	}

	public static void liftup(Model3D m) {
		
		for (Vertex3D v : m.vertices)
			System.out.println(v.index);
		
		final double H = 0.3;
		for (Vertex3D v: m.vertices) {
			final double z = H * v.index;
			v.p.z = -z;
			System.out.println(v.p.z);
		}
	}

	/*
	public static void liftupCentrality(Model3D m) {
		//We use v.index for storing the periphericity per on a Vertex(v)
		//Calculate periphericity for each Vertex

		//Pick up a face
		Face3D outer = m.faces.get(0);
		//Initialize 
		for (Vertex3D v : m.vertices) 
			v.index = -1;
		ArrayList<Vertex3D> stack0 = new ArrayList<Vertex3D>();
		ArrayList<Vertex3D> stack1 = new ArrayList<Vertex3D>();
		for (Halfedge3D he : outer.halfedges)
			stack0.add(he.vertex);
		int per = 0;

		//Tetrahedron Height
		final double height = 0.8;

		while (!stack0.isEmpty()) {
			final int n = stack0.size();
			if (n == 1) {
				Vertex3D v = stack0.get(0);
				v.index = per;
				v.p.set(0, 0, height * per);
			} else if (n == 2) {
				Vertex3D v = stack0.get(0);
				v.index = per;
				v.p.set(0, 0.5, height * per);
				v = stack0.get(1);
				v.index = per;
				v.p.set(0, -0.5, height * per);
			} else {
				final double angle = 2.0 * Math.PI / n;
				final double C = 1.0 / (2 * Math.sin(Math.PI / n));  //radius of circumcircle
				for (int i=0; i<n; i++) {
					Vertex3D v = stack0.get(i);
					v.index = per;
					final double rot = i * angle;
					final double x = C * Math.cos(rot);
					final double y = C * Math.sin(rot);
					v.p.set(x, y, height * per);
				}
			}
			for (Vertex3D v : stack0) {
				for (Halfedge3D he : v.halfedges) {
					Vertex3D vo = he.next.vertex;
					if (vo.index < 0) {
						if (!stack1.contains(vo))
							stack1.add(vo);
					}
				}
			}
			stack0.clear();
			stack0.addAll(stack1);
			stack1.clear();
			per++;
		}
	}
	*/
	
	/*
	public static void liftupCentrality2(Model3D m) {
		//We use v.index for storing the periphericity per on a Vertex(v)
		//Calculate periphericity for each Vertex

		//Pick up a face
		Face3D outer = m.faces.get(0);
		//Initialize 
		for (Vertex3D v : m.vertices) {
			v.index = -1;
			v.n.set(0,0,0);
		}
		ArrayList<Vertex3D> stack0 = new ArrayList<Vertex3D>();
		ArrayList<Vertex3D> stack1 = new ArrayList<Vertex3D>();
		for (int i=0; i<3; i++) {
			Vertex3D v = outer.halfedges.get(i).vertex;
			stack0.add(v);
			final double angle = 2.0 * Math.PI / 3;
			final double C = 1.0 / (2 * Math.sin(Math.PI / 3));  //radius of circumcircle
			v.index = 0;
			final double rot = i * angle;
			final double x = C * Math.cos(rot);
			final double y = C * Math.sin(rot);
			v.p.set(x, y, 0);
		}

		int per = 1;
		for (Vertex3D v : stack0) {
			for (Halfedge3D he : v.halfedges) {
				Vertex3D vo = he.next.vertex;
				if (vo.index < 0) {
					vo.n.add(v.p);
					if (!stack1.contains(vo))
						stack1.add(vo);
				}
			}
		}
		stack0.clear();
		stack0.addAll(stack1);
		stack1.clear();

		//Tetrahedron Height
		final double height = 0.8;

		while (!stack0.isEmpty()) {
			final int n = stack0.size();
			for (int i=0; i<n; i++) {
				Vertex3D v = stack0.get(i);
				v.index = per;
				v.p.set(v.n.x, v.n.y, height * per);
			}

			for (Vertex3D v : stack0) {
				for (Halfedge3D he : v.halfedges) {
					Vertex3D vo = he.next.vertex;
					if (vo.index < 0) {
						vo.n.add(v.p);
						if (!stack1.contains(vo))
							stack1.add(vo);
					}
				}
			}
			stack0.clear();
			stack0.addAll(stack1);
			stack1.clear();
			per++;
		}
		System.out.println(per - 1);

	}
	*/
	
	/** Calculate periphericity for each Vertex
	 * 
	 * @param m
	 * @return maximum periphericity
	 */
	public static int periphericity(Model3D m, Face3D outer) {
		//We use v.index for storing the periphericity per on a Vertex(v)


		ArrayList<Vertex3D> stack0 = new ArrayList<Vertex3D>();
		ArrayList<Vertex3D> stack1 = new ArrayList<Vertex3D>();
		int per = 0;

		for (Vertex3D v : m.vertices)
			v.index = -1;
		for (Halfedge3D he : outer.halfedges)
			stack0.add(he.vertex);

		while (!stack0.isEmpty()) {
			System.out.println("stacksize: "+stack0.size());
			for (Vertex3D v : stack0)
				v.index = per;

			//Search
			for (Vertex3D v : stack0) {
				System.out.println("s:"+v.halfedges.size());
				for (Halfedge3D he : v.halfedges) {
					Vertex3D vo = he.next.vertex;
					if (vo.index < 0) {
						if (!stack1.contains(vo))
							stack1.add(vo);
					}
				}
			}
			stack0.clear();
			stack0.addAll(stack1);
			stack1.clear();
			per++;
		}
		return per - 1;
	}

	public static void barycentric(Model3D m) {
		//Pick up a face
		Face3D outer = m.faces.get(3);

		//Position vertices of outer face in regular triangle inscribed into a circle
		//and put all others in the origin 
		for (Vertex3D v : m.vertices)
			v.p.set(0, 0, 0);
		final double radius = 1.0 / (2 * Math.sin(Math.PI / 3));  //radius of circumcircle
		for (int i=0; i<3; i++) {
			Vertex3D v = outer.halfedges.get(i).vertex;
			final double rad = (i * 120.0 / 180.0) * Math.PI;
			final double x = radius * Math.cos(rad);
			final double y = radius * Math.sin(rad);
			v.p.set(x, y, 0);
			v.checkFlg = true;
		}

		//Constant
		final int loop = 500;
		Vec3D dir = new Vec3D();

		//iterations
		for (int i=0; i<loop; i++) {
			//Move Vertices
			for (Vertex3D v : m.vertices) {
				if (!v.checkFlg) {
					dir.set(0, 0, 0);
					for (Halfedge3D he : v.halfedges) {
						dir.add(he.next.vertex.p);
					}
					dir.scale(v.halfedges.size());
					v.p.set(dir);
				}
			}
		}
	}
}
