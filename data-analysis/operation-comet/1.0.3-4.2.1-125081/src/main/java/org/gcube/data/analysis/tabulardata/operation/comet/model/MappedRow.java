package org.gcube.data.analysis.tabulardata.operation.comet.model;

import java.util.HashMap;
import java.util.Map;

public class MappedRow extends HashMap<String,String>{




	public String id;

	public String getId() {
		return id;
	}

	

	public MappedRow(Map<? extends String, ? extends String> m, String id) {
		super(m);
		this.id = id;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappedRow [id=");
		builder.append(id);
		builder.append(", values=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}


	
	

}
