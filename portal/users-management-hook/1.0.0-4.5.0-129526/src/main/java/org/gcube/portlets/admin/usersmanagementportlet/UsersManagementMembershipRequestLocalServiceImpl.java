/**
 * 
 */
package org.gcube.portlets.admin.usersmanagementportlet;

import java.util.Date;

import com.liferay.portal.MembershipRequestCommentsException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.MembershipRequest;
import com.liferay.portal.model.MembershipRequestConstants;
import com.liferay.portal.service.MembershipRequestLocalService;
import com.liferay.portal.service.MembershipRequestLocalServiceUtil;
import com.liferay.portal.service.MembershipRequestLocalServiceWrapper;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * @author vfloros
 *
 */
public class UsersManagementMembershipRequestLocalServiceImpl extends MembershipRequestLocalServiceWrapper{

	public UsersManagementMembershipRequestLocalServiceImpl(
			MembershipRequestLocalService membershipRequestLocalService) {
		super(membershipRequestLocalService);
	}
	
	@Override
	public void updateStatus(
			long replierUserId, long membershipRequestId, String replyComments,
			int statusId, boolean addUserToGroup, ServiceContext serviceContext)
		throws PortalException, SystemException {

		validate(replyComments);

		MembershipRequest membershipRequest =
			MembershipRequestLocalServiceUtil.getMembershipRequest(membershipRequestId);

		membershipRequest.setReplyComments(replyComments);
		membershipRequest.setReplyDate(new Date());

		if (replierUserId != 0) {
			membershipRequest.setReplierUserId(replierUserId);
		}
		else {
			long defaultUserId = UserLocalServiceUtil.getDefaultUserId(
				membershipRequest.getCompanyId());

			membershipRequest.setReplierUserId(defaultUserId);
		}

		membershipRequest.setStatusId(statusId);

		MembershipRequestLocalServiceUtil.updateMembershipRequest(membershipRequest);

		if ((statusId == MembershipRequestConstants.STATUS_APPROVED) &&
			addUserToGroup) {

			long[] addUserIds = new long[] {membershipRequest.getUserId()};

			UserLocalServiceUtil.addGroupUsers(
				membershipRequest.getGroupId(), addUserIds);
		}
		
//		if (replierUserId != 0) {
//			notify(
//				membershipRequest.getUserId(), membershipRequest,
//				PropsKeys.SITES_EMAIL_MEMBERSHIP_REPLY_SUBJECT,
//				PropsKeys.SITES_EMAIL_MEMBERSHIP_REPLY_BODY, serviceContext);
//		}
	}

	protected void validate(String comments) throws PortalException {
		if (Validator.isNull(comments) || Validator.isNumber(comments)) {
			throw new MembershipRequestCommentsException();
		}
	}

}
