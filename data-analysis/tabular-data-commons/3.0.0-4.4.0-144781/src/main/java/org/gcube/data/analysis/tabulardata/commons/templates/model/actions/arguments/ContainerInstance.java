package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Representable;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.DependencyDescriptor.DependencyType;

@Slf4j
public class ContainerInstance implements InstanceInterface{

	private String identifier = UUID.randomUUID().toString();

	@Getter(value=AccessLevel.PROTECTED)
	private Map<String, ArgumentInstance<?>> argumentInstances = new LinkedHashMap<>();
	@Getter
	private Map<String, List<ContainerInstance>> containerInstances = new LinkedHashMap<String, List<ContainerInstance>>();

	@Getter
	private ArgumentContainer parentArgument;

	@Setter(value=AccessLevel.PRIVATE)
	@Getter
	private ContainerInstance parentInstance = null;	


	protected ContainerInstance(@NonNull ArgumentContainer parentArgument, ContainerInstance parentInstance) {
		super();
		this.parentArgument = parentArgument;
		this.parentInstance = parentInstance;
		this.createInstanceInternally();
	}

	public static  InstanceInterface getChildInstanceById(ContainerInstance instance, String instanceToFindId){
		for (ArgumentInstance<?> argInst: instance.getAllArgumentInstances()){
			if (argInst.getIdentifier().equals(instanceToFindId))
				return argInst;
		}
		for (List<ContainerInstance> contInstList: instance.getContainerInstances().values())
			for (ContainerInstance contInst: contInstList){
				if (contInst.getIdentifier().equals(instanceToFindId))
					return contInst;
				InstanceInterface subInst = getChildInstanceById(contInst, instanceToFindId);
				if (subInst!=null)
					return subInst;
			}
		return null;
	}

	public static  List<ArgumentInstance<?>> getInstancesBydescriptorId(ContainerInstance instance, String descriptorId){
		List<ArgumentInstance<?>> argumentsInstances = new ArrayList<>();
		if (instance.getArgumentInstances().containsKey(descriptorId))
			argumentsInstances.add(instance.getArgumentInstances().get(descriptorId));
		for (Entry<String,List<ContainerInstance>> argCont: instance.getContainerInstances().entrySet())
			for (ContainerInstance contInst: argCont.getValue())
				argumentsInstances.addAll(getInstancesBydescriptorId(contInst, descriptorId));
		return argumentsInstances;
	}

	public static ContainerInstance createFirstInstance(ArgumentContainer argCont){
		if (argCont.getParent()!=null) throw new IllegalArgumentException(argCont.getIdentifier()+" is not root");
		ContainerInstance instance = new ContainerInstance(argCont, null);
		registerEventForContainer(instance, instance);
		return instance;
	}

	private static void registerEventForContainer(ContainerInstance root, ContainerInstance actual){
		for (ArgumentInstance<?> argInstance: actual.getAllArgumentInstances())
			registersEvents(root, argInstance);
		for (ContainerInstance contInstList: actual.getAllContainerInstances())
			registerEventForContainer(root, contInstList);
	}

	public boolean canCopy(){
		int alreadyCreated = 0;
		if (this.getParentInstance().getContainerInstances().containsKey(this.parentArgument.getIdentifier()))
			alreadyCreated = this.getParentInstance().getContainerInstances().get(this.parentArgument.getIdentifier()).size();
		return this.parentArgument.getMaxInstances()>alreadyCreated;
	}
	
	public boolean canRemove(){
		int alreadyCreated = 0;
		if (this.getParentInstance().getContainerInstances().containsKey(this.parentArgument.getIdentifier()))
			alreadyCreated = this.getParentInstance().getContainerInstances().get(this.parentArgument.getIdentifier()).size();
		return this.parentArgument.getMinInstances()<alreadyCreated;
	}
	
	public void remove(){
		if (!canRemove() )
			throw new IllegalStateException("this instance cannot be removed");
		this.getParentInstance().getContainerInstances().get(this.getParentArgument().getIdentifier()).remove(this);
		setNotAliveInstances(this);
	}

	private static void setNotAliveInstances(ContainerInstance instance){
		for (ArgumentInstance<?> argInst : instance.getAllArgumentInstances())
			argInst.setAlive(false);
		for (ContainerInstance contInst : instance.getAllContainerInstances())
			setNotAliveInstances(contInst);
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public ContainerInstance createCopy(){
		if (!canCopy() )
			throw new IllegalStateException("copy of this instance cannot be created");
		ContainerInstance instance = this.getParentInstance().createInstanceOf(parentArgument);

		ContainerInstance root = instance;
		while (root.getParentInstance()!=null)
			root= root.getParentInstance();

		for (ArgumentInstance originalArgInst : this.getAllArgumentInstances())
			if(originalArgInst.getParent().isDependsOnItself()){
				List<ArgumentInstance<?>> copyArgInstances = getInstancesBydescriptorId(instance, originalArgInst.getParent().getIdentifier());
				for (ArgumentInstance<?> argInst : copyArgInstances)
					argInst.setSelector(originalArgInst.getSelector());			
			}
		
		ContainerInstance.registerEventForContainer(root, instance);
		return instance;
	}

	private void createInstanceInternally(){
		for (ArgumentContainer container : parentArgument.getContainers()){
			this.createInstanceOf(container);
		}
		for (ArgumentDescriptor<?> descriptor : parentArgument.getArguments()) 
			this.createInstanceOf(descriptor);
	}

	public Collection<ArgumentInstance<?>> getAllArgumentInstances(){
		return Collections.unmodifiableCollection(this.argumentInstances.values());
	}

	public Collection<ContainerInstance> getAllContainerInstances(){
		Collection<ContainerInstance> toReturn = new ArrayList<>();
		for (List<ContainerInstance> contInstList: this.getContainerInstances().values())
			for (ContainerInstance inst: contInstList)
				toReturn.add(inst);
		return toReturn;
	}


	protected <T extends Representable> ArgumentInstance<T> createInstanceOf(ArgumentDescriptor<T> argumentDescriptor){
		if (!parentArgument.getArguments().contains(argumentDescriptor))
			throw new IllegalArgumentException("this container instance doesn't contain argumen descriptor "+argumentDescriptor.getName());
		if (this.argumentInstances.containsKey(argumentDescriptor.getIdentifier()))
			throw new IllegalArgumentException("instance for argument descriptor "+argumentDescriptor.getName()+" already created for this container");
		ArgumentInstance<T> instance =  argumentDescriptor.createInstance(this);
		this.argumentInstances.put(argumentDescriptor.getIdentifier(), instance);
		log.trace("creating instance for "+argumentDescriptor.getName());
		return instance;
	}

	private static void registersEvents(ContainerInstance rootInstance, ArgumentInstance<?> instance){

		log.trace("rootInstance is "+rootInstance.getIdentifier());

		if (!instance.getParent().getDependsOnArguments().isEmpty())
			for (DependencyDescriptor dependsOnDescriptor : instance.getParent().getDependsOnArguments()){
				List<ArgumentInstance<?>> dependsOnInstances = getInstancesBydescriptorId(rootInstance, dependsOnDescriptor.getDependencyId());
				for (ArgumentInstance<?> dependsOnInstance: dependsOnInstances)
					if (!dependsOnInstance.getIdentifier().equals(instance.getIdentifier()) && 
					(!dependsOnDescriptor.getDepType().equals(DependencyType.LOCAL) 
					|| dependsOnInstance.getContainerInstanceBelongsTo().getIdentifier().equals(instance.getContainerInstanceBelongsTo().getIdentifier())))
						dependsOnInstance.registerListener(instance);
			}
	}

	protected ContainerInstance createInstanceOf(ArgumentContainer argumentContainer){
		if (!parentArgument.getContainers().contains(argumentContainer))
			throw new IllegalArgumentException("this container instance doesn't contain container descriptor "+argumentContainer.getIdentifier());
		List<ContainerInstance> instancesAlreadyCreated = containerInstances.get(argumentContainer.getIdentifier());
		if (instancesAlreadyCreated !=null && instancesAlreadyCreated.size()==argumentContainer.getMaxInstances())
			throw new IllegalArgumentException("max instances created reached for "+argumentContainer.getIdentifier());
		ContainerInstance instance = argumentContainer.createinstance(this);
		log.trace("parent instance is "+instance.getParentInstance());
		if (this.containerInstances.containsKey(argumentContainer.getIdentifier()))
			this.containerInstances.get(argumentContainer.getIdentifier()).add(instance);
		else{
			List<ContainerInstance> list = new ArrayList<>();
			list.add(instance);
			this.containerInstances.put(argumentContainer.getIdentifier(), list);
		}
		return instance;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean isValid(){
		//TODO: implement validation
		return true;
	}

	@Override
	public String getDescription() {
		return parentArgument.getDescription();
	}

	@Override
	public String getName() {
		return parentArgument.getName();
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
