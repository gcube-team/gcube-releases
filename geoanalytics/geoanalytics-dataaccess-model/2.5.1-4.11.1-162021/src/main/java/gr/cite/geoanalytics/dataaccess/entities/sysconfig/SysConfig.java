package gr.cite.geoanalytics.dataaccess.entities.sysconfig;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import gr.cite.geoanalytics.dataaccess.entities.Identifiable;
import gr.cite.geoanalytics.dataaccess.entities.Stampable;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;

@Entity
@Table(name = "\"SysConfig\"")
public class SysConfig implements gr.cite.geoanalytics.dataaccess.entities.Entity, Identifiable, Stampable {
	public enum SysConfigClass {
		/**
		 * 0=system global configuration (taxonomy use: layer taxonomy, geography taxonomy, alternative geography taxonomy, land uses taxonomy)
		 * 1=Presentation template/Theme
		 * 2=Shape layer configuration (zoom level range)
		 * 3=Import mapping configuration'
		 */
		GLOBALCONFIG((short)0), PRESENTATION((short)1), LAYERCONFIG((short)2), ATTRIBUTEMAPPING((short)3);
		
		private final short classCode;
		
		private static final Map<Short,SysConfigClass> lookup  = new HashMap<Short,SysConfigClass>();
		 
		static {
		      for(SysConfigClass s : EnumSet.allOf(SysConfigClass.class))
		           lookup.put(s.configClassCode(), s);
		 }
		
		SysConfigClass(short configClassCode)
		{
			this.classCode = configClassCode;
		}
		
		public short configClassCode() { return classCode; }
	
		public static SysConfigClass fromConfigClassCode(short configClassCode)
		{
			return lookup.get(configClassCode);
		}
	};
	
	@Id	
	@Type(type="org.hibernate.type.PostgresUUIDType") //DEPWARN dependency to Hibernate and PostgreSQL
	@Column(name="\"SYSC_ID\"", nullable = false)
	private UUID id = null;
	
	@Lob
	@Type(type = "org.hibernate.type.TextType") //DEPWARN dependency to Hibernate and PostgreSQL (workaround for text~~bigint hibernate bug)
	@Column(name="\"SYSC_Config\"", nullable = false)
	private String config = null;
	
	@Column(name = "\"SYSC_Class\"", nullable = false)
	private short configClass = -1;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"SYSC_CreationDate\"", nullable = false)
	private Date creationDate = null;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="\"SYSC_LastUpdate\"", nullable = false)
	private Date lastUpdate = null;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"SYSC_Creator\"", nullable = false)
	private Principal creator = null;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public SysConfigClass getConfigClass() {
		return SysConfigClass.fromConfigClassCode(configClass);
	}

	public void setConfigClass(SysConfigClass configClass) {
		this.configClass = configClass.configClassCode();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Principal getCreator() {
		return creator;
	}

	public void setCreator(Principal creator) {
		this.creator = creator;
	}

	@Override
	public String toString() {
		return "SysConfig(" + "id=" + getId() + " configClass=" + getConfigClass() + " config=" + getConfig()
				+ " creation=" + getCreationDate() + " lastUpdate=" + getLastUpdate() + " creator="
				+ (creator != null ? creator.getId() : null);
	}
}
