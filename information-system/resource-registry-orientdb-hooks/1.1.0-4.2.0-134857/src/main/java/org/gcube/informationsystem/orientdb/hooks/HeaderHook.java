/**
 * 
 */
package org.gcube.informationsystem.orientdb.hooks;

import java.util.Calendar;
import java.util.UUID;

import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @param <OrientGraphNoTx>
 */
public class HeaderHook<OrientGraphNoTx> extends ODocumentHookAbstract
		implements ODatabaseLifecycleListener {

	protected void init() {
		setIncludeClasses(Entity.NAME, Relation.NAME);
	}

	@SuppressWarnings("deprecation")
	public HeaderHook() {
		super();
		// System.out.println("HeaderHook()");
		init();
	}

	public HeaderHook(ODatabaseDocument database) {
		super(database);
		// System.out.println("HeaderHook(ODatabaseDocument database)");
		init();
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.BOTH;
	}

	@Override
	public RESULT onRecordBeforeCreate(final ODocument iDocument) {
		OLogManager.instance().debug(this, "Checking %s on %s",
				Header.NAME, iDocument.toJSON());

		ODocument oDocument = iDocument.field(Entity.HEADER_PROPERTY);
		if (oDocument == null) {
			OLogManager.instance().debug(this,
					"%s not present. Going to create it on %s",
					Header.NAME, iDocument.toJSON());
			
			oDocument = new ODocument(Header.NAME);
			UUID uuid = UUID.randomUUID();
			oDocument.field(Header.UUID_PROPERTY, uuid.toString());

			OLogManager.instance().debug(this, 
					"Creator is unknown setting as %s", Header.UNKNOWN_USER);
			oDocument.field(Header.CREATOR_PROPERTY, Header.UNKNOWN_USER);

			long timestamp = Calendar.getInstance().getTimeInMillis();
			oDocument.field(Header.CREATION_TIME_PROPERTY, timestamp);
			oDocument.field(Header.LAST_UPDATE_TIME_PROPERTY, timestamp);

			iDocument.field(Entity.HEADER_PROPERTY, oDocument);
			OLogManager.instance().debug(this, 
					"%s has now an %s", iDocument.toJSON(), Header.NAME);
			return RESULT.RECORD_CHANGED;
		} else {
			OLogManager.instance().debug(this, "%s already present on %s",
					Header.NAME, iDocument.toJSON());
			return RESULT.RECORD_NOT_CHANGED;
		}
	}

	@Override
	public RESULT onRecordBeforeUpdate(final ODocument iDocument) {
		OLogManager.instance().debug(this, "Updating Last Update Time on %s of %s",
				Header.NAME, iDocument.toJSON());
		
		ODocument oDocument = iDocument.field(Entity.HEADER_PROPERTY);
		Calendar calendar = Calendar.getInstance();
		long timestamp = calendar.getTimeInMillis();
		oDocument.field(Header.LAST_UPDATE_TIME_PROPERTY, timestamp);

		OLogManager.instance().debug(this, "Updated Document is %s", iDocument);

		return RESULT.RECORD_CHANGED;
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