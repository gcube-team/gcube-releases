#!/bin/bash
#
if [ -f "$HOME/.bashrc_portal" ]
then
	source "$HOME/.bashrc_portal"
fi

if [ -z "$CATALINA_HOME"  ]
then
	echo  "Cannot deploy portlet(s), tomcat was not detected!"
	exit 1
else
	ant -f build.xml d4s-deploy >& ant_logs.txt
	if [ $? != 0 ]
	then
		echo  "Cannot deploy portlet(s), build was not successfull!"
		exit 1
	fi
exit 0
fi
