package org.gcube.data.publishing.gCatFeeder.collectors.dm;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorCategory;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.InternalAlgorithmDescriptor;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.Parameter;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.UserIdentity;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.utils.ISUtils;
import org.gcube.data.publishing.gCatfeeder.collectors.DataCollector;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CollectorFault;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DMAlgorithmsInfoCollector implements DataCollector<InternalAlgorithmDescriptor> {

	private static final Pattern p = Pattern.compile("\\{Published by (.*)\\((.*\\..*\\)).*\\}$");

	
	private Map<String,String> env=null;
	
	
	public void setEnvironmentConfiguration(EnvironmentConfiguration envConfig) {
		if(envConfig!=null) {
			log.debug("Current Environment Configuration is : "+envConfig.getCurrentConfiguration());
			this.env=envConfig.getCurrentConfiguration();
		}else { 
			env=Collections.emptyMap();
			log.warn("NO ENVIRONMENT CONFIGURATION FOUND");
		}
	}
	

	@Override
	public Set<InternalAlgorithmDescriptor> collect() throws CollectorFault {
		try {
			log.trace("Collecting information for Dataminer algorithms");
			HashSet<InternalAlgorithmDescriptor> toReturn=new HashSet();
			SClient client=new DataMinerService().getClient();
			String wpsbaseUrl=getWPSBasePath();

			List<OperatorsClassification> opClassifications=client.getOperatorsClassifications();
			log.debug("Found {} classifications.",opClassifications.size());
			
			for(OperatorsClassification opClass: opClassifications) {
				// Load info
				String opClassName =opClass.getName();

				
				List<Operator> ops=opClass.getOperators();
				log.debug("Found {} operators under classification {} ",ops.size(),opClassName);
				for(Operator op : ops) {					
					
					InternalAlgorithmDescriptor desc=new InternalAlgorithmDescriptor();
					desc.setClassName(opClassName);
					
					// OperatorCategory Info
					OperatorCategory cat = op.getCategory();
					String categoryBriefDescription = cat.getBriefDescription();
					String categoryDescription= cat.getDescription();
					String categoryID=cat.getId();
					String categoryName=cat.getName();

					
					desc.setCategoryBriefDescription(categoryBriefDescription);
					desc.setCategoryID(categoryID);
					desc.setCategoryName(categoryName);
					desc.setCategoryDescription(categoryDescription);
					
					
					
					// Operator info

					String opBriefDescription=op.getBriefDescription();
					String opDescription=op.getDescription();
					String opID=op.getId();
					String operatorName=op.getName();

					
					desc.setBriefDescription(opBriefDescription);
					desc.setDescription(opDescription);
					desc.setId(opID);
					desc.setName(operatorName);
					
					
					desc.setAuthor(parseUser(getAuthor(opDescription)));
					desc.setMaintainer(parseUser(getAuthor(opDescription)));
					
					
					
					// Parameters info
					for(org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter param:client.getInputParameters(op)) {
						String paramDescription=param.getDescription();
						String paramName=param.getName();
						String paramType=param.getTypology().toString();
						String paramValue=param.getValue();
						
						desc.getInputParameters().add(
								new Parameter(paramName, paramType, paramDescription, paramValue));
						
					}
					
					for(org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter param:client.getOutputParameters(op)) {
						String paramDescription=param.getDescription();
						String paramName=param.getName();
						String paramType=param.getTypology().toString();
						String paramValue=param.getValue();
						
						desc.getOutputParameters().add(
								new Parameter(paramName, paramType, paramDescription, paramValue));
						
					}
					
					
					desc.getTags().add(categoryName);
					
					
					String guiBasePath=getGUIBasePath();
					if(guiBasePath!=null)
						desc.setGuiLink(getGUIBasePath()+"?"+
							DataMinerCollectorProperties.getProperty(DataMinerCollectorProperties.GUI_PARAM_NAME)+"="+opID);
					
					
					if(wpsbaseUrl!=null) {
						desc.setWpsLink(wpsbaseUrl+"?Request=DescribeProcess&Version=1.0.0&Service=WPS"+"&Identifier="+opID);
					}
					desc.setPrivateFlag(Boolean.parseBoolean(env.get(Constants.PRIVATE)));
					
					toReturn.add(desc);
				}


				// Create bean
				
				
			}





			return toReturn;
		}catch(Exception e) {
			throw new CollectorFault("Unable to retrieve information",e);
		}
	}

	
	private String getGUIBasePath() {
		return env.get(Constants.GUI_BASE_URL);		
	}
	
	private String getAuthor(String algorithmDescription) {
		String toReturn=parseDescription(algorithmDescription);
		if(toReturn==null)
			toReturn=env.get(Constants.DEFAULT_AUTHOR);
		if(toReturn==null)
			toReturn=DataMinerCollectorProperties.getProperty(DataMinerCollectorProperties.DEFAULT_AUTHOR);
		return toReturn;
	}
	
	
	private static final UserIdentity parseUser(String userString) {
		String splitter=null;
		if(userString.contains(" "))
			splitter=" ";
		else if (userString.contains(".")) splitter="\\.";
		
		String[] splitted=userString.split(splitter);
		return new UserIdentity(splitted[0], splitted[1], null, null); 
	}
	
	/*
	 * i.e. "Basic statistic max min average {Published by Giancarlo Panichi (giancarlo.panichi) on 2018/07/20 10:24 GMT}"
	 */
	private static final String parseDescription(String description) {
		Matcher m=p.matcher(description);
		if(m.find())
			return m.group(1); // group 0 == {...}, group 1 == Giancarlo Panichi, group 2 == giancarlo.panichi
		else return null;		
	}
	
	
	private final String getWPSBasePath() {
		try{
			ServiceEndpoint se=ISUtils.queryForServiceEndpoints("DataAnalysis", "DataMiner").get(0);
			return se.profile().accessPoints().iterator().next().address();
		}catch(Throwable t) {
			log.warn("Unable to find DM proxy. No WPS URL will be provided",t);
			return null;
		}
	}
	
}
