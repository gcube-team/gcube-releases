package org.gcube.data.access.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gcube.data.access.ui.widgets.MultiSelectionWidget;
import org.gcube.data.access.ui.widgets.SingleSelectorWidget;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentContainer;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentDescriptor;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentInstance;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ContainerInstance;

public class Util {

	private static Component createSingleContainer(ContainerInstance contInstance, JFrame f){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(contInstance.getName()));
		for (ArgumentInstance<?> argInst : contInstance.getAllArgumentInstances())
			panel.add(createArgumentComponent(argInst));
		for (Entry<String,List<ContainerInstance>> entry: contInstance.getContainerInstances().entrySet()){
			if(entry.getValue().size()>0)
				for (Component comp: createContainer(entry.getValue(), f))
					panel.add(comp);
		}
		return panel;
	}
	
	public static List<Component> createContainer(final List<ContainerInstance> contInstances, final JFrame f){
		List<Component> components = new ArrayList<>();
		for (ContainerInstance contInstance:contInstances){
			components.add(createSingleContainer(contInstance, f));
		}
		final ArgumentContainer argumentContainer = contInstances.get(0).getParentArgument();
		if (argumentContainer.getMaxInstances()>argumentContainer.getMinInstances()){
						
			final JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(BorderFactory.createTitledBorder("instance"));
			for (Component comp: components)
				panel.add(comp);
			JButton button = new JButton("ADD");
			
			final JScrollPane scrollPane = new JScrollPane(panel);
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        scrollPane.setBounds(50, 30, 300, 50);
			
			button.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {
					ContainerInstance inst = contInstances.get(0).createCopy();
					panel.add(createSingleContainer(inst, f),panel.getComponentCount()-1);
					System.out.println("MOUSE PRESSED");
					scrollPane.invalidate();
					scrollPane.validate();
					scrollPane.repaint();
				}
				
				@Override
				public void mouseExited(MouseEvent e) {}
				
				@Override
				public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
			panel.add(button);
			
			return Collections.singletonList((Component)scrollPane);
		}else return components;
	}

	public static Component createArgumentComponent(ArgumentInstance<?> argInstance){

		if (argInstance.getParent().getSelection()!=null && argInstance.getParent().getSelection().size()>0){
			if (!argInstance.getParent().isMultiArgument()){
				JPanel panel = new JPanel(new FlowLayout());
				JLabel label = new JLabel(argInstance.getName());
				panel.add(label);
				panel.add(new SingleSelectorWidget<>(argInstance));
				System.out.println("added "+argInstance.getName());
				return panel;
			}else{
				JPanel panel = new JPanel(new FlowLayout());
				JLabel label = new JLabel(argInstance.getName());
				panel.add(label);
				panel.add(new MultiSelectionWidget<>(argInstance));
				System.out.println("added "+argInstance.getName());
				return panel;
			}
		} else {
			System.out.println("added "+argInstance.getName());
			JPanel panel = new JPanel(new FlowLayout());
			JLabel label = new JLabel(argInstance.getName());
			panel.add(label);
			panel.add(new JTextField(30));
			return panel;
		}
	}

	public static StringBuilder printContainerInstance(ContainerInstance instance, int depth){
		StringBuilder sb = new StringBuilder();

		String depthString ="";
		for (int i=0; i<depth;i++)
			depthString+="\t";

		sb.append(depthString).append("-").append(instance.getName()).append(" - ").append(instance.getIdentifier()).append(" - parent ID: ").append(instance.getParentArgument().getIdentifier()).append("\n");
		if (instance.getParentInstance()!=null)
			sb.append(depthString).append(" - parent INSTANCE ID: ").append(instance.getParentInstance().getIdentifier()).append("\n");
		for (ArgumentInstance<?> argInst: instance.getAllArgumentInstances())
			sb.append(depthString).append("\t- "+argInst.toString()).append("    parent Id -").append(argInst.getParent().getIdentifier()).append("\n");
		for (Entry<String,List<ContainerInstance>> argCont: instance.getContainerInstances().entrySet()){
			sb.append("parent argument id: "+argCont.getKey()+" // \n");
			for (ContainerInstance inst: argCont.getValue())
				sb.append(printContainerInstance(inst, depth+1));
		}

		return sb;
	}

	public static StringBuilder printArgumentContainer(ArgumentContainer container, int depth){
		StringBuilder sb = new StringBuilder();

		String depthString ="";
		for (int i=0; i<depth;i++)
			depthString+="\t";

		sb.append(depthString).append("-").append(container.getName()).append(" - ").append(container.getIdentifier()).append("\n");
		for (ArgumentDescriptor<?> argInst: container.getArguments())
			sb.append(depthString).append("\t- "+argInst.toString()).append("\n");
		for (ArgumentContainer argCont: container.getContainers()){
			sb.append(printArgumentContainer(argCont, depth+1));
		}

		return sb;
	}
}
