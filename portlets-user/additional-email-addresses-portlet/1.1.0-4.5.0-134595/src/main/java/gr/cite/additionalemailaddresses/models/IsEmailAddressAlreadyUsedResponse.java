package gr.cite.additionalemailaddresses.models;

import java.util.List;

/**
 * @author mnikolopoulos
 *
 */
public class IsEmailAddressAlreadyUsedResponse {

	private boolean isUsed;

	public boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
}
