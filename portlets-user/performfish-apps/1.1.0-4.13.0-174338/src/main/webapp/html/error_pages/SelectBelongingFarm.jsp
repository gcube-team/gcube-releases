<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@include file="../init.jsp"%>

<%
	List<GCubeTeam> theFarms = (List<GCubeTeam>) request.getAttribute("theFarms");
	pageContext.setAttribute("theFarms", theFarms);
%>
<script>
	function setFarm(theFarmid) {
		window.location.search = 'farmId=' + theFarmid;
	}
</script>
<div style="width: 100%; text-align: left; color: #3B5998;">
	<table id="example" class="display">
		<thead>
			<th>Select</th>
			<th><span style="margin-left: 25px; font-size: 1.1em;">Farm
					Name</span></th>
			<th><span style="margin-left: 10px; font-size: 1.1em;">Creation
					Date (UTC)</span></th>
		</thead>
		<tbody>
			<c:forEach var="farm" items="${theFarms}">
				<tr>
					<td><button class="btn btn-primary"
							onClick="setFarm('${farm.teamId}');">Select</button></td>
					<td><span
						style="margin-left: 25px; font-size: 1.1em; color: #666;">${farm.teamName}</span></td>
					<td><span
						style="margin-left: 10px; font-size: 1.1em; color: #666;"><fmt:formatDate
								type="both" dateStyle="medium" timeStyle="short"
								value="${farm.createdate}" /></span></td>

				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>