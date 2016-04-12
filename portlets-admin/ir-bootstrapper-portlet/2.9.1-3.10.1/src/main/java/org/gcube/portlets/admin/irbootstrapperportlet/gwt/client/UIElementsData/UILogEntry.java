/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class UILogEntry implements IsSerializable{
	
	public enum UILogEntryLevel { 
		TYPE_INFORMATION, 
		TYPE_WARNING,
		TYPE_ERROR
	};
	
	private String message;
	private UILogEntryLevel type;
	
	public UILogEntry() { }
	
	/**
	 * Constructs a new {@link UILogEntry}.
	 * @param message the message that this entry will contain
	 * @param bIsError if true, the message is an error. If false, the message is a warning
	 */
	public UILogEntry(String message, UILogEntryLevel type) {
		this.message = message;
		this.type = type;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public UILogEntryLevel getLevel() {
		return this.type;
	}
}
