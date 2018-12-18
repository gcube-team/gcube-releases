<%@include file="../init.jsp"%>
<%
	GCubeTeam asso = (GCubeTeam) renderRequest.getAttribute("theAssociation");
	pageContext.setAttribute("theAssociation", asso);
	String associationLogoURL = (String) renderRequest.getAttribute("associationLogoURL");
	pageContext.setAttribute("logoURL", associationLogoURL);

	LinkedHashMap<Company, List<Farm>> associationCompanies = (LinkedHashMap<Company, List<Farm>>) renderRequest
			.getAttribute("associationCompanies");
	pageContext.setAttribute("associationCompanies", associationCompanies);
%>
<div style="text-align: left; width: 100%;">
	<div style="text-align: right;">
		<div>
			<c:if test="${not empty logoURL}">
				<img src="${logoURL}"
					style="width: 48px; float: right; padding: 10px;">
			</c:if>
			<h1 style="color: #317eac; padding-left: 5px;">${theAssociation.teamName}</h1>
		</div>
		<div style="margin-top: -15px;">
			<p class="lead" style="color: #317eac; padding-left: 5px;">${theAssociation.description}</p>
		</div>
	</div>
	<div style="width: 100%; text-align: left;">
		<c:forEach var="company" items="${associationCompanies}">
			<div
				style="font-size: 1.2em; margin-top: 10px; padding: 5px; border-bottom: 1px solid #CCC;">
				<table id="theTable" class="display">
					<tbody>
						<tr>
							<td><span style="font-size: 1.5em; color: black;">${company.key.name}</span></td>
							<td><img src="${company.key.imageUrl}"
								style="width: 32px; padding-left: 10px;"></td>
						</tr>
					</tbody>
				</table>
			</div>
			<c:if test="${not empty company.value}">
				<table id="theFarmsTable" class="paleBlueRows">
					<thead>
						<tr>
							<th>Farm name</th>
							<th>Farm location</th>
							<th>Last form submitted</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="farm" items="${company.value}">
							<tr>
								<td><b>${farm.name}</b></td>
								<td>${farm.location}</td>
								<td><fmt:formatDate type="date" dateStyle = "long"
										value="${farm.dateLastActivity}" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</c:forEach>
	</div>
</div>