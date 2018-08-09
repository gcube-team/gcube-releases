#!/bin/bash
ls  $GLOBUS_LOCATION/lib/*.* | xargs -n1 basename  > $GLOBUS_LOCATION/config/gcore-filelist.txt 
