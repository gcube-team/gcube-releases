package org.gcube.common.resources.kxml.service.version;

/**
 * Describes a restriction in versioning.
 * 
 */
public class Restriction {
	private final ArtifactVersion lowerBound;

	private final boolean lowerBoundInclusive;

	private final ArtifactVersion upperBound;

	private final boolean upperBoundInclusive;

	static final Restriction EVERYTHING = new Restriction(null, false, null,
			false);

	public Restriction(ArtifactVersion lowerBound, boolean lowerBoundInclusive,
			ArtifactVersion upperBound, boolean upperBoundInclusive) {
		this.lowerBound = lowerBound;
		this.lowerBoundInclusive = lowerBoundInclusive;
		this.upperBound = upperBound;
		this.upperBoundInclusive = upperBoundInclusive;
	}

	public ArtifactVersion getLowerBound() {
		return lowerBound;
	}

	public boolean isLowerBoundInclusive() {
		return lowerBoundInclusive;
	}

	public ArtifactVersion getUpperBound() {
		return upperBound;
	}

	public boolean isUpperBoundInclusive() {
		return upperBoundInclusive;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof Restriction)) {
			return false;
		}

		Restriction restriction = (Restriction) other;
		if (lowerBound != null) {
			if (!lowerBound.equals(restriction.lowerBound)) {
				return false;
			}
		} else if (restriction.lowerBound != null) {
			return false;
		}

		if (lowerBoundInclusive != restriction.lowerBoundInclusive) {
			return false;
		}

		if (upperBound != null) {
			if (!upperBound.equals(restriction.upperBound)) {
				return false;
			}
		} else if (restriction.upperBound != null) {
			return false;
		}

		if (upperBoundInclusive != restriction.upperBoundInclusive) {
			return false;
		}

		return true;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append(isLowerBoundInclusive() ? "[" : "(");
		if (getLowerBound() != null) {
			buf.append(getLowerBound().toString());
		}
		buf.append(",");
		if (getUpperBound() != null) {
			buf.append(getUpperBound().toString());
		}
		buf.append(isUpperBoundInclusive() ? "]" : ")");

		return buf.toString();
	}
}
