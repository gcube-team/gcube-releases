#!/bin/bash
if [ -z "$GLOBUS_LOCATION" ]
then
echo "Cannot install the software, gCore was not detected!"
exit 1
else
cp datanucleus.buffer.properties $GLOBUS_LOCATION
cp datanucleus.derby.properties $GLOBUS_LOCATION
cp resourceregistry.properties $GLOBUS_LOCATION
cp targets.model.properties $GLOBUS_LOCATION
cp config.gcubebridge.properties $GLOBUS_LOCATION
if [ $? != 0 ]
then
echo "Cannot install the software!"
exit 1
fi
exit 0
fi
