package org.gcube.vomanagement.usermanagement.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class GCubeMembershipRequest implements Serializable {



	private long membershipRequestId;
	private long groupId;
	private GCubeUser requestingUser;
	private Date createDate;
	private String userComment;
	private String managerReplyComment;
	private GCubeUser replierUser;
	private Date replyDate;
	private MembershipRequestStatus status;

	public GCubeMembershipRequest() {
		super();
	}
	

	public GCubeMembershipRequest(long membershipRequestId, long groupId,
			GCubeUser requestingUser, Date createDate, String userComment,
			String managerReplyComment, GCubeUser replierUser, Date replyDate,
			MembershipRequestStatus status) {
		super();
		this.membershipRequestId = membershipRequestId;
		this.groupId = groupId;
		this.requestingUser = requestingUser;
		this.createDate = createDate;
		this.userComment = userComment;
		this.managerReplyComment = managerReplyComment;
		this.replierUser = replierUser;
		this.replyDate = replyDate;
		this.status = status;
	}





	/**
	 * Returns the membership request ID of this membership request.
	 *
	 * @return the membership request ID of this membership request
	 */
	public long getMembershipRequestId() {
		return membershipRequestId;
	}

	/**
	 * Sets the membership request ID of this membership request.
	 *
	 * @param membershipRequestId the membership request ID of this membership request
	 */
	public void setMembershipRequestId(long membershipRequestId) {
		this.membershipRequestId = membershipRequestId;
	}
	/**
	 * Returns the group ID of this membership request.
	 *
	 * @return the group ID of this membership request
	 */
	public long getGroupId() {
		return groupId;
	}

	/**
	 * Sets the group ID of this membership request.
	 *
	 * @param groupId the group ID of this membership request
	 */
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}



	/**
	 * Returns the create date of this membership request.
	 *
	 * @return the create date of this membership request
	 */
	public Date getCreateDate() {
		return this.createDate;
	}

	/**
	 * Sets the create date of this membership request.
	 *
	 * @param createDate the create date of this membership request
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * Returns the comment of this membership request.
	 *
	 * @return the comment of this membership request
	 */
	public String getComment() {
		return userComment;
	}

	/**
	 * Sets the comments of this membership request.
	 *
	 * @param userComment the comments of this membership request
	 */
	public void setComment(String userComment) {
		this.userComment = userComment;
	}

	/**
	 * Returns the reply comments of this membership request.
	 *
	 * @return the reply comments of this membership request
	 */
	public String getManagerReplyComment() {
		return managerReplyComment;
	}

	/**
	 * Sets the reply comments of this membership request.
	 *
	 * @param replyComment the reply comments of this membership request
	 */
	public void setReplyComment(String replyComment) {
		this.managerReplyComment = replyComment;
	}

	/**
	 * Returns the reply date of this membership request.
	 *
	 * @return the reply date of this membership request
	 */
	public Date getReplyDate() {
		return replyDate;
	}

	/**
	 * Sets the reply date of this membership request.
	 *
	 * @param replyDate the reply date of this membership request
	 */
	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}

	/**
	 * Returns the status of this membership request.
	 *
	 * @return the status of this membership request
	 */
	//note for developer
	public MembershipRequestStatus getStatus() {
		return this.status;
	}

	/**
	 * Sets the status of this membership request.
	 *
	 * @param statusId the status of this membership request
	 */
	public void setStatusId(MembershipRequestStatus status) {
		this.status = status;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public void setManagerReplyComment(String managerReplyComment) {
		this.managerReplyComment = managerReplyComment;
	}

	public void setStatus(MembershipRequestStatus status) {
		this.status = status;
	}


	public GCubeUser getRequestingUser() {
		return requestingUser;
	}


	public void setRequestingUser(GCubeUser requestingUser) {
		this.requestingUser = requestingUser;
	}


	public GCubeUser getReplierUser() {
		return replierUser;
	}


	public void setReplierUser(GCubeUser replierUser) {
		this.replierUser = replierUser;
	}


	@Override
	public String toString() {
		return "GCubeMembershipRequest [membershipRequestId="
				+ membershipRequestId + ", groupId=" + groupId
				+ ", requestingUser=" + requestingUser + ", createDate="
				+ createDate + ", userComment=" + userComment
				+ ", managerReplyComment=" + managerReplyComment
				+ ", replierUser=" + replierUser + ", replyDate=" + replyDate
				+ ", status=" + status + "]";
	}
	
	
	
}
