/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.widgets;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.MaskEvent;
import org.gcube.portlets.user.trendylyzer_portlet.client.occurences.EventBusProvider;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;


public class ImagesViewer extends VerticalPanel {

	protected static final String SAVE_OK_MESSAGE = "The images have been saved on the Workspace in the folder ";
	protected static final String SAVE_OK_TITLE = "Saving operation was successful";
	protected static final String SAVE_FAIL_TITLE = "Error";
	protected static final String SAVE_FAIL_MESSAGE = "Error in saving images.";
	private Map<String, String> mapImages;
	Logger log = Logger.getLogger("");

	private String computationId;

	/**
	 * @param mapImages
	 */
	public ImagesViewer(String computationId, Map<String, String> mapImages) {
		super();
		this.mapImages = mapImages;
		this.computationId = computationId;
		this.setSpacing(5);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {		
		super.onRender(parent, pos);
		
		if (mapImages.size()==0)
			this.add(new Html("<i>No images found.</i>"));
		else {
////			this.add(new Button("Save all images on the Workspace", Images.save(), new SelectionListener<ButtonEvent>() {
////				@Override
////				public void componentSelected(ButtonEvent ce) {
////					saveImages();
////				}
////			}));			
//			
			for (String name: mapImages.keySet()) {
				String smpEncoded = URL.encodeQueryString(mapImages.get(name));
				String imgUrl = GWT.getModuleBaseURL() + "DownloadService?url=" + smpEncoded + "&name=image&type=images";
				log.log(Level.SEVERE,"URL:"+imgUrl);
				final Image img = new Image(imgUrl);
				Html alg= new Html(name);
				alg.setStyleName("jobViewer-output-outputType");
				this.add( alg);
				this.add(img);
			}
			
			this.add( new Button("Save on the Workspace", Images.save(), new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					saveImages();
				}
			}));
		}
		this.layout();



	}

	protected void saveImages() {
		EventBusProvider.getInstance().fireEvent(new MaskEvent("Saving on the workspace..."));
//		handler.maskAll("Saving on the workspace...");
		TrendyLyzer_portlet.getService().saveImages(computationId, mapImages, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				EventBusProvider.getInstance().fireEvent(new MaskEvent(null));
//				handler.unmaskAll();
				MessageBox.info(SAVE_OK_TITLE, SAVE_OK_MESSAGE +"\""+result+"\"", null);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				EventBusProvider.getInstance().fireEvent(new MaskEvent(null));
//				handler.unmaskAll();
				MessageBox.alert(SAVE_FAIL_TITLE, SAVE_FAIL_MESSAGE, null);
			}
		});
	}
}
