package org.gcube.data.analysis.tabulardata.model.metadata.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VersionMetadata implements TableMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7149497935228705769L;

	private String version = null;

	public static String VERSION_REGEX = "^(([1-9][0-9]*[1-9])|[0-9])\\.(([1-9][0-9]*[1-9])|[0-9])$";

	@SuppressWarnings("unused")
	private VersionMetadata() {
	}

	public VersionMetadata(String version) {
		setVersion(version);
	}

	public String getVersion() {
		return version;
	}

	private void setVersion(String version) {
		if (version == null || !version.matches(VERSION_REGEX))
			throw new IllegalArgumentException(String.format("Version '%s' does not match regex '%s'", version,
					VERSION_REGEX));
		this.version = version;

	}

	public boolean isInheritable() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		VersionMetadata other = (VersionMetadata) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VersionMetadata [version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

}
