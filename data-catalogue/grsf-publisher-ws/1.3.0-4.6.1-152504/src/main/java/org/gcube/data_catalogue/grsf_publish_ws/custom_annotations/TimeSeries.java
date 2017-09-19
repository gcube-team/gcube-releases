package org.gcube.data_catalogue.grsf_publish_ws.custom_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TimeSeries annotation. A list annotated with such field is transformed in a catalogue resource.
 * Its elements should look like as (year, value1, value2 ...).
 * The list is put in a csv file which is in turn uploaded on a shared vre folder, to replace the url
 * that the catalogue creates for its inner resources.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TimeSeries {

}
