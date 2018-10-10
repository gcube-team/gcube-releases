package org.gcube.application.aquamaps.aquamapsportlet.client;

import com.gwtext.client.widgets.form.ValidationException;
import com.gwtext.client.widgets.form.Validator;

public class Validators {

	public static Validator percentageValidator=new Validator(){

		public boolean validate(String value) throws ValidationException {
			Double parsedNew=null;
			try{
				parsedNew=new Double(value);
				if((parsedNew.intValue()>-1)&&(parsedNew.intValue()<2))return true;
				else return false;				
			}catch(Exception e){return false;}			
		}
		
	};
	
	/*public static Validator integerValidator=new Validator(){
		public boolean validate(String value) throws ValidationException {
			Integer parsed=null;
			
		}
	};
	*/
}
