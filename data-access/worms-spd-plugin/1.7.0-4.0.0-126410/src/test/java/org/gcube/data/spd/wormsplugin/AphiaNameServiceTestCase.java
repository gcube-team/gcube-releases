/**
 * AphiaNameServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 16, 2004 (12:19:44 EST) WSDL2Java emitter.
 */

package org.gcube.data.spd.wormsplugin;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import javax.xml.rpc.ServiceException;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import static org.junit.Assert.*;

public class AphiaNameServiceTestCase {
   
	
	public AphiaNameServiceTestCase(java.lang.String name) throws ServiceException {
                
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'WoRMS' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

//		System.out.println(resources.size());
		
		WormsPlugin a = new WormsPlugin();
		if(resources.size() != 0) {	   
			try {
				a.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
				
		WormsPlugin.binding = (aphia.v1_0.AphiaNameServiceBindingStub)
				new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
		
    }

    public void testAphiaNameServicePortWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePortAddress() + "&wsdl=1");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new aphia.v1_0.AphiaNameServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1AphiaNameServicePortGetAphiaID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        int value = -3;
        value = binding.getAphiaID("solea solea", true);
        // TBD - validate results
    }

    public void test2AphiaNameServicePortGetAphiaRecords() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.AphiaRecord[] value = null;
        value = binding.getAphiaRecords(new java.lang.String(), true, true, true, 0);
        // TBD - validate results
    }

    public void test3AphiaNameServicePortGetAphiaNameByID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        java.lang.String value = null;
        value = binding.getAphiaNameByID(0);
        // TBD - validate results
    }

    public void test4AphiaNameServicePortGetAphiaRecordByID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.AphiaRecord value = null;
        value = binding.getAphiaRecordByID(0);
        // TBD - validate results
    }

    public void test5AphiaNameServicePortGetAphiaRecordByTSN() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.AphiaRecord value = null;
        value = binding.getAphiaRecordByTSN(0);
        // TBD - validate results
    }

    public void test6AphiaNameServicePortGetAphiaRecordsByNames() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.AphiaRecord[][] value = null;
        value = binding.getAphiaRecordsByNames(new java.lang.String[0], true, true, true);
        // TBD - validate results
    }

    public void test7AphiaNameServicePortGetAphiaRecordsByVernacular() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.AphiaRecord[] value = null;
        value = binding.getAphiaRecordsByVernacular(new java.lang.String(), true, 0);
        // TBD - validate results
    }

    public void test8AphiaNameServicePortGetAphiaClassificationByID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.Classification value = null;
        value = binding.getAphiaClassificationByID(0);
        // TBD - validate results
    }

    public void test9AphiaNameServicePortGetSourcesByAphiaID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.Source[] value = null;
        value = binding.getSourcesByAphiaID(0);
        // TBD - validate results
    }

    public void test10AphiaNameServicePortGetAphiaSynonymsByID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.AphiaRecord[] value = null;
        value = binding.getAphiaSynonymsByID(0);
        // TBD - validate results
    }

    public void test11AphiaNameServicePortGetAphiaVernacularsByID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.Vernacular[] value = null;
        value = binding.getAphiaVernacularsByID(0);
        // TBD - validate results
    }

    public void test12AphiaNameServicePortGetAphiaChildrenByID() throws Exception {
        aphia.v1_0.AphiaNameServiceBindingStub binding;
        try {
            binding = (aphia.v1_0.AphiaNameServiceBindingStub)
                          new aphia.v1_0.AphiaNameServiceLocator().getAphiaNameServicePort();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        aphia.v1_0.AphiaRecord[] value = null;
        value = binding.getAphiaChildrenByID(0, 0);
        // TBD - validate results
    }

}
