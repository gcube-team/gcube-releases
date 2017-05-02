package gr.cite.geoanalytics.security;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

public class GeoanalyticsAuthenticatedUser extends User {
	private static final long serialVersionUID = 4264742519887372537L;
	
	private UUID vreUsrId = null;
	private UUID tenantId = null;

	public UUID getVreUsrId() {
		return vreUsrId;
	}

	public GeoanalyticsAuthenticatedUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities, UUID vreUsrId, UUID tenantId) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.tenantId = tenantId;
		this.vreUsrId = vreUsrId;
	}

	public GeoanalyticsAuthenticatedUser(String username, String password,
			Collection<? extends GrantedAuthority> authorities, UUID vreUsrId, UUID tenantId) {
		super(username, password, authorities);
		this.tenantId = tenantId;
		this.vreUsrId = vreUsrId;
	}

	public void setVreUsrId(UUID vreUsrId) {
		this.vreUsrId = vreUsrId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
		result = prime * result + ((vreUsrId == null) ? 0 : vreUsrId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoanalyticsAuthenticatedUser other = (GeoanalyticsAuthenticatedUser) obj;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		if (vreUsrId == null) {
			if (other.vreUsrId != null)
				return false;
		} else if (!vreUsrId.equals(other.vreUsrId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GeoanalyticsAuthenticatedUser [vreUsrId=" + vreUsrId + ", tenantName=" + tenantId + "]";
	}

}
