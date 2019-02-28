<%@include file="../init.jsp"%>
<%	

Team theCompany = (Team) request.getAttribute("theCompany");
pageContext.setAttribute("companyId", theCompany.getTeamId()); 
GCubeTeam theFarm = (GCubeTeam) request.getAttribute("theFarm");
pageContext.setAttribute("farmId", theFarm.getTeamId()); 

%>
<portlet:resourceURL var="validateFileURL">
	<portlet:param name="urlEncoded" value="uri=" />
</portlet:resourceURL>
<script>
	Liferay.on('validateUserData', function(event) {
		console.log('URL Encoded:' + event.urlEncoded);
		console.log('selectedPhase:' + event.selectedPhase);
		$('div.submitted-form-validation-portlet').show();
		$('#theValidatingFileName').text(event.fileName);
		validateFile('${companyId}','${farmId}','${validateFileURL}', event.urlEncoded, event.fileName,	event.selectedPhase);
	});
</script>

<div id="validation-portlet-container" style="padding: 50px 50px 10px;">
	<div style="text-align: center;">
		<p class="lead">
			Submitted form validation (<span style="font-weight: 500;"
				id="theValidatingFileName"></span>) in progress, please wait few
			seconds ...
		</p>
		<div class="loading-div" id="vloader"></div>
	</div>
</div>
<div id="validation-result-container"
	style="padding: 50px 50px 10px; display: none;">
	<div style="text-align: center;">
		<p id="resultFeedback" class="lead"></p>
		<button id="resultFeedbackButton" style="display: none;" class="btn" onClick="location.reload();">Refresh repository</button>
	</div>
</div>