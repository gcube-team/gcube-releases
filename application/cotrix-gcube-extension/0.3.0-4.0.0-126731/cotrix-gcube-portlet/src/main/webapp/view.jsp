<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ page import="org.cotrix.gcube.portlet.CotrixUrlProvider" %>

<portlet:defineObjects />

<script>
function resizeCotrixContainer(){
  var cotrixContainer = document.getElementById("cotrixContainer");
  var containerHeight = window.innerHeight - cotrixContainer.offsetTop;
  cotrixContainer.style.height = containerHeight+"px";
  
  var cotrixLoader = document.getElementById("cotrixLoader");
  var loaderHeight = window.innerHeight - cotrixLoader.offsetTop;
  cotrixLoader.style.height = loaderHeight+"px"; 
};

function hideCotrixLoader() {
  document.getElementById('cotrixLoader').style.display='none';
  document.getElementById('cotrixContainer').style.display='inline';
  resizeCotrixContainer();
}

window.onresize=resizeCotrixContainer;
window.onload=resizeCotrixContainer;
</script>

<div id="cotrixLoader" style="display: table;width: 100%;">  
  <div style="display: table-cell;vertical-align: middle;">
    <div style=" margin-left: auto;margin-right: auto;width:300px;">
      <div style="display: block;margin-left: auto;margin-right: auto;text-align: center;line-height: 30px;">Loading Cotrix...</div>
      <img src="<%= request.getContextPath() %>/images/loader.gif" style="display: block;margin-left: auto;margin-right: auto;"/>
    </div>
  </div>
</div>

<iframe id="cotrixContainer" src="<%= CotrixUrlProvider.getCotrixUrl(session, request) %>" style="display:none;width:100%;border:none" onLoad="hideCotrixLoader()"></iframe>

