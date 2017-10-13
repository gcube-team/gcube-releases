package org.gcube.data.access.ui;

import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentInstance;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ContainerInstance;

public class DinamicPanelContainer extends JPanel {

	ContainerInstance container;
	
	public DinamicPanelContainer(ContainerInstance container) {
		super();
		this.container = container;
	}
	
	public JPanel getPanel(){
		JPanel boxingPanel = new JPanel();
		boxingPanel.setLayout(new BoxLayout(boxingPanel, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(container.getName()));
		for (ArgumentInstance<?> argInst : container.getAllArgumentInstances())
			panel.add(Util.createArgumentComponent(argInst));
		for (List<ContainerInstance> subInst: container.getContainerInstances().values())
			panel.add(new DinamicPanelContainer(subInst.get(0)).getPanel());
		
		boxingPanel.add(new JButton("add"));
		return boxingPanel;
	}
	
}
