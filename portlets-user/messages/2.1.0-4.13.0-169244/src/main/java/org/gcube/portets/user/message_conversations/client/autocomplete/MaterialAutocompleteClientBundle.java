package org.gcube.portets.user.message_conversations.client.autocomplete;



import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Client Bundle for Autocomplete component
 *
 * @author kevzlou7979
 */
interface MaterialAutocompleteClientBundle extends ClientBundle {

    MaterialAutocompleteClientBundle INSTANCE = GWT.create(MaterialAutocompleteClientBundle.class);

    @Source("autocomplete.min.css")
    TextResource autocompleteCss();
}
