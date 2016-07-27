package gr.uoa.di.madgik.workflow.client.library.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Types {

	public static class AccessInfo {
		@XmlElement
		public String port;

		@XmlElement
		public String userId;

		@XmlElement
		public String password;

	}

	public static class JDLParams {
		@XmlElement
		public Long executionLease;

		@XmlElement
		public String jdlDescription;

		@XmlElement
		public List<JDLResource> jdlResources;

		@XmlElement
		public JDLConfig config;

	}

	public static class JDLResource {
		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceType;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public AccessInfo resourceAccessInfo;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

	}

	public static class JDLConfig {
		@XmlElement
		public Boolean chokeProgressEvents;

		@XmlElement
		public Boolean chokePerformanceEvents;

		@XmlElement
		public Boolean queueSupport;

		@XmlElement
		public Float utilization;

		@XmlElement
		public Integer passedBy;
	}

	public static class GRIDParams {
		@XmlElement
		public Long executionLease;

		@XmlElement
		public List<GRIDResource> gridResources;

		@XmlElement
		public GRIDConfig config;

	}

	public static class GRIDResource {

		@XmlElement
		public String resourceType;

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public AccessInfo resourceAccessInfo;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

	}

	public static class GRIDConfig {
		@XmlElement
		public Boolean chokeProgressEvents;

		@XmlElement
		public Boolean chokePerformanceEvents;

		@XmlElement
		public Boolean queueSupport;

		@XmlElement
		public Float utilization;

		@XmlElement
		public Integer passedBy;
	}


	public static class CONDORParams {
		
		@XmlElement
		public Long executionLease;
		
		@XmlElement
		public List<CONDORResource> condorResources;
		
		@XmlElement
		public CONDORConfig config;
	}

	public static class CONDORResource {
		
		@XmlElement
		public String resourceType;
		
		@XmlElement
		public String resourceKey;
		
		@XmlElement
		public String resourceAccess;
		
		@XmlElement
		public String resourceReference;
		
		@XmlElement
		public byte[] inMessageBytePayload;
		
		@XmlElement
		public String inMessageStringPayload;
	}
	
	public static class CONDORConfig {
		
		@XmlElement
		public Boolean chokeProgressEvents;
		
		@XmlElement
		public Boolean chokePerformanceEvents;
		
		@XmlElement
		public Boolean retrieveJobClassAd;
		
		@XmlElement
		public Long waitPeriod;
		
		@XmlElement
		public Long timeout;
		
		@XmlElement
		public Boolean isDag;
	}
	
	public static class HADOOPParams {
		
		@XmlElement
		public Long executionLease;
		
		@XmlElement
		public HADOOPResource hadoopResources;
		
		@XmlElement
		public HADOOPConfig config;
		
	}
	
	public static class HADOOPResource {
		
		@XmlElement
		public List<HADOOPArchiveResource> archives;
		
		@XmlElement
		public HADOOPJarResource jar;
		
		@XmlElement
		public HADOOPConfigurationResource configuration;
		
		@XmlElement
		public List<HADOOPLibResource> libs;
		
		@XmlElement
		public List<HADOOPFileResource> files;
		
		@XmlElement
		public List<HADOOPArgumentResource> arguments;
		
		@XmlElement
		public List<String> properties;
		
		@XmlElement
		public String main;
		
		@XmlElement
		public List<HADOOPInputResource> inputs;
		
		@XmlElement
		public List<HADOOPOutputResource> outputs;
		
	}
	
	public static class HADOOPArchiveResource {

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

		@XmlElement
		public Boolean hdfsPresent;
	}

	public static class HADOOPJarResource {

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

		@XmlElement
		public Boolean hdfsPresent;
	}

	public static class HADOOPConfigurationResource {

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

		@XmlElement
		public Boolean hdfsPresent;
	}

	public static class HADOOPLibResource {

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

		@XmlElement
		public Boolean hdfsPresent;
	}

	public static class HADOOPFileResource {

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

		@XmlElement
		public Boolean hdfsPresent;
	}

	public static class HADOOPArgumentResource {

		@XmlElement
		public String resourceValue;

		@XmlElement
		public Integer order;
	}

	public static class HADOOPInputResource {

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public byte[] inMessageBytePayload;

		@XmlElement
		public String inMessageStringPayload;

		@XmlElement
		public Boolean cleanup;
	}

	public static class HADOOPOutputResource {

		@XmlElement
		public String resourceKey;

		@XmlElement
		public String resourceAccess;

		@XmlElement
		public AccessInfo resourceAccessInfo;

		@XmlElement
		public String resourceReference;

		@XmlElement
		public Boolean cleanup;
	}

	public static class HADOOPConfig {

		@XmlElement
		public Boolean chokeProgressEvents;

		@XmlElement
		public Boolean chokePerformanceEvents;
	}

	public static class StatusRequest {
		
		@XmlElement
		public String executionID;
		
		@XmlElement
		public Boolean includePlan;
		
	}
	
	public static class StatusReport {
		
		@XmlElement
		public Boolean isCompleted;
		
		@XmlElement
		public String plan;
		
		@XmlElement
		public List<ExecutionEvent> events;
		
		@XmlElement
		public List<JobOutput> output;
		
		@XmlElement
		public String error;
		
		@XmlElement
		public String errorDetails;
	}
	
	public static class JobOutput {
		
		@XmlElement
		public String key;
		
		@XmlElement
		public String subKey;
		
		@XmlElement
		public String storageSystemID;
		
	}
	
	public static class ExecutionEvent {
		
		@XmlElement
		public String eventType;
		
		@XmlElement
		public Long eventTimestamp;
		
		@XmlElement
		public ExecutionProgressEvent progressEventInfo;
		
		@XmlElement
		public ExecutionExternalProgressEvent progressExternalEventInfo;
		
		@XmlElement
		public ExecutionPerformanceEvent performanceEventInfo;
		
	}
	
	public static class ExecutionProgressEvent {
		
		@XmlElement
		public Boolean reportProgress;
		
		@XmlElement
		public Boolean reportNodeProgress;
		
		@XmlElement
		public Boolean reportNodeStatus;
		
		@XmlElement
		public Integer currentStep;
		
		@XmlElement
		public Integer totalStep;
		
		@XmlElement
		public String nodeName;
		
		@XmlElement
		public String nodeHostName;
		
		@XmlElement
		public Integer nodePort;
		
		@XmlElement
		public String message;
		
		@XmlElement
		public String emiterID;
	}
	
	public static class ExecutionExternalProgressEvent {
		
		@XmlElement
		public Boolean reportProgress;
		
		@XmlElement
		public Integer currentStep;
		
		@XmlElement
		public Integer totalStep;
		
		@XmlElement
		public String message;
		
		@XmlElement
		public String emiterID;
		
		@XmlElement
		public String externalEmiterName;
		
	}
	
	public static class ExecutionPerformanceEvent {
		
		@XmlElement
		public String emiterID;
		
		@XmlElement
		public Long totalTime;
		
		@XmlElement
		public Long initializationTime;
		
		@XmlElement
		public Long finalizationTime;
		
		@XmlElement
		public Long childrenTotalTime;
		
		@XmlElement
		public Integer numberOfSubcalls;
		
		@XmlElement
		public Long subcallsTotalTime;
		
	}
	
	/*
	 * @WebFault(name = "UnrecoverableFault") public static class
	 * UnrecoverableFault extends RuntimeException { private static final long
	 * serialVersionUID = 1L;
	 * 
	 * public UnrecoverableFault(String s) { super(s); } }
	 * 
	 * @WebFault(name = "RetrySameFault") public static class RetrySameFault
	 * extends RuntimeException { private static final long serialVersionUID =
	 * 1L;
	 * 
	 * public RetrySameFault(String s) { super(s); } }
	 * 
	 * @WebFault(name = "RetryEquivalentFault") public static class
	 * RetryEquivalentFault extends RuntimeException { private static final long
	 * serialVersionUID = 1L;
	 * 
	 * public RetryEquivalentFault(String s) { super(s); } }
	 */
}
