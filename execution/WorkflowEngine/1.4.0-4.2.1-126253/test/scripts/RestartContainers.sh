#!/bin/bash

if [ $# -ne 1 ]
then
  echo "ftp | gcube | container single argument expected"
  exit 1
fi

ssh gpapanikos@dl22.di.uoa.gr MadgikExecution/Restart.sh dl05.di.uoa.gr 3000 Grid $1 workflow 8080
ssh root@88.197.20.240 MadgikExecution/Restart.sh 88.197.20.240 3000 Hadoop $1 execution 8084
ssh condor@88.197.20.246 MadgikExecution/Restart.sh 88.197.20.240 3000 Condor $1 execution 8080
ssh gpapanikos@dl05.di.uoa.gr MadgikExecution/Restart.sh dl05.di.uoa.gr 3000 Grid $1 execution 8080
ssh gpapanikos@dl13.di.uoa.gr MadgikExecution/Restart.sh dl13.di.uoa.gr 3000 GCube $1 execution 8080
ssh gpapanikos@dl14.di.uoa.gr MadgikExecution/Restart.sh dl14.di.uoa.gr 3000 GCube $1 execution 8080
# ssh gpapanikos@dl15.di.uoa.gr MadgikExecution/Restart.sh dl15.di.uoa.gr 3000 GCube $1 execution 8080
