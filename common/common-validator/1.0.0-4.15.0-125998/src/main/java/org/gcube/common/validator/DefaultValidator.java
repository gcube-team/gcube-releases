package org.gcube.common.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.validator.annotations.FieldValidator;
import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.ValidityChecker;
import org.gcube.common.validator.context.ValidatorContext;


public class DefaultValidator implements Validator{

	public List<ValidationError> validate(Object obj){
		try{
			return validateObject(obj, null);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<ValidationError> validateObject(Object obj, Object parent) throws Exception{
		List<ValidationError> errors = new ArrayList<ValidationError>();
		Class<?> _clazz = obj.getClass();
		for (Field field:Util.getAllFields(_clazz)){
			ValidatorContext context = new ValidatorContext(parent, field, obj);
			validateField(context);
			errors.addAll(context.errors());
		}
		return errors;
	}
	
	private void validateField(ValidatorContext context) throws Exception{
		Field field = context.field();
		field.setAccessible(true);
		Object object = context.value();
		for (Annotation annotation:field.getAnnotations()){
			if (annotation.annotationType().isAnnotationPresent(ValidityChecker.class)){
				Class<? extends FieldValidator<?>> managedClass = ((ValidityChecker)annotation.annotationType().getAnnotation(ValidityChecker.class)).managed();
				FieldValidator<?> validator = managedClass.newInstance();
				Object toCheck = field.get(object);
				if (!validator.isValid(toCheck))
					context.addError(validator.getErrorSuffix());
			}
		}
		
		if(field.isAnnotationPresent(IsValid.class) && field.get(object)!=null){
			if (field.getType().isArray() ){
				for (Object o : (Object[])field.get(object))
					context.addErrors(validateObject(o, object));
				
			}else if ( field.get(object) instanceof Iterable<?>){
				for (Object o: (Iterable<?>)field.get(object)){
					context.addErrors(validateObject(o, object));
				}
			} else if (field.get(object) instanceof Map<?,?>){
				for (Entry<?, ?> entry: ((Map<?,?>)field.get(object)).entrySet())
					context.addErrors(validateObject(entry.getValue(), object));
			} else if (!field.getType().isPrimitive())
				context.addErrors(validateObject(field.get(object), object));
			
		}

	}
	
}
