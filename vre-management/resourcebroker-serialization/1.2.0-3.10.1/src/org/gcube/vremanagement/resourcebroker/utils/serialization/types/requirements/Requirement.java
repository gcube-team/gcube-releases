/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: Requirements.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <p>
 * When requiring a new deployment plan to the ResourceBroker service, the
 * {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService#getPlan(String)}
 * method is activated.
 * </p>
 *
 * <p>
 * This method requires the String consisting of the XML representation of a deployment
 * request coming from the VRE Manager. Aside, it is offered the possibility to express
 * requirements on the GHN to choose for a deployment plan. The requirements are so expressed
 * by instantiating {@link Requirement} class.
 * </p>
 *
 * <p><b>Usage samples:</b></p>
 * <pre>
 * // To require a GHN running on Linux OS.
 * new Requirement(RequirementElemPath.OS, RequirementElemType.STRING , "Linux", RequirementRelationType.EQUAL),
 * // Requires that the GHN is deployed on a machine inside the isti.cnr.it domain
 * new Requirement(RequirementElemPath.HOST, RequirementElemType.STRING , "isti.cnr.it", RequirementRelationType.CONTAINS),
 * // Requires a machine having a CPU with at least 5000 bogomips.
 * new Requirement(RequirementElemPath.PROCESSOR_BOGOMIPS, RequirementElemType.NUMBER , "5000", RequirementRelationType.GREATER_OR_EQUAL)
 * </pre>
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
@XStreamAlias("Requirement")
public class Requirement {
	public static final String NODE_TAG = "Requirement";
	@XStreamAlias("node")
	private RequirementElemPath elem = null;
	@XStreamAlias("key")
	private String key = null;
	private RequirementRelationType relType = null;
	@XStreamAlias("value")
	private String value = null;

	/**
	 * Creates a new requirement.
	 */
	public Requirement(
			RequirementElemPath elem,
			RequirementRelationType relType,
			String value) {
		super();

		this.elem = elem;
		this.value = value;
		this.relType = relType;
	}

	public Requirement(
			RequirementElemPath elem,
			String value) {
		super();
		this.elem = elem;
		this.value = value;
	}

	/**
	 * This constructor is instead used for only environmental variable to check
	 * <ul>
	 * <li> That a key is present in the environmental variables of GHN descriptor. In a such case the associated value must be null. Usage:
	 * <pre>
	 *  new Requirement(RequirementElemPath.RUNTIME_ENV, RequirementElemType.STRING , "GLOBUS_OPTIONS", <b>null</b>, RequirementRelationType.EQUAL)
	 * </pre>
	 * </li>
	 * <li> That both (key, value) couple is exactly equals to the parameters. Usage:
	 * <pre>
	 *  new Requirement(RequirementElemPath.RUNTIME_ENV, RequirementElemType.STRING , "ANT_HOME", "/usr/share/ant", RequirementRelationType.EQUAL),
	 * </pre>
	 * </li>
	 * <li> That both (key, value) couple where the key is exactly equals to the key parameter and the value contains the element passed as value parameter. Usage:
	 * <pre>
	 *  new Requirement(RequirementElemPath.RUNTIME_ENV, RequirementElemType.STRING , "ANT_HOME", "/ant", RequirementRelationType.CONTAINS),
	 * </pre>
	 * </li>
	 * </ul>
	 *
	 * @param value if the value is NULL it is assumed a containsKey operator. No comparison on the value will be done.
	 */
	public Requirement(
			RequirementElemPath elem,
			String key,
			RequirementRelationType relType,
			String value) {
		this(elem, relType, value);
		this.key = key;
	}

	public RequirementElemPath getElem() {
		return elem;
	}

	public String getValue() {
		return value;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public RequirementRelationType getRelType() {
		return relType;
	}

	public String getEvalString() {
		String query = this.elem.getPath();

		// 1 - RUNTIME ENVIRONMENT
		// just for environment requiments also the key value is required.
		if (this.getElem() == RequirementElemPath.RUNTIME_ENV_STRING ||
				this.getElem() == RequirementElemPath.RUNTIME_ENV_NUMBER) {
			if (this.key == null) {
				return null;
			}
			// the key is always a string
			query = query.replace("$1", "Key = '" + this.key + "'");

			if (this.value != null) {
				if (this.getRelType() != RequirementRelationType.CONTAINS) {
					query = query.replace("$2", "and Value " + this.getRelType().getRelType() + " " + this.getFormattedValue());
				} else {
					query = query.replace("$2", "and Value [contains (., " + this.getFormattedValue() + ")]");
				}
			}
			// If no value is specified the operator is implicitly a CONTAINSKEY.
			// no value specified only a check on key exists.
			else {
				query = query.replace("$2", "");
			}
			return query;
		}

		// 2- CUSTOM REQUIREMENTS
		if (this.getElem() == RequirementElemPath.CUSTOM_REQUIREMENT) {
			if (this.getValue() == null) {
				return null;
			}

			query = query.replace("$1", this.getValue());
			return query;
		}

		// OTHERS
		if (this.getRelType() != RequirementRelationType.CONTAINS) {
			query = query.replace("$1", this.getRelType().getRelType());
			query = query.replace("$2", this.getFormattedValue());
		} else { // the CONTAINS EXPRESSION
			if (!query.contains("[")) {
				query = query.replace("$1", "[contains(., " + this.getFormattedValue() + ")]");
			} else {
				query = query.replace("$1", "contains(.," + this.getFormattedValue() + ")");
			}
			// in this case the second param expression is simply removed.
			query = query.replace("$2", "");
		}
		return query;
	}

	// Depending on the type of the value of requirement the string delimiter ' must be used.
	// This variable will contain the prefix to use when replacing the requirement value.
	private final String getFormattedValue() {
		if (value != null) {
		 return (this.getElem().getValueType() == RequirementElemType.STRING ? "'" : "") + this.value.trim() + (this.getElem().getValueType() == RequirementElemType.STRING ? "'" : "");
		}
		return null;
	}

	@Override
	public final int hashCode() {
		// TODO Auto-generated method stub
		return this.getEvalString().hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		if (!(obj instanceof Requirement)) {
			return false;
		}
		Requirement param = (Requirement) obj;
		if (this.getEvalString().compareTo(param.getEvalString()) == 0) {
			return true;
		}
		return false;
	}
}
