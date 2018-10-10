package org.gcube.data.analysis.tabulardata.operation.parameters;

public class Cardinality {

	public static Cardinality ONE = new Cardinality(1, 1);
	public static Cardinality OPTIONAL = new Cardinality(0, 1);

	private int minimum;
	private int maximum;
	
	@SuppressWarnings("unused")
	private Cardinality() {}

	public Cardinality(int minimum, int maximum) {
		setMinimum(minimum);
		setMaximum(maximum);
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		if (minimum < 0)
			throw new IllegalArgumentException("Minimum cardinality must be equal or greater than 0.");
		this.minimum = minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		if (maximum < 1)
			throw new IllegalArgumentException("Maximum cardinality must be equal or greater than 1.");
		this.maximum = maximum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maximum;
		result = prime * result + minimum;
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
		Cardinality other = (Cardinality) obj;
		if (maximum != other.maximum)
			return false;
		if (minimum != other.minimum)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cardinality [minimum=");
		builder.append(minimum);
		builder.append(", maximum=");
		builder.append(maximum);
		builder.append("]");
		return builder.toString();
	}

}
