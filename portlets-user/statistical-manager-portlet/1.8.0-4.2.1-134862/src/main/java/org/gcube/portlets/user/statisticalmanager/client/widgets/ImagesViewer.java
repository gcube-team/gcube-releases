/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.widgets;

import java.util.Map;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.events.MaskEvent;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;

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

/**
 * @author ceras
 *
 */
public class ImagesViewer extends VerticalPanel {

	protected static final String SAVE_OK_MESSAGE = "The images have been saved on the Workspace in the folder ";
	protected static final String SAVE_OK_TITLE = "Saving operation was successful";
	protected static final String SAVE_FAIL_TITLE = "Error";
	protected static final String SAVE_FAIL_MESSAGE = "Error in saving images.";
	private Map<String, String> mapImages;
	private String computationId;
//	private ImagesViewerHandler handler;

	/**
	 * @param mapImages
	 */
	public ImagesViewer(String computationId, Map<String, String> mapImages) {
		super();
		this.mapImages = mapImages;
		this.computationId = computationId;
//		this.handler = handler;
		this.setSpacing(5);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {		
		super.onRender(parent, pos);
		
		if (mapImages.size()==0)
			this.add(new Html("<i>No images found.</i>"));
		else {
			this.add(new Button("Save all images on the Workspace", Images.save(), new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					saveImages();
				}
			}));			
			
			for (String name: mapImages.keySet()) {
				String smpEncoded = URL.encodeQueryString(mapImages.get(name));
				String imgUrl = GWT.getModuleBaseURL() + "DownloadService?url=" + smpEncoded + "&name=image&type=images";
				
				final Image img = new Image(imgUrl);
				this.add(new Html(name));
				this.add(img);
			}
			
			this.add( new Button("Save all images on the Workspace", Images.save(), new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					saveImages();
				}
			}));
		}
		this.layout();


//			final Button save = new Button("Save on the Workspace", Images.save(), new SelectionListener<ButtonEvent>() {
//				@Override
//				public void componentSelected(ButtonEvent ce) {
//				}
//			});
//			save.setVisible(false);
//			
//			img.addMouseOverHandler(new MouseOverHandler() {
//				@Override
//				public void onMouseOver(MouseOverEvent event) {
//					save.setVisible(true);
//				}
//			});
//			img.addMouseOutHandler(new MouseOutHandler() {
//				@Override
//				public void onMouseOut(MouseOutEvent event) {
//					System.out.println("("+event.getX()+","+event.getY()+")"+";		"+" top:"+img.getAbsoluteTop() + " left:"+img.getAbsoluteLeft()
//							+" offW:"+img.getOffsetWidth()
//							+" offH:"+img.getOffsetHeight()
//							+" oTop:"+img.getOriginTop()
//							+" oLeft:"+img.getOriginLeft());
//					
//					int x = event.getX();
//					int y = event.getY();
//					int w = img.getOffsetWidth();
//					int h = img.getOffsetHeight();
//					if (x<0 || y<0 || x>w || y>h)
//						save.setVisible(false);
//				}
//			});
//			
//			LayoutContainer lc = new LayoutContainer(new AbsoluteLayout());
//			lc.add(img);
//			lc.add(save, new AbsoluteData(20, 20));
//			
//			this.add(lc);
	}

	/**
	 * 
	 */
	protected void saveImages() {
		EventBusProvider.getInstance().fireEvent(new MaskEvent("Saving on the workspace..."));
//		handler.maskAll("Saving on the workspace...");
		StatisticalManager.getService().saveImages(computationId, mapImages, new AsyncCallback<String>() {
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
