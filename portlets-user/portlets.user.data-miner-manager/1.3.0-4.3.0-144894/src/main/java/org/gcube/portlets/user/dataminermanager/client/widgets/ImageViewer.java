package org.gcube.portlets.user.dataminermanager.client.widgets;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationId;
import org.gcube.portlets.user.dataminermanager.shared.data.output.ImageResource;

import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ImageViewer extends SimpleContainer {

	protected static final String SAVE_OK_MESSAGE = "The images have been saved on the Workspace in the folder ";
	protected static final String SAVE_OK_TITLE = "Saving operation was successful";
	protected static final String SAVE_FAIL_TITLE = "Error";
	protected static final String SAVE_FAIL_MESSAGE = "Error in saving images.";
	private ImageResource imagesResource;
	//private ComputationId computationId;


	/**
	 * @param mapImages
	 */
	public ImageViewer(ComputationId computationId,
			ImageResource imagesResource) {
		super();
		this.imagesResource = imagesResource;
		//this.computationId = computationId;
		create();
	}

	private void create() {
		VerticalLayoutContainer v = new VerticalLayoutContainer();
		add(v);
		if (imagesResource == null || imagesResource.getLink() == null
				|| imagesResource.getLink().isEmpty())
			v.add(new HtmlLayoutContainer("<i>No image found.</i>"), new VerticalLayoutData(-1, -1,
					new Margins(0)));
		else {
			v.add(new HtmlLayoutContainer(imagesResource.getName()),new VerticalLayoutData(-1, -1, new Margins(0)));
			TextButton saveImageBtn = new TextButton(
					"Download Image");
			saveImageBtn.setIcon(DataMinerManager.resources.download());
			saveImageBtn.addSelectHandler(new SelectEvent.SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					com.google.gwt.user.client.Window.open(imagesResource.getLink(), imagesResource.getName(), "");

				}
			});

			v.add(saveImageBtn,new VerticalLayoutData(-1, -1, new Margins(0)));
			final Image img = new Image(imagesResource.getLink());
			v.add(img,new VerticalLayoutData(1, -1, new Margins(0)));
		}

		forceLayout();

	}

	/*
	private void saveImages() {
		EventBusProvider.INSTANCE.fireEvent(new MaskEvent(
				"Saving on the workspace..."));
		// handler.maskAll("Saving on the workspace...");
		DataMinerManager.getService().saveImage(computationId, imagesResource,
				new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						EventBusProvider.INSTANCE
								.fireEvent(new MaskEvent(null));
						// handler.unmaskAll();
						UtilsGXT3.info(SAVE_OK_TITLE, SAVE_OK_MESSAGE + "\""
								+ result + "\"");
					}

					@Override
					public void onFailure(Throwable caught) {
						EventBusProvider.INSTANCE
								.fireEvent(new MaskEvent(null));
						// handler.unmaskAll();
						UtilsGXT3.alert(SAVE_FAIL_TITLE, SAVE_FAIL_MESSAGE,
								null);
					}
				});
	}
	*/
}
