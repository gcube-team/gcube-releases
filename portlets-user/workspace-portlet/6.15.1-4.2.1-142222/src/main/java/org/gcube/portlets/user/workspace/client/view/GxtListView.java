package org.gcube.portlets.user.workspace.client.view;


import org.gcube.portlets.user.workspace.client.ConstantsPortlet;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.ListStoreModel;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtListView extends LayoutContainer {

	private ContentPanel cp = new ContentPanel();
//	private String headerTitle = ConstantsPortlet.RESULT;
	private ListStore<FileGridModel> store = ListStoreModel.getInstance().getStore();

  public GxtListView() {

	cp.setBodyBorder(false);
	cp.setHeading(ConstantsPortlet.RESULT);
	cp.setHeaderVisible(true);

    ListView<FileGridModel> view = new ListView<FileGridModel>() {
      @Override
      protected FileGridModel prepareData(FileGridModel model) {
        String s = model.get(FileModel.NAME);
        model.set("shortName", Format.ellipse(s, 15));
        return model;
      }

    };

    view.setStore(store);
    view.setItemSelector("div.thumb-wrap");
    view.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<FileModel>>() {

          public void handleEvent(SelectionChangedEvent<FileModel> be) {
        	  cp.setHeading("Simple ListView (" + be.getSelection().size()
                + " items selected)");
          }

        });

    cp.add(view);
    add(cp);
  }

  private native String getTemplate() /*-{
	return [ '<tpl for=".">', '<div class="thumb-wrap" id="{Name}">',
			'<div class="thumb"><img src="{path}" title="{Name}"></div>',
			'<span class="x-editable">{shortName}</span></div>', '</tpl>',
			'<div class="x-clear"></div>' ].join("");

	}-*/;
}
