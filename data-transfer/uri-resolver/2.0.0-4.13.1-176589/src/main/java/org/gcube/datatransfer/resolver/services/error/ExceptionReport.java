/**
 *
 */
package org.gcube.datatransfer.resolver.services.error;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 22, 2018
 */
@XmlRootElement(name="ExceptionReport")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@XmlType (propOrder={"request","method","success","error", "help"})
public class ExceptionReport implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 7029237703105669823L;

	String request;
	String method;
	boolean success;
	URI help;
	ErrorReport error;
}
