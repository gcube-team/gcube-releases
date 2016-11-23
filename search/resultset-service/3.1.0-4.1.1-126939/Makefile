createStubs: clean jarStubs
createGar: jars gar
clean:
	ant clean -Dbasedir=ResultSetService
stubs:
	ant compileStubs -Dpackage=globus_wsrf_core_samples -Dinterface.name=ResultSet -Denv.GLOBUS_LOCATION=${VANILA_GLOBUS_LOCATION} -Dschema.path=gt4ide/ResultSetService -Dservice.name=ResultSetService -Dbasedir=./ResultSetService -Dmappings.file=../namespace2package.mappings
jarStubs:
	ant jarStubs -Dpackage=globus_wsrf_core_samples -Dinterface.name=ResultSet -Dpackage.dir=org/diligentproject/searchservice/resultsetservice -Denv.GLOBUS_LOCATION=${VANILA_GLOBUS_LOCATION} -Dschema.path=gt4ide/ResultSetService -Dservice.name=ResultSetService -Dbasedir=./ResultSetService -Dmappings.file=../namespace2package.mappings -Dgar.filename=org_diligentproject_searchservice_resultsetservice
jars:
	ant jars -Dpackage=globus_wsrf_core_samples -Dinterface.name=ResultSet -Dpackage.dir=org/diligentproject/searchservice/resultsetservice -Denv.GLOBUS_LOCATION=${VANILA_GLOBUS_LOCATION} -Dschema.path=gt4ide/ResultSetService -Dservice.name=ResultSetService -Dbasedir=./ResultSetService -Dsrc.dir=./src -Dmappings.file=../namespace2package.mappings -Dgar.filename=org_diligentproject_searchservice_resultsetservice -Dresultset.library.location=../../dependencies_v02 -Dresultsetservice.stubs.location=../../dependencies_v02 -Dnal.library.location=../../dependencies_v02 -Ddvos-delegation.library.location=../../dependencies_v02
javadoc:
	ant javadoc -Dresultset.library.location=../ResultSetLibrary/
gar:
	ant dist_nodep -Detc.dir=build/etc -Dgar.filename=ResultSetService -Denv.GLOBUS_LOCATION=${VANILA_GLOBUS_LOCATION}
