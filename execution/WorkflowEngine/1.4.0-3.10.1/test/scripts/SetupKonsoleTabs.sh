
#!/bin/bash


# ftpuser@dl13.di.uoa.gr
# gpapanikos@dl15.di.uoa.gr

SERVER="
root@88.197.20.240
condor@88.197.20.246
gpapanikos@dl05.di.uoa.gr
gpapanikos@dl13.di.uoa.gr
gpapanikos@dl14.di.uoa.gr
gpapanikos@dl22.di.uoa.gr
"

# start a new konsole window and save the handle in $konsole
konsole=$(dcopstart konsole-script)


# maximize the new window
#dcop $konsole konsole-mainwindow#1 maximize


# get current session for the first (just created) window
thissession=$(dcop $konsole konsole currentSession)


# rename this window/session
dcop $konsole $thissession renameSession "init"


# start a new session tab for each server
for s in $SERVER ; do
    # this output is displayed on the terminal which is running your script
    echo "connect to server:  $s"


    # create another konsole tab and save handle in $newsession
    newsession=`dcop $konsole konsole newSession "ssh $s"`


    # wait for shell startup - raise if needed
    #sleep 2


    # rename the new session
    dcop $konsole $newsession renameSession "$s"


    # and start the ssh session
    dcop $konsole $newsession sendSession "exec ssh $s"


done


# close the first session window
dcop $konsole $thissession closeSession > /dev/null
