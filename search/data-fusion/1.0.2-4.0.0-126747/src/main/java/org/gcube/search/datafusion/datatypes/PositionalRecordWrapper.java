package org.gcube.search.datafusion.datatypes;

import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.io.Serializable;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class PositionalRecordWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long position;
	private GenericRecord rec;
	
	public PositionalRecordWrapper(long position, GenericRecord rec) {
		super();
		this.position = position;
		this.rec = rec;
	}

	public Long getPosition() {
		return position;
	}

	public GenericRecord getRec() {
		return rec;
	}

}
