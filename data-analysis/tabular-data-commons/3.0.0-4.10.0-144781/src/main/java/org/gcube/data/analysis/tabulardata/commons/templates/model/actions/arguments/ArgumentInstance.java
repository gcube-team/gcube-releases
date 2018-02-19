package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Representable;

@Slf4j
@EqualsAndHashCode(of={"identifier"})
public class ArgumentInstance<T extends Representable> implements InstanceInterface, InstanceChangedEventListener {

	@Getter
	private String identifier = UUID.randomUUID().toString();
	@Getter
	private boolean valid = false;
	
	private boolean alive = true;
	
	private List<T> instance;
	
	@Getter
	@Setter(value=AccessLevel.PROTECTED)
	private ContainerInstance containerInstanceBelongsTo;
	
	@Setter
	@Getter
	private List<T> selector;
	
	@Setter
	private Map<String,ChangeHandler<T>> handlerMap;
	
	private List<InstanceChangedEventListener> instanceChangeListener = new ArrayList<>();
	
	private List<InvalidationEventListener> invalidationEventListeners = new ArrayList<>();
	
	@Getter
	private ArgumentDescriptor<T> parent;
		
	protected ArgumentInstance(ArgumentDescriptor<T> parent) {
		super();
		this.parent = parent;
		if (this.parent.getSelection()!=null)
			this.selector = new ArrayList<>(this.parent.getSelection());
	}
		
	public List<T> values() {
		return instance;
	}
	
	public void addInvalidatorListener(InvalidationEventListener listener){
		log.trace("registering invalidation listener ");
		this.invalidationEventListeners.add(listener);
	}
	
	protected synchronized void registerListener(InstanceChangedEventListener listener){
		log.trace("registering on change listener ");
		instanceChangeListener.add(listener);
	}
	
	public void set(@SuppressWarnings("unchecked") T ... instance) {
		this.instance = Arrays.asList(instance);
		log.trace("instanceModified - notifying parent");
		instanceChangedNotification();
	}
	
	public void set(List<T> instances) {
		this.instance = instances;
		log.trace("instanceModified - notifying parent");
		instanceChangedNotification();
	}
	
	public List<T> get(){
		return Collections.unmodifiableList(this.instance);
	}
	
	protected void invalidationNotification(){
		log.trace("notifying registered listener(instance changed)");
		for (InvalidationEventListener descriptor :invalidationEventListeners)
			descriptor.onInvalid(this.getIdentifier());
	}
	
	protected void selectorChangedNotification(){
		log.trace("notifying registered listener(instance changed)");
		for (InvalidationEventListener descriptor :invalidationEventListeners)
			descriptor.onSelectorChanged(this.getIdentifier());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected synchronized void instanceChangedNotification(){
		log.trace("notifying registered listener(instance changed)");
		List<InstanceChangedEventListener> toRemove = new ArrayList<>();
		for (InstanceChangedEventListener descriptor :instanceChangeListener)
			if (descriptor.isAlive())
				descriptor.onChange((List)this.get(), this.parent.getIdentifier(), this.getIdentifier());
			else toRemove.add(descriptor);
		this.instanceChangeListener.removeAll(toRemove);
	}
	
	private void reset(){
		this.instance = new ArrayList<>();
		instanceChangedNotification();
	}
	
	protected void setInvalid(){
		this.valid = false;
		reset();
		invalidationNotification();
	}

	@Override
	public String getDescription() {
		return parent.getDescription();
	}

	@Override
	public String getName() {
		return parent.getName();
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public String toString() {
		return "ArgumentInstance [identifier=" + identifier
				+ ", getDescription()=" + getDescription() + ", getName()="
				+ getName() + " isMultiSelection="+parent.isMultiArgument()+"]";
	}

	@Override
	public void onChange(List<Object> values, String senderArgumentId, String senderInstanceId) {
		log.trace("handler is null? "+(handlerMap.get(senderArgumentId)==null)+" in "+parent.getName());
		if (handlerMap.get(senderArgumentId)==null) return;
		List<T> newvalues = handlerMap.get(senderArgumentId).change(values, Collections.unmodifiableList(this.parent.getSelection()), senderInstanceId);	
		if (newvalues!=null){
			this.selector = newvalues;
			selectorChangedNotification();
			log.trace("notifiing selector change");
		}
		log.trace("invalidating instance");
		this.setInvalid();
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
}
