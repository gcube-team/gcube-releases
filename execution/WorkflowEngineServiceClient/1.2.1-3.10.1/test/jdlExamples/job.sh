#!/bin/sh
# job.sh

echo $1 > job.output
echo $1 >> job.output
echo $1 >> job.output
echo $1 >> job.output
echo $1 >> job.output
echo "" >> job.output
echo $PATH >> job.output
echo $TEST_ENV_VAR1 >> job.output
echo $TEST_ENV_VAR2 >> job.output
echo "" >> job.output
cat sig.txt >> job.output

echo "Hello World of stdOut"
echo $1
echo ""
cat sig.txt

echo "hello World of stdErr" 1>&2
echo $1 1>&2
echo "" 1>&2
cat sig.txt 1>&2

