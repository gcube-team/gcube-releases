package org.gcube.resourcemanagement.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.informationsystem.model.impl.utils.discovery.RegistrationProvider;
import org.gcube.resourcemanagement.model.reference.entity.facet.SoftwareFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasContact;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Activates;

public class GCubeModelRegistrationProvider implements RegistrationProvider {

	@Override
	public List<Package> getPackagesToRegister() {
		List<Package> packages = new ArrayList<>();
		packages.add(SoftwareFacet.class.getPackage());
		packages.add(EService.class.getPackage());
		packages.add(Activates.class.getPackage());
		packages.add(HasContact.class.getPackage());
		return packages;
	}
	
}
