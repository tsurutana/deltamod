package deltamod.geom;

import java.util.ArrayList;
import java.util.ListIterator;

public class ModelOps {

	static double EPS = 0.001D;

	static double tetrahedronHeight;
	static double icosahedronHeight;
	static double squarePyramidHeight;
	static double pentagonalPyramidHeight;
	static double antiprismHegiht;

	public static void selectAllFaces(Model3D m) {
		for (Face3D f : m.faces) {
			f.isSelected = true;
		}
	}
	public static void unselectAllFaces(Model3D m){
		for (Face3D f : m.faces) {
			f.isSelected = false;
		}
	}

	public static void uncheckAllVertices(Model3D m){
		for (Vertex3D v : m.vertices) {
			v.checkFlg = false;
		}
	}
	public static void unselectAllVertices(Model3D m) {
		for (Vertex3D v : m.vertices) {
			v.isSelected = false;
		}
	}

	public static void setOriginToCenterOfGeometry(Model3D m) {
		Vec3D avg = new Vec3D();
		for (Vertex3D v : m.vertices) {
			avg.add(v.p);
		}
		avg.scale(1.0/m.vertices.size());
		for (Vertex3D v : m.vertices) {
			v.p.sub(avg);
		}
	}

	public static void printDihedralAngles(Model3D m) {
		System.out.println("------Dihedral Angles------");
		for (Edge3D e : m.edges)
			System.out.println(e.getDihedralAngle());
		System.out.println("------------");
	}
	public static boolean hasDihedralAngle180(Model3D m) {
		//System.out.println("------Dihedral Angles------");
		final double error = 1.0e-2;
		for (Edge3D e : m.edges) {
			double dif = e.getDihedralAngle() - 180;
			dif = (dif > 0) ? dif: -dif;
			if (dif < error) {
				System.out.println("D, " + dif);
				return true;
			}
		}
		System.out.println("C, 2");
		return false;
		//System.out.println("------------");
	}
	public static ArrayList<Double> getDihedralAngles(Model3D m) {
		ArrayList<Double> angles = new ArrayList<Double>();
		for (Edge3D e : m.edges) {
			double da = Math.round(e.getDihedralAngle()*1000)*0.001;

			if (!angles.contains(da))
				angles.add(da);
		}
		return angles;
	}

	/// add tetrahedron
	public static void addTetrahedron(Model3D m) {
		ArrayList<Face3D> face_new = new ArrayList<Face3D>();
		for (Face3D f : m.faces) {
			if (f.halfedges.size() == 3) {
				if (f.isSelected) {
					Vertex3D v0 = f.halfedges.get(0).vertex;
					Vertex3D v1 = f.halfedges.get(1).vertex;
					Vertex3D v2 = f.halfedges.get(2).vertex;

					// get incenter
					Vec3D incenter = GeomUtil.getIncenter(f);

					// get normal from cross product
					Vec3D norm = GeomUtil.getNormal(f);
					norm.scale(tetrahedronHeight);

					// new vertex will be at incenter + normal
					incenter.add(norm);

					Vertex3D v_new = getVertex(incenter, m.vertices);
					face_new.add(new Face3D(v_new, v0, v1));
					face_new.add(new Face3D(v_new, v1, v2));
					face_new.add(new Face3D(v_new, v2, v0));
				}
			} else {
				f.isSelected = false;
			}
		}
		m.faces.addAll(face_new);
		m.removeSelectedFaces();
		m.makeEdges();
		m.setNormals();
		uncheckAllVertices(m);
	}

	// add octahedron
	public static void addOctahedron (Model3D m){
		ArrayList<Face3D> face_new = new ArrayList<Face3D>();
		for (Face3D f : m.faces) {
			if (f.halfedges.size() == 3) {
				if (f.isSelected) {
					Vertex3D v0 = f.halfedges.get(0).vertex;
					Vertex3D v1 = f.halfedges.get(1).vertex;
					Vertex3D v2 = f.halfedges.get(2).vertex;

					// point p0 is away from midpoint of he0 by 1/3
					Vec3D p0 = GeomUtil.midpoint(v0.p, v1.p);
					Vec3D sub =GeomUtil.getDir(p0, v2.p);
					sub.scale(1.0/3.0);
					p0.sub(sub);

					// normal
					Vec3D norm = GeomUtil.getNormal(f);
					norm.scale(tetrahedronHeight);

					// p0 will be the first point of octahedron
					p0.add(norm);

					// other 2 points are parallel to he1 and he2 respectively
					Vec3D p1 = new Vec3D(p0);
					Vec3D v1v2 = GeomUtil.getDir(v1.p, v2.p);
					p1.add(v1v2);
					Vec3D p2 = new Vec3D(p0);
					Vec3D v0v2 = GeomUtil.getDir(v0.p, v2.p);
					p2.add(v0v2);

					Vertex3D v0_new = getVertex(p0, m.vertices);
					Vertex3D v1_new = getVertex(p1, m.vertices);
					Vertex3D v2_new = getVertex(p2, m.vertices);
					face_new.add(new Face3D(v0, v1, v0_new));
					face_new.add(new Face3D(v1, v2, v2_new));
					face_new.add(new Face3D(v2, v0, v1_new));
					face_new.add(new Face3D(v0_new, v2_new, v1_new));
					face_new.add(new Face3D(v0_new, v1_new, v0));
					face_new.add(new Face3D(v1_new, v2_new, v2));
					face_new.add(new Face3D(v2_new, v0_new, v1));
				} else {
					f.isSelected = false;
				}
			}
		}
		m.faces.addAll(face_new);
		m.removeSelectedFaces();
		m.makeEdges();
		m.setNormals();
		uncheckAllVertices(m);
	}

	// add icosahedron
	public static void addIcosahedron(Model3D m){
		ArrayList<Face3D> face_new = new ArrayList<Face3D>();
		for (Face3D f : m.faces) {
			if (f.halfedges.size() == 3) {
				if (f.isSelected) {
					Vertex3D v0 = f.halfedges.get(0).vertex;
					Vertex3D v1 = f.halfedges.get(1).vertex;
					Vertex3D v2 = f.halfedges.get(2).vertex;

					Vec3D[] p = new Vec3D[9];
					
					// point p[0] is away from midpoint of he0 by 1/3
					p[0] = GeomUtil.midpoint(v0.p, v1.p);
					Vec3D sub = GeomUtil.getDir(p[0], v2.p);
					sub.scale(1.0/3.0);
					p[0].sub(sub);

					// normal
					Vec3D norm = GeomUtil.getNormal(f);
					norm.scale(icosahedronHeight);

					// p0 will be the first point of icosahedron
					p[0].add(norm);

					// other 2 points are parallel to he1 and he2 respectively
					p[1] = new Vec3D(p[0]);
					Vec3D v1v2 = GeomUtil.getDir(v1.p, v2.p);
					p[1].add(v1v2);
					p[2] = new Vec3D(p[0]);
					Vec3D v0v2 = GeomUtil.getDir(v0.p, v2.p);
					p[2].add(v0v2);
					
					// --- p[0-2] are the vertices of opposite face
					
					// calculate other 6 vertices ---
					Vec3D v1p2 = GeomUtil.getDir(v1.p, p[2]);
					v1p2.normalize();
					v1p2.scale(m.length);
					p[3] = new Vec3D(v2.p);
					p[3].add(v1p2);
					p[4] = new Vec3D(p[0]);
					p[4].sub(v1p2);

					Vec3D v2p1 = GeomUtil.getDir(v2.p, p[1]);
					v2p1.normalize();
					v2p1.scale(m.length);
					p[5] = new Vec3D(v0.p);
					p[5].add(v2p1);
					p[6] = new Vec3D(p[2]);
					p[6].sub(v2p1);

					Vec3D v0p0 = GeomUtil.getDir(v0.p, p[0]);
					v0p0.normalize();
					v0p0.scale(m.length);
					p[7] = new Vec3D(v1.p);
					p[7].add(v0p0);
					p[8] = new Vec3D(p[1]);
					p[8].sub(v0p0);

					Vertex3D[] v_new = new Vertex3D[9];
					for (int i=0; i<9; i++) {
						v_new[i] = getVertex(p[i], m.vertices);
					}
					
					face_new.add(new Face3D(v_new[0], v_new[2], v_new[1]));
					face_new.add(new Face3D(v0, v1, v_new[4]));
					face_new.add(new Face3D(v_new[1], v_new[2], v_new[3]));
					face_new.add(new Face3D(v1, v2, v_new[6]));
					face_new.add(new Face3D(v_new[0], v_new[1], v_new[5]));
					face_new.add(new Face3D(v2, v0, v_new[8]));
					face_new.add(new Face3D(v_new[2], v_new[0], v_new[7]));

					
					face_new.add(new Face3D(v0, v_new[4], v_new[5]));
					face_new.add(new Face3D(v0, v_new[5], v_new[8]));
					face_new.add(new Face3D(v1, v_new[6], v_new[7]));
					face_new.add(new Face3D(v1, v_new[7], v_new[4]));
					face_new.add(new Face3D(v2, v_new[8], v_new[3]));
					face_new.add(new Face3D(v2, v_new[3], v_new[6]));

					
					face_new.add(new Face3D(v_new[0], v_new[5], v_new[4]));
					face_new.add(new Face3D(v_new[0], v_new[4], v_new[7]));
					face_new.add(new Face3D(v_new[1], v_new[3], v_new[8]));
					face_new.add(new Face3D(v_new[1], v_new[8], v_new[5]));
					face_new.add(new Face3D(v_new[2], v_new[7], v_new[6]));
					face_new.add(new Face3D(v_new[2], v_new[6], v_new[3]));
				}
			} else {
				f.isSelected = false;
			}
		}
		m.faces.addAll(face_new);
		m.removeSelectedFaces();
		m.makeEdges();
		m.setNormals();
		//System.out.println(testEdges());
	}

	// connect m1 to original
	public static void addModel(Model3D original, Model3D m1){
		Model3D additional = new Model3D(m1);
		Face3D selectedFace0 = null, selectedFace1 = null;
		Halfedge3D landmark0 = null, landmark1 = null;

		// get selected face
		for (Face3D f : original.faces) 
			if (f.isSelected)
				selectedFace0 = f;
		for (Face3D f : additional.faces) 
			if (f.isSelected)
				selectedFace1 = f;
		if (selectedFace0 == null || selectedFace1 == null)
			return;

		// get landmark vertex
		for (Halfedge3D he : selectedFace0.halfedges) {
			if (he.vertex.isSelected)
				landmark0 = he;
		}
		for (Halfedge3D he : selectedFace1.halfedges) {
			if (he.vertex.isSelected)
				landmark1 = he;
		}
		if (landmark0 == null || landmark1 == null)
			return;

		// arrange 1st landmarks (by parallel translation)
		Vec3D sub = GeomUtil.getDir(landmark1.vertex.p, landmark0.vertex.p);
		for (Vertex3D v : additional.vertices){
			v.p.add(sub);
		}

		// arrange 2nd vertex (by rotation)
		
		Vec3D dir0 = GeomUtil.getDir(landmark0);
		Vec3D dir1 = GeomUtil.getDir(landmark1.vertex.p, landmark1.prev.vertex.p);
		double angle = dir1.angle(dir0);

		Vec3D norm = new Vec3D();
		norm.cross(dir0, dir1);
		norm.normalize();

		Vec3D center = new Vec3D(landmark1.vertex.p);

		// try rotation
		for (Vertex3D v : additional.vertices){
			v.p.sub(center);
			v.p.rotate(norm, angle);
			v.p.add(center);
		}
		// confirm that the landmarks are at the same position
		if (GeomUtil.distance(landmark0.next.vertex.p, landmark1.prev.vertex.p) > EPS) {
			for (Vertex3D v : additional.vertices){
				v.p.sub(center);
				v.p.rotate(norm, -angle * 2);
				v.p.add(center);
			}
		}

		// arrange 3rd vertex
		
		norm = new Vec3D(dir0);
		norm.normalize();

		// dihedral angle
		Vec3D b1 = GeomUtil.getDir(landmark1.next);
		Vec3D b2 = GeomUtil.getDir(landmark0);
		Vec3D b3 = GeomUtil.getDir(landmark0.next);
		Vec3D c12 = Vec3D.getCrossed(b1, b2);
		Vec3D c23 = Vec3D.getCrossed(b2, b3);
		angle = Math.atan2(b1.scale(b2.length()).dot(c23), c12.dot(c23) );

		// try rotation
		for (Vertex3D v : additional.vertices){
			v.p.sub(center);
			v.p.rotate(norm, angle);
			v.p.add(center);
		}
		// confirm that the landmarks are at the same position
		if (GeomUtil.distance(landmark0.prev.vertex.p, landmark1.next.vertex.p) > 0.01) {
			for (Vertex3D v : additional.vertices){
				v.p.sub(center);
				v.p.rotate(norm, -angle * 2);
				v.p.add(center);
			}
		}

		// add faces
		for (Face3D f : additional.faces){
			if (!f.isSelected) {
				Face3D f_new = new Face3D();
				for (Halfedge3D he : f.halfedges) {
					Vertex3D v = getVertex(he.vertex.p, original.vertices);
					f_new.addVertex(v);
				}
				f_new.setHalfedgeLoopRelations();
				original.faces.add(f_new);
			}
		}

		uncheckAllVertices(original);
		original.removeSelectedFaces();
		original.makeEdges();
		original.setNormals();
		//System.out.println(testEdges());
	}

	// connect m1 to original (inside of original model)
	public static void subModel(Model3D original, Model3D m1){
		Model3D additional = new Model3D(m1);
		Face3D selectedFace0 = null, selectedFace1 = null;
		Halfedge3D landmark0 = null, landmark1 = null;

		// get selected face
		for (Face3D f : original.faces) 
			if (f.isSelected)
				selectedFace0 = f;
		for (Face3D f : additional.faces) 
			if (f.isSelected)
				selectedFace1 = f;
		if (selectedFace0 == null || selectedFace1 == null)
			return;

		// get landmark vertex
		for (Halfedge3D he : selectedFace0.halfedges) {
			if (he.vertex.isSelected)
				landmark0 = he;
		}
		for (Halfedge3D he : selectedFace1.halfedges) {
			if (he.vertex.isSelected)
				landmark1 = he;
		}
		if (landmark0 == null || landmark1 == null)
			return;

		// arrange 1st landmarks (by parallel translation)
		Vec3D sub = GeomUtil.getDir(landmark1.vertex.p, landmark0.vertex.p);
		for (Vertex3D v : additional.vertices){
			v.p.add(sub);
		}

		// arrange 2nd vertex (by rotation)
		
		Vec3D dir0 = GeomUtil.getDir(landmark0);
		Vec3D dir1 = GeomUtil.getDir(landmark1);
		double angle = dir1.angle(dir0);

		Vec3D norm = new Vec3D();
		norm.cross(dir0, dir1);
		norm.normalize();

		Vec3D center = new Vec3D(landmark1.vertex.p);

		// try rotation
		for (Vertex3D v : additional.vertices){
			v.p.sub(center);
			v.p.rotate(norm, angle);
			v.p.add(center);
		}
		// confirm that the landmarks are at the same position
		if (GeomUtil.distance(landmark0.next.vertex.p, landmark1.next.vertex.p) > EPS) {
			for (Vertex3D v : additional.vertices){
				v.p.sub(center);
				v.p.rotate(norm, -angle * 2);
				v.p.add(center);
			}
		}

		// arrange 3rd vertex
		norm = new Vec3D(dir0);
		norm.normalize();

		// dihedral angle
		Vec3D b1 = GeomUtil.getDir(landmark1.prev);
		Vec3D b2 = GeomUtil.getDir(landmark0);
		Vec3D b3 = GeomUtil.getDir(landmark0.next);
		Vec3D c12 = Vec3D.getCrossed(b1, b2);
		Vec3D c23 = Vec3D.getCrossed(b2, b3);
		angle = Math.atan2(b1.scale(b2.length()).dot(c23), c12.dot(c23) );

		// try rotation
		for (Vertex3D v : additional.vertices){
			v.p.sub(center);
			v.p.rotate(norm, angle);
			v.p.add(center);
		}
		// confirm that the landmarks are at the same position
		if (GeomUtil.distance(landmark0.prev.vertex.p, landmark1.prev.vertex.p) > EPS) {
			for (Vertex3D v : additional.vertices){
				v.p.sub(center);
				v.p.rotate(norm, -angle * 2);
				v.p.add(center);
			}
		}

		// add faces
		for (Face3D f : additional.faces){
			if (!f.isSelected) {
				Face3D f_new = new Face3D();
				for (int i=f.halfedges.size()-1; i>=0; i--) {
					Halfedge3D he = f.halfedges.get(i);
					Vertex3D v = getVertex(he.vertex.p, original.vertices);
					f_new.addVertex(v);
				}
				f_new.setHalfedgeLoopRelations();
				original.faces.add(f_new);
			}
		}

		uncheckAllVertices(original);
		original.removeSelectedFaces();
		original.makeEdges();
		original.setNormals();
		//System.out.println(testEdges());
	}

	/*
	// put prisms on polygonal faces with 4,5,6 vertices
	public static void fillPolygonWithTriangle(Model3D m) {
		
		ListIterator<Face3D> ite = m.faces.listIterator();
		while (ite.hasNext()) {
			Face3D f = ite.next();
			Vec3D center = GeomUtil.getIncenter(f);
			// square
			if (f.halfedges.size() == 4) {
				Vec3D norm = GeomUtil.getNormal(f);
				norm.scale(ModelOps.squarePyramidHeight);
				center.add(norm);

				Vertex3D v_new = new Vertex3D(center);
				m.vertices.add(v_new);
				ite.remove();
				ite.add(new Face3D(f.halfedges.get(3).vertex, f.halfedges.get(0).vertex, v_new));
				for (int i=0; i<3; i++)
					ite.add(new Face3D(f.halfedges.get(i).vertex, f.halfedges.get(i+1).vertex, v_new));
			}
			// pentagon
			if (f.halfedges.size() == 5) {
				Vec3D norm = GeomUtil.getNormal(f);
				norm.scale(ModelOps.pentagonalPyramidHeight);
				center.add(norm);

				Vertex3D v_new = new Vertex3D(center);
				m.vertices.add(v_new);
				ite.remove();
				ite.add(new Face3D(f.halfedges.get(4).vertex, f.halfedges.get(0).vertex, v_new));
				for (int i=0; i<4; i++)
					ite.add(new Face3D(f.halfedges.get(i).vertex, f.halfedges.get(i+1).vertex, v_new));
			}
			// hexagon
			if (f.halfedges.size() == 6) {
				Vertex3D v_new = new Vertex3D(center);
				m.vertices.add(v_new);
				ite.remove();
				ite.add(new Face3D(f.halfedges.get(5).vertex, f.halfedges.get(0).vertex, v_new));
				for (int i=0; i<5; i++)
					ite.add(new Face3D(f.halfedges.get(i).vertex, f.halfedges.get(i+1).vertex, v_new));
			}
		}
		uncheckAllVertices(m);
		m.makeEdges();
		m.setNormals();
	}*/

	// put prism on square faces
	public static void fillQuadsWithTriangles(Model3D m, int d) {
		ListIterator<Face3D> ite = m.faces.listIterator();
		while (ite.hasNext()) {
			Face3D f = ite.next();
			if (f.isSelected) {
				Vec3D center = GeomUtil.getIncenter(f);
				if (f.halfedges.size() == 4) {
					Vec3D norm = GeomUtil.getNormal(f);
					norm.scale(ModelOps.squarePyramidHeight * d);
					center.add(norm);

					Vertex3D v_new = new Vertex3D(center);
					m.vertices.add(v_new);
					ite.remove();
					ite.add(new Face3D(f.halfedges.get(3).vertex, f.halfedges.get(0).vertex, v_new));
					for (int i=0; i<3; i++)
						ite.add(new Face3D(f.halfedges.get(i).vertex, f.halfedges.get(i+1).vertex, v_new));
				}
			}
		}
		uncheckAllVertices(m);
		m.makeEdges();
		m.setNormals();
	}

	// put prism on pentagonal faces
	public static void fillPentagonsWithTriangles(Model3D m, int d) {
		ListIterator<Face3D> ite = m.faces.listIterator();
		while (ite.hasNext()) {
			Face3D f = ite.next();
			if (f.isSelected) {
				Vec3D center = GeomUtil.getIncenter(f);
				if (f.halfedges.size() == 5) {
					Vec3D norm = GeomUtil.getNormal(f);
					norm.scale(ModelOps.pentagonalPyramidHeight * d);
					center.add(norm);

					Vertex3D v_new = new Vertex3D(center);
					m.vertices.add(v_new);
					ite.remove();
					ite.add(new Face3D(f.halfedges.get(4).vertex, f.halfedges.get(0).vertex, v_new));
					for (int i=0; i<4; i++)
						ite.add(new Face3D(f.halfedges.get(i).vertex, f.halfedges.get(i+1).vertex, v_new));
				}
			}
		}
		uncheckAllVertices(m);
		m.makeEdges();
		m.setNormals();
	}

	// put prism on hexagonal faces
	public static void fillHexagonsWithTriangles(Model3D m) {
		ListIterator<Face3D> ite = m.faces.listIterator();
		while (ite.hasNext()) {
			Face3D f = ite.next();
			if (f.isSelected) {
				Vec3D center = GeomUtil.getIncenter(f);
				if (f.halfedges.size() == 6) {
					Vertex3D v_new = new Vertex3D(center);
					m.vertices.add(v_new);
					ite.remove();
					ite.add(new Face3D(f.halfedges.get(5).vertex, f.halfedges.get(0).vertex, v_new));
					for (int i=0; i<5; i++)
						ite.add(new Face3D(f.halfedges.get(i).vertex, f.halfedges.get(i+1).vertex, v_new));
				}
			}
		}
		uncheckAllVertices(m);
		m.makeEdges();
		m.setNormals();
	}

	// elongate operation
	public static void elongate (Model3D m) {

		uncheckAllVertices(m);

		// list of selected faces
		ArrayList<Face3D> selectedFaces = new ArrayList<Face3D>();
		for (Face3D f : m.faces){
			if (f.isSelected)
				selectedFaces.add(f);
		}
		if (selectedFaces.isEmpty()) return;

		// normal
		ArrayList<Halfedge3D> baseHalfedges = getBaseHalfedges(selectedFaces);
		Vec3D norm = getBaseNormal(baseHalfedges);
		norm.scale(m.length);

		
		for (Halfedge3D he : baseHalfedges){
			// duplicate vertices in base
			Vertex3D v = he.vertex;
			Vertex3D v_new = new Vertex3D(v);
			replaceVertex(selectedFaces, v, v_new);
			m.vertices.add(v_new);
		}
		for (Face3D f : selectedFaces){
			// translate duplicated vertices
			for (Halfedge3D he : f.halfedges) {
				if (!he.vertex.checkFlg){
					he.vertex.p.add(norm);
					he.vertex.checkFlg = true;
				}
			}
		}

		// create faces (side faces will be square)
		for (Halfedge3D he : baseHalfedges){
			
			// square
			Face3D f = new Face3D();
			f.addVertex(he.next.vertex);
			f.addVertex(he.vertex);
			f.addVertex(he.pair.next.vertex);
			f.addVertex(he.pair.vertex);
			f.setHalfedgeLoopRelations();
			m.faces.add(f);
		}
		uncheckAllVertices(m);
		unselectAllFaces(m);
		m.makeEdges();
		m.setNormals();
		m.write();
	}

	/// gyroelongate operation
	public static void gyroelongate(Model3D m) {

		uncheckAllVertices(m);

		// list of selected faces
		ArrayList<Face3D> selectedFaces = getSelectedFaces(m);
		if (selectedFaces.isEmpty()) return;

		// normal
		ArrayList<Halfedge3D> baseHalfedges = getBaseHalfedges(selectedFaces);
		Vec3D norm = getBaseNormal(baseHalfedges);

		// height of antiprism
		double s = Math.cos(GeomUtil.PI / (2.0 * baseHalfedges.size()));
		ModelOps.antiprismHegiht = m.length * Math.sqrt(1.0 - 1.0 / (s * s * 4.0));

		// incenter of base
		Vec3D incenter = new Vec3D();
		for (Halfedge3D he : baseHalfedges)
			incenter.add(he.vertex.p);
		incenter.scale(1.0 / baseHalfedges.size());

		double angle = GeomUtil.PI / baseHalfedges.size();

		for (Halfedge3D he : baseHalfedges){
			// duplicate vertices in base
			Vertex3D v = he.vertex;
			Vertex3D v_new = new Vertex3D(v);
			replaceVertex(selectedFaces, v, v_new);
			m.vertices.add(v_new);
		}
		Vec3D height = new Vec3D(norm);
		height.scale(ModelOps.antiprismHegiht);


		for (Face3D f : selectedFaces){
			// rotate and translate duplicated vertices
			for (Halfedge3D he : f.halfedges) {
				if (!he.vertex.checkFlg){
					Vertex3D v = he.vertex;
					v.p.sub(incenter);
					v.p.rotate(norm, angle);
					v.p.add(incenter);
					v.p.add(height);
					v.checkFlg = true;
				}
			}
		}
		// create faces
		for (Halfedge3D he : baseHalfedges){
			m.faces.add(new Face3D(he.vertex, he.pair.next.vertex, he.pair.vertex));
			m.faces.add(new Face3D(he.pair.vertex, he.next.vertex, he.vertex));
		}

		uncheckAllVertices(m);
		unselectAllFaces(m);
		m.makeEdges();
		m.setNormals();
	}

	// tucking operation
	public static void tuckCone(Model3D m) {

		// list of selected faces
		ArrayList<Face3D> selectedFaces = getSelectedFaces(m);
		if (selectedFaces.isEmpty()) return ;

		/* selected faces must form a prism (NO CHECK) */
		
		// get normal
		ArrayList<Halfedge3D> baseHalfedges = getBaseHalfedges(selectedFaces);
		Vec3D norm = getBaseNormal(baseHalfedges);
		norm.scale(m.length);

		// incenter of base
		Vec3D incenter = new Vec3D();
		for (Halfedge3D he : baseHalfedges) {
			incenter.add(he.vertex.p);
			he.vertex.checkFlg = true;
		}
		incenter.scale(1.0/baseHalfedges.size());
		// tuck a prism
		for (Face3D f : selectedFaces){
			// translate vertex
			for (int i=0,n=f.halfedges.size(); i<n; i++) {
				Halfedge3D he = f.halfedges.get(i);
				if (!he.vertex.checkFlg){
					Vec3D height = GeomUtil.getDir(he.vertex.p, incenter);
					height.scale(2.0);
					he.vertex.p.add(height);
					he.vertex.checkFlg = true;
				}
			}
		}

		uncheckAllVertices(m);
		unselectAllFaces(m);
		m.normalize();
		m.setNormals();
		//System.out.println(testEdges());
	}

	/// calculate heights of polyhedra and prisms
	public static void setConstants(Model3D m) {

		tetrahedronHeight = m.length * Math.sqrt(2.0 / 3.0);
		icosahedronHeight = m.length * (3 + Math.sqrt(5)) / (2 * Math.sqrt(3));
		squarePyramidHeight = m.length * Math.sqrt(2) / 2;
		pentagonalPyramidHeight = m.length * Math.sqrt( (5 - Math.sqrt(5))/10 );

		Vec3D[] pentagonalDipyramid = new Vec3D[6];
		double angle = 2 * Math.PI / 5;
		for (int i=0; i<5; i++)
			pentagonalDipyramid[i] = new Vec3D(m.length * Math.cos(angle * i), m.length * Math.sin(angle * i), 0);
		pentagonalDipyramid[5] = new Vec3D(0, 0, m.length / 10.0 *Math.sqrt(50.0 - 10 * Math.sqrt(5.0)));

		Vec3D[] tpentagonalDipyramid = new Vec3D[6];

		for (int i=0; i<5; i++)
			tpentagonalDipyramid[i] = new Vec3D(m.length * Math.cos(angle* i + Math.PI), m.length * Math.sin(angle * i + Math.PI), 0);
		tpentagonalDipyramid[5] = new Vec3D(0, 0, m.length / 10.0 *Math.sqrt(50.0 - 10 * Math.sqrt(5.0)));
	}

	/// subdivide all faces (a face is separated into 1/n^2 faces)
	public static boolean divideFaces (int n, Model3D m){
		if (!m.isTriangleMesh()) return false;

		ArrayList<Face3D> face_new = new ArrayList<Face3D>();
		Vec3D dir0 = new Vec3D();
		Vec3D dir1 = new Vec3D();

		for (Face3D f : m.faces) {
			if (f.halfedges.size() == 3) {
				Halfedge3D he0 = f.halfedges.get(0), he1 = f.halfedges.get(1);
				GeomUtil.setDir(he0, dir0);
				GeomUtil.setDir(he1, dir1);
				dir0.scale(1.0/n);
				dir1.scale(1.0/n);

				Vertex3D[] pre_vlist = new Vertex3D[1];
				pre_vlist[0] = he0.vertex;

				Vertex3D[] cur_vlist = new Vertex3D[2];
				Vec3D d0 = new Vec3D(he0.vertex.p);
				d0.add(dir0);
				cur_vlist[0] = getVertex(d0, m.vertices);

				for (int i=0; i<n; i++) {

					Vec3D d1 = new Vec3D(d0);

					for (int j=0; j<i+1; j++) {

						d1.add(dir1);
						Vertex3D vp = getVertex(d1, m.vertices);
						cur_vlist[j+1] = vp;
						face_new.add(new Face3D(pre_vlist[j], cur_vlist[j], vp));

						if (j < i) {
							face_new.add(new Face3D(pre_vlist[j], vp, pre_vlist[j+1]));
							d1 = new Vec3D(d1);
						}
					}

					Vertex3D[] tmp = new Vertex3D[i+3];
					d0 = new Vec3D(cur_vlist[0].p);
					d0.add(dir0);
					tmp[0] = getVertex(d0, m.vertices);

					pre_vlist = cur_vlist;
					cur_vlist = tmp;

				}
			}
		}
		m.faces.clear();
		m.faces.addAll(face_new);
		m.makeEdges();
		m.normalize();
		m.setNormals();
		setConstants(m);
		return true;
	}

	public void fractalTetrahedron(Model3D m){
		ArrayList<Face3D> face_new = new ArrayList<Face3D>();
		ListIterator<Face3D> ite = m.faces.listIterator();
		while (ite.hasNext()) {
			Face3D f = ite.next();
			Halfedge3D he0 = f.halfedges.get(0), he1 = f.halfedges.get(1), he2 = f.halfedges.get(2);

			Vec3D p0 = GeomUtil.midpoint(he2.vertex.p, he0.vertex.p);
			Vec3D p1 = GeomUtil.midpoint(he0.vertex.p, he1.vertex.p);
			Vec3D p2 = GeomUtil.midpoint(he1.vertex.p, he2.vertex.p);

			Vertex3D v0 = getVertex(p0, m.vertices);
			Vertex3D v1 = getVertex(p1, m.vertices);
			Vertex3D v2 = getVertex(p2, m.vertices);

			face_new.add(new Face3D(v0, he0.vertex, v1));
			face_new.add(new Face3D(v1, he1.vertex, v2));
			face_new.add(new Face3D(v2, he2.vertex, v0));
			Face3D centerTriangle = new Face3D(v0, v1, v2);
			centerTriangle.isSelected = true;;
			face_new.add(centerTriangle);
		}
		m.faces.clear();
		m.faces.addAll(face_new);
		m.makeEdges();
		m.length /= 2.0;
		//testEdges();
		setConstants(m);
		addTetrahedron(m);
	}

	// solve 3-coloring problem with backtracking
	public static void coloring(Model3D m){
		//初期化
		for (Edge3D e: m.edges) {
			e.color = 0;
		}

		for (int i=0; i<3; i++){
			m.edges.get(i).color = i+1;
		}
		recursiveColoring(3, m);

		System.out.println("complete coloring");
	}
	// get unused color by calculating XOR with connecting edges
	private static boolean recursiveColoring(int n, Model3D m){
		if (n >= m.edges.size()) {
			return true;
		}
		Edge3D e = m.edges.get(n);

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
				if (recursiveColoring(n+1, m))
					return true;
				e.color = oldColor;
			}
		} else if (c == 1) {
			for (int i=2; i<4; i++) {
				e.color = i;
				if (recursiveColoring(n+1, m))
					return true;
				e.color = oldColor;
			}
		} else if (c == 2) {
			for (int i=1; i<4; i+=2) {
				e.color = i;
				if (recursiveColoring(n+1, m))
					return true;
				e.color = oldColor;
			}
		} else if (c == 4) {
			for (int i=1; i<3; i++) {
				e.color = i;
				if (recursiveColoring(n+1, m))
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
			if (recursiveColoring(n+1, m))
				return true;
			e.color = oldColor;
		}
		return false;
	}

	/// get vertex nearest to p, otherwise create new vertex and return it
	private static Vertex3D getVertex (Vec3D p, ArrayList<Vertex3D> vlist) {
		for (Vertex3D v : vlist) {
			if (GeomUtil.distance(p, v.p) < EPS)
				return v;
		}
		Vertex3D v = new Vertex3D(p);
		vlist.add(v);
		return v;
	}
	/// replace vertex o in flist with vertex n
	private static void replaceVertex(ArrayList<Face3D> flist, Vertex3D o, Vertex3D n) {
		for (Face3D f : flist){
			for (Halfedge3D he : f.halfedges) {
				if (he.vertex.equals(o))
					he.vertex = n;
			}
		}
	}

	// create new list of selected faces and return it
	private static ArrayList<Face3D> getSelectedFaces(Model3D m) {
		ArrayList<Face3D> flist = new ArrayList<Face3D>();
		for (Face3D f : m.faces){
			if (f.isSelected)
				flist.add(f);
		}
		return flist;
	}

	/// (when selected faces form a prism) create halfedge list around base and return it
	private static ArrayList<Halfedge3D> getBaseHalfedges(ArrayList<Face3D> flist) {
		ArrayList<Halfedge3D> helist = new ArrayList<Halfedge3D>();
		for (Face3D f : flist){
			for (Halfedge3D he : f.halfedges) {
				if (!he.pair.face.isSelected) {
					helist.add(he);
				}
			}
		}
		return helist;
	}

	/// return normal of base (direction of prism)
	private static Vec3D getBaseNormal(ArrayList<Halfedge3D> helist) {

		Vec3D center = new Vec3D();
		for (Halfedge3D he : helist)
			center.add(he.vertex.p);
		center.scale(1.0/helist.size());

		Vec3D dir = GeomUtil.getDir(helist.get(0));
		center.sub(helist.get(0).vertex.p);
		//center.negate();
		//Vec3D toCenter = GeomUtil.getDir(helist.get(0).vertex.p, center);
		//dir.normalize();
		//center.normalize();
		dir.cross(center);
		dir.normalize();

		return dir;
	}
}