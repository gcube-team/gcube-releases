package org.gcube.data.analysis.tabulardata.operation.test;

import java.util.Observable;
import java.util.Observer;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.junit.Assert;

public class WorkerChecker<T extends Worker<?>> {

	T worker;
	CubeManager cubeManager;
	
	public WorkerChecker(CubeManager cubeManager, T worker) {
		this.cubeManager = cubeManager;
		this.worker = worker;
	}
	
	private void displayResult() {
		if (worker.getResult() instanceof ValidityResult){
			System.err.println("Result:\n" +((ValidityResult)worker.getResult()).isValid());
		} else if (worker.getResult() instanceof EmptyType)	{
			System.err.println("Result:\n" + cubeManager.getTable(worker.getSourceInvocation().getTargetTableId()));	
		} else System.err.println("Result:\n" + worker.getResult());
	}

	private void displayException() {
		worker.getException().printStackTrace(System.err);
	}

	private void displayStatus() {
		System.err.println(String.format("Status: %s %s%%", worker.getStatus(), Math.ceil(worker.getProgress() * 100)));

	}

	public void check() {
		TableId tableId =worker.getSourceInvocation().getTargetTableId(); 
		if (tableId != null)
			System.err.println("Target table before operation:\n" + cubeManager.getTable(tableId));;
		
		WorkerObserver workerObserver = new WorkerObserver();
		worker.addObserver(workerObserver);

		Thread runnerThread = new Thread(worker);
		runnerThread.start();
		
		try {
			runnerThread.join();
//			startFallabackCheckIfAvailable();
		} catch (InterruptedException e) {
			Assert.fail("Error detected: " + e.getMessage());
		}
					
	}

//	private void startFallabackCheckIfAvailable() {
//		if (worker.getStatus() == WorkerStatus.FALLBACK){
//			WorkerChecker fallbackWorkerChecker = new WorkerChecker(cubeManager, worker.getException().getFallbackWorker());
//			System.err.println("Checking fallback worker...");
//			fallbackWorkerChecker.check();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//			}
//			
//		}
//	}

	public class WorkerObserver implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			Worker<?> worker =(Worker<?>) o;
			displayStatus();
			switch (worker.getStatus()) {
			case INITIALIZING:
				checkNullResult(worker);
				break;
			case IN_PROGRESS:
				checkNullResult(worker);
				if (worker.getException() != null)
					throw new IllegalStateException(String.format("Exception must be null when status is %s",
							worker.getStatus()));
				break;
			case SUCCEDED:
				displayResult();
				if (worker.getResult() == null)
					throw new IllegalStateException(String.format("Result table cannot be null while status is %s",
							worker.getStatus()));
				return;
			case FAILED:
				displayException();
				if (worker.getException() == null)
					throw new IllegalStateException(String.format(
							"Exception must be different than null when status is %s", worker.getStatus()));
				Assert.fail(String.format("Failed Execution, error is %s",worker.getException()));
				return;
//			case FALLBACK:
//				displayException();
//				displayFallBackWorker();
//				if (worker.getException().getFallbackWorker() == null)
//					throw new IllegalStateException(String.format(
//							"A fallback worker must be set when status is %s", worker.getStatus()));
//				if (worker.getException().getMessage() == null ||worker.getException().getMessage().isEmpty())
//					throw new IllegalStateException(String.format(
//							"An exception message must be set when status is %s", worker.getStatus()));
//				return;
			}
		}

//		private void displayFallBackWorker() {
//			System.err.println("A fallback worker was provided. Invocation:\n" + worker.getException().getFallbackWorker().getSourceInvocation());
//		}

		private void checkNullResult(Worker<?> worker) {
			if (worker.getResult() != null)
				throw new IllegalStateException(String.format("Result must be null when status is %s",
						worker.getStatus()));
		}

	}

}
