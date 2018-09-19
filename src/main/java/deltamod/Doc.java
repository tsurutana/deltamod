package deltamod;

import deltamod.geom.Model3D;

public class Doc {
	
	private Model3D m;
	private Model3D prev;

	public Model3D getModel() {
		return m;
	}

	public void setModel(Model3D m) {
		this.m = m;
	}
	
	public void save() {
		prev = new Model3D(m);
	}
	
	public void restore() {
		if (prev != null)
			m = new Model3D(prev);
	}
}
