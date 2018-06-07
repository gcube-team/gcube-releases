package org.gcube.vremanagement.softwaregateway.client.fws;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.WebFault;


/**
 * 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class Types {

	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PackageCoordinates{

		@XmlElement(name="serviceClass")
		public String sc;
		
		@XmlElement(name="serviceName")
		public String sn;

		@XmlElement(name="serviceVersion")
		public String sv;
		

		@XmlElement(name="packageName")
		public String pn;
		

		@XmlElement(name="packageVersion")
		public String pv;
		

	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class LocationItem{

		@XmlElement(name="serviceClass")
		public String sc;
		
		@XmlElement(name="serviceName")
		public String sn;

		@XmlElement(name="serviceVersion")
		public String sv;
		

		@XmlElement(name="packageName")
		public String pn;
		

		@XmlElement(name="packageVersion")
		public String pv;
		

	}

	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PluginCoordinates{

		@XmlElement(name="serviceClass")
		public String sc;
		
		@XmlElement(name="serviceName")
		public String sn;

		@XmlElement(name="serviceVersion")
		public String sv;
		

		@XmlElement(name="packageName")
		public String pn;
		

		@XmlElement(name="packageVersion")
		public String pv;
		

	}
	
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class SACoordinates{

		@XmlElement(name="serviceClass")
		public String sc;
		
		@XmlElement(name="serviceName")
		public String sn;

		@XmlElement(name="serviceVersion")
		public String sv;
		

		@XmlElement(name="packageName")
		public String pn;
		

		@XmlElement(name="packageVersion")
		public String pv;
		

	}
	
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class getPluginResponse{

		@XmlElement(name="items")
		public List<LocationItem> items;

	}

	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class getPackagesResponse{

		@XmlElement(name="items")
		public List<LocationItem> items;

	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DependenciesCoordinates{

		@XmlElement(name="serviceClass")
		public String sc;
		
		@XmlElement(name="serviceName")
		public String sn;

		@XmlElement(name="serviceVersion")
		public String sv;
		

		@XmlElement(name="packageName")
		public String pn;
		

		@XmlElement(name="packageVersion")
		public String pv;
		

	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ServiceCoordinates{

		@XmlElement(name="serviceClass")
		public String sc;
		
		@XmlElement(name="serviceName")
		public String sn;

		@XmlElement(name="serviceVersion")
		public String sv;
		
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class LocationCoordinates{

		@XmlElement(name="serviceClass")
		public String sc;
		
		@XmlElement(name="serviceName")
		public String sn;

		@XmlElement(name="serviceVersion")
		public String sv;
		

		@XmlElement(name="packageName")
		public String pn;
		

		@XmlElement(name="packageVersion")
		public String pv;
		

	}
		
	@WebFault(name="ServiceNotAvaiableFault")
	public static class ServiceNotAvaiableFault extends RuntimeException {

		
		private static final long serialVersionUID = 1L;

		public ServiceNotAvaiableFault(String s) {
			super(s);
		}
	}


}
