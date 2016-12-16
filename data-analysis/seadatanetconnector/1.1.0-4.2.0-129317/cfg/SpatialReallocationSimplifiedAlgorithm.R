# Spatial Data Reallocation algorithm for FIGIS
inputFile <- "statistics.xml"
outputFile <- "spread_statistics.csv"

#package needs
require(rsdmx)
require(RFigisGeo)

#business logic
#-------------
print(Sys.time())

#read stats
print("Reading SDMX stat data ...")
sdmx <- readSDMX(inputFile, isURL = FALSE) # here isURL = FALSE because SM seems to download inputFile from URL
statistics <- as.data.frame(sdmx)
print(head(statistics))

#read intersections
print("Reading intersection...")
intersectionURL <- paste("http://www.fao.org/figis/geoserver/GeoRelationship/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=GeoRelationship:", inputIntersection, sep = "")
intersections <- readWFS(intersectionURL)
if(class(intersections) == "SpatialPolygonsDataFrame") intersections <- intersections@data

targetAreaField <- unlist(strsplit(inputIntersection,"_x_"))[1]
aggregateField <- NULL
print(includeCalculations)
if(!includeCalculations){
	aggregateField <- unlist(strsplit(inputIntersection,"_x_"))[2]
}

#reallocation
result <- reallocate(
			x = statistics,
			y = intersections,
			area.x = refAreaField,
			area.y = targetAreaField,
			by.x = NULL,
			by.y = NULL,
			data = statField,
			warea = "INT_AREA",
			wprob = NULL,
			aggregates = aggregateField
		)

write.table(result, outputFile, row.names = FALSE, col.names = TRUE, sep=",", dec=".")
print(Sys.time())