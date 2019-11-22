/**
 * 
 */
package org.gcube.informationsystem.orientdb.hooks;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.reference.relation.Relation;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class RelationHook extends ODocumentHookAbstract implements
		ODatabaseLifecycleListener {

	protected final String relationType;
	protected final PropagationConstraint propagationConstraint;

	protected void init(String relationType) {
		setIncludeClasses(relationType);
	}

	@SuppressWarnings("deprecation")
	public RelationHook(String relationType,
			PropagationConstraint propagationConstraint) {
		super();
		this.relationType = relationType;
		this.propagationConstraint = propagationConstraint;
		init(relationType);
	}

	public RelationHook(ODatabaseDocument database, String relationType,
			PropagationConstraint propagationConstraint) {
		super(database);
		this.relationType = relationType;
		this.propagationConstraint = propagationConstraint;
		init(relationType);
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.BOTH;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T extends Enum> RESULT fixConstraint(RESULT result,
			ODocument oDocument, String property, Class<T> clz, T enumValue) {

		Object propertyObject = oDocument.field(property);

		if (propertyObject == null) {
			OLogManager.instance().debug(this,
					"%s not present. Going to create it on %s", property,
					oDocument.toJSON());
			oDocument.field(property, enumValue.toString());
			result = RESULT.RECORD_CHANGED;
		} else {
			try {
				Enum.valueOf(clz, propertyObject.toString());
			} catch (Exception e) {
				OLogManager
						.instance()
						.warn(this,
								"%s is not a valid value for % in %. Going to set default value %s.",
								propertyObject.toString(), property,
								relationType, enumValue.toString());
				oDocument.field(property, enumValue.toString());
				result = RESULT.RECORD_CHANGED;
			}

		}

		return result;
	}

	protected RESULT checkPropagationConstraint(final ODocument iDocument) {
		OLogManager.instance().debug(this, "Checking %s on %s",
				PropagationConstraint.NAME, iDocument.toJSON());

		RESULT result = RESULT.RECORD_NOT_CHANGED;

		ODocument oDocument = iDocument.field(Relation.PROPAGATION_CONSTRAINT);
		if (oDocument == null) {
			OLogManager.instance().debug(this,
					"%s not present. Going to create it on %s",
					PropagationConstraint.NAME, iDocument.toJSON());

			oDocument = new ODocument(PropagationConstraint.NAME);

			oDocument.field(PropagationConstraint.REMOVE_PROPERTY,
					propagationConstraint.getRemoveConstraint().toString());
			oDocument.field(PropagationConstraint.ADD_PROPERTY,
					propagationConstraint.getAddConstraint().toString());

			iDocument.field(Relation.PROPAGATION_CONSTRAINT, oDocument);

			OLogManager.instance().debug(this, "%s has now a %s",
					iDocument.toJSON(), PropagationConstraint.NAME);
			result = RESULT.RECORD_CHANGED;

		} else {
			OLogManager.instance().debug(this,
					"%s already present on %s. Going to validate it.",
					Relation.PROPAGATION_CONSTRAINT, iDocument.toJSON());

			result = fixConstraint(result, oDocument,
					PropagationConstraint.REMOVE_PROPERTY,
					RemoveConstraint.class,
					propagationConstraint.getRemoveConstraint());

			result = fixConstraint(result, oDocument,
					PropagationConstraint.ADD_PROPERTY, AddConstraint.class,
					propagationConstraint.getAddConstraint());

		}

		return result;
	}

	@Override
	public RESULT onRecordBeforeCreate(final ODocument iDocument) {
		return checkPropagationConstraint(iDocument);
	}

	@Override
	public RESULT onRecordBeforeUpdate(final ODocument iDocument) {
		return checkPropagationConstraint(iDocument);
	}

	@Override
	public PRIORITY getPriority() {
		return PRIORITY.REGULAR;
	}

	@Override
	public void onCreate(
			@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
		// REGISTER THE HOOK
		iDatabase.registerHook(this);
	}

	@Override
	public void onOpen(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
		// REGISTER THE HOOK
		iDatabase.registerHook(this);
	}

	@Override
	public void onClose(
			@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
		// REGISTER THE HOOK
		iDatabase.unregisterHook(this);
	}

	@Override
	public void onDrop(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
		iDatabase.unregisterHook(this);
	}

	@Override
	public void onCreateClass(
			@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase,
			OClass iClass) {
	}

	@Override
	public void onDropClass(
			@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase,
			OClass iClass) {
	}

	@Override
	public void onLocalNodeConfigurationRequest(ODocument iConfiguration) {
	}

}