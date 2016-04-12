package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.softwaregateway;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@BindingAnnotation @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME)
public @interface DefaultSG {

}
