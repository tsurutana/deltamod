package deltamod.render;

public class Camera {

	public double[] eye = { 0.0d, 0.0d, 10.0d, 0.0d };
	public float rotX = 0.0f, rotY = 0.0f;
	public float traX = 0.0f, traY = 0.0f;
	public float scale = 1.0f;
	
	private static float magnitude = 0.5f;
	private static float magnitude2 = 0.1f;
	
	public void rotateX(float dx) {
		rotX += dx * magnitude;
		if (rotX < 0) 
			rotX += 360;
		else if (rotX >= 360)
			rotX -= 360;
	}
	
	public void rotateY(float dy) {
		rotY += dy * magnitude;
		if (rotY < 0) 
			rotY += 360;
		else if (rotY >= 360)
			rotY -= 360;
	}
	
	public void translateX(float dx) {
		traX += dx;
	}
	
	public void translateY(float dy) {
		traY -= dy;
	}
	
	public void scale(float amount) {
		scale += amount * magnitude2;
		if (scale < 0.1)
			scale = 0.1f;
	}
}
