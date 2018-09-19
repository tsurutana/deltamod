package deltamod.comp;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import deltamod.DeltaMod;
import deltamod.geom.Model3D;
import deltamod.geom.ModularOrigamiConverter;

public class OpsPanelAssemble extends AbstractOpsPanel implements ChangeListener {

	JLabel lblDescription = new JLabel(DeltaMod.res.getString("AssemblyDescription"));
	
	JLabel lblNoise = new JLabel(DeltaMod.res.getString("Hand-assemblyNoise"));
	JSlider sldNoise = new JSlider(0, 10, 4);
	JLabel lblPyramidHeight = new JLabel(DeltaMod.res.getString("PyramidHeight"));
	JSlider sldPyramidHeight = new JSlider(0, 200, 40);
	JCheckBox chkColoring = new JCheckBox(DeltaMod.res.getString("AutomaticColoring"));
	
	public OpsPanelAssemble() {
		
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(chkColoring)
				.addComponent(lblNoise)
				.addComponent(lblPyramidHeight));
		//hGroup.addGap(50);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sldNoise)
				.addComponent(sldPyramidHeight));
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(chkColoring));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblNoise)
				.addComponent(sldNoise));
		vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(lblPyramidHeight)
				.addComponent(sldPyramidHeight));
		
		// register to layout manager
		layout.setVerticalGroup(vGroup);
		
		setLayout(new BorderLayout());
		add(lblDescription, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		
		sldPyramidHeight.addChangeListener(this);
		sldPyramidHeight.setPaintLabels(true);
		sldNoise.addChangeListener(this);
		sldNoise.setPaintLabels(true);
		chkColoring.setSelected(true);
	}
	
	public void panelShown() {
		DeltaMod.doc.save();
		assemble();
	}
	
	@Override
	public void perform() {
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		DeltaMod.doc.restore();
		assemble();
	}

	private void assemble() {
		if (chkColoring.isSelected())
			DeltaMod.doc.getModel().coloring();
		Model3D m = ModularOrigamiConverter.getAssembledIsosceles(
				DeltaMod.doc.getModel(),
				sldPyramidHeight.getValue() * 0.01,
				sldNoise.getValue() * 0.01
				);
		DeltaMod.doc.setModel(m);
		DeltaMod.mainFrame.mainScreen.setModel(m);
	}
}
