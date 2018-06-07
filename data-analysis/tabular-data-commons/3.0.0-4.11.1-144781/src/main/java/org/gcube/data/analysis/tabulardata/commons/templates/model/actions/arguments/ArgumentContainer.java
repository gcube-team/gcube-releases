package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of={"identifier"})
public class ArgumentContainer {
	
	private String identifier = UUID.randomUUID().toString();
	
	private List<ArgumentDescriptor<?>> arguments;
	
	private List<ArgumentContainer> containers;
		
	@Setter
	@Getter
	private String name;
	
	@Setter
	@Getter
	private String description;
	
	private int minInstances;
	
	private int maxInstances;
	
	@Setter(value= AccessLevel.PRIVATE)
	@Getter
	private ArgumentContainer parent;
	
	//private Map<String, ArgumentInstance<?>> createdInstances;
			
	public ArgumentContainer(@NonNull List<ArgumentDescriptor<?>> arguments,
			@NonNull List<ArgumentContainer> containers, int minInstances, int maxInstances) {
		super();
		this.arguments = arguments;
		this.containers = containers;
		for (ArgumentContainer container: containers)
			container.setParent(this);
		
		this.minInstances = minInstances;
		this.maxInstances = maxInstances;
	}

	public Set<ArgumentDescriptor<?>> getFlattenedArguments(){
		Set<ArgumentDescriptor<?>> toReturn = new HashSet<>();
		toReturn.addAll(arguments);
		for (ArgumentContainer container : containers)
			toReturn.addAll(container.getFlattenedArguments());
		return toReturn;
	}
			
	protected ContainerInstance createinstance(ContainerInstance parentInstance){
		ContainerInstance instance = new ContainerInstance(this, parentInstance);
		return instance;	
	}

}
