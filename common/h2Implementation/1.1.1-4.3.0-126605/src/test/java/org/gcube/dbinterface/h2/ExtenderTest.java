package org.gcube.dbinterface.h2;
import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public class ExtenderTest {

	@FieldDefinition(specifications={Specification.PRIMARY_KEY})
	private int topolino;

	@FieldDefinition
	private String altro;
	
	/**
	 * @return the topolino
	 */
	public int getTopolino() {
		return topolino;
	}

	/**
	 * @param topolino the topolino to set
	 */
	public void setTopolino(int topolino) {
		this.topolino = topolino;
	}

	public String getAltro() {
		return altro;
	}

	public void setAltro(String altro) {
		this.altro = altro;
	}
	
	
	
}
