package deltamod.file;

import javax.swing.filechooser.FileFilter;

public class FileFilterEx extends FileFilter {
	
	private String extensions[];
	private String msg;

	public static FileFilterEx OBJ = new FileFilterEx(new String[]{"." + "obj"}, "(*.obj) OBJ File");
	public static FileFilterEx MOBJ = new FileFilterEx(new String[]{"." + "obj"}, "(*.obj) Modular OBJ+MTL File");
	public static FileFilterEx SVG = new FileFilterEx(new String[]{"." + "svg"}, "(*.svg) SVG File");
	public static FileFilterEx GraphML = new FileFilterEx(new String[]{"." + "graphml"}, "(*.graphml) GraphML File");
	public static FileFilterEx PCODE = new FileFilterEx(new String[]{"." + "pcode"}, "(*.pcode) Planar Code File");
	public static FileFilterEx M = new FileFilterEx(new String[]{"." + "m"}, "(*.m) LiveGraphics3D File");
	
	public FileFilterEx(String[] extensions , String msg) {
		this.extensions = extensions;
		this.msg = msg;
	}
	public boolean accept(java.io.File f) {
		for(int i = 0; i < extensions.length; i++) {
			if(f.isDirectory()) {
				return true;
			}
			if(f.getName().endsWith(extensions[i])) {
				return true;
			}
		}
		return false;
	}

	public String getDescription() { return msg; }
}
