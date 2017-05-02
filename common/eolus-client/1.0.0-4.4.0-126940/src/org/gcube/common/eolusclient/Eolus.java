/**
 * Eolus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 14, 2006 (10:23:53 EST) WSDL2Java emitter.
 */

package org.gcube.common.eolusclient;

public interface Eolus extends java.rmi.Remote {
    public boolean VMtoTemplate(java.lang.String VMname, java.lang.String templateName) throws java.rmi.RemoteException;
    public void addScript(java.lang.String scriptName, java.lang.String scriptContnet, java.lang.String scriptDescription) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException;
    public void addUser(java.lang.String username) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.ReservedUserException;
    public void adminAddScript(java.lang.String userOwner, java.lang.String scriptName, java.lang.String scriptContnet, java.lang.String scriptDescription) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException;
    public void adminApplyScript(java.lang.String userOwner, java.lang.String scriptName, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException;
    public void adminAssignVMtoUser(java.lang.String userOwner, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void adminAssignVNettoUser(java.lang.String user, java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException;
    public void adminCreateVM(java.lang.String userOwner, java.lang.String VMtemplateName, java.lang.String VMname, java.lang.String hostname, int cores, int memSize, net.java.dev.jaxb.array.StringArray networks) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VMExistsException, org.gcube.common.eolusclient.UnknownTemplateException, org.gcube.common.eolusclient.TemplateNotReadyException;
    public void adminCreateVNet(java.lang.String VNetName, int VNetSubnet) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VNExistsException;
    public net.java.dev.jaxb.array.StringArray adminExecCMD(java.lang.String userOwner, java.lang.String cmd, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException;
    public net.java.dev.jaxb.array.StringArray adminGetAllVNetList() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public java.lang.String adminGetDescription(java.lang.String userOwner, java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownScriptException;
    public net.java.dev.jaxb.array.StringArray adminGetScriptList(java.lang.String userOwner) throws java.rmi.RemoteException;
    public net.java.dev.jaxb.array.StringArray adminGetStrayVMlist() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public java.lang.String adminGetTemplateStatus(java.lang.String userOwner, java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.UnknownTemplateException;
    public net.java.dev.jaxb.array.StringArray adminGetTemplates(java.lang.String userOwner) throws java.rmi.RemoteException;
    public net.java.dev.jaxb.array.StringArray adminGetUserVMlist(java.lang.String userOwner) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public java.lang.String adminGetVMIP(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public java.lang.String adminGetVMInfo(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public java.lang.String adminGetVMStatus(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public net.java.dev.jaxb.array.StringArray adminGetVMlist() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public java.lang.String adminGetVNetInfo(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException;
    public net.java.dev.jaxb.array.StringArray adminGetVNetList(java.lang.String user) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public void adminRemovePublicTemplate(java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.TemplateNotReadyException;
    public void adminRemoveScript(java.lang.String userOwner, java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.UnknownScriptException;
    public void adminRemoveTemplate(java.lang.String userOwner, java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.TemplateNotReadyException;
    public void adminRemoveVNet(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException;
    public void adminResumeVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void adminShutdownVM(java.lang.String VMname, boolean forceShutdown) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void adminSuspendVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void adminSyncUserScripts(java.lang.String user) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException;
    public void adminSyncUserTemplates(java.lang.String user) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException;
    public void adminTransferTemplate(java.lang.String userOwner, java.lang.String templateName, java.lang.String newUser, boolean move) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public boolean adminVMtoTemplate(java.lang.String userOwner, java.lang.String VMname, java.lang.String templateName) throws java.rmi.RemoteException;
    public void applyScript(java.lang.String scriptName, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException;
    public void createHost(java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public void createVM(java.lang.String VMtemplateName, java.lang.String VMname, int cores, int memSize, net.java.dev.jaxb.array.StringArray networks) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VMExistsException, org.gcube.common.eolusclient.UnknownTemplateException, org.gcube.common.eolusclient.TemplateNotReadyException;
    public void createVNet(java.lang.String VNetName, int VNetSubnet) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.VNExistsException;
    public void deleteHost(java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public void deleteUser(java.lang.String username) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.ReservedUserException;
    public void enableHost(java.lang.String hostname, boolean enable) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public net.java.dev.jaxb.array.StringArray execCMD(java.lang.String cmd, java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException, org.gcube.common.eolusclient.VMContactErrorException;
    public java.lang.String getDescription(java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownScriptException;
    public java.lang.String getHostInfo(java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public net.java.dev.jaxb.array.StringArray getHostList() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public net.java.dev.jaxb.array.StringArray getPublicTemplates() throws java.rmi.RemoteException;
    public net.java.dev.jaxb.array.StringArray getScriptList() throws java.rmi.RemoteException;
    public java.lang.String getTemplateStatus(java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.UnknownTemplateException;
    public net.java.dev.jaxb.array.StringArray getTemplates() throws java.rmi.RemoteException;
    public net.java.dev.jaxb.array.StringArray getUsers() throws java.rmi.RemoteException;
    public java.lang.String getVMIP(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public java.lang.String getVMInfo(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public java.lang.String getVMStatus(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public net.java.dev.jaxb.array.StringArray getVMlist() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public java.lang.String getVNetInfo(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException;
    public net.java.dev.jaxb.array.StringArray getVNetList() throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public void makeTemplatePublic(java.lang.String templateName, java.lang.String templatePublicName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownTemplateException, org.gcube.common.eolusclient.TemplateNotReadyException;
    public void migrateVM(java.lang.String VMName, java.lang.String hostname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void removeScript(java.lang.String scriptName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.UnknownScriptException;
    public void removeTemplate(java.lang.String templateName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.DirectoryException, org.gcube.common.eolusclient.MultipleTemplatesException, org.gcube.common.eolusclient.TemplateNotReadyException;
    public void removeVNet(java.lang.String VNetName) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVNException;
    public void resumeVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void setConfigurationParameter(java.lang.String key, java.lang.String value) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
    public void shutdownVM(java.lang.String VMname, boolean forceShutdown) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void suspendVM(java.lang.String VMname) throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException, org.gcube.common.eolusclient.InternalErrorException, org.gcube.common.eolusclient.UnknownVMException;
    public void syncScripts() throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException;
    public void syncTemplates() throws java.rmi.RemoteException, org.gcube.common.eolusclient.UnknownUserException;
    public void transferTemplate(java.lang.String templateName, java.lang.String newUserOwner, boolean move) throws java.rmi.RemoteException, org.gcube.common.eolusclient.InternalErrorException;
}
