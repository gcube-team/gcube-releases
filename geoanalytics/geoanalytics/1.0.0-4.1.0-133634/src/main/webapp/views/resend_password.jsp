<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link href='//fonts.googleapis.com/css?family=Didact+Gothic&subset=latin,greek-ext,greek' rel='stylesheet' type='text/css'>
<link rel="stylesheet" type="text/css" href="resources/css/geoanalytics-login.css">
<link rel="stylesheet" href="../resources/css/bootstrap-3.0.0.min.css">

<link rel="icon" type="image/png" href="resources/img/logo3.png">

<title>Geopolis password change</title>
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
			<div id="logo"> <img src="resources/img/logo3.png" alt="Geopolis_Logo" width="250">  </div>
			
			<c:if test="${not empty error}">
				<div class="errorblock">
					<c:if test="${error eq 'NO_USERNAME' }">
						Please enter a username.
					</c:if>
					<c:if test="${error eq 'EMAIL_NO_MATCH' }">
						The username or e-mail you entered is not correct.
					</c:if>
				</div>
			</c:if>
			<c:if test="${empty error}">
				<c:if test="${not empty status }">
					<div class="errorblock">
						An e-mail describing the actions you need to take in order to be able to log in again has been sent to you.
					</div>
				</c:if>
			</c:if>
			<form method="post" class="signin" action="resend_password">
			<div id="unpw">
			
				<div id="username"> <p> <h2> Username </h2> </p>  <input id="username" type="text" name="username" /></div>
				
				<div id="email"> <p> <h2>e-mail </h2> </p> <input id="email" type="email" name="email" /> </div>
			
			</div>
			
			<div id="rememberme" style="visibility:hidden"> <input type="checkbox" name="_spring_security_remember_me" > <p> <h2> Remember me </h2> </p>  </div>
			
			<div id="enter_button"> <input name="commit" type="submit" value="Enter" class="enter_button"> </div>
			</form>
		</div>
	</div>

	<script type="text/javascript">
		document.getElementById('username').focus();
	</script>
</body>
</html>