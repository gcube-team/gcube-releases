#!/bin/sh
# Concat.sh

while [ $# != 0 ]
do
	cat $1 >> concat.out
	shift
done

