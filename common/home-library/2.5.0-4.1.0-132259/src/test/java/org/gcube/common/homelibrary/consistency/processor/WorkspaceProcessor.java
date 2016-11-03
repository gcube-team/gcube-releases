/**
 * 
 */
package org.gcube.common.homelibrary.consistency.processor;

import java.util.LinkedList;
import java.util.List;

import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceProcessor extends AbstractProcessor<Workspace, WorkspaceItem>
{

	@Override
	public void process(Workspace w) throws Exception {
		List<WorkspaceItem> toProcess = new LinkedList<WorkspaceItem>();
		toProcess.add(w.getRoot());
		while(toProcess.size()>0){
			WorkspaceItem item = toProcess.remove(0);
			subProcess(item);
			if (w.exists(item.getId())) toProcess.addAll(item.getChildren());
		}
	}
	
}