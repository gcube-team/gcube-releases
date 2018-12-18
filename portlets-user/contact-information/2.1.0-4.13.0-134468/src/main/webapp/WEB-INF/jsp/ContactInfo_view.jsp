<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<script type="text/javascript">
     if(window.parent.PageBus) {
     window.PageBus = window.parent.PageBus;
   }
   </script>
    <script type="text/javascript" src="https://apis.google.com/js/platform.js"></script>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/contactinfo/contactinfo.nocache.js"></script>
<div id="ContactProfileDiv"></div>