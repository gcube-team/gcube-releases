package org.gcube.datatransfer.portlets.user.client.utils;

import org.gcube.datatransfer.portlets.user.client.Common;
import org.gcube.datatransfer.portlets.user.shared.obj.BaseDto;

import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.examples.resources.client.images.ExampleImages;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Utils extends Common{

	public static final IconProvider<BaseDto> iconProviderForTarget=new IconProvider<BaseDto>() {				
		@Override
		public ImageResource getIcon(BaseDto model) {	
			if (model.getShortname().compareTo("<< Back") == 0){
				return null;						
			}
			else if (model.getShortname().compareTo("") == 0){
				return null;
			}
			else if(targetTree.getStore().getChildCount(model) == 1 
				&& targetTree.getStore().getFirstChild(model)
							.getName().compareTo("") == 0){
				return null;						
			}
			else{ //in case of simple file
				String fname=model.getName().toLowerCase();
				return getMyImage(fname);
			}
		}		
	};
	
	public static final IconProvider<BaseDto> iconProviderForSource=new IconProvider<BaseDto>() {				
		@Override
		public ImageResource getIcon(BaseDto model) {	
			if (model.getShortname().compareTo("<< Back") == 0){
				return null;						
			}
			else if (model.getShortname().compareTo("") == 0){
				return null;
			}
			else if(sourceTree.getStore().getChildCount(model) == 1 
				&& sourceTree.getStore().getFirstChild(model)
							.getName().compareTo("") == 0){
				return null;						
			}
			else{ //in case of simple file
				String fname=model.getName().toLowerCase();
				return getMyImage(fname);
			}
		}
		
	};
	
	public static ImageResource getMyImage(String fname){
		if(fname==null)return null;
		
		if(combo1!=null){
			if(combo1.isTreeBased()){
				return ExampleImages.INSTANCE.list();
			}
		}		
		if(fname.endsWith(".xml"))return ExampleImages.INSTANCE.xml();
		else if (fname.endsWith(".css"))return ExampleImages.INSTANCE.css();
		else if (fname.endsWith(".html"))return ExampleImages.INSTANCE.html();
		else if (fname.endsWith(".java"))return ExampleImages.INSTANCE.java();
		
		else return ExampleImages.INSTANCE.text();
	}
}
