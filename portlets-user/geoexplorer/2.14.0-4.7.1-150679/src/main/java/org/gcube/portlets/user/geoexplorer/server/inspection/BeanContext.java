package org.gcube.portlets.user.geoexplorer.server.inspection;

import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextSupport;

/**
 * A test program that adds a bean to a beancontext, and
 * reports on various aspects of the context's membership state. 
 * This program also shows that a bean's getBeanContext() method 
 * can be called to get a reference to its enclosing context.
 */
public class BeanContext {
    private static BeanContextSupport context = new BeanContextSupport(); // The BeanContext
    private static BeanContextChildSupport bean = new BeanContextChildSupport(); // The JavaBean
  
    public static void main(String[] args) {
        report();  

        // Add the bean to the context
        System.out.println("Adding bean to context...");
        context.add(bean);

        report();
    }

    private static void report() {
        // Print out a report of the context's membership state.
        System.out.println("=============================================");

        // Is the context empty?
        System.out.println("Is the context empty? " + context.isEmpty());

        // Has the context been set for the child bean?
        boolean result = (bean.getBeanContext()!=null);
        System.out.println("Does the bean have a context yet? " + result);

        // Number of children in the context
        System.out.println("Number of children in the context: " + context.size());

        // Is the specific bean a member of the context?
        System.out.println("Is the bean a member of the context? " + context.contains(bean));

        // Equality test
        if (bean.getBeanContext() != null) {
            boolean isEqual = (bean.getBeanContext()==context); // true means both references point to the same object
            System.out.println("Contexts are the same? " + isEqual);
        }
        System.out.println("=============================================");   
    }
}

