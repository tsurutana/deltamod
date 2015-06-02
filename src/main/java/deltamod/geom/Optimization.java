
package deltamod.geom;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;

public class Optimization {

	private Model3D m;

	private FlexCompRowMatrix A;
	private DenseMatrix hessian;  // approximation of Hessian
	private DenseMatrix jacobian;
	private DenseVector fx;
	private DenseVector d_fx;
	private DenseVector dir;
	private DenseVector tmp;
	private DenseMatrix backup;

	private int NUM_OF_CONSTRAINTS = 1;
	private DenseVector constraintEdges;
	private double lambda;

	private int[] NUM_OF_VERTICES = new int[4];
	private int NUM_OF_EDGES;
	private double DELTA = 1.0e-7;
	
	public Optimization(Model3D model, double error) {
		m = new Model3D(model);

		for (int i=0, n=m.vertices.size(); i<4; i++)
			NUM_OF_VERTICES[i] = n * i;
		NUM_OF_EDGES = m.edges.size();

		for (int i=0; i<NUM_OF_EDGES; i++)
			m.edges.get(i).index = i;

		A = new FlexCompRowMatrix(NUM_OF_VERTICES[3], NUM_OF_VERTICES[3]);
		hessian = new DenseMatrix(NUM_OF_VERTICES[3], NUM_OF_VERTICES[3]);
		jacobian = new DenseMatrix(NUM_OF_CONSTRAINTS, NUM_OF_VERTICES[3]);
		constraintEdges = new DenseVector(NUM_OF_EDGES);
		fx = new DenseVector(NUM_OF_CONSTRAINTS);
		d_fx = new DenseVector(NUM_OF_VERTICES[3]);
		dir = new DenseVector(NUM_OF_VERTICES[3]);
		tmp = new DenseVector(NUM_OF_CONSTRAINTS);
		backup = new DenseMatrix(3, NUM_OF_VERTICES[1]);

	}

	public Model3D optimize(int itr) {
		System.out.println("---------------------");
		long start = System.currentTimeMillis();

		double cost = cost(), prevCost = cost;
		double difference = Double.MAX_VALUE;
		System.out.println("initial cost :" + cost);

		int i = 0;
		lambda = 1.0;

		for (; i<itr && difference > 1.0e-16 ; i++) {

			computeNumericalJacobian();
			jacobian.transAmult(jacobian, hessian);

			boolean foundBetter = false;
			for( int k = 0; k < 5; k++ ) {
				computeA(lambda);

				gaussSeidel(A, dir, d_fx);

				// save positions of all vertices
				backupVertexPosition();

				// save positions of all vertices
				for (int j=0; j<NUM_OF_VERTICES[1]; j++) {
					Vec3D h = new Vec3D(dir.get(j), dir.get(j+NUM_OF_VERTICES[1]), dir.get(j+NUM_OF_VERTICES[2]));
					Vertex3D v = m.vertices.get(j);
					v.p.sub(h);
				}

				cost = cost();
				if( cost < prevCost ) {
					// smaller solution have found
					foundBetter = true;
					difference = prevCost - cost;
					//System.out.println(difference);
					prevCost = cost;
					lambda *= 0.1;
				} else {
					lambda *= 10.0;
					// restore positions of all vertices
					restoreVertexPosition(jacobian);
				}
			}

			if (!foundBetter) {
				System.out.println("can't find better direction");
				break;
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("<-- finish at iteration " + i +". Final cost: " + cost);
		System.out.println("time [ms]："+ (end - start));

		m.normalize();
		return m;
	}

	///ヤコビアンの計算
	private DenseMatrix computeNumericalJacobian() {
		final double invDelta = 1.0/DELTA;
		final double fx_tmp = -invDelta * cost(); 
		tmp.set(-invDelta, fx);

		for(int i=0; i<NUM_OF_VERTICES[1]; i++ ) {

			Vec3D p = m.vertices.get(i).p;
			p.x += DELTA;
			d_fx.set(i, cost(i)*invDelta + fx_tmp);
			fx.scale(invDelta);
			fx.add(tmp);
			for (int j=0; j<NUM_OF_CONSTRAINTS; j++)
				jacobian.set(j, i, fx.get(j));
			p.x -= DELTA;

			p.y += DELTA;
			d_fx.set(i + NUM_OF_VERTICES[1], cost(i)*invDelta + fx_tmp);
			fx.scale(invDelta);
			fx.add(tmp);
			for (int j=0; j<NUM_OF_CONSTRAINTS; j++)
				jacobian.set(j, i + NUM_OF_VERTICES[1], fx.get(j));
			p.y -= DELTA;

			p.z += DELTA;
			d_fx.set(i + NUM_OF_VERTICES[2], cost(i)*invDelta + fx_tmp);
			fx.scale(invDelta);
			fx.add(tmp);
			for (int j=0; j<NUM_OF_CONSTRAINTS; j++)
				jacobian.set(j, i + NUM_OF_VERTICES[2], fx.get(j));
			p.z -= DELTA;

			cost(i);
		}
		return jacobian;
	}

	private void computeA( double lambda ) {
		A.set(hessian);
		for( int i = 0; i < NUM_OF_VERTICES[3]; i++ ) {
			A.set(i,i, A.get(i,i) + lambda);
		}
	}

	private DenseVector computeEdgeLengthUniqueness() {
		m.setMeanEdgeLength();
		for (int i=0; i<NUM_OF_EDGES; i++) {
			Edge3D e = m.edges.get(i);
			final double dif = (m.length - e.getLength());
			constraintEdges.set(i, dif * dif);
		}
		return constraintEdges;
	}
	private DenseVector computeEdgeLengthUniqueness(int i) {
		Vertex3D v = m.vertices.get(i);
		for (Halfedge3D he : v.halfedges) {
			Edge3D e = he.edge;
			final double dif = (m.length - e.getLength());
			constraintEdges.set(e.index, dif * dif);
		}
		return constraintEdges;
	}

	private double cost() {
		computeEdgeLengthUniqueness();
		fx.set(0, getElementSum(constraintEdges));

		return getElementSum(fx);
	}
	private double cost(int i) {
		computeEdgeLengthUniqueness(i);
		fx.set(0, getElementSum(constraintEdges));
		
		return getElementSum(fx);
	}

	private void backupVertexPosition() {
		for (int i=0; i< NUM_OF_VERTICES[1]; i++) {
			Vertex3D v = m.vertices.get(i);
			backup.set(0, i, v.p.x);
			backup.set(1, i, v.p.y);
			backup.set(2, i, v.p.z);
		}
	}
	private void restoreVertexPosition(DenseMatrix jacobian2) {
		for (int i=0; i< NUM_OF_VERTICES[1]; i++) {
			Vertex3D v = m.vertices.get(i);
			v.p.x = backup.get(0, i);
			v.p.y = backup.get(1, i);
			v.p.z = backup.get(2, i);
		}
	}

	private double getElementSum(DenseVector vec) {
		double sum = 0;
		for (VectorEntry ve : vec)
			sum += ve.get();
		return sum;
	}

	public void printout(){
		for (int i=0; i<NUM_OF_CONSTRAINTS; i++)
			System.out.println(fx.get(i));
	}

	private void gaussSeidel(FlexCompRowMatrix M, DenseVector v, DenseVector b) {
		int numRows = M.numRows();
		SparseVector curr;
		double diag;
		for(int i=0;i<numRows;++i) {
			curr = M.getRow(i);
			diag = curr.get(i);
			v.add(i,(b.get(i)-curr.dot(v))/diag);
		}
	}

}
