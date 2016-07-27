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
 * Filename: RequirementElemPath.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements;

/**
 * <p>
 * The {@link Requirement} elements are expressed by declaring the path of
 * the GHN profile to use as constrain key.
 * All the possible accessible nodes are here declared.
 * </p>
 * <p><b>Samples:</b>
 * <pre>
 * // Requires an operating system different from OSX
 * new Requirement(RequirementElemPath.OS , RequirementRelationType.NOT_EQUAL, "OSX");
 * // At least 3Gb of available memory
 * new Requirement(RequirementElemPath.MEM_RAM_SIZE, RequirementRelationType.GREATER, "3000");
 * // In the profile environment must be defined the key "ANT_HOME" and the value must be "/usr/share/ant"
 * new Requirement(RequirementElemPath.RUNTIME_ENV_STRING, "ANT_HOME", RequirementRelationType.EQUAL, "/usr/share/ant")
 *
 * // Custom xquery: allows to access custom paths of the service profile.
 * new Requirement(RequirementElemPath.CUSTOM_REQUIREMENT, "/GHNDescription/Architecture[@PlatformType = 'i386']");
 * // The same result is obtained by a pre-defined query
 * new Requirement(RequirementElemPath.PLATFORM, RequirementRelationType.EQUAL, "i386");
 * </pre>
 * </p>
 * @author Daniele Strollo (ISTI-CNR)
 */
public enum RequirementElemPath {
	MEM_RAM_AVAILABLE("/GHNDescription/MainMemory[@RAMAvailable $1 $2]", RequirementElemType.NUMBER),
	MEM_RAM_SIZE("/GHNDescription/MainMemory[@RAMSize $1 $2]", RequirementElemType.NUMBER),
	MEM_VIRTUAL_AVAILABLE("/GHNDescription/MainMemory[@VirtualAvailable $1 $2]", RequirementElemType.NUMBER),
	MEM_VIRTUAL_SIZE("/GHNDescription/MainMemory[@VirtualSize $1 $2]", RequirementElemType.NUMBER),
	HOST("/GHNDescription/Name $1 $2", RequirementElemType.STRING),
	OS("/GHNDescription/OperatingSystem[@Name $1 $2]", RequirementElemType.STRING),
	DISK_SPACE("/GHNDescription/LocalAvailableSpace[text() $1 $2]", RequirementElemType.NUMBER),
	LOAD1MIN("/GHNDescription/Load[@Last1Min $1 $2]", RequirementElemType.NUMBER),
	LOAD5MIN("/GHNDescription/Load[@Last5Min $1 $2]", RequirementElemType.NUMBER),
	LOAD15MIN("/GHNDescription/Load[@Last15Min $1 $2]", RequirementElemType.NUMBER),
	PLATFORM("/GHNDescription/Architecture[@PlatformType $1 $2]", RequirementElemType.STRING),
	PROCESSOR_NUM("/GHNDescription/Processor[last() $1 $2]", RequirementElemType.NUMBER),
	PROCESSOR_BOGOMIPS("/GHNDescription/Processor[@Bogomips $1 $2]", RequirementElemType.NUMBER),
	// (e.g. Pisa ...)
	SITE_LOCATION("/Site/Location $1 $2", RequirementElemType.STRING),
	// The nation in lower case prefix. (E.g. it, uk...)
	SITE_COUNTRY("/Site/Country $1 $2", RequirementElemType.STRING),
	SITE_DOMAIN("/Site/Domain $1 $2", RequirementElemType.STRING),

	CUSTOM_REQUIREMENT("$1", RequirementElemType.STRING),

	RUNTIME_ENV_STRING("/GHNDescription/RunTimeEnv/node()[$1 $2]", RequirementElemType.STRING),
	RUNTIME_ENV_NUMBER("/GHNDescription/RunTimeEnv/node()[$1 $2]", RequirementElemType.NUMBER);

	private String path = null;
	// NOTE before it was /RIONGHN/ProfileXML/Resource/Profile
	private final String pathPrefix = "/RIONGHN/ProfileXML";
	private RequirementElemType valueType = null;

	// The constructor
	private RequirementElemPath(final String path, final RequirementElemType type) {
		this.path = path;
		this.valueType = type;
	}
	public String getPath() {
		return pathPrefix + this.path;
	}
	public RequirementElemType getValueType() {
		return this.valueType;
	}
}
