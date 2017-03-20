#!/bin/sh
# CleanIS.sh

#ftp -in meteora.di.uoa.gr <<EOF
ftp -in dl13.di.uoa.gr <<EOF
quote USER ftpuser
quote PASS za73ba97ra
delete d5s/is/Boundaries.is.tmp
delete d5s/is/Nodes.is.tmp
quit
EOF
