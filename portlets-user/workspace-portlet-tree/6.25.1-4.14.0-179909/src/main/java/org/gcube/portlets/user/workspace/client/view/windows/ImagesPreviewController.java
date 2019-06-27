package org.gcube.portlets.user.workspace.client.view.windows;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTExternalImage;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube.GWTImageDocument;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.EnhancedImage;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.ui.Carousel;

import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;



/**
 * The Class ImagesPreviewController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 24, 2017
 */
public class ImagesPreviewController {

	private Window window = new Window();

	private Image loader = Resources.getIconLoading().createImage();

	private List<GWTWorkspaceItem> parentImages = null;

	private Carousel carousel;

	private FileModel folderParent;

	int  currentIndex = 0;

	/**
	 * Instantiates a new window image preview.
	 *
	 * @param folderParent the folder parent
	 */
	private ImagesPreviewController(FileModel folderParent){
		this.folderParent = folderParent;

		carousel = new Carousel(){

			/* (non-Javadoc)
			 * @see org.gcube.portlets.widgets.imagepreviewerwidget.client.ui.Carousel#onClickPrev()
			 */
			@Override
			public void onClickPrev() {

				if(parentImages==null)
					return;

				if(currentIndex>0){
					currentIndex--;
				}else {
					currentIndex = parentImages.size() - 1;
				}

				GWTWorkspaceItem wi = parentImages.get(currentIndex);
				EnhancedImage ei = toEnhancedImage(wi);
				if(ei!=null)
					show(ei);

			}

			/* (non-Javadoc)
			 * @see org.gcube.portlets.widgets.imagepreviewerwidget.client.ui.Carousel#onClickNext()
			 */
			@Override
			public void onClickNext() {

				if(parentImages==null)
					return;

				if(currentIndex<parentImages.size()-1){
					currentIndex++;
				}else {
					currentIndex = 0;
				}

				GWTWorkspaceItem wi = parentImages.get(currentIndex);
				EnhancedImage ei = toEnhancedImage(wi);
				if(ei!=null)
					show(ei);
			}
		};
	}

	/**
	 * To enhanced image.
	 *
	 * @param wi the wi
	 * @return the enhanced image
	 */
	private EnhancedImage toEnhancedImage(GWTWorkspaceItem wi){
		if(wi instanceof GWTImageDocument){
			GWTImageDocument image = (GWTImageDocument) wi;
			GWT.log("GWTImageDocument image: "+image);
			return new EnhancedImage(image.getThumbnailUrl(), image.getName(), image.getName(), image.getImageUrl());
		}else if(wi instanceof GWTExternalImage){
			GWTExternalImage image = (GWTExternalImage) wi;
			GWT.log("GWTExternalImage image: "+image);
			return new EnhancedImage(image.getThumbnailUrl(), image.getName(), image.getName(), image.getImageUrl());
		}

		return null;

	}


	/**
	 * Instantiates a new window image preview.
	 *
	 * @param name the name
	 * @param folderParent the folder parent
	 * @param image the image
	 * @param positionX the position x
	 * @param positionY the position y
	 */
	public ImagesPreviewController(String name, FileModel folderParent, GWTImageDocument image, int positionX, int positionY) {
		this(folderParent);
		showCarousel(name, image.getThumbnailUrl(), image.getImageUrl());
		preloadImagesForFolderParent(folderParent, image.getId());
	}

	/**
	 * Instantiates a new window image preview.
	 *
	 * @param name the name
	 * @param folderParent the folder parent
	 * @param image the image
	 * @param positionX the position x
	 * @param positionY the position y
	 */
	public ImagesPreviewController(String name, FileModel folderParent, GWTExternalImage image, int positionX, int positionY){
		this(folderParent);
		showCarousel(name, image.getThumbnailUrl(), image.getImageUrl());
		preloadImagesForFolderParent(folderParent, image.getId());
	}


	/**
	 * Preload images for folder parent.
	 *
	 * @param folderParent the folder parent
	 * @param currentImageId the current image id
	 */
	private void preloadImagesForFolderParent(FileModel folderParent, String currentImageId){

		AppControllerExplorer.rpcWorkspaceService.getImagesForFolder(folderParent.getIdentifier(), currentImageId, new AsyncCallback<List<GWTWorkspaceItem>>() {

			@Override
			public void onFailure(Throwable caught) {
				parentImages = null;
			}

			@Override
			public void onSuccess(List<GWTWorkspaceItem> result) {

				if(result==null)
					return;

				GWT.log("Preloaded image/s");
				for (GWTWorkspaceItem gwtWorkspaceItem : result) {
					GWT.log("Preloaded: "+gwtWorkspaceItem);
				}
				parentImages = result;
				if(parentImages.size()>0)
					carousel.showArrows(true);

			}
		});
	}


	/**
	 * Show carousel.
	 *
	 * @param imageTitle the image title
	 * @param thumbnailURL the thumbnail url
	 * @param imageURL the image url
	 */
	private void showCarousel(String imageTitle, String thumbnailURL, String imageURL) {
		List<EnhancedImage> list = new ArrayList<EnhancedImage>();
		GWT.log("Generating thumbnailURL: "+thumbnailURL +"\n imageURL: "+imageURL);
		EnhancedImage myimg = new EnhancedImage(thumbnailURL, imageTitle, imageTitle, imageURL);
		list.add(myimg);
		carousel.updateImages(list);
		carousel.showArrows(false);
		carousel.show();
	}

	/**
	 * Log.
	 *
	 * @param msg the msg
	 */
	public static native void log(String msg) /*-{
	  console.log(msg);
	}-*/;

}
