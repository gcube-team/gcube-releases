package gr.uoa.di.madgik.execution.engine.queue;

import gr.uoa.di.madgik.execution.engine.monitoring.ExecutionMonitor;

import java.util.LinkedList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A queue used to store plans before they are executed. Plans are evaluated for
 * requested resources and if not qualified to start executing they miss their
 * turn and following plans are evaluated. Every queue element has a threshold
 * of maximum percentage of utilization and the number of allowed times it can
 * miss turn.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class PlanQueue {
	private Logger log = LoggerFactory.getLogger(PlanQueue.class.getName());
	
	private BlockingDeque<PlanQueueElement> queue;
	private ExecutionMonitor monitor;

	public PlanQueue(ExecutionMonitor monitor) {
		queue = new LinkedBlockingDeque<PlanQueueElement>();
		this.monitor = monitor;
	}

	public void putLast(PlanQueueElement e) throws InterruptedException {
		queue.putLast(e);
	}

	public PlanQueueElement takeFirst() throws InterruptedException {
		LinkedList<PlanQueueElement> tempQ = new LinkedList<PlanQueueElement>();
		LinkedList<PlanQueueElement> toGetDecreased = new LinkedList<PlanQueueElement>();

		PlanQueueElement elem;

		while (true) {
			elem = queue.takeFirst();

			if (monitor.evaluate(elem.getHandle(), elem.getUtil()))
				break;

			tempQ.addFirst(elem);

			if (!elem.canGetDecreaseTtl()) {
				log.debug("Found plan element that cant be decreased. Returing null");
				elem = null;
				break;
			} else {
				toGetDecreased.add(elem);
			}
				
			log.debug("Passing by an element from queue with remaining " + elem.getTtl());
			if (queue.isEmpty()) {
				elem = null;
				break;
			}
		}
		
		if (elem == null)
			toGetDecreased.clear();
		while (!toGetDecreased.isEmpty()) {
			toGetDecreased.remove().decreaseTtl();
		}
		
		// Restore passed by elements back to queue
		while (!tempQ.isEmpty()) {
			queue.addFirst(tempQ.removeFirst());
		}

		return elem;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
}