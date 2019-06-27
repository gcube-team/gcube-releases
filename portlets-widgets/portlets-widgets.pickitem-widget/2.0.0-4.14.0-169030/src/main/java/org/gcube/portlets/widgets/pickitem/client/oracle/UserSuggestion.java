package org.gcube.portlets.widgets.pickitem.client.oracle;

import org.gcube.portlets.widgets.pickitem.client.bundle.CssAndImages;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.user.client.ui.SuggestOracle;

public class UserSuggestion implements SuggestOracle.Suggestion {

    private ItemBean user;

    public UserSuggestion(ItemBean user) {
        this.user = user;
        if (user.isItemGroup())
        	user.setThumbnailURL(CssAndImages.INSTANCE.iconTeam().getSafeUri().asString());
    }

    @Override
    public String getDisplayString() {
        return user.getAlternativeName();
    }

    @Override
    public String getReplacementString() {
        return user.getAlternativeName();
    }

    public ItemBean getUser() {
        return user;
    }
}
