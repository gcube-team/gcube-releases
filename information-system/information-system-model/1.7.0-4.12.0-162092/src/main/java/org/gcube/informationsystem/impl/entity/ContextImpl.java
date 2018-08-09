/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.impl.relation.IsParentOfImpl;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.exceptions.InvalidEntity;
import org.gcube.informationsystem.model.relation.IsParentOf;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Context.NAME)
public class ContextImpl extends EntityImpl implements Context {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5070590328223454087L;
	
	protected String name;
	
	protected IsParentOf<Context, Context> parent;
	protected List<IsParentOf<Context, Context>> children;
	
	protected ContextImpl() {
		super();
		this.parent = null;
		this.children = new ArrayList<>();
	}
	
	public ContextImpl(String name) {
		this(name, null);
	}
	
	public ContextImpl(String name, UUID uuid) {
		this();
		this.name = name;
		if(uuid == null){
			uuid = UUID.randomUUID();
		}
		this.header = new HeaderImpl(uuid);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public IsParentOf<Context, Context> getParent() {
		return parent;
	}
	
	
	@Override
	public void setParent(UUID uuid) {
		Context parent = new ContextImpl();
		parent.setHeader(new HeaderImpl(uuid));
		setParent(parent);
	}
	
	@Override
	public void setParent(Context context) {
		IsParentOf<Context, Context> isParentOf = new IsParentOfImpl<Context, Context>(context, this, null);
		this.parent = isParentOf;
	}
	
	@Override
	public void setParent(IsParentOf<Context, Context> isParentOf) {
		this.parent = isParentOf;
	}
	
	@JsonSetter(value=PARENT_PROPERTY)
	protected void setParentFromJson(IsParentOf<Context, Context> isParentOf) throws JsonProcessingException, InvalidEntity {
		if(isParentOf!=null) {
			Context parent = isParentOf.getSource();
			isParentOf.setTarget(this);
			((ContextImpl) parent).addChild(isParentOf);
			this.parent = isParentOf;
		}
	}
	
	@Override
	public List<IsParentOf<Context, Context>> getChildren() {
		return children;
	}

	@JsonSetter(value=CHILDREN_PROPERTY)
	protected void setChildrenFromJson(List<IsParentOf<Context, Context>> children) throws InvalidEntity, JsonProcessingException {
		for(IsParentOf<Context, Context> isParentOf : children){
			addChildFromJson(isParentOf);
		}
	}

	protected void addChildFromJson(IsParentOf<Context, Context> isParentOf) throws InvalidEntity, JsonProcessingException {
		isParentOf.setSource(this);
		addChild(isParentOf);
	}
	
	@Override
	public void addChild(UUID uuid) {
		Context child = new ContextImpl();
		child.setHeader(new HeaderImpl(uuid));
		addChild(child);
	}
	
	@Override
	public void addChild(Context child) {
		IsParentOf<Context, Context> isParentOf = new IsParentOfImpl<Context, Context>(this, child, null);
		this.addChild(isParentOf);
	}
	
	@Override
	public void addChild(IsParentOf<Context, Context> isParentOf) {
		((ContextImpl) isParentOf.getTarget()).setParent(this);
		children.add(isParentOf);
	}
	
}
