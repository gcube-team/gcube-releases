/**
 *
 */
package org.gcube.data.analysis.dminvocation.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class DataMinerParam.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 4, 2018
 */
@XmlRootElement(name = "dataminer-param")
@XmlAccessorType (XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DataMinerParam {

	private String key;
	private String value;

}
