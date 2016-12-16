package org.gcube.application.aquamaps.aquamapsportlet.client.perturbation;

import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.widgets.form.ComboBox;

public class CustomComboBox extends ComboBox {

	private static final int DEFAULT_WIDTH=100;
	
	public CustomComboBox(String label,String[] values) {
		super (label);
		SimpleStore booleanStore = new SimpleStore("value", values);  
        booleanStore.load();  
        this.setDisplayField("value");  
        this.setWidth(DEFAULT_WIDTH);
        this.setStore(booleanStore);  
        this.setListWidth(this.getWidth());
        this.setValue(values[0]);
	}
	
	
}
