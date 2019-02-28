<%@include file="../init.jsp"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<%
	List<ExternalFile> theSheets = (List<ExternalFile>) renderRequest.getAttribute("thefiles");
	List<String> theSheetNames = (List<String>) renderRequest.getAttribute("theSheetNames");
	pageContext.setAttribute("theSheetNames", theSheetNames);
	pageContext.setAttribute("theSheets", theSheets);
	List<ExternalFile> theInstructions = (List<ExternalFile>) renderRequest.getAttribute("theInstructions");
	pageContext.setAttribute("theInstructions", theInstructions);
	String selectedPhase = GetterUtil.getString(portletPreferences.getValue(PFISHConstants.PHASE_PREFERENCE_ATTR_NAME, StringPool.BLANK));
	pageContext.setAttribute("selectedPhase", selectedPhase);
	GCubeTeam theFarm = (GCubeTeam) request.getAttribute("theFarm"); 
	pageContext.setAttribute("theFarm", theFarm);
%>
<c:if test="${not empty theFarm }">
<div class="row-fluid">
	<div class="span6" style="border-right: 1px solid #ccc;">
		<div style="width: 100%; text-align: left;">
			<p style="font-size: 1.2em; padding-top: 12px; padding-left: 5px;">Empty
				forms <span style="font-size: 1em;" class="hidden-desktop"> (Click on the file names to download)</span></p>
			<c:choose>
				<c:when test="${empty theSheets}">
					<p style="font-size: 1.2em; padding-top: 12px; padding-left: 5px;">The
						empty forms repository is empty at the moment. Please contact the
						Managers</p>
				</c:when>
				<c:otherwise>
					<table class="display">
						<tbody>
							<c:forEach var="file" items="${theSheets}">
								<tr>
									<portlet:resourceURL var="downloadFileURL">
										<portlet:param name="fileToDownloadId" value="${file.id}" />
									</portlet:resourceURL>
									<%
										WorkspaceItem item = (WorkspaceItem) pageContext.getAttribute("file");
													String iconHTML = IconsManager.getMDIconTextualName(item.getName()).getHtml();
									%>
									<td><%=iconHTML%></td>
									<td><span style="font-size: 1.1em; padding: 15px 10px; "><a
											href="javascript:downloadItem('${downloadFileURL}');"
											title="Download ${file.name}">${file.name}</a></span></td>
									<td><a style="margin-right: 10px; "class="btn btn-primary btn-small visible-desktop"
										href="javascript:downloadItem('${downloadFileURL}');">Download</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
		<div style="width: 100%; text-align: left;">
			<p style="font-size: 1.2em; padding-top: 12px; padding-left: 5px;">Instructions:</p>
			<c:choose>
				<c:when test="${empty theSheets}">
					<p style="font-size: 1.2em; padding-top: 12px; padding-left: 5px;">There
						are no instructions at the moment. Please contact the Managers</p>
				</c:when>
				<c:otherwise>
					<table class="display">
						<tbody>
							<c:forEach var="file" items="${theInstructions}">
								<tr>
									<portlet:resourceURL var="downloadFileURL">
										<portlet:param name="fileToDownloadId" value="${file.id}" />
									</portlet:resourceURL>
									<%
										WorkspaceItem item = (WorkspaceItem) pageContext.getAttribute("file");
													String iconHTML = IconsManager.getMDIconTextualName(item.getName()).getHtml();
									%>
									<td><%=iconHTML%></td>
									<td><span style="font-size: 1.1em; padding: 15px;"><a
											href="javascript:downloadItem('${downloadFileURL}');"
											title="Download ${file.name}">${file.name}</a></span></td>
									<td><a class="btn btn-small visible-desktop"
										href="javascript:downloadItem('${downloadFileURL}');">Download</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="span6">
	
			<portlet:resourceURL var="uploadFileURL" id="uploadFiles" />
			<script
				src="<%=request.getContextPath()%>/js/jquery.uploadfile.min.js"></script>
			<script>
	  		$(document).ready(function(){
	  		var uploadObj =	$("#multipleupload");
		  	$("#multipleupload").uploadFile({
		      url:"<%=uploadFileURL.toString()%>",
						multiple : false,
						dragDrop : true,
						sequential : true,
						sequentialCount : 10,
						maxFileSize: 50*1024*1024,
						allowedTypes:"xlsx",
						uploadErrorStr: "Upload not allowed: wrong file name, see note below.",
						showStatusAfterSuccess: false,
						fileName : "myfile",
						onSubmit:function(files) {					
							var valideFormNamesLength = valideFormNames.length;
							for (var i = 0; i < valideFormNamesLength; i++) {
								if (valideFormNames[i] == files[0]) {
									console.log("File name is OK: " + files[0]);
									return true;
								}
							}
							console.log("File name is wrong: " + files[0]);
							return false;
						},
						onSuccess : function(files, data, xhr, pd) {
							var content = JSON.parse(data);
							console.log("content.uri=" + content.urlEncoded);
							Liferay.fire('validateUserData',{
								urlEncoded: content.urlEncoded,
								fileName: content.fileName,
								selectedPhase: '${selectedPhase}'
							});
						}
					});
				});
			</script>
			<div id="multipleupload" style="width:100%; height: 100px;">Select file to attach</div>	
			<div style="font-size: 1em; padding: 15px; margin-top: 75px; width: 90%;"><strong>Note:</strong> 
			files having different name from the original will not be accepted for submission. <button class="btn btn-primary" onClick="showFileNameWarningFromDOM(true);"><strong>?</strong></button></div>
			<div class="alert alert-block" id="fileNamesExplain" style="display:none;">
	 			<button type="button" class="close" data-dismiss="alert" onClick="showFileNameWarningFromDOM(false);">&times;</button>
	  			<h4>Accepted file names are the following:</h4>
	 			<ul>
	 			<c:forEach var="file" items="${theSheets}">
					<li>${file.name}</li>
				</c:forEach>
				</ul>
			</div>
			<script>
			var valideFormNames = ${theSheetNames};
			</script>
		
	</div>
</div>
</c:if>