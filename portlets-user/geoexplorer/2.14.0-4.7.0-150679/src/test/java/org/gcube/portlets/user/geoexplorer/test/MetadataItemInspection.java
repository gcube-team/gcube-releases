package org.gcube.portlets.user.geoexplorer.test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.gcube.portlets.user.geoexplorer.shared.MetadataItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.identification.DataIdentificationItem;

public class MetadataItemInspection {
	
	public MetadataItemInspection(MetadataItem meta) throws IntrospectionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		
		// We use Inspector class to get a BeanInfo object
		// containing informations about the Java Bean class
		BeanInfo testBeanInfo = Introspector.getBeanInfo(MetadataItem.class);

		// Here we will print out all the discovered
		// property names of our bean

		ArrayList<String> properties = new ArrayList<String>();
		
		
		for (PropertyDescriptor propertyDescriptor : testBeanInfo.getPropertyDescriptors()) {
			
			System.out.println("Property: " + propertyDescriptor.getName());
			Class<?> propertyType = propertyDescriptor.getPropertyType();
			System.out.println("Property type: " + propertyType);
			System.out.println("\n\n");
			properties.add(propertyDescriptor.getName());
			
		}

		
//		System.out.println("default style: "+ToStringBuilder.getDefaultStyle());
		
		
		for (String prop : properties) {
			
			try {

				Object propertyValue = PropertyUtils.getProperty(meta, prop);
				
				
				if(propertyValue instanceof String){
					String value = (String) propertyValue;
					System.out.println("PropertyUtils Example property " + prop+ ": " + value);
					
				}else if(propertyValue instanceof Collection<?>){
					
					Collection<?> list = (Collection<?>) propertyValue;
					
					System.out.println("FOUND COLLECTION");
					
					for (Object object : list) {
						System.out.println("quiuiququiui");
						BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
						
						getProperties(beanInfo);
					}
					
				}else if(propertyValue instanceof DataIdentificationItem){
					
					DataIdentificationItem dataInfo = (DataIdentificationItem) propertyValue;
					System.out.println("data info "+dataInfo);
				}


			} catch (Exception ex) {
				
				System.out.println("\n");
				System.out.println("Exception on prop: "+prop);
				System.out.println(ToStringBuilder.reflectionToString(PropertyUtils.getProperty(meta, prop)));
				System.out.println("\n");
				
				
//				ex.printStackTrace();
				
//				try{
//			        Field stringListField = MetadataItem.class.getDeclaredField(prop);
//			        ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
//			        Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
//			        System.out.println("REFLECTION: "+stringListClass); // class java.lang.String.
//				}catch (Exception e) {
//					System.err.println("error reflection"+e);
//				}
		
			}

		}
		


//        Field integerListField = Test.class.getDeclaredField("integerList");
//        ParameterizedType integerListType = (ParameterizedType) integerListField.getGenericType();
//        Class<?> integerListClass = (Class<?>) integerListType.getActualTypeArguments()[0];
//        System.out.println(integerListClass); // class java.lang.Integer.
		
		
	}


	
	public static void getProperties(BeanInfo testBeanInfo){
		
		for (PropertyDescriptor propertyDescriptor : testBeanInfo.getPropertyDescriptors()) {
			
			System.out.println("Property: " + propertyDescriptor.getName());
			Class<?> propertyType = propertyDescriptor.getPropertyType();
			System.out.println("Property type: " + propertyType);
			System.out.println("\n\n");
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {

		MetadataItem meta = new GeoExplorerMetadataConverter().meta;
		
		new MetadataItemInspection(meta);
		    
		
	}
}
