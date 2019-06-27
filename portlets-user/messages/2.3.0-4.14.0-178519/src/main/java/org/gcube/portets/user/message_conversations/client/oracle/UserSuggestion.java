package org.gcube.portets.user.message_conversations.client.oracle;

import org.gcube.portets.user.message_conversations.shared.WSUser;

import com.google.gwt.user.client.ui.SuggestOracle;

public class UserSuggestion implements SuggestOracle.Suggestion {

    private WSUser user;

    public UserSuggestion(WSUser user) {
        this.user = user;
    }

    @Override
    public String getDisplayString() {
        return getReplacementString();
    }

    @Override
    public String getReplacementString() {
        return user.getFullName() + " ("+user.getEmail()+")";
    }

    public WSUser getUser() {
        return user;
    }
}
