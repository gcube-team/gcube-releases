#!/bin/bash
#
# tomcat     This shell script takes care of starting and stopping Tomcat

CATALINA_HOME=/home/cotrix/apache-tomcat-7.0.50
WAR_NAME=dev.war
SHUTDOWN_WAIT=45

print_info() {
    echo "Butler 1.0" 
    echo "CATALINA_HOME $CATALINA_HOME"
    echo "WAR_NAME $WAR_NAME"
    echo "SHUTDOWN_WAIT $SHUTDOWN_WAIT"	
}
 
tomcat_pid() {
    echo `ps aux | grep org.apache.catalina.startup.Bootstrap | grep -v grep | awk '{ print $2 }'`
}
 
start() {
    pid=$(tomcat_pid)
    if [ -n "$pid" ]
    then
        echo "Tomcat is already running (pid: $pid)"
    else
        # Start tomcat
        echo "Starting tomcat"
        sh $CATALINA_HOME/bin/startup.sh
    fi
    return 0
}
 
stop() {
    pid=$(tomcat_pid)
    if [ -n "$pid" ]
    then
        echo "Stopping Tomcat"
        sh $CATALINA_HOME/bin/shutdown.sh
 
    let kwait=$SHUTDOWN_WAIT
    count=0
    count_by=5
    until [ `ps -p $pid | grep -c $pid` = '0' ] || [ $count -gt $kwait ]
    do
        echo "Waiting for processes to exit. Timeout before we kill the pid: ${count}/${kwait}"
        sleep $count_by
        let count=$count+$count_by;
    done
 
    if [ $count -gt $kwait ]; then
        echo "Killing processes which didn't stop after $SHUTDOWN_WAIT seconds"
        kill -9 $pid
    fi
    else
        echo "Tomcat is not running"
    fi
 
    return 0
}

deployWar() {

    filename=$(basename "$WAR_NAME")
    filename="${filename%.*}"
    echo "filename: $filename"

    rm -r $CATALINA_HOME/webapps/$filename
    rm $CATALINA_HOME/webapps/$WAR_NAME
    cp $WAR_NAME $CATALINA_HOME/webapps/

    echo "War deployed"

    return 0
}
 
print_info
case $1 in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    deploy)
        stop
	deployWar
        start
        ;;
    status)
       pid=$(tomcat_pid)
        if [ -n "$pid" ]
        then
           echo "Tomcat is running with pid: $pid"
        else
           echo "Tomcat is not running"
        fi
        ;;
esac
 
exit 0
