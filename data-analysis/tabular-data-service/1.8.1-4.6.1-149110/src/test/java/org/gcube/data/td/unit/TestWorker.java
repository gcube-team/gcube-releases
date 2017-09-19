package org.gcube.data.td.unit;

import java.util.List;

import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class TestWorker extends DataWorker {

	
	private boolean aborted = false;
	
	public TestWorker(OperationInvocation sourceInvocation) {
		super(sourceInvocation);
	}

	@Override
	protected WorkerResult execute() throws WorkerException, OperationAbortedException {
		
		int  tries = 0;
		while (!aborted && tries<5){
			tries++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		
		if (aborted) throw new OperationAbortedException();
		return new WorkerResult() {
			
			@Override
			public Table getResultTable() {
				return new Table(new GenericTableType());
			}
			
			@Override
			public Table getDiffTable() {
				return new Table(new GenericTableType());
			}
			
			@Override
			public List<Table> getCollateralTables() {
				return null;
			}
		};
	}

	@Override
	public void abort() {
		this.aborted= true;
	}
	
}
