package org.gcube.vomanagement.occi.utils;

import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.Network;
import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.Storage;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;
import org.gcube.vomanagement.occi.datamodel.cloud.VMNetwork;
import org.gcube.vomanagement.occi.datamodel.cloud.VMStorage;

public class PrettyPrinter {

  private static final String prefix = "    ";
  private String indent = "";
  private int level = 0;

  private String getIndent() {
    String out = "";
    for (int i = 0; i < level; i++) {
      out += prefix;
    }
    return out;
  }

  private void inc() {
    this.level++;
    this.indent = this.getIndent();
  }

  private void dec() {
    this.level--;
    this.indent = this.getIndent();
  }

  private String indent(String key, String label) {
    return this.indent + String.format("%-15s %s\n", key + ":", label);
  }

  private String indent(String text) {
    return this.indent + String.format("%s\n", text);
  }

  /**
   * Print OS template properties.
   */
  public String print(OSTemplate template) {
    String out = this.indent("{");
    this.inc();
    out += indent("id", template.getId());
    out += indent("os", template.getOs());
    out += indent("OS version", template.getOsVersion());
    out += indent("name", template.getName());
    out += indent("description", template.getDescription());
    out += indent("template ver", template.getVersion());
    out += indent("size", template.getDiskSize() == null ? null : template
        .getDiskSize().toString() + " Bytes");
    this.dec();
    out += this.indent("}");
    return out;
  }

  /**
   * Print resource template properties.
   * @param template the template to print.
   * @return the printed resource description 
   */
  public String print(ResourceTemplate template) {
    String out = this.indent("{");
    this.inc();
    out += indent("id", template.getId());
    out += indent("name", template.getName());
    out += indent("cores", template.getCores() + "");
    out += indent("memory", template.getMemory() == null ? null : template
        .getMemory().toString() + " Bytes");
    this.dec();
    out += this.indent("}");
    return out;
  }

  /**
   * Print VM-Network link properties and the corresponding network.
   * @param link the link to print.
   * @return the printed link description
   */
  public String print(VMNetwork link) {
    String out = this.indent("{");
    this.inc();
    out += indent("id", link.getId());
    out += indent("name", link.getName());
    out += indent("interface", link.getIface());
    out += indent("status", link.getStatus());
    out += indent("address", link.getAddress());
    out += indent("mac", link.getMac());
    if (link.getNetwork() != null) {
      out += this.indent("network", "");
      this.inc();
      out += this.print(link.getNetwork());
      this.dec();
    }
    this.dec();
    out += this.indent("}");
    return out;
  }

  /**
   * Print VM-Storage link properties along with the corresponding storage.
   * @param link the link to print.
   * @return the printed description.
   */
  public String print(VMStorage link) {
    String out = this.indent("{");
    this.inc();
    out += indent("id", link.getId());
    out += indent("name", link.getName());
    out += indent("deviceId", link.getDeviceId());
    out += indent("status", link.getStatus());
    if (link.getStorage() != null) {
      out += this.indent("storage", "");
      this.inc();
      out += this.print(link.getStorage());
      this.dec();
    }
    this.dec();
    out += this.indent("}");
    return out;
  }

  /**
   * Print network properties.
   * @param network the network to print
   * @return the printed description.
   */
  public String print(Network network) {
    String out = this.indent("{");
    this.inc();
    out += indent("id", network.getId());
    out += indent("name", network.getName());
    out += indent("description", network.getDescription());
    out += indent("status", network.getStatus());
    out += indent("allocation", network.getAllocation());
    out += indent("gateway", network.getGateway());
    out += indent("address", network.getAddress());
    out += indent("endpoint", network.getEndpoint() == null ? null : network
        .getEndpoint().toString());
    this.dec();
    out += this.indent("}");
    return out;
  }

  /**
   * Print storage properties.
   * @param storage the storage to print.
   * @return the printed storage description.
   */
  public String print(Storage storage) {
    String out = this.indent("{");
    this.inc();
    out += indent("id", storage.getId());
    out += indent("name", storage.getName());
    out += indent("summary", storage.getSummary());
    out += indent("size", storage.getSize() == null ? null : storage.getSize()
        .toString() + " Bytes");
    out += indent("status", storage.getStatus());
    out += indent("endpoint", storage.getEndpoint() == null ? null : storage
        .getEndpoint().toString());
    this.dec();
    out += this.indent("}");
    return out;
  }

  /**
   * Print VM properties along with its network, disks and corresponding links.
   * @param vm the vm to print.
   * @return the printed description.
   */
  public String print(VM vm) {
    String out = this.indent("{");
    this.inc();
    out += indent("id", vm.getId());
    out += indent("name", vm.getName());
    out += indent("hostname", vm.getHostname());
    out += indent("provider", vm.getProvider());
    out += indent("cores", vm.getCores() + "");
    out += indent("disk", vm.getDiskSize() + "");
    out += indent("memory", vm.getMemory() + "");
    out += indent("status", vm.getStatus());
    out += indent("endpoint", vm.getEndpoint() == null ? null : vm.getEndpoint().toString());

    out += indent("networks", "[");
    this.inc();
    for (VMNetwork networkLink : vm.getNetworks()) {
      out += this.print(networkLink);
    }
    this.dec();
    out += indent("]");

    out += indent("storages", "[");
    this.inc();
    for (VMStorage storageLink : vm.getStorage()) {
      out += this.print(storageLink);
    }
    this.dec();
    out += this.indent("]");
    this.dec();
    out += this.indent("}");
    return out;
  }

  /**
   * Test main method.
   * @param args unused.
   */
  public static void main(String[] args) {
    Network network = new Network();
    VMNetwork networkLink = new VMNetwork();
    networkLink.setNetwork(network);
    Storage storage = new Storage();
    VMStorage storageLink = new VMStorage();
    storageLink.setStorage(storage);
    VM vm = new VM();
    vm.addNetwork(networkLink);
    vm.addStorage(storageLink);
    vm.addStorage(storageLink);
    ResourceTemplate rt = new ResourceTemplate();
    OSTemplate ost = new OSTemplate();
    System.out.println(new PrettyPrinter().print(ost));
  }

}
