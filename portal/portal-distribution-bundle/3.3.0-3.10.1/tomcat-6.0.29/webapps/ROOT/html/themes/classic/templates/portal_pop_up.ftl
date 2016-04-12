<!DOCTYPE html>

<#include init/>

<html dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<title>${the_title}</title>

	${theme.include(top_head_include)}

	<@liferay.css file_name=css_main_file/>
	<@liferay.js file_name=js_main_file/>
</head>

<body class="portal-popup ${css_class}">

${theme.include(content_include)}

${theme.include(bottom_ext_include)}

</body>

</html>