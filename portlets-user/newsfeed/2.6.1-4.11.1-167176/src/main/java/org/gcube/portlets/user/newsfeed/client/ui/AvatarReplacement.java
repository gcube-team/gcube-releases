package org.gcube.portlets.user.newsfeed.client.ui;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AvatarReplacement extends Composite {

	private static NoAvatarUiBinder uiBinder = GWT.create(NoAvatarUiBinder.class);
	
	/**
	 * to maintain the color assigned to an avatar replacement, for each page load
	 * this hashmap maintain the assigned users color, chosen reandomly at the beginning
	 */
	public static final HashMap<String, String> avatarReplacementAssignedColors = new HashMap<String, String>();

	interface NoAvatarUiBinder extends UiBinder<Widget, AvatarReplacement> {
	}

	@UiField HTML avatarBox;
	/**
	 * the random colors
	 */
	private String[] randomColors = {
			"#8e8e93",
			"#ff2d55",
			"#ff3b30",
			"#ff9500",
			"#ffcc00",
			"#4cd964",
			"#5ac8fa",
			"#34aadc",
			"#007aff",
			"#5856d6"
			};
	
	
	public AvatarReplacement() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setInitials(String username, String firstName, String lastName) {
		pickRandomColor(username);
		String first = "A";
		if (firstName != null && firstName.trim().length() > 0) 
			first = firstName.trim().substring(0, 1);
		String second = "Z";
		if (lastName != null && lastName.trim().length() > 0) 
			second = lastName.trim().substring(0, 1);
		avatarBox.setText(first+second);
	}
	/**
	 * randomize of does not find it, else color is maintained through all the session
	 * @param username
	 */
	private void pickRandomColor(String username) {
		if (! avatarReplacementAssignedColors.containsKey(username)) {
			String randomColor = randomColors[Random.nextInt(randomColors.length)];
			avatarBox.getElement().getStyle().setBackgroundColor(randomColor);
			avatarReplacementAssignedColors.put(username, randomColor);
		} else
			avatarBox.getElement().getStyle().setBackgroundColor(avatarReplacementAssignedColors.get(username));
	}
	
	
}
