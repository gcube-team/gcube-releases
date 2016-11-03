PS1='$ '
TOMCAT_DST_FOLDER="tomcat"
TOMCAT_VERSION="${tomcat.version}"
TOMCAT_PID_FILENAME="tomcat.pid"
SMARTGEAR_DISTRIBUTION_DIRECTORY=$(ls -d smartgears-distribution-*)
SMARTGEAR_ROOT=${PWD}

BUNDLE_HOME=${SMARTGEAR_ROOT}
STARTUP_SCRIPT="startContainer.sh"
STOP_SCRIPT="stopContainer.sh"

GHN_HOME=${SMARTGEAR_ROOT}/SmartGears
CONTAINER_FILENAME="container.xml"
CONTAINER_XML=${GHN_HOME}/${CONTAINER_FILENAME}

CATALINA_OPTS_BASHRC="export CATALINA_OPTS=\"-Xmx2000m -Xms2000m\""
GHN_HOME_BASHRC="export GHN_HOME=${GHN_HOME}"
CATALINA_PID_BASHRC="export CATALINA_PID=${SMARTGEAR_ROOT}/${TOMCAT_PID_FILENAME}"
CATALINA_HOME_BASHRC="export CATALINA_HOME=${SMARTGEAR_ROOT}/${TOMCAT_DST_FOLDER}"
BUNDLE_HOME_BASHRC="export BUNDLE_HOME=${SMARTGEAR_ROOT}"

function showhelp {
	echo -e "\nUsage:  setup.sh [-n <hostname>] [-f | -h] \n"
	echo    " -n <hostname> = The hostname to set in container.xml"
	echo    " -f  = Really run the setup. By default it will be a dry-run"
	echo -e " -h  = shows this help.\n"
}

function hostname {
	local HOSTNAME=$(/bin/hostname)
	
	if [ -z "$HOST" ]; then
		HOST=${HOSTNAME}
	fi
	
	local HOST_OK=false
	
	while [ "$HOST_OK" = false ]
	do
		
		echo "Is ${HOST} correct?"
		select yn in "Yes" "No";
		do
		    case $yn in
		        Yes )
		        	ok=true
		        	HOST_OK=true;
		        	break;;
		        No )
		        	ok=false
		        	break;;
		    esac
		done
		
		if [ "$ok" = false ]; then
			read -p "Please input the hostname [followed by ENTER]: " HOST
		fi
		
	done
}

function escape_slashes {
    sed 's/\//\\\//g' 
}

function change_line {
    local OLD_LINE_PATTERN=$1; shift
    local NEW_LINE=$1; shift
    local FILE=$1

    local NEW=$(echo "${NEW_LINE}" | escape_slashes)
    sed -i '/'"${OLD_LINE_PATTERN}"'/s/.*/'"${NEW}"'/' "${FILE}"
}

while getopts ":n:fh" opt; do
	case $opt in
		n) HOST=$OPTARG;;
		f) force=true;;
    	h) showhelp
			exit 0 ;;
		\?) echo -e "\nERROR:invalid option: -$OPTARG"; 
			showhelp; 
			echo -e "\naborting.\n" 
			exit 1 >&2 ;;
  	esac
done

if [ $force ]; then
	echo "Creating tomcat symlink..."
	ln -s apache-tomcat-${TOMCAT_VERSION} ${TOMCAT_DST_FOLDER}
	echo "done."
	ls -l ${TOMCAT_DST_FOLDER}

	echo -e "\n"
	echo "Adding the following variables to environment..."
	sed -i '/export CATALINA_OPT/d' ~/.bashrc
	echo "${CATALINA_OPTS_BASHRC}"
	echo "${CATALINA_OPTS_BASHRC}" >> ~/.bashrc
	sed -i '/export GHN_HOME/d' ~/.bashrc
	echo "${GHN_HOME_BASHRC}"
	echo "${GHN_HOME_BASHRC}" >> ~/.bashrc
	sed -i '/export CATALINA_PID/d' ~/.bashrc
	echo "${CATALINA_PID_BASHRC}"
	echo "${CATALINA_PID_BASHRC}" >> ~/.bashrc
	sed -i '/export CATALINA_HOME/d' ~/.bashrc
	echo "${CATALINA_HOME_BASHRC}"
	echo "${CATALINA_HOME_BASHRC}" >> ~/.bashrc
	sed -i '/export BUNDLE_HOME/d' ~/.bashrc
	echo "${BUNDLE_HOME_BASHRC}"
	echo "${BUNDLE_HOME_BASHRC}" >> ~/.bashrc
	echo "done."
	
	echo -e "\n"
	echo "Creating GHN_HOME ${GHN_HOME}"
	mkdir -p ${GHN_HOME}
	
	echo -e "\n"
	echo "Loading new environment..."
	source ~/.bashrc
	
	echo -e "\n"
	echo -e "Entering on SmartGear distribution directory ${SMARTGEAR_DISTRIBUTION_DIRECTORY}\n"
	export CATALINA_HOME=${SMARTGEAR_ROOT}/${TOMCAT_DST_FOLDER}
	cd ${SMARTGEAR_DISTRIBUTION_DIRECTORY}
	
	echo ""
	echo "Launching SmartGear install for Tomcat...."
	
	./install -s tomcat -g ${GHN_HOME}
	
	hostname
	change_line "<hostname>" "\t<hostname>$HOST</hostname>" ${CONTAINER_XML}
	
	echo -e "\n\n\n"
	echo "Just few steps to do to reach the goal:"
		
	echo "Load new environments with the following command:"
	echo "source ~/.bashrc"
	
	echo -e "\n\n"
	echo "Then:"
else
	showhelp
	
	echo "Here it is the list of steps to setup SmartGear."
	
	echo ""
	echo "Create tomcat symlink with the following command:"
	echo "ln -s apache-tomcat-${TOMCAT_VERSION} ${TOMCAT_DST_FOLDER}"
		
	echo ""
	echo "Add the following variables to your ~/.bashrc"
	echo "${CATALINA_OPTS_BASHRC}"
	echo "${GHN_HOME_BASHRC}"
	echo "${CATALINA_PID_BASHRC}"
	echo "${CATALINA_HOME_BASHRC}"
	echo "${BUNDLE_HOME_BASHRC}"
	
	
	echo ""
	echo "Create GHN_HOME ${GHN_HOME}"
	echo "mkdir -p ${GHN_HOME}"
	
	echo ""
	echo "Load new environments with the following command:"
	echo "source ~/.bashrc"
	
	echo ""
	echo "Enter on SmartGear distribution directory"
	echo "cd ${SMARTGEAR_DISTRIBUTION_DIRECTORY}"
	
	echo ""
	echo "Launch SmartGear install for Tomcat with the following command:"
	echo "./install -s tomcat"
	
	echo ""
	echo "The previous steps can be made by launching this script with -f option"
	
	echo -e "\n\n"
	
	echo "Then:"
	
	echo "- Modify ${CONTAINER_XML} with your hostname"
fi

echo "- Modify ${CONTAINER_XML} startup infrastructure and vres"

echo "- You can Start the container from ${BUNDLE_HOME} directory using the command ./${STARTUP_SCRIPT}"
echo "- You can Stop the container from ${BUNDLE_HOME} directory using the command ./${STOP_SCRIPT}"

echo ""
echo "PLEASE NOTE:"
echo "By default Tomcat start on 8080 port. If you want to change this port REMEMBER to modify ${CONTAINER_XML} consistently"