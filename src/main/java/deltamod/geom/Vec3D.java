package deltamod.geom;

public class Vec3D {

	public double x, y, z;

	public Vec3D() {
	}
	public Vec3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vec3D(Vec3D v){
		set(v);
	}
	

	public void set(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Vec3D v){
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public void add(Vec3D v){
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}

	public void sub(Vec3D v){
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}

	public double angle(Vec3D v) {
		return Math.acos( dot(v) / (length() * v.length()) );
	}

	public void cross(Vec3D v) {
		double nx = y * v.z - v.y * z;
		double ny = z * v.x - v.z * x;
		double nz = x * v.y - v.x * y;
		x = nx;
		y = ny;
		z = nz;
	}
	public void cross(Vec3D v1, Vec3D v2) {
		double nx = v1.y * v2.z - v2.y * v1.z;
		double ny = v1.z * v2.x - v2.z * v1.x;
		double nz = v1.x * v2.y - v2.x * v1.y;
		x = nx;
		y = ny;
		z = nz;
	}
	public static Vec3D getCrossed(Vec3D v1, Vec3D v2) {
		Vec3D c = new Vec3D();
		double nx = v1.y * v2.z - v2.y * v1.z;
		double ny = v1.z * v2.x - v2.z * v1.x;
		double nz = v1.x * v2.y - v2.x * v1.y;
		c.x = nx;
		c.y = ny;
		c.z = nz;
		return c;
	}

	public double dot(Vec3D v) {
		return x * v.x + y * v.y + z * v.z;
	}
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	public double lengthSquared() {
		return x * x + y * y + z * z;
	}

	public double distanceTo(Vec3D v) {
		double dx = x - v.x;
		double dy = y - v.y;
		double dz = z - v.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public double squaredDistanceTo(Vec3D v) {
		final double dx = x - v.x;
		final double dy = y - v.y;
		final double dz = z - v.z;
		return dx * dx + dy * dy + dz * dz;
	}


	public void normalize(){
		final double len = length();
		if (len == 0)
			return;
		final double scale = 1.0 / len;
		x *= scale;
		y *= scale;
		z *= scale;
	}
	
	public Vec3D scale(double scale){
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}
	
	public void negate(){
		x *= -1;
		y *= -1;
		z *= -1;
	}

	public void rotate(Vec3D axis, double theta) {
		final double ux = axis.x * x;
		final double uy = axis.x * y;
		final double uz = axis.x * z;
		final double vx = axis.y * x;
		final double vy = axis.y * y;
		final double vz = axis.y * z;
		final double wx = axis.z * x;
		final double wy = axis.z * y;
		final double wz = axis.z * z;
		final double si = Math.sin(theta);
		final double co = Math.cos(theta);
		double nx = (axis.x * (ux + vy + wz) + (x * (axis.y * axis.y + axis.z * axis.z) - axis.x * (vy + wz))
				* co + (-wy + vz) * si);
		double ny = (axis.y * (ux + vy + wz)+ (y * (axis.x * axis.x + axis.z * axis.z) - axis.y	* (ux + wz))
				* co + (wx - uz) * si);
		double nz =	(axis.z				* (ux + vy + wz)	+ (z * (axis.x * axis.x + axis.y * axis.y) - axis.z	* (ux + vy))
				* co + (-vx + uy) * si);
		x = nx;
		y = ny;
		z = nz;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3D) {
            Vec3D v = (Vec3D) obj;
            return x == v.x && y == v.y && z == v.z;
        }
        return false;
    }
	
	public void printout() {
		System.out.println("(" + x + ", " + y + ", " + z + ")");
	}
}
