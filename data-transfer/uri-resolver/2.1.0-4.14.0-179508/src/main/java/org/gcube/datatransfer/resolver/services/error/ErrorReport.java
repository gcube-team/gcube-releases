/**
 *
 */
package org.gcube.datatransfer.resolver.services.error;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;


@XmlRootElement(name="error")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@XmlType (propOrder={"name","httpErrorCode","message","thrownBy"})
public class ErrorReport implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -3114830757465862418L;
	private Integer httpErrorCode;
    private String name;
    private String message;
    private String thrownBy;

}
