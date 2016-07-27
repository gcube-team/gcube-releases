#!/bin/sh

cat <<FIN
<?xml version="1.0" encoding="UTF-8"?>
<exampleProbedInformation>
    <description>
This is example information fabricated for $1.
In a real deployment, the script generating this output would
actually make a real measurement or gather real data.
    </description>
    <time>$(date)</time>
</exampleProbedInformation>
FIN

