/**
 * 
 */
package org.gcube.informationsystem.model.entity;

import java.io.Serializable;

import org.gcube.informationsystem.impl.entity.EntityImpl;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.exceptions.InvalidEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Basic_Concepts
 */
@Abstract
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = Entities.CLASS_PROPERTY)
@JsonDeserialize(as=EntityImpl.class)
@JsonIgnoreProperties(ignoreUnknown=true)
public interface Entity extends Serializable {
	
	public static final String NAME = "Entity"; //Entity.class.getSimpleName();
	
	public static final String HEADER_PROPERTY = "header";
	
	@ISProperty(name=HEADER_PROPERTY, mandatory=true, nullable=false)
	public Header getHeader();
	
	public void setHeader(Header header);

	public void validate() throws InvalidEntity;
	
}
