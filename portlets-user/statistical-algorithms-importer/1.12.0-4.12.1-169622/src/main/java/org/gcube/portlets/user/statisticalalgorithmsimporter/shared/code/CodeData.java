package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CodeData implements Serializable {

	private static final long serialVersionUID = 2507639790500338861L;

	private Integer id;
	private String codeLine;

	public CodeData(){
		super();
	}
	
	public CodeData(int id, String codeLine) {
		super();
		this.id = id;
		this.codeLine = codeLine;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCodeLine() {
		return codeLine;
	}

	public void setCodeLine(String codeLine) {
		this.codeLine = codeLine;
	}

	@Override
	public String toString() {
		return "CodeData [id=" + id + ", codeLine=" + codeLine + "]";
	}

}
