package org.gcube.resource.management.quota.manager.persistence.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ServicePackageManagerEntity 
 * composed:
 * id: 				identifier db
 * name: 			name of package
 * listdetail:		list detail of service contain this package 
 * 
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 */
@Entity
@Inheritance
@NamedQueries({
	@NamedQuery(name="ServicePackage.all", query="SELECT servicePackagesManager FROM ServicePackageManagerEntity servicePackagesManager"),
	@NamedQuery(name="ServicePackage.getById", query="SELECT servicePackagesManager FROM ServicePackageManagerEntity servicePackagesManager WHERE  "
			+ " servicePackagesManager.id=:id")
})
@Table(name="ServicePackage")
public class ServicePackageManagerEntity {


	@Column (name="PACK_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;

	@Column(unique=true)
	protected String name;

	@OneToMany(cascade = {CascadeType.ALL},mappedBy="servicepackage")
	private List<ServicePackageDetailEntity> listdetail;


	public void addPackageDetail(ServicePackageDetailEntity listdetail) {
		this.listdetail.add(listdetail);
		if (listdetail.getServicePackage() != this) {
			listdetail.setServicePackage(this);
		}
	}

	public ServicePackageManagerEntity(){
		listdetail = new ArrayList<ServicePackageDetailEntity>();
	}

	
	public ServicePackageManagerEntity(String name) {		
		super();
		this.name = name;
		
	}
	public ServicePackageManagerEntity(String name, List<ServicePackageDetailEntity> listdetail) {		
		super();
		this.name = name;
		this.listdetail = listdetail;
	}

	//	protected ServicePackageManagerEntity() {}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ServicePackageDetailEntity> getListdetail() {
		return listdetail;
	}

	public void setListdetail(List<ServicePackageDetailEntity> listdetail) {
		this.listdetail = listdetail;
	}
}
