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

import org.gcube.data.analysis.dminvocation.ActionType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * The Class DataMinerInvocation.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 4, 2018
 */
@XmlRootElement(name = "dataminer-invocation")
@XmlAccessorType (XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DataMinerInvocation implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 8506240125306265159L;


	@XmlElement(name = "operator-id", required=true, nillable=false)
	@JsonProperty(required = true)
	@SerializedName(value="operator-id")
	private String operatorId;

	@XmlElement(name = "action", required=true, nillable=false)
	@JsonProperty(required = true)
	@SerializedName(value="action")
	private ActionType actionType = ActionType.RUN;

	@XmlElement(name = "parameters", required=false, nillable=false)
	@JsonProperty(required = false)
	@SerializedName(value="parameters")
	private DataMinerParameters parameters;
}
