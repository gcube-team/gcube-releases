package org.gcube.spatial.data.sdi.model.health;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ServiceHealthReport {

	private Level overallStatus;
	
	private List<Status> checkReports;

	public ServiceHealthReport(List<Status> checkReports) {
		super();
		this.checkReports = checkReports;
		overallStatus =Level.OK; 
		for(Status st:checkReports)
			if(st.getLevel().equals(Level.ERROR)) {
				overallStatus=Level.ERROR;
				break;
			}
			else if(st.getLevel().equals(Level.WARNING)&&(overallStatus.equals(Level.OK)))
				overallStatus=Level.WARNING;
		
		
	} 

	
	
}
