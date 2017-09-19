package org.gcube.portlets.admin.fhn_manager_portlet.client;

import org.gcube.portlets.admin.fhn_manager_portlet.client.resources.ImageName;
import org.gcube.portlets.admin.fhn_manager_portlet.client.resources.ImageType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.Image;

public class GUICommon {

	public static final Image getResourceIcon(ObjectType resType,ImageType imgType){
		
		switch(resType){
		case REMOTE_NODE : return getImage(ImageName.REMOTE_NODE, imgType);
		
		case SERVICE_PROFILE : return getImage(ImageName.SERVICE_PROFILE, imgType);
		
		case VM_PROVIDER : return getImage(ImageName.VM_PROVIDER, imgType);
		
		case VM_TEMPLATES : return getImage(ImageName.VM_TEMPLATE, imgType);
		
		}
		return null;
		
	}
	
	public static final Image getImage(ImageName name, ImageType type){
		Image toReturn=new Image(name.getTheImg());
		toReturn.setStyleName(type.getAssociatedCss());
		return toReturn;
	}
	
	
	public static NumberFormat decimalFormat=NumberFormat.getDecimalFormat();
	public static DateTimeFormat dateFormat=DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
			
	public static NumberFormat byteFormat=NumberFormat.getFormat("#,##0.#");
	
	public static String readableBytes(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return byteFormat.format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
			
}
