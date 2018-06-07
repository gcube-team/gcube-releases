package org.gcube.common.resources.kxml.service.version;


/**
 * Describes an artifact version in terms of its components, converts it to/from a string and
 * compares two versions.
 *
 */
public interface ArtifactVersion extends Comparable<ArtifactVersion> {
	
    int getMajorVersion();

    int getMinorVersion();

    int getIncrementalVersion();

    int getBuildNumber();

    String getQualifier();

    void parseVersion( String version );
    
}
