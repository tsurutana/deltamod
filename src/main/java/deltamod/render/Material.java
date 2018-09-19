package deltamod.render;

import static com.jogamp.opengl.GL2.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.jogamp.opengl.GL2;

/**
 * Materials
 * @author tsuruta
 *
 */

public class Material {

	private float[] ambient = new float[4];
	private float[] diffuse = new float[4];
	private float[] specular = new float[4];
	private float[] shiness = new float[1];

	public static HashMap<String, Material> materials = new HashMap<String, Material>();

	// create materials. run once on start up.
	public static void generateMaterials() {
		createMaterial("emerald", 0.0215F, 0.1745F, 0.0215F, 0.07568F, 0.61424F, 0.07568F, 0.633F, 0.727811F, 0.633F, 76.8F);
		createMaterial("silver", 0.19F, 0.19F, 0.19F, 0.51F, 0.51F, 0.51F, 0.51F, 0.51F, 0.51F, 51.2F);
		createMaterial("gold", 0.55F, 0.52F, 0.36F, 0.35F, 0.31F, 0.09F, 0.80F, 0.72F, 0.21F, 83.1F);
		createMaterial("chrom", 0.25F, 0.25F, 0.25F, 0.40F, 0.40F, 0.40F, 0.77F, 0.77F, 0.77F, 76.8F);
		createMaterial("bronze", 0.21F, 0.13F, 0.05F, 0.71F, 0.43F, 0.18F, 0.39F, 0.27F, 0.17F, 25.6F);
		createMaterial("kupfer", 0.19F, 0.07F, 0.02F, 0.70F, 0.27F, 0.08F, 0.26F, 0.14F, 0.09F, 12.8F);
		
		createMaterial("red", 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.1F, 0.1F, 0.1F, 1F);
		createMaterial("green", 0.0F, 0.0F, 0.0F, 0.1F, 0.35F, 0.1F, 0.1F, 0.1F, 0.1F, 1F);
		createMaterial("blue", 0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.1F, 0.1F, 0.1F, 1F);
		/*
		for (int i=0; i<100; i++) {
			final float d0 = (float) Math.random();
			final float d1 = (float) Math.random();
			final float d2 = (float) Math.random();
			float a0 = (float) (d0 * 1.2);
			a0 = (a0 > 1.0)? 1.0f : a0;
			float a1 = (float) (d1 * 1.2);
			a1 = (a1 > 1.0)? 1.0f : a1;
			float a2 = (float) (d2 * 1.2);
			a2 = (a2 > 1.0)? 1.0f : a2;
			final float s0 = (float) (a0 * 0.8);
			final float s1 = (float) (a1 * 0.8);
			final float s2 = (float) (a2 * 0.8);
			createMaterial("randmtl" + i, a0, a1, a2, d0, d1, d2, s0, s1, s2, 30F);
		}*/
	}

	public static void createMaterial(String name, float ambR, float ambG, float ambB, 
			float difR, float difG, float difB, float speR, float speG, float speB, float shi) {
		Material mat = new Material();

		mat.ambient[0] = ambR;
		mat.ambient[1] = ambG;
		mat.ambient[2] = ambB;
		mat.ambient[3] = 1.0F;

		mat.diffuse[0] = difR;
		mat.diffuse[1] = difG;
		mat.diffuse[2] = difB;
		mat.diffuse[3] = 1.0F;

		mat.specular[0] = speR;
		mat.specular[1] = speG;
		mat.specular[2] = speB;
		mat.specular[3] = 1.0F;

		mat.shiness[0] = shi;

		materials.put(name, mat);
	}

	public static void setMaterial(GL2 gl, String name) {
		Material mat = (Material) materials.get(name);
		gl.glMaterialfv(GL_FRONT, GL_AMBIENT, mat.ambient, 0);
		gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, mat.diffuse, 0);
		gl.glMaterialfv(GL_FRONT, GL_SPECULAR, mat.specular, 0);
		gl.glMaterialfv(GL_FRONT, GL_SHININESS, mat.shiness, 0);
	}

	public static void export(String filepath) throws IOException {
		FileWriter fw = new FileWriter(filepath);
		BufferedWriter bw = new BufferedWriter(fw);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(6);

		for (Iterator<Entry<String, Material>> ite = materials.entrySet().iterator(); ite.hasNext();){
			Entry<String, Material> entry = ite.next();
			String key = entry.getKey();
			Material mat = entry.getValue();
			bw.write("newmtl " + key); bw.newLine();
			bw.write("Ka " + nf.format(mat.ambient[0]) + " " + nf.format(mat.ambient[1]) + " " + nf.format(mat.ambient[2]));
			bw.newLine();
			bw.write("Kd " + nf.format(mat.diffuse[0]) + " " + nf.format(mat.diffuse[1]) + " " + nf.format(mat.diffuse[2]));
			bw.newLine();
			bw.write("Ks " + nf.format(mat.specular[0]) + " " + nf.format(mat.specular[1]) + " " + nf.format(mat.specular[2]));
			bw.newLine();
			bw.write("Ns " + nf.format(mat.shiness[0]));
			bw.newLine();
			bw.newLine();
		}
		bw.close();
		
		System.out.println("materials are exported.");
	}
}
