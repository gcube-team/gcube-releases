package org.gcube.portal.social.networking.ws.inputs;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic input bean for methods that allow to write messages
 * @author Costantino Perciante at ISTI-CNR
 */
@JsonIgnoreProperties(ignoreUnknown = true) // ignore in serialization/deserialization
@ApiModel(description="A message object", value="Message")
public class MessageInputBean implements Serializable {

	private static final long serialVersionUID = -1317811686036127456L;

	@JsonProperty("sender")
	@ApiModelProperty( 
			example="andrea.rossi", 
			required=false, 
			hidden=true, // do not show this information
			value="The sender of the message. If not specified is the gcube-token's owner")
	private String sender;

	@JsonProperty("body")
	@NotNull(message="body cannot be missing")
	@Size(min=1, message="body cannot be empty")
	@ApiModelProperty(
			example="This is the body of the mail ...", 
			required= true, 
			value="The body of the message")
	private String body;

	@JsonProperty("subject")
	@NotNull(message="subject cannot be missing")
	@Size(min=1, message="subject cannot be empty")
	@ApiModelProperty(
			example="This is the subject of the mail ...", 
			required= true, 
			value="The subject of the message")
	private String subject;

	@JsonProperty("recipients")
	@NotNull(message="recipients cannot be missing")
	@Size(min=1, message="at least a recipient is needed")
	@Valid // validate recursively
	@ApiModelProperty( 
			required= true, 
			value="The recipients of this message. At least one is needed")
	private ArrayList<Recipient> recipients;

	public MessageInputBean() {
		super();
	}

	public MessageInputBean(String sender, String body, String subject,
			ArrayList<Recipient> recipients) {
		super();
		this.sender = sender;
		this.body = body;
		this.subject = subject;
		this.recipients = recipients;
	}

	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public ArrayList<Recipient> getRecipients() {
		return recipients;
	}
	public void setRecipients(ArrayList<Recipient> recipients) {
		this.recipients = recipients;
	}

	@Override
	public String toString() {
		return "MessageInputBean [sender=" + sender + ", body=" + body
				+ ", subject=" + subject + ", recipients=" + recipients + "]";
	}

}
