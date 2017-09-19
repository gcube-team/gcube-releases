package org.gcube.data.tm;

import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;

import javax.xml.namespace.QName;


/**
 * Constants and utilities for interaction with the CM service.
 * 
 * @author Fabio Simeoni
 */
public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "tree-manager-service";

	/** Service class. */
	public static final String SERVICE_CLASS = "DataAccess";

	/** Namespace. */
	public static final String NS = "http://gcube-system.org/namespaces/data/tm";
	
	/**Single-string field RS definition*/
	public static RecordDefinition[] UNTYPED_RECORD=new RecordDefinition[]{
		new GenericRecordDefinition((new FieldDefinition[] { 
        new StringFieldDefinition("payload")
      }))
    };
	
	/**Default size of the writer's buffer.*/
	public static final int DEFAULT_DOC_WRITEBUFFER = 25;
	
	/**Default size of the writer's buffer.*/
	public static final int DEFAULT_ADDOUTCOME_WRITEBUFFER = 25;
	
	/**Default size of the writer's buffer.*/
	public static final int DEFAULT_UPDATEOUTCOME_WRITEBUFFER = 25;
	
	/**Default size of the writer's buffer.*/
	public static final int DEFAULT_IDENTIFIER_READTIMEOUT_IN_SECONDS = 30;
	
	/**Default size of the document.node writer's buffer.*/
	public static final int DEFAULT_DOC_READTIMEOUT_IN_SECONDS = 30;
	
	
	/** Common namespace. */
	public static final String COMMON_NS="http://gcube-system.org/namespaces/common";
	/** JNDI Base Name. */
	public static final String JNDI_NAME = "gcube/data/tm";
	
	/** Activation Record Secondary Type */
	public static final String ACTIVATIONRECORD_TYPE = "ActivationRecord";
	/** Activation Record Type Name */
	public static final String ACTIVATIONRECORD_NAME = "TMRecord";
	
	/** Relative endpoint of the Binder service. */
	public static final String TBINDER_NAME = JNDI_NAME+"/binder";
	
	/** Singleton engine identifier. */
	public static final String SINGLETON_BINDER_ID = "binder";
	
	/** Relative endpoint of the Reader service. */
	public static final String TREADER_NAME = JNDI_NAME+"/reader";
	/** Relative endpoint of the Writer service. */
	public static final String TWRITER_NAME = JNDI_NAME+"/writer";
	
	/** Name of the plugin RP of the Binder resource. */
	public static final String BINDER_PLUGIN_RPNAME = "Plugin";
	/** Fully qualified name of the Plugin RP of the Binder resource. */
	public static final QName BINDER_PLUGIN_RP = new QName(NS, BINDER_PLUGIN_RPNAME);

	/** Name of the source id RP of the Binder. */
	public static final String SOURCENAME_RPNAME = "Name";
	/** Fully qualified name of the SourceID RP of accessor services. */
	public static final QName SOURCENAME_RP = new QName(NS, SOURCENAME_RPNAME);
	
	/** Name of the source id RP of the Binder. */
	public static final String SOURCEID_RPNAME = "SourceId";
	/** Fully qualified name of the SourceID RP of accessor services. */
	public static final QName SOURCEID_RP = new QName(NS, SOURCEID_RPNAME);
	
	/** Name of the Type RP of accessors. */
	public static final String SOURCETYPE_RPNAME = "Type";
	/** Fully qualified name of the Type RP of accessors. */
	public static final QName SOURCETYPE_RP = new QName(NS, SOURCETYPE_RPNAME);
	
	/** Name of the Property RP of accessor services. */
	public static final String PROPERTY_RPNAME = "Property";
	/** Fully qualified name of the Property RP of accessor services. */
	public static final QName PROPERTY_RP = new QName(NS, PROPERTY_RPNAME);

	
	/** Name of the Plugin RP of accessor services. */
	public static final String PLUGIN_RPNAME = "Plugin";
	/** Fully qualified name of the Plugin RP of collection managers. */
	public static final QName PLUGIN_RP = new QName(NS, PLUGIN_RPNAME);
	
	/** Name of the State RP of accessor services. */
	public static final String STATE_RPNAME = "State";
	/** Fully qualified name of the State RP of accessor services. */
	public static final QName STATE_RP = new QName(NS, STATE_RPNAME);
	
	/** Name of the Cardinality RP of accessor services. */
	public static final String CARDINALITY_RPNAME = "Cardinality";
	/** Fully qualified name of the Cardinality RP of accessor services. */
	public static final QName CARDINALITY_RP = new QName(NS, CARDINALITY_RPNAME);

	/** Name of the LastUpdate RP of accessor services. */
	public static final String LAST_UPDATE_RPNAME = "LastUpdate";
	/** Fully qualified name of the LastUpdate RP of accessor services. */
	public static final QName LAST_UPDATE_RP = new QName(NS, LAST_UPDATE_RPNAME);
	
	/**Maximum number of publication attempts for activation records.*/
	public static final int MAX_ACTIVATIONRECORD_PUBLICATION_ATTEMPTS = 10;

	/**Maximum number of publication attempts for source profiles.*/
	public static final int MAX_SOURCEPROFILE_PUBLICATION_ATTEMPTS = 10;
	
	public static final QName UPDATETOPIC_QNAME= new QName(NS, "UpdateTopic");


}
