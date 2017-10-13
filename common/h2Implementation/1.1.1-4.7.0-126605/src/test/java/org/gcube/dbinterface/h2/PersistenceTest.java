package org.gcube.dbinterface.h2;

import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.annotations.ExternalReference;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public class PersistenceTest {

	@FieldDefinition(specifications={Specification.PRIMARY_KEY})
	private String pippo;
	@FieldDefinition
	private int pluto;
	@FieldDefinition
	private String minny;
	
	@ExternalReference(fieldName="topolino",relationClass=ExtenderTest.class)
	private ExtenderTest test;
	
	public String getPippo() {
		return pippo;
	}
	public void setPippo(String pippo) {
		this.pippo = pippo;
	}
	public int getPluto() {
		return pluto;
	}
	public void setPluto(int pluto) {
		this.pluto = pluto;
	}
	public String getMinny() {
		return minny;
	}
	public void setMinny(String minny) {
		this.minny = minny;
	}
	public ExtenderTest getTest() {
		return test;
	}
	public void setTest(ExtenderTest test) {
		this.test = test;
	}
	
	
	
}
