/**
 * 
 */
package org.gcube.informationsystem.orientdb.hooks;

import java.util.UUID;

import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.orientdb.impl.embedded.Header;
import org.gcube.informationsystem.model.relation.Relation;

import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * @param <OrientGraphNoTx>
 */
public class HeaderHook<OrientGraphNoTx> extends ODocumentHookAbstract
		implements ODatabaseLifecycleListener {

	protected void init(){
		setIncludeClasses(Entity.NAME, Relation.NAME);
	}
	
	@SuppressWarnings("deprecation")
	public HeaderHook() {
		super();
		//System.out.println("HeaderHook()");
		init();
	}
	
	public HeaderHook(ODatabaseDocument database) {
		super(database);
		//System.out.println("HeaderHook(ODatabaseDocument database)");
		init();
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.BOTH;
	}

	@Override
	public RESULT onRecordBeforeCreate(final ODocument iDocument) {
		System.out.println("\n\n--------------\n" + iDocument);
		
		ODocument oDocument = iDocument.field(Entity.HEADER_PROPERTY, Header.class);
		if(oDocument==null){
			System.out.println("Header not present. Going to create it");
			Header header = new Header();
			
			UUID uuid = UUID.randomUUID();
			System.out.println("Setting generated UUID : " + uuid.toString());
			header.setUUID(uuid);
			
			System.out.println("Creator is unknown setting as : " + org.gcube.informationsystem.model.embedded.Header.UNKNOWN_USER);
			header.setCreator(org.gcube.informationsystem.model.embedded.Header.UNKNOWN_USER);
			
			long timestamp = System.currentTimeMillis();
			System.out.println("Setting Last Update and Creation Time to " + timestamp);
			header.setCreationTime(timestamp);
			header.setLastUpdateTime(timestamp);
		
			System.out.println("Setting newly create header " + header);
			iDocument.field(Entity.HEADER_PROPERTY, header);
			return RESULT.RECORD_CHANGED;
		}else{
			System.out.println("Header already present : " + oDocument);
			return RESULT.RECORD_NOT_CHANGED;
		}
	}
	
	@Override
	public RESULT onRecordBeforeUpdate(final ODocument iDocument) {
		System.out.println("\n\n--------------\nUpdating Last Update Time");
		
		ODocument oDocument = iDocument.field(Entity.HEADER_PROPERTY);
		long timestamp = System.currentTimeMillis();
		System.out.println("Updating Last Update Time in header of " + iDocument + " to " + timestamp);
		oDocument.field(Header.LAST_UPDATE_TIME_PROPERTY, timestamp);
		
		System.out.println("Updated Document is " + iDocument);
		
		String entityType = iDocument.getClassName();
		System.out.println("Document Type is " + entityType);
		
		OMetadata oMetadata = database.getMetadata();
		OSchema oSchema = oMetadata.getSchema();
		OClass oClass = oSchema.getClass(entityType);
		
		if(oClass.isSubClassOf(Facet.NAME)){
			System.out.println("Updating a Facet. Also Attached Resources Last Update Time MUST be updated to " + timestamp);
			// TODO 
			// Get all Resources attached to this Facet and set last update time
		}
		
		return RESULT.RECORD_CHANGED;
	}
	
	@Override
	public PRIORITY getPriority() { return PRIORITY.REGULAR; }

	@Override
	public void onCreate(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
		// REGISTER THE HOOK
		iDatabase.registerHook(this);
	}

	@Override
	public void onOpen(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
		// REGISTER THE HOOK
		iDatabase.registerHook(this);
	}

	@Override
	public void onClose(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
		// REGISTER THE HOOK
	    iDatabase.unregisterHook(this);
	}

	@Override
	public void onDrop(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase) {
	}

	@Override
	public void onCreateClass(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase, OClass iClass) {
	}

	@Override
	public void onDropClass(@SuppressWarnings("rawtypes") ODatabaseInternal iDatabase, OClass iClass) {
	}

	@Override
	public void onLocalNodeConfigurationRequest(ODocument iConfiguration) {
	}
	
}