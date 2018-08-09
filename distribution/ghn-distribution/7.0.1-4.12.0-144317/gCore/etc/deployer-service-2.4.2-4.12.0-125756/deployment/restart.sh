#! /bin/sh

cd ${GLOBUS_LOCATION}

. ${GLOBUS_LOCATION}/etc/globus-devel-env.sh

sleep 15

nohup ${GLOBUS_LOCATION}/bin/globus-start-container -p $1 -nosec &


