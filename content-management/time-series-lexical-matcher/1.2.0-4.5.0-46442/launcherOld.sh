#!/bin/bash
TODAY=`date +%Y-%m-%d`;
SERVICE=seat-ivr-it
TMPFILE=/tmp/.${SERVICE}.tmp
OUTFILE=/home/hdademo/HDA_stage_seat/reports/Reporting-${TODAY}.xsl
LOGS_DIR=/home/hdademo/stage/hda-logs/
LOGS="hda-brain-tracing.log.${TODAY}*"

for i in `ls ${LOGS_DIR}/$LOGS`;
do
	echo "$i\n\n\n"
	grep $SERVICE $i >> $TMPFILE;
done 

java -cp ./libs/:./:./bin/:./libs/c3p0-0.9.1.2.jar:./libs/commons-collections-3.1.jar:./libs/dom4j-1.6.1.jar:./libs/geronimo-stax-api_1.0_spec-1.0.jar:./libs/hibernate3.jar:./libs/jta-1.1.jar:./libs/log4j-1.2.16.jar:./libs/mysql-connector-java-5.1.12-bin.jar:./libs/ooxml-lib:./libs/poi-3.6-20091214.jar:./libs/poi-bin-3.6-20091214.zip:./libs/poi-contrib-3.6-20091214.jar:./libs/poi-examples-3.6-20091214.jar:./libs/poi-ooxml-3.6-20091214.jar:./libs/poi-ooxml-schemas-3.6-20091214.jar:./libs/poi-scratchpad-3.6-20091214.jar:./libs/slf4j-api-1.6.0.jar:./libs/slf4j-log4j12-1.6.0.jar:./libs/xmlbeans-2.3.0.jar:./libs/commons-logging-1.1.jar it.hcare.seat.report.management.ReportingPopulator $TMPFILE $OUTFILE 

rm $TMPFILE

