package org.gcube.portlets.user.tdtemplateoperation.client;

import org.gcube.portlets.user.tdtemplateoperation.client.rpc.TemplateColumnOperationService;
import org.gcube.portlets.user.tdtemplateoperation.client.rpc.TemplateColumnOperationServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TdTemplateOperation implements EntryPoint {

  /**
   * Create a remote service proxy to talk to the server-side TemplateColumnOperationService service.
   */
  public static final TemplateColumnOperationServiceAsync templateOperationService = GWT.create(TemplateColumnOperationService.class);


  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

  }
}
