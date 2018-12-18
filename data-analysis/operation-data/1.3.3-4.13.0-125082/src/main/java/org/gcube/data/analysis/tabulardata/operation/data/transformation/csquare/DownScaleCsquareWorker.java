package org.gcube.data.analysis.tabulardata.operation.data.transformation.csquare;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.operation.data.replace.ReplaceByExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class DownScaleCsquareWorker extends DataWorker {

	private CubeManager cm;
	private ReplaceByExpressionFactory replaceFactory;
	private DatabaseConnectionProvider connProvider;



	public DownScaleCsquareWorker(OperationInvocation sourceInvocation,
			CubeManager cm, ReplaceByExpressionFactory replaceFactory,
			DatabaseConnectionProvider connProvider) {
		super(sourceInvocation);
		this.cm = cm;
		this.replaceFactory = replaceFactory;
		this.connProvider = connProvider;
	}



	@Override
	protected WorkerResult execute() throws WorkerException,
	OperationAbortedException {
		try{
			updateProgress(0.1f, "Initializing");
			Resolution toSetResolution=DownScaleCsquareFactory.getFinalResolution(getSourceInvocation(), cm, connProvider);
			ColumnReference targetColRef=new ColumnReference(getSourceInvocation().getTargetTableId(),getSourceInvocation().getTargetColumnId());
			Expression toSet=new SubstringByIndex(targetColRef, new TDInteger(1), new TDInteger(toSetResolution.getCsquareLength()));
			checkAborted();
			updateProgress(0.3f,"Downscaling..");

			WorkerWrapper<DataWorker,WorkerResult> wrapper=createWorkerWrapper(replaceFactory);
			Map<String,Object> parameters=new HashMap<>();
			Expression condition=new GreaterThan(new Length(targetColRef), new TDInteger(toSetResolution.getCsquareLength()));

			parameters.put(ReplaceByExpressionFactory.CONDITION_PARAMETER.getIdentifier(), condition);
			parameters.put(ReplaceByExpressionFactory.VALUE_PARAMETER.getIdentifier(), toSet);
			checkAborted();
			WorkerStatus status=wrapper.execute(getSourceInvocation().getTargetTableId(), getSourceInvocation().getTargetColumnId(), parameters);
			checkAborted();
			updateProgress(0.9f,"Finalizing..");
			if(!status.equals(WorkerStatus.SUCCEDED)) throw new Exception("Unexpected internal error on replacing values.");
			else return new ImmutableWorkerResult(wrapper.getResult().getResultTable());

		}catch(InvalidInvocationException e){
			throw new WorkerException("Unexpected internal error, unable to modify values",e);
		}catch(OperationAbortedException e){
			throw e;
		}catch(WorkerException e){
			throw e;
		}catch(Exception e){
			throw new WorkerException("Unexpected internal error, unable to modify values",e);
		}

	}

}
