package org.gcube.portlets.user.td.gwtservice.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TestService {

	private static Logger logger = LoggerFactory.getLogger(TestService.class);

	private Map<String, Object> getParameterInvocationSDMX() {
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances
				.put("registryBaseUrl",
						"http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
		parameterInstances.put("id", "CL_SPECIES");
		parameterInstances.put("version", "1.0");
		parameterInstances.put("agency", "FAO");
		return parameterInstances;
	}

	@Test
	public void createSDMX() throws Exception {
		logger.debug("------------Create SDMX TR--------------");
		TDService tdService=new TDService();
		TabularDataService service = tdService.getService();
		
		TabularResource resource = service.createTabularResource();
		logger.debug("after createTabResource " + ScopeProvider.instance.get());
		logger.debug("name: "
				+ ((NameMetadata) resource.getMetadata(NameMetadata.class))
						.getValue());

		logger.debug("tabular data id: " + resource.getId().getValue());
		TabularResourceId resourceId = resource.getId();

		logger.debug("------------List Operations------------");

		List<OperationDefinition> ops = service.getCapabilities();
		OperationDefinition csvop = null;
		for (OperationDefinition op : ops) {
			logger.debug(op.getName());
			if (op.getName().compareTo("SDMX Codelist import") == 0) {
				logger.debug("----Takes");
				csvop = op;

			}
		}
		//
		logger.debug("------------Invocation------------");

		OperationExecution invocation = new OperationExecution(
				csvop.getOperationId(), getParameterInvocationSDMX());

		//
		logger.debug("------------Execute------------");
		Task task = service.execute(invocation, resource.getId());
		logger.debug("------------Check Task------------");
		while (task.getStatus() != TaskStatus.FAILED
				&& task.getStatus() != TaskStatus.SUCCEDED) {
			logger.debug(task.getStatus() + " " + task.getStartTime().getTime());

			Thread.sleep(3000);
		}

		logger.debug("------------Show Task------------");

		logger.debug(task.getStatus() + " " + task.getStartTime().getTime()
				+ " " + task.getEndTime().getTime());

		logger.debug("Task Progress:" + task.getProgress());
		Assert.assertNotNull(task.getResult());
		logger.debug("Task getResult: " + task.getResult());
		Table resultTable = task.getResult().getPrimaryTable();

		logger.debug("resultTable :" + resultTable);

		logger.debug(service.getLastTable(resourceId).getName());

		logger.debug("QueryLenght: "
				+ service.getQueryLenght(resultTable.getId(), null));
		logger.debug(service.queryAsJson(resultTable.getId(), new QueryPage(0,
				200)));

	}

	public static final String ENCODING = "encoding";
	public static final String HASHEADER = "hasHeader";
	public static final String SEPARATOR = "separator";
	public static final String URL = "url";

	private Map<String, Object> getParameterInvocationCSV() {
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances
				.put(URL,
						"smp://importCSV/import.csv?5ezvFfBOLqZDktisNW+YdntfYeiRwwsx6mcSk7QrncHxpcgkYS6DSZuEmrrVwjNcQni7q/eoILP1k7BwnoPjFNqJykZW71mI7F/ELBV1lV8qTh/bosrhjOzQb50+GI/1aaspqr2Xc3LqUp9Q101dAT29nbhl9SyJqgEi0BgUQmMaZs/n4coLNw==");
		parameterInstances.put(SEPARATOR, ",");
		parameterInstances.put(ENCODING, "UTF-8");
		parameterInstances.put(HASHEADER, true);
		return parameterInstances;
	}

	@Test
	public void createCSV() throws Exception {
		logger.debug("------------Create CSV TR--------------");
		try {
			TDService tdService=new TDService();
			TabularDataService service = tdService.getService();

			TabularResource resource = service.createTabularResource();
			logger.debug("after createTabResource "
					+ ScopeProvider.instance.get());
			logger.debug("name: "
					+ ((NameMetadata) resource.getMetadata(NameMetadata.class))
							.getValue());

			logger.debug("tabular data id: " + resource.getId().getValue());
			TabularResourceId resourceId = resource.getId();

			List<OperationDefinition> ops = service.getCapabilities();
			OperationDefinition csvop = null;
			for (OperationDefinition op : ops) {
				logger.debug(op.getName());
				if (op.getName().equals("CSV Import"))
					csvop = op;
			}

			OperationExecution invocation = new OperationExecution(
					csvop.getOperationId(), getParameterInvocationCSV());

			Task task = service.execute(invocation, resource.getId());

			while (task.getStatus() != TaskStatus.FAILED
					&& task.getStatus() != TaskStatus.SUCCEDED) {
				logger.debug(task.getStatus() + " "
						+ task.getStartTime().getTime());

				Thread.sleep(3000);
			}

			logger.debug("Task Status: " + task.getStatus());

			if (task.getStatus() == TaskStatus.FAILED)
				logger.debug("Failed: " + task.getErrorCause());

			Assert.assertEquals(TaskStatus.SUCCEDED, task.getStatus());

			logger.debug(task.getStartTime().getTime() + " "
					+ task.getEndTime().getTime());

			Assert.assertNotNull(task.getResult());

			Table resultTable = task.getResult().getPrimaryTable();

			logger.debug("resultTable :" + task.getResult().getPrimaryTable());

			logger.debug(service.getLastTable(resourceId).getName());

			Assert.assertNotNull(service);

			logger.debug(service.queryAsJson(resultTable.getId(),
					new QueryPage(0, 200)));

			System.out
					.println(service.getQueryLenght(resultTable.getId(), null));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void listTR() throws Exception {
		logger.debug("------------List TR--------------");
		TDService tdService=new TDService();
		TabularDataService service = tdService.getService();

		List<TabularResource> trs = service.getTabularResources();
		Assert.assertTrue(trs.size() > 0);
		logger.debug("---Tabular Resource---");
		Table lastTable = null;
		for (TabularResource tr : trs) {
			lastTable = service.getLastTable(tr.getId());

			if (lastTable != null) {
				logger.debug("TabularResource: [" + tr.getId()
						+ ", lastTable: " + lastTable.getTableType().getName()
						+ "]");
			} else {
				logger.debug("TabularResource: [" + tr.getId()
						+ ", lastTable: " + lastTable + "]");

			}
		}

		if (lastTable != null) {
			logger.debug("---Test Column---");
			logger.debug("Table " + lastTable.getId() + " "
					+ lastTable.getName());
			for (Column col : lastTable.getColumns()) {
				logger.debug("Column: [name:" + col.getName() + ", localId:"
						+ col.getLocalId() + ", dataType:" + col.getDataType()
						+ "]");

			}

		}

	}

	

}
