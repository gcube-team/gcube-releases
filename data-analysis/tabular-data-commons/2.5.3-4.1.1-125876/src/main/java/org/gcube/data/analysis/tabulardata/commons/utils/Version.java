package org.gcube.data.analysis.tabulardata.commons.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Version implements Comparable<Version>{

	private int maior;

	private int minor;

	private int revision;

	private int age;

	@SuppressWarnings("unused")
	private Version(){}

	public Version(int maior, int minor, int revision, int age) {
		super();
		this.maior = maior;
		this.minor = minor;
		this.revision = revision;
		this.age = age;
	}


	@Override
	public String toString() {
		return maior+"."+minor+"."+revision+"-"+age;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maior;
		result = prime * result + minor;
		result = prime * result + revision;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (maior != other.maior)
			return false;
		if (minor != other.minor)
			return false;
		if (revision != other.revision)
			return false;
		return true;
	}

	public int compareTo(Version o) {
		if (maior>o.maior) return 1;
		else if (maior<o.maior) return -1;

		if (minor>o.minor) return 1;
		else if (minor<o.minor) return -1;

		if (revision>o.revision) return 1;
		else if (revision<o.revision) return -1;
		return 0;
	}

	public static Version parse(String version){
		try{
			String[] ageSplit = version.split("-");
			int age =Integer.parseInt(ageSplit[1]);
			String[] mmrSplit = ageSplit[0].split("\\.");

			int maior = Integer.parseInt(mmrSplit[0]);
			int minor = Integer.parseInt(mmrSplit[1]);
			int revision = Integer.parseInt(mmrSplit[2]);

			return new Version(maior, minor, revision, age);
		}catch(Throwable t){
			throw new IllegalArgumentException(version+" is not a valid TDM version");
		}
	}

}
