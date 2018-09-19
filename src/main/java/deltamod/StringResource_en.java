package deltamod;

import java.util.ListResourceBundle;

/**
 * String resource (English)
 * @author tsuruta
 *
 */

public class StringResource_en extends ListResourceBundle {
	static final Object[][] strings = { 
		{"Title", "DeltaMod 1.1" }, 
		{"File", "File" }, 
		{"New", "New" }, 
		{"Open", "Open..." }, 
		{"Save", "Save..." }, 
		{"SaveAs", "Save As..." }, 
		{"Export", "Export..."},
		{"Exit", "Exit" }, 

		{"Edit", "Edit" }, 

		{"View", "View" },
		{"Reset", "Reset camera position" },
		{"Flat", "Flat" },
		{"FlatLines", "Flat with Lines" },
		{"Wireframe", "Wireframe" },

		{"Options", "Options"},

		{"Optimization", "Optimization"},
		{"Optimize", "Optimize"},
		{"AcceptableError", "Acceptable Error :"},
		{"NumOfIteration", "Num of Iterations :"},
		{"OptimizeDescription", "<html>Regularization tries to make all edge-length unique by iterative deformation.<br>" +
		"An input polyhedron which has triangle mesh will become a deltahedron.<br>"},
		{"LoadReference", "Load reference 3D model"},
		
		{"Assembly", "Assembly"},
		{"Assemble", "Optimize"},
		{"AutomaticColoring", "Auto 3-Coloring"},
		{"Colors", "Colors :"},
		{"Hand-assemblyNoise", "Hand-assembly Error :"},
		{"PyramidHeight", "Pyramid Height :"},
		{"AssemblyDescription", "<html>Assembled form of your model.<br>"},

		{"Help", "Help" }, 
		{"Version", "Version..."},

		{"DialogTitle_FileSave", "Save As"},
		{"Warning_SameNameFileExist", "The file already exist. Do you want to replace the existing file?"},
		{"Error_FileSaveFailed", "File Saving Failed!"},
		{"Manual", "Rotation: Ctrl + Drag, Move: Shift + Drag, Perform Operation: Ctrl + X"},

		{"Connect", "Connect"},
		{"AddTetra", "Tetrahedron"},
		{"AddOcta", "Octahedron"},
		{"AddIcosa", "Icosahedron"},
		{"AddModel", "Other Deltahedron"},
		{"Load", "Load..."},
		{"Subtract", "Connect inside"},
		{"ConnectDescription", "<html>To connect another deltahedron on selected face(s),<br>" +
		"select a single face on an additional deltahedron (except for regular polyhedron).<br>"},

		{"Elongate", "Elongate"},
		{"Gyroelongate", "Gyroelongate"},
		{"ElongateDescription", "<html>Elongate/Gyroelongate selected faces that form a pyramid. " +
		"(Insert a prism/antiprism under the selected pyramid.)"},

		{"Tuck", "Tuck"},
		{"TuckDescription", "<html>Tuck/Pull selected faces that form a pyramid."},

		{"Fill", "Fill"},
		{"FillQuads", "Fill Quads"},
		{"FillPentagons", "Fill Pentagons"},
		{"FillHexagons", "Fill Hexagons"},
		{"FillInside", "Put inside"},
		{"FillDescription", "<html>Fill the selected N-polygonal(N>3) faces  with triangles.<br>" +
		"In the case of N=4,5 , a regular pyramid will be put on the face."},

		{"Divide", "Subdivide"},
		{"NumOfDivision", "Division by :"},
		{"DivDescription", "<html>Divide each edges into N equal edges. " +
				"Thus, each faces will be divide into N^2 triangles.<br>" +
		"(Editing polyhedron must be Deltahedron)"},

		{"Operation", "Operation"},

		{"SelectAll", "Select all faces (Ctrl+A)"},
		{"UnselectAll", "Unselect all faces and vertex (ESC)"},
		{"GeomToOrig", "Set the center of geometry to origin"},

		{"C4", "Tetrahedron"},
		{"C6", "Triangular dipyramid"},
		{"C8", "Octahedron"},
		{"C10", "Pentagonal dipyramid"},
		{"C12", "Snub disphenoid"},
		{"C14", "Triaugmented triangular prism"},
		{"C16", "Gyroelongated square dipyramid"},
		{"C20", "Icosahedron"},
	};

	protected Object[][] getContents() {
		return strings;
	}
}
