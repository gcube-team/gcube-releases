package org.gcube.resource.management.quota.manager.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ServicePackageDetailEntity 
 * composed:
 * id: 				identifier db
 * servicepackage: 	id service package master
 * content:			name service package 
 * 
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 */
@Entity
@Inheritance
@Table(name="ServicePackageDetail")
public class ServicePackageDetailEntity {


	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SERVICE_PACKAGE_ID", updatable = true,insertable = true )
	private ServicePackageManagerEntity servicepackage;

	@Column(nullable=false)
	protected String content;

	protected ServicePackageDetailEntity() {}

	public ServicePackageDetailEntity(ServicePackageManagerEntity servicepackage, String content) {
		super();
		this.servicepackage=servicepackage;
		this.content=content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ServicePackageManagerEntity getServicePackage() {
		return servicepackage;
	}

	public void setServicePackage(ServicePackageManagerEntity servicepackage) {
		this.servicepackage = servicepackage;
		if (!servicepackage.getListdetail().contains(this)) {
			servicepackage.getListdetail().add(this);
		}
	}

}
