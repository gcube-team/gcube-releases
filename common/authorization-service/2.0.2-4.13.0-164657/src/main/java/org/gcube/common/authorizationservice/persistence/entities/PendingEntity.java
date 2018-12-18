package org.gcube.common.authorizationservice.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class PendingEntity {

	@Column
	@Id
	String containerId;
	
	@Column(nullable=false)
	private String context;
	
	@Column(nullable=false)
	private String key;

	@Column(nullable=false)
	private String hostname;
	
	@Column(nullable=false)
	private int port;
	
	@Column(nullable=false)
	private String ip;
	
}
