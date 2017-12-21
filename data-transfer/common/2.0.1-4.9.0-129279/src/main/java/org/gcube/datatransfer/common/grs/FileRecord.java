package org.gcube.datatransfer.common.grs;

import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;


/**
 * 
 * @author Andres Manzi(CERN)
 *
 */
public class FileRecord {
	
	public static RecordDefinition[] fileRecordDef=new RecordDefinition[]{ 
		      new GenericRecordDefinition((new FieldDefinition[] { 
		        new FileFieldDefinition("FileField"),
		        new	StringFieldDefinition("FileNameField") 
		      }))
		    };

}
