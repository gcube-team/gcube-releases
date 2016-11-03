/**
 * 
 */
package org.gcube.vremanagement.executor.client.util;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class Tuple<Name,Value> {
	
	protected Name name;
	protected Value value;
	
	public Tuple(){}
	
	public Tuple(Name name, Value value){
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the a
	 */
	public Name getName() {
		return name;
	}

	/**
	 * @param a the a to set
	 */
	public void setName(Name name) {
		this.name = name;
	}

	/**
	 * @return the b
	 */
	public Value getValue() {
		return value;
	}

	/**
	 * @param b the b to set
	 */
	public void setValue(Value value) {
		this.value = value;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Tuple other = (Tuple) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return String.format("<%s,%s>", name.toString(), value.toString());
	}
	
}
