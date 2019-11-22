package org.gcube.resource.management.quota.manager.service;

import javax.ws.rs.Path;



/***
 * 
 * QuotaPackageManager
 * Service for manager package (insert,list, and remove)
 * 
 * @author alessandro.pieve@isti.cnr.it
 *
 */
//@Path("quotaPackageManager")
public class QuotaPackageManager {

	/**
	 * insert a package 
	 * quotaPackageManager/insert
	 * content-type:text/xml
	 * Request Method: POST
	 *<servicepackage>
	 *		<name>Pacchetto 2</name>
	 *		<servicesPackagesDetail>
	 *			<content>Nome servizio 2-1</content>
	 *		</servicesPackagesDetail>
	 *		<servicesPackagesDetail>
	 *			<content>Nome servizio 2-2</content>
	 *		</servicesPackagesDetail>
	 *</servicepackage>
	 * @param servicePackages
	 * @return
	 */
	/*
	@POST
	@Consumes(MediaType.TEXT_XML)
	@Path("/insert/")
	public Response insertPackage(ServicePackage servicePackages) {
		try{
			log.info("insert service packages: {}", servicePackages.getServicePackagesAsString());
			quotaPackagePersistence.addPackage(servicePackages);

		}catch(Exception e){
			log.error("error service packages",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error insert service packages: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
		return Response.ok().build();
	}		
*/
	
	
	
	/**
	 * list of package
	 * example:
	 * <servicepackages>
	 *		<servicepackage>
	 *			<id>4</id>
	 *			<name>Pacchetto 1</name>
	 *			<servicesPackagesDetail>
	 *				<id>9</id>
	 *				<idServicesPackage>4</idServicesPackage>
	 *				<content>Nome servizio 2</content>
	 *			</servicesPackagesDetail>
	 *			<servicesPackagesDetail>
	 *				<id>10</id>
	 *				<idServicesPackage>4</idServicesPackage>
	 *				<content>Nome servizio 1</content>
	 *			</servicesPackagesDetail>
	 *		</servicepackage>
	 *</servicepackages>
	 * @return
	 */
	/*
	@GET	
	@Produces(MediaType.APPLICATION_XML)
	@Path("/")
	public ServicePackages getPackages() {
		try{
			log.info("retrieving packages {}");

			return new ServicePackages(quotaPackagePersistence.getPackages());

		}catch(Exception e){
			log.error("error retrieving list packages ", e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error retrieving list packages: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
	}
	*/
	
	/*
	@GET
	@Path("/detail/{servicepackage_id}")	
	@Produces(MediaType.TEXT_XML)
	public ServicePackage getDetailPackage(@NotNull @PathParam("servicepackage_id") long servicepackage_id) {
		try{
			log.info("retrieving detail package {}",servicepackage_id);
			ServicePackage servicePackage = quotaPackagePersistence.getPackage(servicepackage_id);
			log.info("ServicePackage getDetailPackage:{}",servicePackage);
			return servicePackage;
		}catch(Exception e){
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error : "+e.getMessage()).type(MediaType.TEXT_PLAIN).build()); 
		}
	}
*/
	/**
	 * Remove a package
	 * @param servicepackagedetailId
	 * @return
	 */
	/*
	@DELETE		
	@Path("/remove/{servicepackage_id}")
	public Response removePackage(@Null @PathParam("servicepackage_id") long servicepackageId) {
		try{
			log.info("removing Package with id {}", servicepackageId);
			quotaPackagePersistence.removePackage(servicepackageId);
		}catch(Exception e){
			log.error("error removing package", e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error removing package: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
		return Response.ok().build();
	}
*/

	/**
	 * Remove a detail package
	 * @param servicepackagedetailId
	 * @return
	 */
	/*
	@DELETE		
	@Path("/removedetail/{servicepackagedetail_id}")
	public Response removePackageDetail(@Null @PathParam("servicepackagedetail_id") long servicepackagedetailId) {
		try{
			log.info("removing Package detail with id {}", servicepackagedetailId);
			quotaPackagePersistence.removePackageDetail(servicepackagedetailId);
		}catch(Exception e){
			log.error("error removing package detail", e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error removing package detail: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
		return Response.ok().build();
	}
	*/
	
	
	
}
