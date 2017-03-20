package org.gcube.data.analysis.dataminermanagercl.test;

import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorsClassification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DataMinerServiceTest extends TestCase {
	private static Logger logger = LoggerFactory
			.getLogger(DataMinerServiceTest.class);

	public void testOperators() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test Operators");
			try {
				SClient sClient = new DataMinerService().getClient();
				List<OperatorsClassification> operatorsClassifications = sClient
						.getOperatorsClassifications();
				logger.debug("OperatorsClassifications: " + operatorsClassifications);
				
				if(operatorsClassifications!=null && operatorsClassifications.size()>0){
					OperatorsClassification firstCategory=operatorsClassifications.get(0);
					if(firstCategory.getOperators()!=null&& !firstCategory.getOperators().isEmpty()){
						Operator operator=firstCategory.getOperators().get(0);
						logger.debug("First Operator: "+operator);
						List<Parameter> parameters=sClient.getInputParameters(operator);
						logger.debug("Parameters: "+parameters);
						
						
					} else {
						logger.debug("Operators void");
					}
				} else {
					logger.debug("OperatorsClassifcation void");
				}
				
				assertTrue("Success",true);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success",true);
		}
	}
	
	
	
	public void testOperarorName() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test DBScan");
			try {
				DataMinerService dataMinerService=new DataMinerService();
				SClient sClient = dataMinerService.getClient();
				List<OperatorsClassification> operatorsClassifications = sClient
						.getOperatorsClassifications();
				logger.debug("OperatorsClassifications rerieved");
				
				if(operatorsClassifications!=null && operatorsClassifications.size()>0){
					for(OperatorsClassification oc:operatorsClassifications){
						for(Operator op:oc.getOperators()){
							logger.debug("Operator: "+op.getName()+" ("+op.getId()+")");
						}
					}
					
				} else {
					logger.debug("OperatorsClassifcation void");
				}
				
				assertTrue("Success",true);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success",true);
		}
	}
	

}
