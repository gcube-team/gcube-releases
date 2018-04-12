package org.gcube.portal.social.networking.ws.inputs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Generic input bean for methods that allow to write posts
 * @author Costantino Perciante at ISTI-CNR
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore in serialization/deserialization
@ApiModel(description="A post object", value="Post")
public class PostInputBean implements Serializable{

	private static final long serialVersionUID = 5274608088828232980L;

	@JsonProperty("text")
	@NotNull(message="text cannot be null")
	@Size(min=1, message="text cannot be empty")
	@ApiModelProperty(
			example="Dear vre members, ...", 
			required=true,  
			value="The text of the post")
	private String text;

	@JsonProperty("preview_title")
	@ApiModelProperty(  
			required=false,  
			value="A preview title for the preview",
			name="preview_title")
	private String previewtitle; 

	@JsonProperty("preview_description")
	@ApiModelProperty( 
			required=false,  
			value="A preview description for the preview",
			name="preview_description")
	private String previewdescription; 

	@JsonProperty("preview_host")
	@ApiModelProperty( 
			required=false,  
			value="A preview host for the preview",
			name="preview_host")
	private String previewhost; 

	@JsonProperty("preview_url")
	@ApiModelProperty( 
			required=false,  
			value="A preview url for the preview",
			name="preview_url")
	private String previewurl;

	@JsonProperty("image_url")
	@ApiModelProperty(
			required=false,  
			value="An image url for the preview",
			name="image_url")
	private String httpimageurl;

	@JsonProperty("enable_notification")
	@ApiModelProperty(
			required=false,  
			value="If true send a notification to the other vre members about this post",
			name="enable_notification")
	private boolean enablenotification;

	@JsonProperty("params")
	@ApiModelProperty(  
			required=false,  
			value="Other parameters for the application's posts")
	private String params;

	public PostInputBean() {
		super();
	}

	public PostInputBean(String text, String previewtitle,
			String previewdescription, String previewhost, String previewurl,
			String httpimageurl, boolean enablenotification, String params) {
		super();
		this.text = text;
		this.previewtitle = previewtitle;
		this.previewdescription = previewdescription;
		this.previewhost = previewhost;
		this.previewurl = previewurl;
		this.httpimageurl = httpimageurl;
		this.enablenotification = enablenotification;
		this.params = params;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPreviewtitle() {
		return previewtitle;
	}

	public void setPreviewtitle(String previewtitle) {
		this.previewtitle = previewtitle;
	}

	public String getPreviewdescription() {
		return previewdescription;
	}

	public void setPreviewdescription(String previewdescription) {
		this.previewdescription = previewdescription;
	}

	public String getPreviewhost() {
		return previewhost;
	}

	public void setPreviewhost(String previewhost) {
		this.previewhost = previewhost;
	}

	public String getPreviewurl() {
		return previewurl;
	}

	public void setPreviewurl(String previewurl) {
		this.previewurl = previewurl;
	}

	public String getHttpimageurl() {
		return httpimageurl;
	}

	public void setHttpimageurl(String httpimageurl) {
		this.httpimageurl = httpimageurl;
	}

	public boolean isEnablenotification() {
		return enablenotification;
	}

	public void setEnablenotification(boolean enablenotification) {
		this.enablenotification = enablenotification;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "PostInputBean [text=" + text + ", previewtitle=" + previewtitle
				+ ", previewdescription=" + previewdescription
				+ ", previewhost=" + previewhost + ", previewurl=" + previewurl
				+ ", httpimageurl=" + httpimageurl + ", enablenotification="
				+ enablenotification + ", params=" + params + "]";
	}

}
