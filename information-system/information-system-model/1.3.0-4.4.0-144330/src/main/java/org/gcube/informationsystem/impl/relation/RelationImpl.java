/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.informationsystem.impl.ERImpl;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Relation.NAME)
public abstract class RelationImpl<Out extends Entity, In extends Entity>
		extends ERImpl implements Relation<Out, In> {

	protected Out source;
	protected In target;

	protected PropagationConstraint propagationConstraint;

	@JsonIgnore
	protected Map<String, Object> additionalProperties;

	/**
	 * Used to allow to have an additional property starting with '_' or '@'
	 */
	protected final Set<String> allowedAdditionalKeys;

	protected RelationImpl() {
		super();
		this.additionalProperties = new HashMap<>();
		this.allowedAdditionalKeys = new HashSet<>();
		this.allowedAdditionalKeys.add(SUPERCLASSES_PROPERTY);
	}

	protected RelationImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		this();
		this.source = source;
		this.target = target;
		this.propagationConstraint = propagationConstraint;
	}

	@Override
	public Out getSource() {
		return source;
	}

	@Override
	public In getTarget() {
		return target;
	}

	protected void setTarget(In target) {
		this.target = target;
	}

	@Override
	public PropagationConstraint getPropagationConstraint() {
		return this.propagationConstraint;
	}

	@Override
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	@Override
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public Object getAdditionalProperty(String key) {
		return additionalProperties.get(key);
	}

	@Override
	public void setAdditionalProperty(String key, Object value) {
		if (!allowedAdditionalKeys.contains(key)) {
			if (key.startsWith("_")) {
				return;
			}
			if (key.startsWith("@")) {
				return;
			}
			if (key.compareTo(PROPAGATION_CONSTRAINT) == 0) {
				return;
			}
			if (key.compareTo(TARGET_PROPERTY) == 0) {
				return;
			}
			if (key.compareTo(SOURCE_PROPERTY) == 0) {
				return;
			}
		}
		this.additionalProperties.put(key, value);
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		try {
			ISMapper.marshal(this, stringWriter);
			return stringWriter.toString();
		} catch (Exception e) {
			try {
				ISMapper.marshal(this.header, stringWriter);
				return stringWriter.toString();
			} catch (Exception e1) {
				return super.toString();
			}
		}
	}
}
