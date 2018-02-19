package org.gcube.portlets.user.notifications.shared;

import java.io.Serializable;
import java.util.Arrays;

import org.gcube.portal.databook.shared.NotificationChannelType;
import org.gcube.portal.databook.shared.NotificationType;
/**
 * @author Massimiliano Assante, CNR-ISTI
 * This class represent a notification preference with its type (allowing associating a label and a description) 
 * and the channels selected by the user for this type
 */
@SuppressWarnings("serial")
public class NotificationPreference implements Serializable, Comparable<NotificationPreference> {
	NotificationType type;
	String typeLabel;
	String typeDesc;
	NotificationChannelType[] selectedChannels;
	public NotificationPreference() {
		super();
	}

	
	public NotificationPreference(NotificationType type, String typeLabel,
			String typeDesc, NotificationChannelType[] selectedChannels) {
		super();
		this.type = type;
		this.typeLabel = typeLabel;
		this.typeDesc = typeDesc;
		this.selectedChannels = selectedChannels;
	}


	public NotificationChannelType[] getSelectedChannels() {
		return selectedChannels;
	}


	public void setSelectedChannels(NotificationChannelType[] selectedChannels) {
		this.selectedChannels = selectedChannels;
	}


	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getTypeLabel() {
		return typeLabel;
	}

	public void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}


	@Override
	public String toString() {
		return "NotificationPreference [type=" + type + ", typeLabel="
				+ typeLabel + ", typeDesc=" + typeDesc + ", selectedChannels="
				+ Arrays.toString(selectedChannels) + "]";
	}


	@Override
	public int compareTo(NotificationPreference o) {
		if (this.typeLabel.length() <= o.getTypeLabel().length())
			return -1;
		else
			return 1;
	}

	
	
}
