/**
 * 
 */
package org.gcube.informationsystem.model.entity;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.impl.entity.ContextImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.relation.IsParentOf;
import org.gcube.informationsystem.model.relation.Relation;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) This Entity is for internal use only
 *         https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Context
 */
@JsonDeserialize(as = ContextImpl.class)
public interface Context extends Entity {

	public static final String NAME = "Context"; // Context.class.getSimpleName();

	public static final String NAME_PROPERTY = "name";
	public static final String PARENT_PROPERTY = "parent";
	public static final String CHILDREN_PROPERTY = "children";

	@ISProperty(name = NAME_PROPERTY, mandatory = true, nullable = false)
	public String getName();

	public void setName(String name);

	@JsonGetter
	@JsonIgnoreProperties({ Relation.TARGET_PROPERTY })
	public IsParentOf<Context, Context> getParent();

	@JsonIgnore
	public void setParent(UUID uuid);

	@JsonIgnore
	public void setParent(Context context);

	@JsonIgnore
	public void setParent(IsParentOf<Context, Context> isParentOf);

	@JsonGetter
	@JsonIgnoreProperties({ Relation.SOURCE_PROPERTY })
	public List<IsParentOf<Context, Context>> getChildren();

	public void addChild(UUID uuid);

	public void addChild(Context child);

	public void addChild(IsParentOf<Context, Context> isParentOf);

}
