<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<!-- <link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'> -->
<link href='//fonts.googleapis.com/css?family=Didact+Gothic&subset=latin,greek-ext,greek' rel='stylesheet' type='text/css'>
<link rel="stylesheet" type="text/css" href="resources/css/geopolis-login.css">
<link rel="stylesheet" type="text/css" href="resources/css/geopolis-checkbox.css" />

<link rel="icon" type="image/png" href="resources/img/logo3.png">

<!--[if lt IE 9]>
	<link rel="stylesheet" type="text/css" href="resources/css/geoanalytics-checkbox-reset.css" />
<![endif]-->
<title>Geopolis login</title>
</head>
<body>
	<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
		<div class="background">
			<div class="transbox">
			</div>
		</div>
	<div id="logincontainer">
		<s:url var="authUrl" value="/static/j_spring_security_check" />
		<div id="loginform">
			<div id="logo"> <img src="resources/img/logo3.png" alt="Geopolis" width="250">  </div>
			
			<c:if test="${not empty error}">
				<div class="errorblock">
					${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
				</div>
			</c:if>
			<form method="post" class="signin" action="${authUrl}">
			<div id="unpw">
			
				<div id="username"> <p> <h2> Username </h2> </p>  <input id="username" type="username" name="username" /></div>
				
				<div id="password"> <p> <h2> Password </h2> </p> <input id="password" type="password" name="password" /> </div>
			
			</div>
			<div id="rememberme"> 
				<input type="checkbox" id="rememberme-checkbox" name="_spring_security_remember_me" class="chkbox-bg-white chkbox-large shadowed"> 
				<label id="rememberme-checkbox-label" for="rememberme-checkbox"><span></span></label>
				<p> <h2> Remember me </h2> </p> 
			
				
			</div>			
			<div id="enter_button"> <input name="commit" type="submit" value="Enter" class="enter_button"> </div>
			</form>
			
			<div id="footer">
				<!-- <a href="/resend_password">Sign up</a> -->
				<a href="resend_password">I forgot my password</a>
			</div>
		</div>
	</div>

	<script type="text/javascript">
		document.getElementById('username').focus();
	</script>
</body>
</html>