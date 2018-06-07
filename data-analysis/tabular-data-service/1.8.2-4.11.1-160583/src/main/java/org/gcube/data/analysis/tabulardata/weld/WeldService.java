package org.gcube.data.analysis.tabulardata.weld;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sun.xml.ws.api.server.InstanceResolverAnnotation;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
@InstanceResolverAnnotation(WeldResolver.class)
public @interface WeldService {

}
