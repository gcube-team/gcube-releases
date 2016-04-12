package org.gcube.portlets.admin.wfdocviewer.client.presenter;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;

import com.google.gwt.user.client.ui.HasWidgets;
/**
 * <code> Presenter </code> interface is the presenter interface that the actual presenter component of this webapp must implement
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public abstract interface Presenter {	
	abstract void go(final HasWidgets container);
	abstract void doInstanciateNewWorkflow(String reportid, String reportName);
	abstract void doAddRolesToSelectedStep(ArrayList<WfRoleDetails> roles);
}