package org.gcube.dbinterface.h2;
import java.sql.Date;

import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public class ImportPersistenceItem {

	

	@FieldDefinition(specifications={Specification.NOT_NULL})
	private Date date;
	
	@FieldDefinition(specifications={Specification.NOT_NULL})
	private int length;
	
	@FieldDefinition(precision={80},  specifications={Specification.NOT_NULL, Specification.PRIMARY_KEY})
	private String scope;

	public enum PHASE{P, C} ; 
	
	@FieldDefinition(precision={1},  specifications={Specification.NOT_NULL})
	private PHASE phase;
	
	public ImportPersistenceItem(Date date, int length, String scope) {
		super();
		this.date = date;
		this.length = length;
		this.scope = scope;
		this.phase=PHASE.C;
	}

	
	public PHASE getPhase() {
		return phase;
	}

	public void setPhase(PHASE phase) {
		this.phase = phase;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	
}
