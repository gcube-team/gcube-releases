package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Representable;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.DependencyDescriptor.DependencyType;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(of={"identifier"})
@Slf4j
public class ArgumentDescriptor<T extends Representable>{
	
	@Getter
	private String identifier = UUID.randomUUID().toString();
	@Getter
	private String name;
	@Getter
	private String description;
	@Getter
	private boolean multiArgument;
	@Getter @Setter
	private List<T> selection;
	@Getter
	private ArgumentType argumentType;
	
	@Getter
	private Set<DependencyDescriptor> dependsOnArguments = new HashSet<>();

	@Getter
	private boolean dependsOnItself = false;
	
	/*	@Getter
	private Set<String> headOnArguments = new HashSet<>();*/
	
	private Map<String, ChangeHandler<T>> handlerMap = new HashMap<>();
	
	@Getter
	private ArgumentInstance<T> instance;
	
	@Setter(value=AccessLevel.PROTECTED)
	private ContainerInstance containerInstance;
	
	public ArgumentDescriptor(String name, String description,
			boolean multiArgument, ArgumentType argumentType) {
		super();
		this.name = name;
		this.description = description;
		this.multiArgument = multiArgument;
		this.argumentType = argumentType;
	}
	
	public ArgumentInstance<T> createInstance(ContainerInstance belongsTo){
		ArgumentInstance<T> instance = new ArgumentInstance<T>(this);
		instance.setHandlerMap(handlerMap);
		instance.setContainerInstanceBelongsTo(belongsTo);
		return instance;
	}

	public void setDependsOn(ArgumentDescriptor<?> argument, ChangeHandler<T> handler, DependencyType type){
		log.trace("["+this.getName()+"] added as depend on "+argument.getIdentifier()+" named "+argument.getName());
		this.dependsOnArguments.add(new DependencyDescriptor(argument.getIdentifier(), type));
		if (argument.getIdentifier()==this.getIdentifier()){
			log.trace("depends on itself "+this.getName());
			dependsOnItself =true;
		}
		/*log.trace("["+argument.getName()+"] added as heads on "+this.identifier+" named "+this.getName());
		argument.headOnArguments.add(this.identifier);*/
		this.handlerMap.put(argument.getIdentifier(), handler);
	}
	
	
	
	@Override
	public String toString() {
		return "ArgumentDescriptor [identifier=" + identifier + ", name="
				+ name + "]";
	}
	
	

}
