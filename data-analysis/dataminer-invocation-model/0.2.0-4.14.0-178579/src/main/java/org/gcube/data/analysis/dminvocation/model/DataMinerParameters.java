/**
 *
 */

package org.gcube.data.analysis.dminvocation.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * The Class DataMinerParameters.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 *         Dec 4, 2018
 */
@XmlRootElement(name = "dataminer-parameters")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DataMinerParameters implements Serializable {

	private static final long serialVersionUID = 8298755690515099551L;
	@XmlElement(name = "input", required = true, nillable = false)
	@JsonProperty(required = true)
	@SerializedName(value = "input")
	private DataMinerParamList input;

	@XmlElement(name = "output", required = false, nillable = false)
	@JsonProperty(required = false)
	@SerializedName(value = "output")
	private DataMinerParamList output;
}
