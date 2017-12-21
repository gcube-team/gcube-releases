package org.gcube.common.resources.kxml.service.version;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Construct a version range from a specification.
 * 
 */
public class VersionSpecificationParser {

	

	/**
	 * Create a version range from a string representation
	 * 
	 * Some spec examples are
	 * <ul>
	 * <li><code>1.0</code> Version 1.0</li>
	 * <li><code>[1.0,2.0)</code> Versions 1.0 (included) to 2.0 (not
	 * included)</li>
	 * <li><code>[1.0,2.0]</code> Versions 1.0 to 2.0 (both included)</li>
	 * <li><code>[1.5,)</code> Versions 1.5 and higher</li>
	 * <li><code>(,1.0],[1.2,)</code> Versions up to 1.0 (included) and 1.2
	 * or higher</li>
	 * </ul>
	 * 
	 * @param spec
	 *            string representation of a version or version range
	 * @return a new {@link Range} object that represents the specification
	 * @throws InvalidVersionException
	 */
	public static Range parse(String spec)	throws InvalidVersionException {
		if (spec == null) 
			throw new InvalidVersionException("Null range");
		

		List<Restriction> restrictions = new ArrayList<Restriction>();

		String process = spec;

		ArtifactVersion version = null;
		ArtifactVersion upperBound = null;
		ArtifactVersion lowerBound = null;

		while (process.startsWith("[") || process.startsWith("(")) {
			int index1 = process.indexOf(")");
			int index2 = process.indexOf("]");

			int index = index2;
			if (index2 < 0 || index1 < index2) {
				if (index1 >= 0) {
					index = index1;
				}
			}

			if (index < 0) throw new InvalidVersionException("Unbounded range: " + spec);
			

			Restriction restriction = parseRestriction(process.substring(0,
					index + 1));
			if (lowerBound == null) {
				lowerBound = restriction.getLowerBound();
			}
			if (upperBound != null) {
				if (restriction.getLowerBound() == null
						|| restriction.getLowerBound().compareTo(upperBound) < 0) {
					throw new InvalidVersionException("Ranges overlap: " + spec);
				}
			}
			restrictions.add(restriction);
			upperBound = restriction.getUpperBound();

			process = process.substring(index + 1).trim();

			if (process.length() > 0 && process.startsWith(",")) {
				process = process.substring(1).trim();
			}
		}

		if (process.length() > 0) {
			if (restrictions.size() > 0) {
				throw new InvalidVersionException(
						"Only fully-qualified sets allowed in multiple set scenario: "
								+ spec);
			} else {
				version = new DefaultArtifactImpl(process);
				restrictions.add(Restriction.EVERYTHING);
			}
		}

		return new Range(version, restrictions);
	}

	private static Restriction parseRestriction(String spec)
			throws InvalidVersionException {
		boolean lowerBoundInclusive = spec.startsWith("[");
		boolean upperBoundInclusive = spec.endsWith("]");

		String process = spec.substring(1, spec.length() - 1).trim();

		Restriction restriction;

		int index = process.indexOf(",");

		if (index < 0) {
			if (!lowerBoundInclusive || !upperBoundInclusive) {
				throw new InvalidVersionException(
						"Single version must be surrounded by []: " + spec);
			}

			ArtifactVersion version = new DefaultArtifactImpl(process);

			restriction = new Restriction(version, lowerBoundInclusive,
					version, upperBoundInclusive);
		} else {
			String lowerBound = process.substring(0, index).trim();
			String upperBound = process.substring(index + 1).trim();
			if (lowerBound.equals(upperBound)) {
				throw new InvalidVersionException(
						"Range cannot have identical boundaries: " + spec);
			}

			ArtifactVersion lowerVersion = null;
			if (lowerBound.length() > 0) {
				lowerVersion = new DefaultArtifactImpl(lowerBound);
			}
			ArtifactVersion upperVersion = null;
			if (upperBound.length() > 0) {
				upperVersion = new DefaultArtifactImpl(upperBound);
			}

			if (upperVersion != null && lowerVersion != null
					&& upperVersion.compareTo(lowerVersion) < 0) {
				throw new InvalidVersionException(
						"Range defies version ordering: " + spec);
			}

			restriction = new Restriction(lowerVersion, lowerBoundInclusive,
					upperVersion, upperBoundInclusive);
		}

		return restriction;
	}

		
	public static class Range {
	
		private final ArtifactVersion recommendedVersion;

		private final List<Restriction> restrictions;
		
		Range(ArtifactVersion recommendedVersion, List<Restriction> restrictions) {
			this.recommendedVersion = recommendedVersion;
			this.restrictions = restrictions;
		}

		public ArtifactVersion getRecommendedVersion() {
			return recommendedVersion;
		}

		public List<Restriction> getRestrictions() {
			return restrictions;
		}
		
		public String toString() {
			if (recommendedVersion != null) {
				return recommendedVersion.toString();
			} else {
				StringBuffer buf = new StringBuffer();
				for (Iterator<Restriction> i = restrictions.iterator(); i.hasNext();) {
					Restriction r = (Restriction) i.next();

					buf.append(r.toString());

					if (i.hasNext()) {
						buf.append(",");
					}
				}
				return buf.toString();
			}
		}


	}
	
	/** Invalid version or version range specified exception */
	public static class InvalidVersionException extends Exception {
		private static final long serialVersionUID = -1592821816921603892L;

		InvalidVersionException(String message) {super(message);}
	}
}
