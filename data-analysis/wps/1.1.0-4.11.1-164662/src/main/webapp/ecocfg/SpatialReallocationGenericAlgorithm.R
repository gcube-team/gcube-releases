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

#read intersections
print("Reading intersection...")
intersections <- readWFS(inputIntersection)
if(class(intersections) == "SpatialPolygonsDataFrame") intersections <- intersections@data

#reallocation
result <- reallocate(
			x = statistics,
			y = intersections,
			area.x = refAreaField,
			area.y = intersectionAreaField,
			by.x = NULL,
			by.y = NULL,
			data = statField,
			warea = surfaceField,
			wprob = NULL,
			aggregates = aggregateField
		)

write.table(result, outputFile, row.names = FALSE, col.names = TRUE, sep=",", dec=".")
print(Sys.time())