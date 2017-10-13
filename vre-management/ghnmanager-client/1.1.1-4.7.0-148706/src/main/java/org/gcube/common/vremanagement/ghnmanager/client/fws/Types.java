package org.gcube.common.vremanagement.ghnmanager.client.fws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Types {
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AddScopeInputParams{

		@XmlElement
		public String scope;
		@XmlElement
		public String map;
		
		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
		public String getMap() {
			return map;
		}
		public void setMap(String map) {
			this.map = map;
		}

	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ScopeRIParams{
		@XmlElement
		public String scope;
		
		@XmlElement
		public String name;
		
		@XmlElement
		public String clazz;

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}
		
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class RIData{
		@XmlElement
		public String name;
		
		@XmlElement
		public String clazz;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}
	}

	

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ShutdownOptions{
		@XmlElement
		public boolean restart;
		
		@XmlElement
		public boolean clean;

		public boolean isRestart() {
			return restart;
		}

		public void setRestart(boolean restart) {
			this.restart = restart;
		}

		public boolean isClean() {
			return clean;
		}

		public void setClean(boolean clean) {
			this.clean = clean;
		}
		

	}
}
