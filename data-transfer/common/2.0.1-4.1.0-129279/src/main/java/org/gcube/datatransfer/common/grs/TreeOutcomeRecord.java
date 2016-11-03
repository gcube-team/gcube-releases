package org.gcube.datatransfer.common.grs;

import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;

public class TreeOutcomeRecord {

	
	public static RecordDefinition[] treeOutcomeRecordDef=new RecordDefinition[]{ 
	      new GenericRecordDefinition((new FieldDefinition[] { 
	  	        new StringFieldDefinition("SourceIDField"),
		        new StringFieldDefinition("DestIDField"),
		        new	StringFieldDefinition("ReadTreesField"),
		        new	StringFieldDefinition("WrittenTreesField"),
		        new	StringFieldDefinition("OutcomeField"),
		        new	StringFieldDefinition("ExceptionField")
	      }))
	    };
}
