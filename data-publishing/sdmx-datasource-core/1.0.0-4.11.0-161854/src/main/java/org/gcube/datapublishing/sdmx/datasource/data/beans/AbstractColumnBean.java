package org.gcube.datapublishing.sdmx.datasource.data.beans;

public abstract class AbstractColumnBean implements ColumnBean 
{

	
	private String 	concept,
					id;


	public AbstractColumnBean (String id, String concept)
	{
		if (id == null || concept == null) throw new NullPointerException ("Illegal parameters "+id+ " "+concept);
		
		this.id = id;
		this.concept = concept;
	}

	/* (non-Javadoc)
	* @see org.gcube.data.analysis.sdmx.datasource.data.beans.ColumnBean#getConcept()
	*/
	@Override
	public String getConcept() 
	{
				return concept;
	}


	/* (non-Javadoc)
	* @see org.gcube.data.analysis.sdmx.datasource.data.beans.ColumnBean#getId()
	*/
	@Override
	public String getId() {
			return id;
	}

	@Override
	public boolean equals(Object obj) {
	
		if (obj != null && obj.getClass() == this.getClass())
		{
		return this.id.equals(((ColumnBean) obj).getId());
		}
		else return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	


}
