cat("Retrieving Input Parameters\n")
inputFile<-'tacsat.csv'
outputFile<-'tacsat_interpolated.csv'

require(data.table)
print(Sys.time())

memory.size(max = TRUE)
memory.limit(size = 4000)

interCubicHermiteSpline <- function(spltx,spltCon,res,params,headingAdjustment){
  
  #Formula of Cubic Hermite Spline
  t   <- seq(0,1,length.out=res)
  F00 <- 2*t^3 -3*t^2 + 1
  F10 <- t^3-2*t^2+t
  F01 <- -2*t^3+3*t^2
  F11 <- t^3-t^2
  
  #Making tacsat dataset ready
  spltx[spltCon[,1],"SI_HE"][which(is.na(spltx[spltCon[,1],"SI_HE"]))] <- 0
  spltx[spltCon[,2],"SI_HE"][which(is.na(spltx[spltCon[,2],"SI_HE"]))] <- 0
  
  #Heading at begin point in degrees
  Hx0 <- sin(spltx[spltCon[,1],"SI_HE"]/(180/pi))
  Hy0 <- cos(spltx[spltCon[,1],"SI_HE"]/(180/pi))
  
  #Heading at end point in degrees
  Hx1 <- sin(spltx[spltCon[,2]-headingAdjustment,"SI_HE"]/(180/pi))
  Hy1 <- cos(spltx[spltCon[,2]-headingAdjustment,"SI_HE"]/(180/pi))
  
  #Start and end positions
  Mx0 <- spltx[spltCon[,1],"SI_LONG"]
  Mx1 <- spltx[spltCon[,2],"SI_LONG"]
  My0 <- spltx[spltCon[,1],"SI_LATI"]
  My1 <- spltx[spltCon[,2],"SI_LATI"]
  
  #Corrected for longitude lattitude effect
  Hx0 <- Hx0 * params$fm * spltx[spltCon[,1],"SI_SP"] /((params$st[2]-params$st[1])/2+params$st[1])
  Hx1 <- Hx1 * params$fm * spltx[spltCon[,2],"SI_SP"] /((params$st[2]-params$st[1])/2+params$st[1])
  Hy0 <- Hy0 * params$fm * lonLatRatio(spltx[spltCon[,1],"SI_LONG"],spltx[spltCon[,1],"SI_LATI"]) * spltx[spltCon[,1],"SI_SP"]/((params$st[2]-params$st[1])/2+params$st[1])
  Hy1 <- Hy1 * params$fm * lonLatRatio(spltx[spltCon[,2],"SI_LONG"],spltx[spltCon[,2],"SI_LATI"]) * spltx[spltCon[,2],"SI_SP"]/((params$st[2]-params$st[1])/2+params$st[1])
  
  #Get the interpolation
  fx  <- outer(F00,Mx0,"*")+outer(F10,Hx0,"*")+outer(F01,Mx1,"*")+outer(F11,Hx1,"*")
  fy  <- outer(F00,My0,"*")+outer(F10,Hy0,"*")+outer(F01,My1,"*")+outer(F11,Hy1,"*")
  
  #Create output format
  intsx   <- lapply(as.list(1:nrow(spltCon)),function(x){
    matrix(rbind(spltx$ID[spltCon[x,]],cbind(fx[,x],fy[,x])),ncol=2,
           dimnames=list(c("startendVMS",seq(1,res,1)),c("x","y")))})
  return(intsx)}

rbindTacsat <- function(set1,set2){
  cln1  <- colnames(set1)
  cln2  <- colnames(set2)
  if(any(duplicated(cln1)==TRUE) || any(duplicated(cln2)==TRUE)) stop("Duplicate column names in datasets")
  idx1  <- which(is.na(pmatch(cln1,cln2))==TRUE)
  idx2  <- which(is.na(pmatch(cln2,cln1))==TRUE)
  
  if(length(idx1)>0){
    for(i in idx1) set2 <- cbind(set2,NA)
    colnames(set2) <- c(cln2,cln1[idx1])}
  if(length(idx2)>0){
    for(i in idx2) set1 <- cbind(set1,NA)
    colnames(set1) <- c(cln1,cln2[idx2])}
  cln1  <- colnames(set1)
  cln2  <- colnames(set2)
  mtch  <- pmatch(cln1,cln2)
  if(any(is.na(mtch))==TRUE) stop("Cannot find nor create all matching column names")
  set3  <- rbind(set1,set2[,cln2[mtch]])
  return(set3)}

bearing <- function(lon,lat,lonRef,latRef){
  
  x1  <- lon
  y1  <- lat
  x2  <- lonRef
  y2  <- latRef
  
  y   <- sin((x2-x1)*pi/180) * cos(y2*pi/180)
  x   <- cos(y1*pi/180) * sin(y2*pi/180) - sin(y1*pi/180) * cos(y2*pi/180) * cos((x2-x1)*pi/180)
  bearing <- atan2(y,x)*180/pi
  bearing <- (bearing + 360)%%360
  return(bearing)}

`distance` <-
  function(lon,lat,lonRef,latRef){
    
    pd <- pi/180
    
    a1<- sin(((latRef-lat)*pd)/2)
    a2<- cos(lat*pd)
    a3<- cos(latRef*pd)
    a4<- sin(((lonRef-lon)*pd)/2)
    a <- a1*a1+a2*a3*a4*a4
    
    c <- 2*atan2(sqrt(a),sqrt(1-a));
    return(6371*c)}

distanceInterpolation <- function(interpolation){
  
  res <- unlist(lapply(interpolation,function(x){
    dims        <- dim(x)
    res         <- distance(x[3:dims[1],1],x[3:dims[1],2],x[2:(dims[1]-1),1],x[2:(dims[1]-1),2])
    return(sum(res,na.rm=TRUE))}))
  
  return(res)}


equalDistance <- function(interpolation,res=10){
  
  #Calculate ditance of all interpolations at the same time
  totDist <- distanceInterpolation(interpolation)
  #Get dimensions of interpolations
  lngInt  <- lapply(interpolation,dim)
  
  #Warn if resolution of equal distance is too high compared to original resolution of interpolation
  if(min(unlist(lngInt)[seq(1,length(totDist),2)],na.rm=TRUE) < 9*res) warnings("Number of intermediate points in the interpolation might be too small for the equal distance pionts chosen")
  
  #Get distance steps to get equal distance
  eqStep  <- totDist/(res-1)
  
  #Get x-y values of all interpolations
  intidx  <- matrix(unlist(lapply(interpolation,function(x){return(x[1,])})),ncol=2,byrow=TRUE)
  
  #Do the calculation
  result  <- lapply(interpolation,function(ind){
    i       <- which(intidx[,1] == ind[1,1] & intidx[,2] == ind[1,2])
    idx     <- apply(abs(outer(
      cumsum(distance(ind[3:lngInt[[i]][1],1],ind[3:lngInt[[i]][1],2],ind[2:(lngInt[[i]][1]-1),1],ind[2:(lngInt[[i]][1]-1),2])),
      seq(eqStep[i],totDist[i],eqStep[i]),
      "-")),
      2,which.min)+1
    idx     <- c(1,idx)
    return(ind[c(1,idx+1),])})
  #Return the equal distance interpolated set in the same format as the interpolated dataset (as a list)
  return(result)}

interStraightLine <- function(spltx,spltCon,res){

     fx <- mapply(seq,spltx[spltCon[,1],"SI_LONG"],spltx[spltCon[,2],"SI_LONG"],length.out=res)
     fy <- mapply(seq,spltx[spltCon[,1],"SI_LATI"],spltx[spltCon[,2],"SI_LATI"],length.out=res)

     #Create output format
     intsx   <- lapply(as.list(1:nrow(spltCon)),function(x){
                   matrix(rbind(spltx$ID[spltCon[x,]],cbind(fx[,x],fy[,x])),ncol=2,
                          dimnames=list(c("startendVMS",seq(1,res,1)),c("x","y")))})
  return(intsx)}
  
interpolation2Tacsat <- function(interpolation,tacsat,npoints=10,equalDist=TRUE){
  
  # This function takes the list of tracks output by interpolateTacsat and converts them back to tacsat format.
  # The npoints argument is the optional number of points between each 'real' position.
  tacsat            <- sortTacsat(tacsat)
  if(!"HL_ID" %in% colnames(tacsat)) tacsat$HL_ID <- 1:nrow(tacsat)
  if(!"SI_DATIM" %in% colnames(tacsat)) tacsat$SI_DATIM  <- as.POSIXct(paste(tacsat$SI_DATE,  tacsat$SI_TIME,   sep=" "), tz="GMT", format="%d/%m/%Y  %H:%M")
  if(equalDist){
    interpolationEQ <- equalDistance(interpolation,npoints)  #Divide points equally along interpolated track (default is 10).
  } else {
    interpolationEQ <- lapply(interpolation,function(x){idx <- round(seq(2,nrow(x),length.out=npoints)); return(x[c(1,idx),])})
  }
  res <- lapply(interpolationEQ,function(x){
    idx                     <- unlist(x[1,1:2]@.Data); x <- data.frame(x)
    colnames(x)             <- c("SI_LONG","SI_LATI")
    cls                     <- which(apply(tacsat[c(idx),],2,function(y){return(length(unique(y)))})==1)
    for(i in cls){
      x           <- cbind(x,rep(tacsat[idx[1],i],nrow(x)));
      colnames(x) <- c(colnames(x)[1:(ncol(x)-1)],colnames(tacsat)[i])
    }
    if(!"VE_COU" %in% colnames(x)) x$VE_COU                <- rep(tacsat$VE_COU[idx[1]],nrow(x))
    if(!"VE_REF" %in% colnames(x)) x$VE_REF                <- rep(tacsat$VE_REF[idx[1]],nrow(x))
    if(!"FT_REF" %in% colnames(x)) x$FT_REF                <- rep(tacsat$FT_REF[idx[1]],nrow(x))
    x$SI_DATIM              <- tacsat$SI_DATIM[idx[1]]
    x$SI_DATIM[-c(1:2)]     <- as.POSIXct(cumsum(rep(difftime(tacsat$SI_DATIM[idx[2]],tacsat$SI_DATIM[idx[1]],units="secs")/(nrow(x)-2),nrow(x)-2))+tacsat$SI_DATIM[idx[1]],tz="GMT",format = "%d/%m/%Y  %H:%M")
    x$SI_DATE               <- format(x$SI_DATIM,format="%d/%m/%Y")
    timeNotation            <- ifelse(length(unlist(strsplit(tacsat$SI_TIME[1],":")))>2,"secs","mins")
    if(timeNotation == "secs") x$SI_TIME  <- format(x$SI_DATIM,format="%H:%M:%S")
    if(timeNotation == "mins") x$SI_TIME  <- format(x$SI_DATIM,format="%H:%M")
    x$SI_SP                 <- mean(c(tacsat$SI_SP[idx[1]],tacsat$SI_SP[idx[2]]),na.rm=TRUE)
    x$SI_HE                 <- NA;
    x$SI_HE[-c(1,nrow(x))]  <- bearing(x$SI_LONG[2:(nrow(x)-1)],x$SI_LATI[2:(nrow(x)-1)],x$SI_LONG[3:nrow(x)],x$SI_LATI[3:nrow(x)])
    x$HL_ID                 <- tacsat$HL_ID[idx[1]]
    return(x[-c(1,2,nrow(x)),])})
  
  #interpolationTot  <- do.call(rbind,res)
  interpolationTot  <- res[[1]][,which(duplicated(colnames(res[[1]]))==FALSE)]
  if(length(res)>1){
    for(i in 2:length(res)){
      if(nrow(res[[i]])>0)
        interpolationTot  <- rbindTacsat(interpolationTot,res[[i]][,which(duplicated(colnames(res[[i]]))==FALSE)])
    }
  }
  #tacsatInt         <- rbind(interpolationTot,tacsat[,colnames(interpolationTot)])
  tacsatInt         <- rbindTacsat(tacsat,interpolationTot)
  tacsatInt         <- sortTacsat(tacsatInt)
  
  return(tacsatInt)
  
}

`sortTacsat` <-
  function(dat){
    require(doBy)
    
    if(!"SI_DATIM" %in% colnames(dat)) dat$SI_DATIM  <- as.POSIXct(paste(dat$SI_DATE,  dat$SI_TIME,   sep=" "), tz="GMT", format="%d/%m/%Y  %H:%M")
    
    #Sort the tacsat data first by ship, then by date
    if("VE_REF" %in% colnames(dat)) dat <- orderBy(~VE_REF+SI_DATIM,data=dat)
    if("OB_REF" %in% colnames(dat)) dat <- orderBy(~OB_REF+SI_DATIM,data=dat)
    
    return(dat)}

`lonLatRatio` <-
  function(x1,lat){
    #Based on the Haversine formula
    #At the position, the y-position remains the same, hence, cos(lat)*cos(lat) instead of cos(lat) * cos(y2)
    a <- cos(lat*pi/180)*cos(lat*pi/180)*sin((0.1*pi/180)/2)*sin((0.1*pi/180)/2);
    c <- 2*atan2(sqrt(a),sqrt(1-a));
    R <- 6371;
    dx1 <- R*c
    
    return(c(dx1/11.12))}


`an` <-
  function(x){return(as.numeric(x))}


`findEndTacsat` <-
  function(tacsat
           ,startTacsat #Starting point of VMS
           ,interval #Specify in minutes, NULL means use all points  
           ,margin   #Specify the margin in minutes it might deviate from the interval time, in minutes
  ){
    VMS         <- tacsat
    if(!"SI_DATIM" %in% colnames(VMS)) VMS$SI_DATIM   <- as.POSIXct(paste(tacsat$SI_DATE,  tacsat$SI_TIME,   sep=" "), tz="GMT", format="%d/%m/%Y  %H:%M")
    
    startVMS    <- startTacsat
    clStartVMS  <- startVMS #Total VMS list starting point instead of subset use
    iShip       <- VMS$VE_REF[startVMS]
    VMS.        <- subset(VMS,VE_REF==iShip)
    startVMS    <- which(VMS$VE_REF[startVMS] == VMS.$VE_REF & VMS$SI_DATIM[startVMS] == VMS.$SI_DATIM)
    if(clStartVMS != dim(VMS)[1]){
      if(VMS$VE_REF[clStartVMS] != VMS$VE_REF[clStartVMS+1]){
        #End of dataset reached
        endDataSet <- 1
        endVMS <- NA
      } else {
        #Calculate the difference in time between the starting VMS point and its succeeding points
        diffTime  <- difftime(VMS.$SI_DATIM[(startVMS+1):dim(VMS.)[1]],VMS.$SI_DATIM[startVMS],units=c("mins"))
        if(length(which(diffTime >= (interval-margin) & diffTime <= (interval+margin)))==0){
          warning("No succeeding point found, no interpolation possible")
          endVMS  <- NA
          #Check if end of dataset has been reached
          ifelse(all((diffTime < (interval-margin))==TRUE),endDataSet <- 1,endDataSet <- 0)
        } else {
          res <- which(diffTime >= (interval-margin) & diffTime <= (interval+margin))
          if(length(res)>1){
            res2        <- which.min(abs(interval-an(diffTime[res])))
            endVMS      <- startVMS + res[res2]
            endDataSet  <- 0
          } else {
            endVMS      <- startVMS + res
            endDataSet  <- 0
          }
        }
        #Build-in check
        if(is.na(endVMS)==FALSE){
          if(!an(difftime(VMS.$SI_DATIM[endVMS],VMS.$SI_DATIM[startVMS],units=c("mins"))) %in% seq((interval-margin),(interval+margin),1)) stop("found endVMS point not within interval range")
          endVMS <- clStartVMS + (endVMS - startVMS)
        }
        
      }
    } else { endDataSet <- 1; endVMS <- NA}
    
    return(c(endVMS,endDataSet))}

`interpolateTacsat` <-
function(tacsat                          #VMS datapoints
                              ,interval=120             #Specify in minutes, NULL means use all points
                              ,margin=12                #Specify the margin in minutes that the interval might deviate in a search for the next point
                              ,res=100                  #Resolution of interpolation method (default = 100)
                              ,method="cHs"             #Specify the method to be used: Straight line (SL) of cubic Hermite spline (cHs)
                              ,params=list(fm=0.5,distscale=20,sigline=0.2,st=c(2,6))  #Specify the three parameters: fm, distscale, sigline, speedthreshold
                              ,headingAdjustment=0
                              ,fast=FALSE){

if(!"SI_DATIM" %in% colnames(tacsat)) tacsat$SI_DATIM     <- as.POSIXct(paste(tacsat$SI_DATE,  tacsat$SI_TIME,   sep=" "), tz="GMT", format="%d/%m/%Y  %H:%M")
                              
  #Start interpolating the data
if(!method %in% c("cHs","SL"))  stop("method selected that does not exist")

#-------------------------------------------------------------------------------
#Fast method or not
#-------------------------------------------------------------------------------
if(fast){
  #Interpolation only by vessel, so split tacsat up
  tacsat$ID <- 1:nrow(tacsat)
  splitTa   <- split(tacsat,tacsat$VE_REF)
  spltTaCon <- lapply(splitTa,function(spltx){
                  #Calculate time different between every record
                  dftimex <- outer(spltx$SI_DATIM,spltx$SI_DATIM,difftime,units="mins")
                  iStep   <- 1
                  connect <- list()
                  counter <- 1
                  #Loop over all possible combinations and store if a connection can be made
                  while(iStep <= nrow(spltx)){
                    endp <- which(dftimex[,iStep] >= (interval - margin) & dftimex[,iStep] <= (interval + margin))
                    if(length(endp)>0){
                      if(length(endp)>1) endp <- endp[which.min(abs(interval - dftimex[endp,iStep]))][1]
                      connect[[counter]]    <- c(iStep,endp)
                      counter               <- counter + 1
                      iStep                 <- endp
                    } else { iStep          <- iStep + 1}
                  }
                  #Return matrix of conenctions
                  return(do.call(rbind,connect))})

  if(method=="cHs") returnInterpolations <- unlist(lapply(as.list(names(unlist(lapply(spltTaCon,nrow)))),function(y){
                                              return(interCubicHermiteSpline(spltx=splitTa[[y]],spltCon=spltTaCon[[y]],res,params,headingAdjustment))}),recursive=FALSE)
  if(method=="SL")  returnInterpolations <- unlist(lapply(as.list(names(unlist(lapply(spltTaCon,nrow)))),function(y){
                                              return(interStraightLine(splitTa[[y]],spltTaCon[[y]],res))}),recursive=FALSE)

} else {
  

    #Initiate returning result object
  returnInterpolations <- list()

    #Start iterating over succeeding points
  for(iStep in 1:(dim(tacsat)[1]-1)){
    if(iStep == 1){
      iSuccess    <- 0
      endDataSet  <- 0
      startVMS    <- 1
      ship        <- tacsat$VE_REF[startVMS]
    } else {
        if(is.na(endVMS)==TRUE) endVMS <- startVMS + 1
        startVMS <- endVMS
        #-Check if the end of the dataset is reached
        if(endDataSet == 1 & rev(unique(tacsat$VE_REF))[1] != ship){
          startVMS  <- which(tacsat$VE_REF == unique(tacsat$VE_REF)[which(unique(tacsat$VE_REF)==ship)+1])[1]
          ship      <- tacsat$VE_REF[startVMS]
          endDataSet<- 0
        }
        if(endDataSet == 1 & rev(unique(tacsat$VE_REF))[1] == ship) endDataSet <- 2 #Final end of dataset
      }

    #if end of dataset is not reached, try to find succeeding point
    if(endDataSet != 2){
      result      <- findEndTacsat(tacsat,startVMS,interval,margin)
      endVMS      <- result[1]
      endDataSet  <- result[2]
      if(is.na(endVMS)==TRUE) int <- 0  #No interpolation possible
      if(is.na(endVMS)==FALSE) int <- 1  #Interpolation possible

        #Interpolate according to the Cubic Hermite Spline method
      if(method == "cHs" & int == 1){

          #Define the cHs formula
        F00 <- numeric()
        F10 <- numeric()
        F01 <- numeric()
        F11 <- numeric()
        i   <- 0
        t   <- seq(0,1,length.out=res)
        F00 <- 2*t^3 -3*t^2 + 1
        F10 <- t^3-2*t^2+t
        F01 <- -2*t^3+3*t^2
        F11 <- t^3-t^2

        if (is.na(tacsat[startVMS,"SI_HE"])=="TRUE") tacsat[startVMS,"SI_HE"] <- 0
        if (is.na(tacsat[endVMS,  "SI_HE"])=="TRUE") tacsat[endVMS,  "SI_HE"] <- 0

          #Heading at begin point in degrees
        Hx0 <- sin(tacsat[startVMS,"SI_HE"]/(180/pi))
        Hy0 <- cos(tacsat[startVMS,"SI_HE"]/(180/pi))
          #Heading at end point in degrees
        Hx1 <- sin(tacsat[endVMS-headingAdjustment,"SI_HE"]/(180/pi))
        Hy1 <- cos(tacsat[endVMS-headingAdjustment,"SI_HE"]/(180/pi))

        Mx0 <- tacsat[startVMS, "SI_LONG"]
        Mx1 <- tacsat[endVMS,   "SI_LONG"]
        My0 <- tacsat[startVMS, "SI_LATI"]
        My1 <- tacsat[endVMS,   "SI_LATI"]

          #Corrected for longitude lattitude effect
        Hx0 <- Hx0 * params$fm * tacsat[startVMS,"SI_SP"] /((params$st[2]-params$st[1])/2+params$st[1])
        Hx1 <- Hx1 * params$fm * tacsat[endVMS,"SI_SP"]   /((params$st[2]-params$st[1])/2+params$st[1])
        Hy0 <- Hy0 * params$fm * lonLatRatio(tacsat[c(startVMS,endVMS),"SI_LONG"],tacsat[c(startVMS,endVMS),"SI_LATI"])[1] * tacsat[startVMS,"SI_SP"]/((params$st[2]-params$st[1])/2+params$st[1])
        Hy1 <- Hy1 * params$fm * lonLatRatio(tacsat[c(startVMS,endVMS),"SI_LONG"],tacsat[c(startVMS,endVMS),"SI_LATI"])[2] * tacsat[endVMS,"SI_SP"]/((params$st[2]-params$st[1])  /2+params$st[1])

          #Finalizing the interpolation based on cHs
        fx <- numeric()
        fy <- numeric()
        fx <- F00*Mx0+F10*Hx0+F01*Mx1+F11*Hx1
        fy <- F00*My0+F10*Hy0+F01*My1+F11*Hy1

          #Add one to list of successful interpolations
        iSuccess <- iSuccess + 1
        returnInterpolations[[iSuccess]] <- matrix(rbind(c(startVMS,endVMS),cbind(fx,fy)),ncol=2,dimnames=list(c("startendVMS",seq(1,res,1)),c("x","y")))
      }

        #Interpolate according to a straight line
      if(method == "SL" & int == 1){
        fx <- seq(tacsat$SI_LONG[startVMS],tacsat$SI_LONG[endVMS],length.out=res)
        fy <- seq(tacsat$SI_LATI[startVMS],tacsat$SI_LATI[endVMS],length.out=res)

          #Add one to list of successful interpolations
        iSuccess <- iSuccess + 1
        returnInterpolations[[iSuccess]] <- matrix(rbind(c(startVMS,endVMS),cbind(fx,fy)),ncol=2,dimnames=list(c("startendVMS",seq(1,res,1)),c("x","y")))
      }
    }
  }
}

return(returnInterpolations)}


cat("Loading Table\n")
tacsatX <-read.table(inputFile,sep=",",header=T)
cat("Adjusting Columns Types\n")
tacsatX<-transform(tacsatX,  VE_COU= as.character(VE_COU), VE_REF= as.character(VE_REF), SI_LATI= as.numeric(SI_LATI), SI_LONG= as.numeric(SI_LONG), SI_DATE= as.character(SI_DATE),SI_TIME= as.character(SI_TIME),SI_SP= as.numeric(SI_SP),SI_HE= as.numeric(SI_HE))
tacsatX$SI_DATIM=NULL
cat("Sorting dataset\n")
tacsatS <- sortTacsat(tacsatX)
tacsatCut<-tacsatS
tacsatCut <- tacsatS[1:1000,]

cat("Interpolating\n")
interpolation <- interpolateTacsat(tacsatCut,interval=interval,margin=margin,res=res, method=method,params=list(fm=fm,distscale=distscale,sigline=sigline,st=st),headingAdjustment=headingAdjustment,fast=fast)
cat("Reconstructing Dataset\n")
tacsatInt <- interpolation2Tacsat(interpolation=interpolation,tacsat=tacsatCut,npoints=npoints,equalDist=equalDist)
tacsatInt <- sortTacsat(tacsatInt)
cat("Writing output file\n")
write.csv(tacsatInt, outputFile, row.names=T)
print(Sys.time())
cat("All Done.\n")
