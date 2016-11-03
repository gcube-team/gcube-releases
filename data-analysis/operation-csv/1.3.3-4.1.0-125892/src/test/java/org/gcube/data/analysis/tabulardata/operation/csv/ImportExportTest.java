package org.gcube.data.analysis.tabulardata.operation.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.export.csv.Constants;
import org.gcube.data.analysis.tabulardata.operation.export.csv.exporter.CSVExportFactory;
import org.gcube.data.analysis.tabulardata.operation.importer.csv.CSVImportFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ImportExportTest {

	Logger logger = LoggerFactory.getLogger(ImportExportTest.class);

	@Inject
	CSVExportFactory exportFactory;

	@Inject
	CSVImportFactory importFactory;

	@Inject
	CubeManager cubeManager;

	@BeforeClass
	public static void init(){
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public void exportTest() throws Exception{

		Handler.activateProtocol();

		importTest();

		Table table = cubeManager.getTables().toArray(new Table[0])[0];

		Map<String, Object> instances = new HashMap<String, Object>();
		instances.put(Constants.ENCODING, "UTF8");
		instances.put(Constants.SEPARATOR, ";");

		List<String> columns = Lists.newArrayList();
		for(Column column : table.getColumns())
			if (!(column.getColumnType() instanceof IdColumnType))
				columns.add(column.getName());

		instances.put(Constants.COLUMNS, columns);

		OperationInvocation invocation = InvocationCreator.getCreator(exportFactory.getOperationDescriptor())
				.setTargetTable(table.getId())
				.setParameters(instances)
				.create();

		for (Entry<String, Object> entry : invocation.getParameterInstances().entrySet())
			logger.trace(entry.getKey()+" - "+entry.getValue());

		ResourceCreatorWorker exportWorker = exportFactory.createWorker(invocation);

		exportWorker.run();
		Assert.assertNotNull(exportWorker.getProgress());
		Assert.assertNotNull(exportWorker.getStatus());
		if (exportWorker.getException()!=null)
			logger.error("error",exportWorker.getException());
		logger.trace("final state is {}",exportWorker.getStatus().toString());
		logger.trace("progress is {}",exportWorker.getProgress());

		Assert.assertEquals(WorkerStatus.SUCCEDED, exportWorker.getStatus());


		System.out.println(exportWorker.getResult());

		logger.trace("exported file in {}",exportWorker.getResult().getResources().get(0).getResource());

		InputStream is = new URL(exportWorker.getResult().getResources().get(0).getResource().getStringValue()).openConnection().getInputStream();

		File file = File.createTempFile("export", ".csv");
		FileOutputStream fos = new FileOutputStream(file);

		byte[] buffer = new byte[8000];
		while (is.read(buffer)!=-1)
			fos.write(buffer);

		is.close();
		fos.close();

		logger.trace("exported to {}",file.getAbsolutePath());

		System.out.println(cubeManager.getTable(table.getId()));

	}

	private Map<String, Object> getParameterInvocation(){
		Map<String, Object> parameterInstances = new HashMap<String, Object>();
		parameterInstances.put(Constants.URL, "http://goo.gl/pJNZ61");
		parameterInstances.put(Constants.SEPARATOR, ",");
		parameterInstances.put(Constants.ENCODING, "ISO-8859-1");
		parameterInstances.put(Constants.HASHEADER, true);
		parameterInstances.put(Constants.FIELDMASK, Arrays.asList(true, false, true));
		return parameterInstances;
	}

	@Test
	public void factoryValidityTest(){
		Assert.assertNotNull(importFactory);
	}

	@Test
	public void importTest() throws Exception{
		Handler.activateProtocol();
		OperationInvocation invocation = InvocationCreator.getCreator(importFactory.getOperationDescriptor())
				.setParameters(getParameterInvocation())
				.create();
		DataWorker importWorker = importFactory.createWorker(invocation);
		importWorker.run();
		Assert.assertNotNull(importWorker);
		Assert.assertNotNull(importWorker.getProgress());
		Assert.assertNotNull(importWorker.getStatus());
		if (importWorker.getException()!=null)
			importWorker.getException().printStackTrace();
		logger.trace("final state is {}",importWorker.getStatus().toString());
		logger.trace("progress is {}",importWorker.getProgress());

		System.out.println(importWorker.getResult().getResultTable());

		Assert.assertEquals(WorkerStatus.SUCCEDED, importWorker.getStatus());

	}

	@Test
	public void getFileFromStorage() throws Exception{
		Handler.activateProtocol();
		String url ="smp://CSVexport/export458707646624332886.csv?5ezvFfBOLqb2cBxvyAbVnJS+3d44SQz2A8z8JV6S+Yh+yRwW0y21CartYW2ep6BuVQ1q2uWlZqgiuTNo6ixZmxnOePeHTPQDIqO7jtIc3cnVYCTHPAiNHX9c7ZuqyiSAehrJJMkvx5mlBGM70nbTqz3wOuxuTmHkbyxPoeNJKLU=";
		InputStream is = new URL(url).openConnection().getInputStream();

		File file = File.createTempFile("JSONExport", ".json");
		FileOutputStream fos = new FileOutputStream(file);

		byte[] buffer = new byte[1024];
		int read = 0;
		while ((read= is.read(buffer))!=-1)
			fos.write(buffer, 0 , read);

		fos.close();
		is.close();
		
		
		logger.trace("exported to {}",file.getAbsolutePath());
	}

	//	@Test
	//	public void validationTest() throws InvalidInvocationException{
	//		OperationInvocation invocation = InvocationCreator.getCreator(exportFactory.getOperationDescriptor())
	//				.setParameters(getParameterInvocation())
	//				.create();
	//		RegexpStringParameter par = new RegexpStringParameter(SEPARATOR, "Separator", "Char separator", Cardinality.ONE, "^\\W$");
	//		System.out.println("is param valid "+par.validate(",er"));
	//		importFactory.validateInvocation(invocation);
	//	}
}
