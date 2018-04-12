/**
 * 
 */
package org.gcube.portlets.widgets.inviteswidget.client.validation;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.gcube.portlets.widgets.inviteswidget.client.ui.FormViewImpl;
import org.gcube.portlets.widgets.inviteswidget.client.validation.FormView.UserDetails;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.editor.client.EditorDriver;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.validation.client.impl.PathImpl;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class FormErrorsValidation extends AbstractActivity implements FormView.Delegate {

	private FormView view;
	/**
	 * @see com.google.gwt.activity.shared.Activity#start(com.google.gwt.user.client.ui.AcceptsOneWidget, com.google.gwt.event.shared.EventBus)
	 */
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view = new FormViewImpl(this);
		panel.setWidget(view);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean onSendClick() {
		EditorDriver<UserDetails> editorDriver = view.getEditorDriver();
		UserDetails details = editorDriver.flush();
		Set<ConstraintViolation<UserDetails>> violations = validateEmailTextBox(details);
 		//decorate widgets implementing HasEditorErrors with validation messages
		return editorDriver.setConstraintViolations( (Set) violations);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean onEmailSendInviteClick() {
		EditorDriver<UserDetails> editorDriver = view.getEditorDriver();
		UserDetails details = editorDriver.flush();
		Set<ConstraintViolation<UserDetails>> violations = validateName(details);
		return !violations.isEmpty();
	}

	private Set<ConstraintViolation<UserDetails>> validateName(UserDetails user2InviteBean) {
		Set<ConstraintViolation<UserDetails>> violations = new HashSet<ConstraintViolation<UserDetails>>();
		if(user2InviteBean.getName() == null || "".equals(user2InviteBean.getName().trim())) {
			violations.add(buildNotNullConstraintViolation(user2InviteBean, "name"));		
		}
		return violations;
	}
	
	private Set<ConstraintViolation<UserDetails>> validateEmailTextBox(UserDetails user2InviteBean){
		Set<ConstraintViolation<UserDetails>> violations = new HashSet<ConstraintViolation<UserDetails>>();
		if(user2InviteBean.getEmail() == null || "".equals(user2InviteBean.getEmail().trim())) {
			violations.add(buildNotNullConstraintViolation(user2InviteBean, "email"));				
		}
		else if (!isValidEmailAddress(user2InviteBean.getEmail())) {
			violations.add(buildInvalidEmailAddressConstraintViolation(user2InviteBean, "email"));		
		}
	
		return violations;
	}
	
	
	private ConstraintViolation<UserDetails> buildNotNullConstraintViolation(final UserDetails bean, final String path) {
		return new ConstraintViolation<UserDetails>() {
			@Override
			public String getMessage() {
				return "must not be empty";
			}
			@Override
			public String getMessageTemplate() {
				return null;
			}
			@Override
			public UserDetails getRootBean() {
				return bean;
			}
			@Override
			public Class<UserDetails> getRootBeanClass() {
				return UserDetails.class;
			}
			@Override
			public Object getLeafBean() {
				return bean;
			}

			@Override
			public Path getPropertyPath() {
				return new PathImpl().append(path);
			}

			@Override
			public Object getInvalidValue() {
				return null;
			}
			@Override
			public ConstraintDescriptor<?> getConstraintDescriptor() {
				return null;
			}
		};
	}
	
	private ConstraintViolation<UserDetails> buildInvalidEmailAddressConstraintViolation(final UserDetails bean, final String path) {
		return new ConstraintViolation<UserDetails>() {
			@Override
			public String getMessage() {
				return "email address must be valid";
			}
			@Override
			public String getMessageTemplate() {
				return null;
			}
			@Override
			public UserDetails getRootBean() {
				return bean;
			}
			@Override
			public Class<UserDetails> getRootBeanClass() {
				return UserDetails.class;
			}
			@Override
			public Object getLeafBean() {
				return bean;
			}

			@Override
			public Path getPropertyPath() {
				return new PathImpl().append(path);
			}

			@Override
			public Object getInvalidValue() {
				return null;
			}
			@Override
			public ConstraintDescriptor<?> getConstraintDescriptor() {
				return null;
			}
		};
	}
	/**
	 * 
	 * @param emailValue
	 * @return
	 */
	private boolean isValidEmailAddress(String emailValue) {
        if(emailValue == null) return true;
        
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
        
        boolean valid = false;
        
        if(emailValue.getClass().toString().equals(String.class.toString())) {
                valid = ((String)emailValue).matches(emailPattern);
        } else {
                valid = ((Object)emailValue).toString().matches(emailPattern);
        }

        return valid;
}

	
}
