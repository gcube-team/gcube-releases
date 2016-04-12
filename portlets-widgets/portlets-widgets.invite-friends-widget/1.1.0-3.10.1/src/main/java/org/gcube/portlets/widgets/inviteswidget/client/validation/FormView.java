/**
 * 
 */
package org.gcube.portlets.widgets.inviteswidget.client.validation;

import com.google.gwt.editor.client.EditorDriver;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public interface FormView extends IsWidget {

	public static class UserDetails {
		String email;
		String name;
		String lastName;
		
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		           
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		@Override
		public String toString() {
			return "UserDetails [email=" + email + ", name=" + name
					+ ", lastName=" + lastName + "]";
		}
		
	}
	
	public interface Delegate {
		boolean onSendClick();
		
		boolean onEmailSendInviteClick();
	}
	
	
	
	EditorDriver<UserDetails> getEditorDriver();
}
