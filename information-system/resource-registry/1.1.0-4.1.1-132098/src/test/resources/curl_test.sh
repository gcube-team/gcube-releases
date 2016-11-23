#!/bin/bash
#  Creating ROOT
ROOT_NAME="myRoot"
URL="http://localhost:8080/resource-registry/context?name=${ROOT_NAME}"
echo "Going to create ROOT Context with name ${ROOT_NAME} by invoking URL ${URL}"
ROOT_UUID=$(curl -H gcube-scope:/gcube/devNext -X PUT ${URL})
echo "Created Context (${ROOT_NAME}) has UUID ${ROOT_UUID}"
echo ""
echo ""


# Creating VO
VO_NAME="myVO"
URL="http://localhost:8080/resource-registry/context?name=${VO_NAME}&parentContextId=${ROOT_UUID}"
echo "Going to create Context with name ${VO_NAME} as child of ${ROOT_UUID} (${ROOT_NAME}) by invoking URL ${URL}"
VO_UUID=$(curl -H gcube-scope:/gcube/devNext -X PUT ${URL})
echo "Created Context ${VO_NAME} has UUID ${VO_UUID} as child of ${ROOT_UUID} (${ROOT_NAME})"
echo ""
echo ""


# Renaming VO
VO_NEW_NAME="myVONewName"
URL="http://localhost:8080/resource-registry/context/rename/${VO_UUID}?name=${VO_NEW_NAME}"
echo "Going to rename VO Context from name ${VO_NAME} to ${VO_NEW_NAME} by invoking URL ${URL}"
RENAMED_VO_UUID=$(curl -H gcube-scope:/gcube/devNext -X POST ${URL})

if [ "${VO_UUID}" = "${RENAMED_VO_UUID}" ]; then
	echo "VO Context ${VO_UUID} has been renamed from name ${VO_NAME} to ${VO_NEW_NAME}"
else
	echo "VO Context ${VO_UUID} has NOT been renamed from name ${VO_NAME} to ${VO_NEW_NAME}"
	exit 1
fi

echo ""
echo ""


# Moving VO as ROOT
URL="http://localhost:8080/resource-registry/context/move/${VO_UUID}"
echo "Going to move VO Context ${VO_NEW_NAME} as ROOT  by invoking URL ${URL}"
MOVED_VO_UUID=$(curl -H gcube-scope:/gcube/devNext -X POST ${URL})

if [ "${VO_UUID}" = "${MOVED_VO_UUID}" ]; then
	echo "VO Context ${VO_UUID} (${VO_NEW_NAME}) has been moved as ROOT"
else
	echo "VO Context ${VO_UUID} (${VO_NEW_NAME}) has NOT been moved as ROOT"
	exit 1
fi

echo ""
echo ""


# Moving VO under myRoot 
URL="http://localhost:8080/resource-registry/context/move/${VO_UUID}?parentContextId=${ROOT_UUID}"
echo "Going to move VO Context ${VO_NEW_NAME} under ${ROOT_UUID} (${ROOT_NAME}) by invoking URL ${URL}"
MOVED_AGAIN_VO_UUID=$(curl -H gcube-scope:/gcube/devNext -X POST ${URL})

if [ "${VO_UUID}" = "${MOVED_AGAIN_VO_UUID}" ]; then
	echo "VO Context ${VO_UUID} (${VO_NEW_NAME}) has been moved under ${ROOT_UUID} (${ROOT_NAME})"
else
	echo "VO Context ${VO_UUID} (${VO_NEW_NAME}) has NOT been moved  under ${ROOT_UUID} (${ROOT_NAME})"
	exit 1
fi

echo ""
echo ""


# Deleting VO
URL="http://localhost:8080/resource-registry/context/${VO_UUID}"
echo "Going to delete VO Context ${VO_UUID} (${VO_NEW_NAME}) by invoking URL ${URL}"
DELETED_VO_UUID=$(curl -H gcube-scope:/gcube/devNext -X DELETE ${URL})

if [ "${VO_UUID}" = "${DELETED_VO_UUID}" ]; then
	echo "VO Context ${VO_UUID} (${VO_NEW_NAME}) has been removed"
else
	echo "VO Context ${VO_UUID} (${VO_NEW_NAME}) has NOT been moved removed"
	exit 1
fi

echo ""
echo ""


# Deleting ROOT
URL="http://localhost:8080/resource-registry/context/${ROOT_UUID}"
echo "Going to delete ROOT Context ${ROOT_UUID} (${ROOT_NAME}) by invoking URL ${URL}"
DELETED_ROOT_UUID=$(curl -H gcube-scope:/gcube/devNext -X DELETE ${URL})

if [ "${ROOT_UUID}" = "${DELETED_ROOT_UUID}" ]; then
	echo "VO Context ${ROOT_UUID} (${ROOT_NAME}) has been removed"
else
	echo "VO Context ${ROOT_UUID} (${ROOT_NAME}) has NOT been moved removed"
	exit 1
fi

echo ""
echo ""

