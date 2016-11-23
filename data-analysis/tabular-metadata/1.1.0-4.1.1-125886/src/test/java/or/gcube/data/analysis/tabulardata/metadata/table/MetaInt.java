package or.gcube.data.analysis.tabulardata.metadata.table;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MetaInt implements ContainerMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9086064948394822863L;

	private int value;

	public MetaInt() {
		// TODO Auto-generated constructor stub
	}

	public MetaInt(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetaInt [value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaInt other = (MetaInt) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	

}
