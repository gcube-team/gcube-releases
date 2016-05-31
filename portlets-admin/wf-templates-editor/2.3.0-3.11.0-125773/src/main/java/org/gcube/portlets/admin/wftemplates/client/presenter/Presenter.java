package org.gcube.portlets.admin.wftemplates.client.presenter;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wftemplates.client.view.WfStep;

import com.google.gwt.user.client.ui.HasWidgets;
import com.orange.links.client.connection.Connection;
/**
 * <code> Presenter </code> interface is the presenter interface that the actual presenter component of this webapp must implement
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public abstract interface Presenter {
  public abstract void go(final HasWidgets container);
  public abstract void addNewStep(String label, String description);
  public abstract void addRolesOnConnection(Connection selectedEdge, ArrayList<WfRoleDetails> roles);
  public void doSaveTemplate(String templateName); 
  public void doRemoveConnectionFromModel(Connection selected); 
  public void doRemoveConnectionFromView(Connection selected); 
  public void doRemoveStep(WfStep step);
  public void doDeleteTemplate(WfTemplate toDelete);
}
