if exist "%CATALINA_HOME%/jre1.6.0_20/win" (
	if not "%JAVA_HOME%" == "" (
		set JAVA_HOME=
	)

	set "JRE_HOME=%CATALINA_HOME%/jre1.6.0_20/win"
)

set "JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true -Duser.timezone=GMT -Xmx1024m -XX:MaxPermSize=256m"