<%@include file="../init.jsp"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">

<%
	List<SubmittedForm> submittedFormsWithPublishStatus = (List<SubmittedForm>) renderRequest.getAttribute("submittedFormsWithPublishStatus");
	pageContext.setAttribute("submittedFormsWithPublishStatus", submittedFormsWithPublishStatus);
	pageContext.setAttribute("logoURL", renderRequest.getAttribute("companyLogoURL"));
	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	GCubeTeam theCompany = (GCubeTeam) request.getAttribute("theCompany");
	pageContext.setAttribute("theCompanyName", theCompany.getTeamName());
	GCubeTeam theFarm = (GCubeTeam) request.getAttribute("theFarm");
	if (theFarm != null) {
		pageContext.setAttribute("farmyName", theFarm.getTeamName());
		pageContext.setAttribute("farmId", theFarm.getTeamId());
	}
	Group group = (Group) GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request));
	long currentGroupId = group.getGroupId();
	String currentURL = PortalUtil.getCurrentURL(request);
	pageContext.setAttribute("AnalyticalToolkitPortletEndpoint", Utils.ANALYTICAL_TOOLKIT_PORTLET_ENDPOINT);
	pageContext.setAttribute("farmIdParamEncoded", Utils.maskId("farmid"));
	pageContext.setAttribute("batchTypeParamEncoded", Utils.maskId("batchtype"));
%>
<script>
function closeAndReload() {
	$("#modalSuccess").hide();
	location.reload();
}

$(function () {
	checkBatchesThreshold = function (theButton, endpoint, farmId, batchType, farmIdEncoded, batchTypeEncoded) {
        $(theButton).html('<i class="icon-cog"> checking batches, please wait ...');
        $.ajax({
    		url : endpoint,
    		type : 'POST',
    		datatype : 'json',
    		data : {
    			farmId : farmId,
    			userId : Liferay.ThemeDisplay.getUserId(),
    			batchType : batchType,
    			groupId : Liferay.ThemeDisplay.getScopeGroupId()
    		},
    		success : function(data) {
    			var content = JSON.parse(data);
    			if (content.success == "OK") {
    				location.href='${AnalyticalToolkitPortletEndpoint}?${batchTypeParamEncoded}='+batchTypeEncoded+'&${farmIdParamEncoded}='+farmIdEncoded;
    			}
    			else {
    				 $(theButton).html('<i class="icon-warning-sign">' + content.message);
       				 $(theButton).addClass("btn-warning");
       				 $(theButton).unbind('click');
       				 $(theButton).click(function(){
       					alert('You have not published and anonymsed enough batches for this farm');
       				});
    			}
    			$('#publishAndAnonymise-Button').html('Anonymise and Publish');			
    		}
    	});
        
    };
});
</script>
<portlet:resourceURL var="publishAndAnonymiseURL"></portlet:resourceURL>
<portlet:resourceURL var="checkBatchesThresholdURL"></portlet:resourceURL>
<portlet:renderURL var="maximizedState"
	windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>" />
<portlet:renderURL var="normalState"
	windowState="<%=LiferayWindowState.NORMAL.toString()%>" />
<c:set var="maximised" scope="session"
	value="${renderRequest.getWindowState().toString().equalsIgnoreCase('maximized')}" />
<c:if test="${not empty farmyName}">
	<div style="text-align: center; padding-bottom: 15px;">
		<table>
			<tr>
				<c:if test="${not empty logoURL}">
					<td>
						<div class="logo-circular"
							style="background-image: url('${logoURL}');
	 background-size: cover; "></div>
					</td>
				</c:if>
				<td><p
						style="font-size: 1.5em; padding-top: 12px; padding-left: 5px;">${theCompanyName} - ${farmyName} Private Repository Content</p></td>
			</tr>
		</table>
	</div>
	<div style="width: 100%; text-align: left;">
		<portlet:actionURL var="displayVersionsURL" name="displayVersions"></portlet:actionURL>
		<c:choose>
			<c:when test="${empty submittedFormsWithPublishStatus}">
				<p style="font-size: 1.2em; padding-top: 12px; padding-left: 5px;">The
					repository is empty at the moment. You can submit files via the dedicated pages in the VRE (Hatchery, Pre-ongrowing and Grow out)</p>
			</c:when>
			<c:otherwise>
				<form action="${displayVersionsURL}" method="post" id="form_versions" name="fm">
					<table id="example" class="display">
						<tbody>
						<thead>
							<th>Sel.</th>
							<th></th>
							<th>Name</th>
							<th><span title="UTC: Universal Time Coordinates" style="margin-left: 10px;">Uploaded
									by (UTC)</span></th>
							<th><span title="UTC: Universal Time Coordinates" style="margin-left: 10px;">Published by (UTC)</span></th>
							<th><span style="margin-left: 10px; ">Publishing Status</span></th>
						</thead>
						<c:forEach var="form" items="${submittedFormsWithPublishStatus}">
								<c:set var="buttonDisabled" value=""/>							
								<%
								SubmittedForm submittedForm = (SubmittedForm) pageContext.getAttribute("form");
								ExternalFile item = submittedForm.getFormFile();
								String iconHTML = IconsManager.getMDIconTextualName(item.getName()).getHtml();
								String fullName = Utils.getUserByUsername(item.getLastUpdatedBy()).getFullname();
								String userProfileLink = Utils.getUserProfileLink(item.getLastUpdatedBy());
								Date lastUpdated = new Date(item.getLastModificationTime().getTimeInMillis());
								pageContext.setAttribute("lastUpdated", lastUpdated);
								String fullNamePublisher = SubmittedForm.NOT_YET_PUBLISHER;
								String userProfileLinkPublisher = "";
								pageContext.setAttribute("rowBgColor", "#FFF");
								pageContext.setAttribute("buttonCheckProgressStyle", "display: none;");
								
								pageContext.setAttribute("farmIdEncoded", Utils.maskId(theFarm.getTeamId()));
								pageContext.setAttribute("batchTypeEncoded", Utils.maskId(submittedForm.getBatchType()));
								if (submittedForm.getSubmitterIdentity() != null) {
									fullNamePublisher = Utils.getUserByUsername(submittedForm.getSubmitterIdentity()).getFullname();
									pageContext.setAttribute("fullNamePublisher", fullNamePublisher);
									userProfileLinkPublisher  = Utils.getUserProfileLink(submittedForm.getSubmitterIdentity());
									if (submittedForm.getEndTimeinMillis() != PublishAnonymisedJob.EPOCH_TIME_JOB_NOTFINISHED*1000) {
										Date jobEndTime = new Date(submittedForm.getEndTimeinMillis());
										pageContext.setAttribute("jobEndTime", jobEndTime);
									}
									else {
										pageContext.setAttribute("jobEndTime", "");
										pageContext.setAttribute("fullNamePublisher", "");
										pageContext.setAttribute("rowBgColor", "rgb(228, 223, 0)");
										pageContext.setAttribute("buttonCheckProgressStyle", "display: visible;");
									}
								} else {
									pageContext.setAttribute("fullNamePublisher", ""); //reset the field
									pageContext.setAttribute("jobEndTime", "");//reset the field
								}
								%>
							<tr style="border-bottom: 1px solid #CCC; background-color: ${rowBgColor};">
								<portlet:resourceURL var="downloadFileURL">
											<portlet:param name="fileToDownloadId" value="${form.formFile.id}" />
								</portlet:resourceURL>
							
								<td><input type="radio" name="fileItem" value="${form.formFile.id}" id="${downloadFileURL}"
									style="margin: 4px 5px 10px;"></td>
								<td><%=iconHTML%></td>
								<td><a href="javascript:downloadItem('${downloadFileURL}');"
										title="Download latest version of ${form.formFile.name}">${form.formFile.name}</a></td>
								<td><div style="margin-left: 10px; text-align: left;"><a
										href="<%=userProfileLink%>" target="_blank"><%=fullName%></a></div><div
									style="margin-left: 10px; color: #666;"><fmt:formatDate 
											type="both" dateStyle="medium" timeStyle="short"
											value="${lastUpdated}" /></div></td>
								<td><div style="margin-left: 10px;">
										<aui:button style="${buttonCheckProgressStyle}" onClick="window.location.reload()" value="Check progress"/>
										<a	href="<%=userProfileLinkPublisher%>" target="_blank">${fullNamePublisher}</a></div>
										<div style="margin-left: 10px; color: #666;"><fmt:formatDate 
											type="both" dateStyle="medium" timeStyle="short"
											value="${jobEndTime}" /></div>							
								</td>
								<td><span
									style="margin-left: 10px; margin-right: 10px; color: #666;">${form.status}</span>
									<c:if test = "${form.status == 'COMPLETE'}">
										<c:choose>
											<c:when test= "${form.batchType == 'GROW_OUT_AGGREGATED' 
											or form.batchType == 'GROW_OUT_AGGREGATED_CLOSED_BATCHES' 
											or form.batchType == 'HATCHERY_AGGREGATED'}">
												<button class="btn" type="button" disabled title='Analysis not available for this Batch type'>Analyse</button>
											</c:when>
											<c:otherwise>
												<div style="display: inline;">
													<button  class="btn" type="button" 
													onClick="checkBatchesThreshold(this, '${checkBatchesThresholdURL}', '${farmId}', '${form.batchType}', '${farmIdEncoded}', '${batchTypeEncoded}')">
													Analyse
													</button>
												</div>								
											</c:otherwise>
										</c:choose>					
									</c:if>
								</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					<div style="padding: 15px 0;">
						<aui:input name="farmId" type="hidden" value="${farmId}"/>
						<button id="downloadButton" type="button" 
							class="btn"
							onClick="downloadItem(getSelectedRadioFileId());">Download
							latest version</button>
	
						<portlet:renderURL var="showVersionsURL">
							<portlet:param name="jspPage"
								value="/html/farmrepository/show_all_versions.jsp" />
						</portlet:renderURL>
						<button class="btn" type="button" onClick="validateForm()">See all versions</button>
						<button id="publishAndAnonymise-Button" class="btn btn-primary"
						 type="button" 
						 onClick="publishAndAnonymise('${publishAndAnonymiseURL}', '${farmId}', getSelectedRadioFormWorkspaceItemId())">Anonymise and Publish</button>
					</div>
				</form>
			</c:otherwise>
		</c:choose>
	</div>
<div id="modalSuccess" class="modal" style="display: none;">
	<div class="logo-circular"
							style="background-image: url('${logoURL}');
	 background-size: cover; "></div>
	<p class="lead">
		The publishing and anonymising process started correctly.<br>
		Please <a href="#" onClick="closeAndReload();">click here</a> to monitor the progress.
	</p>
</div>
<div id="modalFailed" class="modal" style="display: none;">
	<div class="logo-circular"
							style="background-image: url('${logoURL}');
	 background-size: cover; "></div>
  	<p class="lead">
		We're sorry. An error occurred in the server. <br>
		The publishing and anonymising process could not start, please report this issue.
	</p>
  <a href="#" onClick="closeAndReload();">Close</a>
</div>
</c:if>


