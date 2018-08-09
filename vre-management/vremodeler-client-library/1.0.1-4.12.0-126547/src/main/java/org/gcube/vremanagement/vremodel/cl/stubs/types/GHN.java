package org.gcube.vremanagement.vremodel.cl.stubs.types;

import static org.gcube.vremanagement.vremodel.cl.Constants.TYPES_NAMESPACE;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_NAMESPACE)
public class GHN {

	@XmlElement(namespace=TYPES_NAMESPACE)
	private String id;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private String host;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private boolean securityEnabled;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private Memory memory;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private Site site;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private List<RunningInstanceMessage> relatedRIs;
	@XmlElement(namespace=TYPES_NAMESPACE)
	private boolean selected;
	
	protected GHN() {
		super();
	}

	public GHN(String id, String host, boolean securityEnabled,
			Memory memory, Site site, List<RunningInstanceMessage> relatedRIs,
			boolean selected) {
		super();
		this.id = id;
		this.host = host;
		this.securityEnabled = securityEnabled;
		this.memory = memory;
		this.site = site;
		this.relatedRIs = relatedRIs;
		this.selected = selected;
	}

	
	/**
	 * @return the id
	 */
	public String id() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void id(String id) {
		this.id = id;
	}

	/**
	 * @return the host
	 */
	public String host() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void host(String host) {
		this.host = host;
	}

	/**
	 * @return the securityEnabled
	 */
	public boolean securityEnabled() {
		return securityEnabled;
	}

	/**
	 * @param securityEnabled the securityEnabled to set
	 */
	public void securityEnabled(boolean securityEnabled) {
		this.securityEnabled = securityEnabled;
	}

	/**
	 * @return the memory
	 */
	public Memory memory() {
		return memory;
	}

	/**
	 * @param memory the memory to set
	 */
	public void memory(Memory memory) {
		this.memory = memory;
	}

	/**
	 * @return the site
	 */
	public Site site() {
		return site;
	}

	/**
	 * @param site the site to set
	 */
	public void site(Site site) {
		this.site = site;
	}

	/**
	 * @return the relatedRIs
	 */
	public List<RunningInstanceMessage> relatedRIs() {
		return relatedRIs;
	}

	/**
	 * @param relatedRIs the relatedRIs to set
	 */
	public void relatedRIs(List<RunningInstanceMessage> relatedRIs) {
		this.relatedRIs = relatedRIs;
	}

	/**
	 * @return the selected
	 */
	public boolean selected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void selected(boolean selected) {
		this.selected = selected;
	}

	@XmlRootElement
	public static class Memory{
		@XmlElement
		private long diskSpace;
		@XmlElement
		private long memorySize;
		
		protected Memory() {
			super();
		}

		public Memory(long diskSpace, long memorySize) {
			super();
			this.diskSpace = diskSpace;
			this.memorySize = memorySize;
		}

		/**
		 * @return the diskSpace
		 */
		public long diskSpace() {
			return diskSpace;
		}

		/**
		 * @param diskSpace the diskSpace to set
		 */
		public void diskSpace(long diskSpace) {
			this.diskSpace = diskSpace;
		}

		/**
		 * @return the memorySize
		 */
		public long memorySize() {
			return memorySize;
		}

		/**
		 * @param memorySize the memorySize to set
		 */
		public void memorySize(long memorySize) {
			this.memorySize = memorySize;
		}
		
		
	}
	
	@XmlRootElement
	public static class Site{
		@XmlElement
		private String location;
		@XmlElement
		private String country;
		@XmlElement
		private String domain;
		
		protected Site() {
			super();
		}

		public Site(String location, String country, String domain) {
			super();
			this.location = location;
			this.country = country;
			this.domain = domain;
		}

		/**
		 * @return the location
		 */
		public String location() {
			return location;
		}

		/**
		 * @param location the location to set
		 */
		public void location(String location) {
			this.location = location;
		}

		/**
		 * @return the country
		 */
		public String country() {
			return country;
		}

		/**
		 * @param country the country to set
		 */
		public void country(String country) {
			this.country = country;
		}

		/**
		 * @return the domain
		 */
		public String domain() {
			return domain;
		}

		/**
		 * @param domain the domain to set
		 */
		public void domain(String domain) {
			this.domain = domain;
		}
		
		
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GHN [id=" + id + ", host=" + host + ", securityEnabled="
				+ securityEnabled + ", memory=" + memory + ", site=" + site
				+ ", relatedRIs=" + relatedRIs + ", selected=" + selected + "]";
	}
	

}
