/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.locale;

import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 22, 2014
 *
 * @param <T>
 */
public class SelectableSimpleComboBox<T extends String> extends SimpleComboBox<T> {

    public SelectableSimpleComboBox() {
        super();
        setTypeAhead(true);
//    	setEditable(false);
		setTriggerAction(TriggerAction.ALL);
		setForceSelection(true);
		setAllowBlank(false);
    
      /*  this.addKeyListener(new KeyListener(){
            @Override
            public void componentKeyDown(ComponentEvent event)
            {
            	
            	System.out.println("qui");
                // Get a reference to the combobox in question
                SelectableSimpleComboBox<T> combo = SelectableSimpleComboBox.this;

                // Get the character that has been pressed
                String sChar = String.valueOf((char) event.getKeyCode());
                
                System.out.println("sChar is "+sChar);
                // TODO - add some checking here to make sure the character is
                //        one we actually want to process

                // Make sure we have items in the store to iterate
                int numItems = combo.getStore().getCount();
                
                System.out.println("numItems is "+numItems);
                if(numItems == 0)
                    return;

                // Check each item in the store to see if it starts with our character
                for(int i = 0; i < numItems; i++)
                {
                	
                    String value = combo.getStore().getAt(i).getValue();
                    
                    System.out.println("value is "+value);
                    // If it does, select it and return
                    if(value.startsWith(sChar) || value.startsWith(sChar.toUpperCase()) || value.startsWith(sChar.toLowerCase()))
                    {
                        SelectableSimpleComboBox.this.setSimpleValue((T) value);
                        System.out.println("sChar is "+sChar);
                        return;
                    }
                }
            }
        });*/
    }

}
