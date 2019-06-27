/**
 *
 */
package org.gcube.data.analysis.dminvocation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;


/**
 * The Class DataMinerParamList.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 18, 2018
 */
@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name = "dataminer-param-list")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DataMinerParamList implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 623106816934429133L;

	@XmlElement(name = "param", required=true, nillable=false)
	@JsonProperty(required = true)
	@SerializedName(value="param")
	private List<DataMinerParam> listParam = new ArrayList<>();
}
