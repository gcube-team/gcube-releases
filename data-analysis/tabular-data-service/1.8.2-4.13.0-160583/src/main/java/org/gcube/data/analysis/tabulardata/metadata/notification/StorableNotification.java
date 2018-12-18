package org.gcube.data.analysis.tabulardata.metadata.notification;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.AffectedObject;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.UpdateEvent;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;

@Entity
@NamedQueries({
	@NamedQuery(name="Notification.getByTr", 
			query="SELECT DISTINCT n FROM StorableNotification n LEFT JOIN n.tabularResource str LEFT JOIN str.sharedWith s " +
					" WHERE str.id = :trid and ((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes"
		+ " ORDER BY n.date DESC"),
	@NamedQuery(name="Notification.getByUser", 
	query="SELECT DISTINCT n FROM StorableNotification n JOIN n.tabularResource str LEFT JOIN str.sharedWith s " +
					" WHERE ((CONCAT('u(',:user,')') = s) or (CONCAT('g(',:group,')') = s) or str.owner = :user) and :scope MEMBER OF str.scopes "
					/*"and str.hidden=false "*/
		+ " ORDER BY n.date DESC"),
})
public class StorableNotification{
		
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	
	@ManyToOne
	private StorableTabularResource tabularResource;

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar date;
	
	private AffectedObject affectedObject;
	
	private UpdateEvent updateEvent;
	
	private NotificationObject notificationObject;	
		
	
	
	protected StorableNotification() {
		super();
	}

	public StorableNotification(StorableTabularResource tabularResource,
			AffectedObject affectedObject, UpdateEvent updateEvent,
			NotificationObject notificationObject) {
		super();
		this.tabularResource = tabularResource;
		this.affectedObject = affectedObject;
		this.updateEvent = updateEvent;
		this.notificationObject = notificationObject;
		this.date = Calendar.getInstance();
	}

	public long getId() {
		return id;
	}

	public Calendar getDate() {
		return date;
	}

	public AffectedObject getAffectedObject() {
		return affectedObject;
	}

	public UpdateEvent getUpdateEvent() {
		return updateEvent;
	}

	public NotificationObject getNotificationObject() {
		return notificationObject;
	}

	public StorableTabularResource getTabularResource() {
		return tabularResource;
	}

	@Override
	public String toString() {
		return "Notification ("+notificationObject.getHumanReadableDescription()+") [id=" + id + ", tabularResource=" + tabularResource.getId()
				+ ", date=" + date + ", affectedObject=" + affectedObject
				+ ", updateEvent=" + updateEvent + "]";
	}
	
}
