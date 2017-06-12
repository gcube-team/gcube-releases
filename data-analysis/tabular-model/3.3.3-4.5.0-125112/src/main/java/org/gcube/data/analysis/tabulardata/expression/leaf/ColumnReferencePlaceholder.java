package org.gcube.data.analysis.tabulardata.expression.leaf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ColumnReferencePlaceholder extends LeafExpression{

	private static final long serialVersionUID = 8745477872383674928L;
	private DataType datatype=null;
	private String id=null;
	
	private String label="";
	
	@SuppressWarnings("unused")
	private ColumnReferencePlaceholder(){}

	
	public ColumnReferencePlaceholder(DataType datatype, String id, String label) {
		super();
		this.datatype = datatype;
		this.id = id;
		this.label=label;
	}


	/**
	 * @return the datatype
	 */
	public DataType getDatatype() {
		return datatype;
	}


	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(DataType datatype) {
		this.datatype = datatype;
	}

	
	

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * Use of set
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	@Override
	public DataType getReturnedDataType(){
		return getDatatype();
	}


		
	@Override
	public void validate() throws MalformedExpressionException {
		if(datatype==null) throw new MalformedExpressionException("Datatype cannot be null, "+this);
		if(id==null) throw new MalformedExpressionException("Id cannot be null, "+this);
	}



	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnReferencePlaceholder other = (ColumnReferencePlaceholder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnReferencePlaceholder [datatype=");
		builder.append(datatype);
		builder.append(", id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}


	
	
	
	
}
