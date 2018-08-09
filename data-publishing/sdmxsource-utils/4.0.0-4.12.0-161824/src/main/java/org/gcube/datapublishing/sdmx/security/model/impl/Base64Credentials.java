package org.gcube.datapublishing.sdmx.security.model.impl;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;

import org.gcube.datapublishing.sdmx.security.model.Credentials;


public class Base64Credentials implements Credentials {

	private String 	username,
					password;
	
	public static String CREDENTIAL_TYPE = "BASE64";
	
	public Base64Credentials ()
	{
		this (null,null);
	}
	
	public Base64Credentials (String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	public void setCredentials (String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	public String getBase64Encoding ()
	{
		if (this.username != null && this.password != null)
		{
			StringBuilder value = new StringBuilder();
			value.append(this.username).append(":").append(this.password);
			
			try
			{
				return DatatypeConverter.printBase64Binary(value.toString().getBytes("UTF-8"));

			} catch (UnsupportedEncodingException e)
			{
				return null;
			}
		}
		else return null;
	}

	@Override
	public String getType() {

		return CREDENTIAL_TYPE;
	}
	
}
