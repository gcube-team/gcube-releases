package org.gcube.portlets.user.statisticalalgorithmsimporter.server.is;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */

@XmlRootElement(name = "saidescriptor")
@XmlAccessorType(XmlAccessType.FIELD)
public class SAIDescriptorJAXB {

	@XmlElement(name = "poolmanager")
	private PoolManagerJAXB poolmanager;

	@XmlElement(name = "remotetemplatefile")
	private String remotetemplatefile;

	@XmlElement(name = "ecologicalenginejarurl")
	private String ecologicalenginejarurl;

	@XmlElement(name = "ecologicalenginesmartexecutorjarurl")
	private String ecologicalenginesmartexecutorjarurl;
	
	@XmlElement(name = "availableprojectconfiguration")
	private List<AvailableProjectConfigJAXB> availableprojectconfiguration = new ArrayList<>();

	public PoolManagerJAXB getPoolmanager() {
		return poolmanager;
	}

	public void setPoolmanager(PoolManagerJAXB poolmanager) {
		this.poolmanager = poolmanager;
	}
	
	public String getEcologicalenginejarurl() {
		return ecologicalenginejarurl;
	}

	public void setEcologicalenginejarurl(String ecologicalenginejarurl) {
		this.ecologicalenginejarurl = ecologicalenginejarurl;
	}

	public String getEcologicalenginesmartexecutorjarurl() {
		return ecologicalenginesmartexecutorjarurl;
	}

	public void setEcologicalenginesmartexecutorjarurl(String ecologicalenginesmartexecutorjarurl) {
		this.ecologicalenginesmartexecutorjarurl = ecologicalenginesmartexecutorjarurl;
	}

	public String getRemotetemplatefile() {
		return remotetemplatefile;
	}

	public void setRemotetemplatefile(String remotetemplatefile) {
		this.remotetemplatefile = remotetemplatefile;
	}

	public List<AvailableProjectConfigJAXB> getAvailableprojectconfiguration() {
		return availableprojectconfiguration;
	}

	public void setAvailableprojectconfiguration(List<AvailableProjectConfigJAXB> availableprojectconfiguration) {
		this.availableprojectconfiguration = availableprojectconfiguration;
	}

	@Override
	public String toString() {
		return "SAIDescriptorJAXB [poolmanager=" + poolmanager + ", remotetemplatefile=" + remotetemplatefile
				+ ", availableprojectconfiguration=" + availableprojectconfiguration + "]";
	}

}
