package org.gcube.data.analysis.tabulardata.service.impl.operation.tasks;

import java.util.Comparator;

import org.gcube.data.analysis.tabulardata.service.operation.Task;

public class TaskComparator implements Comparator<Task> {

	public int compare(Task o1, Task o2) {
		return (o1.getStartTime().compareTo(o2.getStartTime()));
	}

}
