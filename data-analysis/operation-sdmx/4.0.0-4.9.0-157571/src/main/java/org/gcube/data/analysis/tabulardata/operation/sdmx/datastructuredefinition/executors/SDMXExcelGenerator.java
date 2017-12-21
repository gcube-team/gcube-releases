package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.executors;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.SDMXDataOperationExecutor;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataResultBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.ExcelGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.ExcelGeneratorFromTable;
import org.gcube.data.analysis.tabulardata.operation.sdmx.template.TemplateWorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXExcelGenerator implements SDMXDataOperationExecutor {

	private final String OPERATION_NAME= "Generating Excel";
	private Logger logger;
	private boolean primary;
	
	public SDMXExcelGenerator(boolean primary) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.primary = primary;
	}
	
	@Override
	public String getOperationName() {

		return OPERATION_NAME;
	}

	@Override
	public SDMXDataResultBean executeOperation(SDMXDataBean inputData, OperationInvocation invocation) {

		this.logger.debug("Generating excel");
		ExcelGenerator generator = new ExcelGeneratorFromTable(inputData.getTableBean());
		boolean result = generator.generateExcel(inputData.getID(), TemplateWorkerUtils.DEFAULT_EXCEL_FOLDER);
		this.logger.debug("Operation completed with result "+result);
		SDMXDataResultBean dataResultBean = new SDMXDataResultBean ();
		
		if (!result)
		{
			dataResultBean.setError(this.primary);
			dataResultBean.addMessage("Unable to save excel");
		}
		
		
		return dataResultBean;
		
	}

	@Override
	public boolean isPrimaryOperation() 
	{
		return this.primary;
	}

	@Override
	public boolean isDataAware() {

		return true;
	}

}
