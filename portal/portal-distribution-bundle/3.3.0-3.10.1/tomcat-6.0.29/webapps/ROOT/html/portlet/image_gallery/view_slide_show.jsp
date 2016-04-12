<%
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
%>

<%@ include file="/html/portlet/image_gallery/init.jsp" %>

<%
IGFolder folder = (IGFolder)request.getAttribute(WebKeys.IMAGE_GALLERY_FOLDER);

long folderId = (folder == null) ? 0 : folder.getFolderId();

List images = IGImageServiceUtil.getImages(scopeGroupId, folderId);

int defaultSpeed = 3000;
%>

<aui:form>
	<aui:fieldset>
		 <aui:column>
			 <aui:button onClick='<%= renderResponse.getNamespace() + "showPrevious();" %>' value="previous" />
			 <aui:button onClick='<%= renderResponse.getNamespace() + "play();" %>' value="play" />
			 <aui:button onClick='<%= renderResponse.getNamespace() + "pause();" %>' value="pause" />
			 <aui:button onClick='<%= renderResponse.getNamespace() + "showNext();" %>' value="next" />
		 </aui:column>
		<aui:column>
			<aui:select inlineLabel="left" name="speed" onChange='<%= renderResponse.getNamespace() + "pause();" + renderResponse.getNamespace() + "speed = this[this.selectedIndex].value * 1000;" + renderResponse.getNamespace() + "play();" %>'>

				<%
				for (int i = 1; i <= 10; i++) {
				%>

					<aui:option label="<%= i %>" selected="<%= (defaultSpeed / 1000) == i %>" />

				<%
				}
				%>

			</aui:select>

		</aui:column>
	</aui:fieldset>
</aui:form>

<br />

<table class="lfr-table">
<tr>
	<td>

		<%
		if (!images.isEmpty()) {
			IGImage image = (IGImage)images.get(0);
		%>

			<img border="0" name="<portlet:namespace />slideShow" src="<%= themeDisplay.getPathImage() %>/image_gallery?img_id=<%= image.getLargeImageId() %>&t=<%= ImageServletTokenUtil.getToken(image.getLargeImageId()) %>" />

		<%
		}
		%>

	</td>
</tr>
</table>

<aui:script>
	var <portlet:namespace />imgArray = new Array();

	<%
	for	(int i = 0; i < images.size(); i++) {
		IGImage image = (IGImage)images.get(i);
	%>

		<portlet:namespace />imgArray[<%= i %>] = "<%= themeDisplay.getPathImage() %>/image_gallery?img_id=<%= image.getLargeImageId() %>&t=<%= ImageServletTokenUtil.getToken(image.getLargeImageId()) %>";

	<%
	}
	%>

	var <portlet:namespace />imgArrayPos = 0
	var <portlet:namespace />speed = <%= defaultSpeed %>;
	var <portlet:namespace />timeout = 0;

	function <portlet:namespace />pause() {
		clearInterval(<portlet:namespace />timeout);
		<portlet:namespace />timeout = 0;
	}

	function <portlet:namespace />play() {
		if (<portlet:namespace />timeout == 0) {
			<portlet:namespace />timeout = setInterval("<portlet:namespace />showNext()", <portlet:namespace />speed);
		}
	}

	function <portlet:namespace />showNext() {
		<portlet:namespace />imgArrayPos++;

		if (<portlet:namespace />imgArrayPos == <portlet:namespace />imgArray.length) {
			<portlet:namespace />imgArrayPos = 0;
		}

		document.images.<portlet:namespace />slideShow.src = <portlet:namespace />imgArray[<portlet:namespace />imgArrayPos];
	}

	function <portlet:namespace />showPrevious() {
		<portlet:namespace />imgArrayPos--;

		if (<portlet:namespace />imgArrayPos < 0) {
			<portlet:namespace />imgArrayPos = <portlet:namespace />imgArray.length - 1;
		}

		document.images.<portlet:namespace />slideShow.src = <portlet:namespace />imgArray[<portlet:namespace />imgArrayPos];
	}

	<portlet:namespace />play();
</aui:script>