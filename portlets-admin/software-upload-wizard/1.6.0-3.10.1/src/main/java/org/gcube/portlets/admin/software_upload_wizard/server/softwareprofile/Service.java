package org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class Service {

	private ServiceData serviceData = new ServiceData();
	private ArrayList<Package> packages = new ArrayList<Package>();

	public ArrayList<Package> getPackages() {
		return packages;
	}

	public ServiceData getData() {
		return serviceData;
	}

	public void setServiceData(ServiceData data) {
		this.serviceData = data;
	}

	public Package getPackage(final UUID uuid) throws Exception{
		Collection<Package> result = Collections2.filter(packages, new Predicate<Package>() {

			@Override
			public boolean apply(Package arg0) {
				if (arg0.getUuid().equals(uuid))
					return true;
				return false;
			}
		});
		
		if (result.isEmpty()) {
			throw new Exception("Unable to find package with the given Id");
		}
		return result.iterator().next();				
	}
	
	public void removePackage(final UUID uuid) throws Exception{
		Collection<Package> packagesToRemove = Collections2.filter(packages, new Predicate<Package>() {

			@Override
			public boolean apply(Package obj) {
				if (obj.getUuid().equals(uuid))
					return true;
				return false;
			}
		});
		if (packagesToRemove.size()!=1){
			throw new Exception("Invalid number of packages with given UUID was retrieved.");
		}
		getPackages().remove(packagesToRemove.iterator().next());
	}
}
