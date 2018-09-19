package deltamod.geom;

public class ModularOrigamiConverter {

	public static Model3D getAssembledIsosceles(Model3D m, double height, double noise) {

		// 新しく3Dモデルを構築------
		Model3D assembled = new Model3D();

		Vec3D tmp = new Vec3D();
		final double h = height;//Math.sqrt(1.0/6.0);//1.080123;//1.5115226;// Height of pyramid
		for (Face3D f : m.faces) {
			f.setNormal();
			f.setIncenter();
		}
			
		final double delta0 = 0.2;  //ポケットと手のずれ
		final double delta2 = -0.2;   //手の縮小
		final double delta1 = noise;  //組み合わせのずれ
		
		for (Edge3D e : m.edges) {
			
			Vertex3D[] points = new Vertex3D[6];  // ユニットの各頂点
			Halfedge3D he0 = e.he[0], he1 = he0.pair;
			Face3D f0 = he0.face, f1 = he1.face;
			//he0始点
			Vec3D p0 = new Vec3D(he0.vertex.p);
			GeomUtil.setDir(he0, tmp);
			tmp.scale(getDeformAmount(delta1));
			p0.add(tmp);
			points[0] = new Vertex3D(p0);
			//f1側flap
			Vec3D p1 = new Vec3D(he1.prev.vertex.p);
			GeomUtil.setDir(he1.next, tmp);
			tmp.scale(delta2); //shorten the flap
			p1.add(tmp);
			tmp.set(he1.next.edge.getNormal());  // move to inside
			//tmp.normalize();
			tmp.scale(getDeformAmount(delta0));
			p1.add(tmp);
			points[1] = new Vertex3D(p1);
			//f1側角錐の頂点
			Vec3D p2 = new Vec3D(f1.incenter);
			tmp.set(f1.n);
			tmp.scale(h);
			p2.add(tmp);
			GeomUtil.setDir(p2, p0, tmp);
			tmp.normalize();
			tmp.scale(getDeformAmount(delta1));
			p2.add(tmp);
			points[2] = new Vertex3D(p2);
			
			//he1始点
			Vec3D p3 = new Vec3D(he1.vertex.p);  
			GeomUtil.setDir(he1, tmp);
			tmp.scale(getDeformAmount(delta1));
			p3.add(tmp);
			points[3] = new Vertex3D(p3);
			//f0側flap
			Vec3D p4 = new Vec3D(he0.prev.vertex.p);
			GeomUtil.setDir(he0.next, tmp);
			tmp.scale(delta2);
			p4.add(tmp);
			tmp.set(he0.next.edge.getNormal());  // move to inside
			tmp.scale(getDeformAmount(delta0));
			p4.add(tmp);
			points[4] = new Vertex3D(p4);
			//f0側角錐の頂点
			Vec3D p5 = new Vec3D(f0.incenter);
			tmp.set(f0.n);
			tmp.scale(h);
			p5.add(tmp);
			GeomUtil.setDir(p5, p3, tmp);
			tmp.normalize();
			tmp.scale(getDeformAmount(delta1));
			p5.add(tmp);
			points[5] = new Vertex3D(p5);
			
			for (int j=0; j<6; j++) {
				assembled.vertices.add(points[j]);
			}
			
			//f1側flap
			Face3D module0 = new Face3D();
			module0.addVertex(points[0]);
			module0.addVertex(points[1]);
			module0.addVertex(points[2]);
			module0.setHalfedgeLoopRelations();
			module0.color = e.color;
			module0.setNormal();
			
			//f1側pocket
			Face3D module1 = new Face3D();
			module1.addVertex(points[0]);
			module1.addVertex(points[2]);
			module1.addVertex(points[3]);
			module1.setHalfedgeLoopRelations();
			module1.color = e.color;
			module1.setNormal();
			
			//f0側pocket
			Face3D module2 = new Face3D();
			module2.addVertex(points[3]);
			module2.addVertex(points[5]);
			module2.addVertex(points[0]);
			module2.setHalfedgeLoopRelations();
			module2.color = e.color;
			module2.setNormal();
			
			//f0側flap
			Face3D module3 = new Face3D();
			module3.addVertex(points[3]);
			module3.addVertex(points[4]);
			module3.addVertex(points[5]);
			module3.setHalfedgeLoopRelations();
			module3.color = e.color;
			module3.setNormal();
			
			assembled.faces.add(module0);
			assembled.faces.add(module1);
			assembled.faces.add(module2);
			assembled.faces.add(module3);
		}
		assembled.makeEdges();
		return assembled;
	}
	
	public static Model3D getAssembledRidgePocket(Model3D m, double height, double noise) {
		// 新しく3Dモデルを構築------
		Model3D assembled = new Model3D();

		Vec3D tmp = new Vec3D();
		final double h = Math.sqrt(1.0 / 6.0); // Height of pyramid
		for (Face3D f : m.faces) {
			f.setNormal();
			f.setIncenter();
		}

		for (Edge3D e : m.edges) {

			Vertex3D[] points = new Vertex3D[5]; // ユニットの各頂点

			// 1辺あたり2個セット
			for (int k = 0; k < 2; k++) {
				Halfedge3D he0 = e.he[k], he1 = he0.pair;
				Face3D f0 = he0.face, f1 = he1.face;

				// 辺の中点
				Vec3D p0 = new Vec3D(he0.vertex.p);
				p0.add(he0.next.vertex.p);
				p0.scale(0.5);
				points[0] = new Vertex3D(p0);
				// f1側角錐の頂点
				Vec3D p1 = new Vec3D(f1.incenter);
				points[1] = new Vertex3D(p1);
				// f0側の底面1
				points[2] = new Vertex3D(he0.next.vertex);
				// f0側の底面2
				tmp.set(he0.prev.vertex.p);
				tmp.sub(he0.next.vertex.p);
				tmp.scale(0.5);
				points[3] = new Vertex3D(he0.next.vertex);
				points[3].p.add(tmp);
				tmp.set(points[3].p);
				// f0側角錐の頂点
				Vec3D p4 = new Vec3D(f0.incenter);
				points[4] = new Vertex3D(p4);
				tmp.set(f0.n);
				tmp.scale(h);
				points[4].p.add(tmp);
				for (int j = 0; j < 5; j++) {
					assembled.vertices.add(points[j]);
				}

				Face3D module0 = new Face3D(); // 面1
				module0.addVertex(points[0]);
				module0.addVertex(points[2]);
				module0.addVertex(points[1]);
				module0.setHalfedgeLoopRelations();
				module0.color = e.color;
				module0.setNormal();

				Face3D module1 = new Face3D(); // 面2
				module1.addVertex(points[2]);
				module1.addVertex(points[3]);
				module1.addVertex(points[4]);
				module1.setHalfedgeLoopRelations();
				module1.color = e.color;
				module1.setNormal();

				Face3D module2 = new Face3D(); // 面3
				module2.addVertex(points[4]);
				module2.addVertex(points[0]);
				module2.addVertex(points[2]);
				module2.setHalfedgeLoopRelations();
				module2.color = e.color;
				module2.setNormal();

				assembled.faces.add(module0);
				assembled.faces.add(module1);
				assembled.faces.add(module2);
			}
		}
		assembled.makeEdges();
		return assembled;
	}
	
	public static Model3D getAssembledSonobe(Model3D m, double height, double noise) {
		// 新しく3Dモデルを構築------
		Model3D assembled = new Model3D();

		Vec3D tmp = new Vec3D();
		final double h = height; //Math.sqrt(1.0 / 6.0); // Height of pyramid
		for (Face3D f : m.faces) {
			f.setNormal();
			f.setIncenter();
		}

		final double delta0 = 0.03;  //ポケットと手のずれ
		final double delta1 = noise;  //組み合わせのずれ
		
		for (Edge3D e : m.edges) {
			Vertex3D[] points = new Vertex3D[5]; // ユニットの各頂点
			// 1辺あたり2個セット
			for (int k = 0; k < 2; k++) {
				Halfedge3D he0 = e.he[k], he1 = he0.pair;
				Face3D f0 = he0.face, f1 = he1.face;

				// 辺の中点
				Vec3D p0 = new Vec3D(he0.vertex.p);
				p0.add(he0.next.vertex.p);
				p0.scale(0.5);
				points[0] = new Vertex3D(p0);
				// f1側角錐の頂点
				Vec3D p1 = new Vec3D(f1.incenter);
				points[1] = new Vertex3D(p1);
				tmp.set(f1.incenter);
				tmp.sub(f1.n);
				tmp.scale(0.5);
				points[1].p.add(tmp);
				// f0側の底面1
				points[2] = new Vertex3D(he0.next.vertex);
				// f0側の底面2
				tmp.set(he0.prev.vertex.p);
				tmp.sub(he0.next.vertex.p);
				tmp.scale(0.9);
				points[3] = new Vertex3D(he0.next.vertex);
				points[3].p.add(tmp);
				tmp.set(f0.incenter);
				tmp.sub(f0.n);
				tmp.sub(points[3].p);
				tmp.scale(delta0);
				points[3].p.add(tmp);
				// f0側角錐の頂点
				Vec3D p4 = new Vec3D(f0.incenter);
				points[4] = new Vertex3D(p4);
				tmp.set(f0.n);
				tmp.scale(h);
				points[4].p.add(tmp);
				tmp.set(he1.vertex.p);
				tmp.sub(points[4].p);
				tmp.scale(getDeformAmount(delta1));
				points[4].p.add(tmp);
				
				for (int j = 0; j < 5; j++) {
					assembled.vertices.add(points[j]);
				}

				Face3D module0 = new Face3D(); // 面1
				module0.addVertex(points[2]);
				module0.addVertex(points[0]);
				
				module0.addVertex(points[1]);
				module0.setHalfedgeLoopRelations();
				module0.color = e.color;
				module0.setNormal();

				Face3D module1 = new Face3D(); // 面2
				module1.addVertex(points[2]);
				module1.addVertex(points[3]);
				module1.addVertex(points[4]);
				module1.setHalfedgeLoopRelations();
				module1.color = e.color;
				module1.setNormal();

				Face3D module2 = new Face3D(); // 面3
				module2.addVertex(points[4]);
				module2.addVertex(points[0]);
				module2.addVertex(points[2]);
				module2.setHalfedgeLoopRelations();
				module2.color = e.color;
				module2.setNormal();

				assembled.faces.add(module0);
				assembled.faces.add(module1);
				assembled.faces.add(module2);
			}
		}
		assembled.makeEdges();
		return assembled;
	}


	private static double getDeformAmount(double d) {
		return d * Math.random();
	}

}
