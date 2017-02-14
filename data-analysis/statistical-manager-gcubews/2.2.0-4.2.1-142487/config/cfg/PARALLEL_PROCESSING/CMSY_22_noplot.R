##--------------------------------------------------------
## CMSY analysis with estimation of total biomass, including Bayesian Schaefer 
## written by Rainer Froese with support from Gianpaolo Coro in 2013-2014
## This version adjusts biomass to average biomass over the year
## It also contains the FutureCrash option to improve prediction of final biomass
## Version 21 adds the purple point to indicate the 25th percentile of final biomass
## Version 22 accepts that no biomass or CPUE area available
##--------------------------------------------------------
library(R2jags)  # Interface with JAGS
library(coda) 

#-----------------------------------------
# Some general settings
#-----------------------------------------
# set.seed(999) # use for comparing results between runs
rm(list=ls(all=TRUE)) # clear previous variables etc
options(digits=3) # displays all numbers with three significant digits as default
graphics.off() # close graphics windows from previous sessions

#-----------------------------------------
# General settings for the analysis
#-----------------------------------------
sigR         <- 0.02 # overall process error; 0.05 works reasonable for simulations, 0.02 for real data; 0 if deterministic model
n            <- 10000 # initial number of r-k pairs
batch.mode   <- T # set to TRUE to suppress graphs
write.output <- T # set to true if table of output is wanted
FutureCrash  <- "No"

#-----------------------------------------
# Start output to screen
#-----------------------------------------
cat("-------------------------------------------\n")
cat("Catch-MSY Analysis,", date(),"\n")
cat("-------------------------------------------\n")

#------------------------------------------
# Read data and assign to vectors
#------------------------------------------
# filename_1  <- "AllStocks_Catch4.csv"
# filename_2  <- "AllStocks_ID4.csv"
# filename_1  <-  "SimCatch.csv"
# filename_2  <-  "SimSpec.csv"
# filename_2  <-  "SimSpecWrongS.csv"
# filename_2  <-  "SimSpecWrongI.csv"
# filename_2  <-  "SimSpecWrongF.csv"
# filename_2  <-  "SimSpecWrongH.csv"
# filename_2  <-  "SimSpecWrongL.csv"
# filename_1  <-  "FishDataLim.csv"
# filename_2  <-  "FishDataLimSpec.csv" 
 filename_1  <-  "WKLIFE4Stocks.csv"
 filename_2  <-  "WKLIFE4ID.csv"

outfile<-"outfile"
outfile.txt <- "outputfile.txt"

cdat         <- read.csv(filename_1, header=T, dec=".", stringsAsFactors = FALSE)
cinfo        <- read.csv(filename_2, header=T, dec=".", stringsAsFactors = FALSE)
cat("Files", filename_1, ",", filename_2, "read successfully","\n")

# Stocks with total biomass data and catch data from StartYear to EndYear
# stocks       <- sort(as.character(cinfo$stock)) # All stocks 
stocks<-"HLH_M07"

# select one stock after the other
for(stock in stocks) {
  # assign data from cinfo to vectors
  res          <- as.character(cinfo$Resilience[cinfo$stock==stock])
  StartYear    <- as.numeric(cinfo$StartYear[cinfo$stock==stock])
  EndYear      <- as.numeric(cinfo$EndYear[cinfo$stock==stock])
  r_low        <- as.numeric(cinfo$r_low[cinfo$stock==stock])
  r_hi         <- as.numeric(cinfo$r_hi[cinfo$stock==stock])
  stb_low      <- as.numeric(cinfo$stb_low[cinfo$stock==stock])
  stb_hi       <- as.numeric(cinfo$stb_hi[cinfo$stock==stock])
  intyr        <- as.numeric(cinfo$intyr[cinfo$stock==stock])
  intbio_low   <- as.numeric(cinfo$intbio_low[cinfo$stock==stock])
  intbio_hi    <- as.numeric(cinfo$intbio_hi[cinfo$stock==stock])
  endbio_low   <- as.numeric(cinfo$endbio_low[cinfo$stock==stock])
  endbio_hi    <- as.numeric(cinfo$endbio_hi[cinfo$stock==stock])
  Btype        <- as.character(cinfo$Btype[cinfo$stock==stock])
  FutureCrash  <- as.character(cinfo$FutureCrash[cinfo$stock==stock])
  comment      <- as.character(cinfo$comment[cinfo$stock==stock])
  
  
  # extract data on stock
  yr           <- as.numeric(cdat$yr[cdat$stock==stock & cdat$yr >= StartYear & cdat$yr <= EndYear])
  ct           <- as.numeric(cdat$ct[cdat$stock==stock & cdat$yr >= StartYear & cdat$yr <= EndYear])/1000  ## assumes that catch is given in tonnes, transforms to '000 tonnes
  if(Btype=="observed" | Btype=="CPUE" | Btype=="simulated") {
    bt <- as.numeric(cdat$TB[cdat$stock==stock & cdat$yr >= StartYear & cdat$yr <= EndYear])/1000  ## assumes that biomass is in tonnes, transforms to '000 tonnes
  } else {bt <- NA}
  nyr          <- length(yr) # number of years in the time series


if(Btype!="observed") {bio <- bt} 
# change biomass to moving average as assumed by Schaefer (but not for simulations or CPUE)
# for last year use reported bio
if(Btype=="observed") {
 ma               <- function(x){filter(x,rep(1/2,2),sides=2)}
 bio              <- ma(bt)
 bio[length(bio)] <- bt[length(bt)] }
  
  # initialize vectors for viable r, k, bt
  rv.all      <- vector()
  kv.all      <- vector()
  btv.all      <- matrix(data=vector(),ncol=nyr+1)
  
 
  
  #----------------------------------------------------
  # Determine initial ranges for parameters and biomass
  #----------------------------------------------------
  # initial range of r from input file
  if(is.na(r_low)==F & is.na(r_hi)==F) {
    start_r <- c(r_low,r_hi)
  } else {
    # initial range of r and CatchMult values based on resilience
    if(res == "High") {
      start_r <- c(0.6,1.5)} else if(res == "Medium") {
        start_r <- c(0.2,0.8)}    else if(res == "Low") {
          start_r <- c(0.05,0.5)}  else { # i.e. res== "Very low"
            start_r <- c(0.015,0.1)} 
  }
  
  
  # initial range of k values, assuming k will always be larger than max catch 
  # and max catch will never be smaller than a quarter of MSY
  
  start_k     <- c(max(ct),16*max(ct)/start_r[1]) 
  
  # initial biomass range from input file
  if(is.na(stb_low)==F & is.na(stb_hi)==F) {
    startbio <- c(stb_low,stb_hi)
  } else {
    # us low biomass at start as default
    startbio <- c(0.1,0.5)
  }
  
  MinYear     <- yr[which.min(ct)]
  MaxYear     <- yr[which.max(ct)]
  # use year and biomass range for intermediate biomass from input file
  if(is.na(intbio_low)==F & is.na(intbio_hi)==F) {
    intyr    <- intyr
    intbio   <- c(intbio_low,intbio_hi)
    # else if year of minimum catch is at least 3 years away from StartYear and EndYear of series, use min catch
  } else if((MinYear - StartYear) > 3 & (EndYear - MinYear) > 3 ) {
    # assume that biomass range in year before minimum catch was 0.01 - 0.4
    intyr    <- MinYear-1
    intbio <- c(0.01,0.4) 
    # else if year of max catch is at least 3 years away from StartYear and EndYear of series, use max catch  
  } else if((MaxYear - StartYear) > 3 & (EndYear - MaxYear) > 3 ) {
    # assume that biomass range in year before maximum catch was 0.3 - 0.9
    intyr    <- MaxYear-1
    intbio <- c(0.3,0.9)  
  } else {
    # assume uninformative range 0-1 in mid-year 
    intyr     <- as.integer(mean(c(StartYear, EndYear)))
    intbio    <- c(0,1) }
  # end of intbio setting
  
  # final biomass range from input file
  if(is.na(endbio_low)==F & is.na(endbio_hi)==F) {
    endbio   <- c(endbio_low,endbio_hi)
  } else {
    # else use Catch/maxCatch to estimate final biomass
    endbio  <- if(ct[nyr]/max(ct) > 0.5) {c(0.4,0.8)} else {c(0.01,0.4)} 
  } # end of final biomass setting

  
  #----------------------------------------------
  # MC with Schaefer Function filtering
  #----------------------------------------------
  Schaefer <- function(ri, ki, startbio, intyr, intbio, endbio, sigR, pt) {

    # if stock is not expected to crash within 3 years if last catch continues
    if(FutureCrash == "No") {
      yr.s     <- c(yr,EndYear+1,EndYear+2,EndYear+3)
      ct.s     <- c(ct,ct[yr==EndYear],ct[yr==EndYear],ct[yr==EndYear])
      nyr.s    <- length(yr.s)
    } else{
      yr.s     <- yr
      ct.s     <- ct
      nyr.s    <- nyr
    }
    
    # create vector for initial biomasses
    startbt     <-seq(from =startbio[1], to=startbio[2], by = (startbio[2]-startbio[1])/10)
    # create vectors for viable r, k and bt
    rv          <- array(-1:-1,dim=c(length(ri)*length(startbt))) #initialize array with -1. The -1 remaining after the process will be removed
    kv          <- array(-1:-1,dim=c(length(ri)*length(startbt)))
    btv         <- matrix(data=NA, nrow = (length(ri)*length(startbt)), ncol = nyr+1)
    intyr.i     <- which(yr.s==intyr) # get index of intermediate year
    
    #loop through r-k pairs
    npoints = length(ri)
    nstartb = length(startbt)

    for(i in 1 : npoints) {
      if (i%%1000==0)
        cat(".")
      
      # create empty vector for annual biomasses
      bt <- vector()
      
      # loop through range of relative start biomasses
      for(j in startbt) {    
        # set initial biomass, including process error
        bt[1]=j*ki[i]*exp(rnorm(1,0, sigR))  ## set biomass in first year
        
        #loop through years in catch time series
        for(t in 1:nyr.s) {  # for all years in the time series
          xt=rnorm(1,0, sigR) # set new random process error for every year  
          
          # calculate biomass as function of previous year's biomass plus surplus production minus catch
          bt[t+1]=(bt[t]+ri[i]*bt[t]*(1-bt[t]/ki[i])-ct.s[t])*exp(xt) 
          
          # if biomass < 0.01 k or > 1.1 k, discard r-k pair
          if(bt[t+1] < 0.01*ki[i] || bt[t+1] > 1.1*ki[i]) { break } # stop looping through years, go to next upper level
          
          if ((t+1)==intyr.i && (bt[t+1]>(intbio[2]*ki[i]) || bt[t+1]<(intbio[1]*ki[i]))) { break }  #intermediate year check
          
        } # end of loop of years
        
        # if last biomass falls without expected ranges goto next r-k pair
        if(t < nyr.s || bt[yr.s==EndYear] > (endbio[2]*ki[i]) || bt[yr.s==EndYear] < (endbio[1]*ki[i])) {
          next } else { 
            # store r, k, and bt, plot point, then go to next startbt
            rv[((i-1)*nstartb)+j]   <- ri[i]
            kv[((i-1)*nstartb)+j]   <- ki[i]
            btv[((i-1)*nstartb)+j,] <- bt[1:(nyr+1)]/ki[i] #substitute a row into the matrix, exclude FutureCrash years
            if(pt==T) {points(x=ri[i], y=ki[i], pch=".", cex=2, col="black")
            next }
          }
      } # end of loop of initial biomasses 
    } # end of loop of r-k pairs 
    
    rv=rv[rv!=-1]
    kv=kv[kv!=-1]
    btv=na.omit(btv) #delete first line
    
    cat("\n")
    return(list(rv, kv,btv))
  } # end of Schaefer function
    
  #------------------------------------------------------------------
  # Uniform sampling of the r-k space
  #------------------------------------------------------------------
  # get random set of r and k from log space distribution 
  ri1 = exp(runif(n, log(start_r[1]), log(start_r[2])))  
  ki1 = exp(runif(n, log(start_k[1]), log(start_k[2])))  
  
  #-----------------------------------------------------------------
  # Plot data and progress
  #-----------------------------------------------------------------
  #windows(14,9)
  par(mfcol=c(2,3))
  # plot catch
  plot(x=yr, y=ct, ylim=c(0,1.2*max(ct)), type ="l", bty="l", main=paste(stock,"catch"), xlab="Year", 
       ylab="Catch", lwd=2)
  points(x=yr[which.max(ct)], y=max(ct), col="red", lwd=2)
  points(x=yr[which.min(ct)], y=min(ct), col="red", lwd=2)
  
  # plot r-k graph
  plot(ri1, ki1, xlim = start_r, ylim = start_k, log="xy", xlab="r", ylab="k", main="Finding viable r-k", pch=".", cex=2, bty="l", col="lightgrey")
  
  #1 - Call MC-Schaefer function to preliminary explore the space without prior information
  cat(stock, ": First Monte Carlo filtering of r-k space with ",n," points\n")
  MCA <-  Schaefer(ri=ri1, ki=ki1, startbio=startbio, intyr=intyr, intbio=intbio, endbio=endbio, sigR=sigR, pt=T)
  rv.all  <- append(rv.all,MCA[[1]])
  kv.all  <- append(kv.all,MCA[[2]])
  btv.all  <- rbind(btv.all,MCA[[3]])
  #take viable r and k values 
  nviablepoints = length(rv.all)
  cat("* Found ",nviablepoints," viable points from ",n," samples\n");

  
  #if few points were found then resample and shrink the k log space
  if (nviablepoints<=1000){
    log.start_k.new  <- log(start_k) 
    max_attempts = 3
    current_attempts = 1
    while (nviablepoints<=1000 && current_attempts<=max_attempts){
      if(nviablepoints > 0) {
      log.start_k.new[1] <- mean(c(log.start_k.new[1], min(log(kv.all))))
      log.start_k.new[2] <- mean(c(log.start_k.new[2], max(log(kv.all)))) }
      n.new=n*current_attempts #add more points
      ri1 = exp(runif(n.new, log(start_r[1]), log(start_r[2])))  
      ki1 = exp(runif(n.new, log.start_k.new[1], log.start_k.new[2]))
      cat("Shrinking k space: repeating Monte Carlo in the interval [",exp(log.start_k.new[1]),",",exp(log.start_k.new[2]),"]\n")
      cat("Attempt ",current_attempts," of ",max_attempts," with ",n.new," points","\n")
      MCA <-  Schaefer(ri=ri1, ki=ki1, startbio=startbio, intyr=intyr, intbio=intbio, endbio=endbio, sigR=sigR, pt=T)
      rv.all  <- append(rv.all,MCA[[1]])
      kv.all  <- append(kv.all,MCA[[2]])
      btv.all  <- rbind(btv.all,MCA[[3]])
      nviablepoints = length(rv.all) #recalculate viable points
      cat("* Found altogether",nviablepoints," viable points \n");
      current_attempts=current_attempts+1 #increment the number of attempts
    }
  }

  # If tip of viable r-k pairs is 'thin', do extra sampling there
  gm.rv = exp(mean(log(rv.all)))
  if(length(rv.all[rv.all > 0.9*start_r[2]]) < 10) { 
  l.sample.r        <- (gm.rv + max(rv.all))/2
  cat("Final sampling in the tip area above r =",l.sample.r,"\n")
  log.start_k.new <- c(log(0.8*min(kv.all)),log(max(kv.all[rv.all > l.sample.r])))
  ri1 = exp(runif(50000, log(l.sample.r), log(start_r[2])))  
  ki1 = exp(runif(50000, log.start_k.new[1], log.start_k.new[2]))
  MCA <-  Schaefer(ri=ri1, ki=ki1, startbio=startbio, intyr=intyr, intbio=intbio, endbio=endbio, sigR=sigR, pt=T)
  rv.all  <- append(rv.all,MCA[[1]])
  kv.all  <- append(kv.all,MCA[[2]])
  btv.all  <- rbind(btv.all,MCA[[3]])
  nviablepoints = length(rv.all) #recalculate viable points
  cat("Found altogether", length(rv.all), "unique viable r-k pairs and biomass trajectories\n")
  }


 # ------------------------------------------------------------
 # Bayesian analysis of catch & biomass with Schaefer model
 # ------------------------------------------------------------
 if(Btype == "observed" | Btype=="simulated") {
   cat("Running Schaefer MCMC analysis....\n")
   mcmc.burn <- as.integer(30000)
   mcmc.chainLength <- as.integer(60000)  # burn-in plus post-burn
   mcmc.thin = 10 # to reduce autocorrelation
   mcmc.chains = 3 # needs to be at least 2 for DIC
   
   # Parameters to be returned by JAGS
   jags.save.params=c('r','k','sigma.b', 'alpha', 'sigma.r') # 
   
   # JAGS model
   Model = "model{
   # to avoid crash due to 0 values
   eps<-0.01
   # set a quite narrow variation from the expected value  
   sigma.b <- 1/16
   tau.b   <- pow(sigma.b,-2)   

   Bm[1]   <- log(alpha*k)
   bio[1]  ~  dlnorm(Bm[1],tau.b)
   
   
   for (t in 2:nyr){
   bio[t]  ~  dlnorm(Bm[t],tau.b)
   Bm[t]   <- log(max(bio[t-1] + r*bio[t-1]*(1 - (bio[t-1])/k) - ct[t-1], eps))
   }
   
   # priors
  alpha              ~  dunif(0.01,1) # needed for fit of first biomass
  #inverse cubic root relationship between the range of viable r and the size of the search space 
  inverseRangeFactor <- 1/((start_r[2]-start_r[1])^1/3)
  
  # give sigma some variability in the inverse relationship 
  sigma.r             ~  dunif(0.001*inverseRangeFactor,0.02*inverseRangeFactor)
  tau.r               <- pow(sigma.r,-2)
  rm                  <- log((start_r[1]+start_r[2])/2)  
  r                   ~  dlnorm(rm,tau.r)
  
  # search in the k space from the center of the range. Allow high variability
  km                  <- log((start_k[1]+start_k[2])/2)
  tau.k               <- pow(km,-2)
  k                   ~  dlnorm(km,tau.k)
  
   #end model
 }"

   # Write JAGS model to file
   cat(Model, file="r2jags.bug")  
   
   ### random seed
   set.seed(runif(1,1,500)) # needed in JAGS 
   
   ### run model
   jags_outputs <- jags(data=c('ct','bio','nyr', 'start_r', 'start_k'), 
                        working.directory=NULL, inits=NULL, 
                        parameters.to.save= jags.save.params, 
                        model.file="r2jags.bug", n.chains = mcmc.chains, 
                        n.burnin = mcmc.burn, n.thin = mcmc.thin, n.iter = mcmc.chainLength,
                        refresh=mcmc.burn/20, )
   
   # ------------------------------------------------------
   # Results from JAGS Schaefer
   # ------------------------------------------------------
   r_out       <- as.numeric(mcmc(jags_outputs$BUGSoutput$sims.list$r))
   k_out       <- as.numeric(mcmc(jags_outputs$BUGSoutput$sims.list$k))
##   sigma_out   <- as.numeric(mcmc(jags_outputs$BUGSoutput$sims.list$sigma.b))
   alpha_out   <- as.numeric(mcmc(jags_outputs$BUGSoutput$sims.list$alpha))
##   sigma.r_out <- as.numeric(mcmc(jags_outputs$BUGSoutput$sims.list$sigma.r))
   
   mean.log.r.jags  <- mean(log(r_out)) 
   SD.log.r.jags    <- sd(log(r_out))
   lcl.log.r.jags   <- mean.log.r.jags-1.96*SD.log.r.jags
   ucl.log.r.jags   <- mean.log.r.jags+1.96*SD.log.r.jags
   gm.r.jags        <- exp(mean.log.r.jags) 
   lcl.r.jags       <- exp(lcl.log.r.jags)
   ucl.r.jags       <- exp(ucl.log.r.jags)
   mean.log.k.jags  <- mean(log(k_out)) 
   SD.log.k.jags    <- sd(log(k_out))
   lcl.log.k.jags   <- mean.log.k.jags-1.96*SD.log.k.jags
   ucl.log.k.jags   <- mean.log.k.jags+1.96*SD.log.k.jags
   gm.k.jags        <- exp(mean.log.k.jags) 
   lcl.k.jags       <- exp(lcl.log.k.jags)
   ucl.k.jags       <- exp(ucl.log.k.jags)
   mean.log.MSY.jags<- mean(log(r_out)+log(k_out)-log(4))
   SD.log.MSY.jags  <- sd(log(r_out)+log(k_out)-log(4))
   gm.MSY.jags      <- exp(mean.log.MSY.jags)
   lcl.MSY.jags     <- exp(mean.log.MSY.jags-1.96*SD.log.MSY.jags)
   ucl.MSY.jags     <- exp(mean.log.MSY.jags+1.96*SD.log.MSY.jags)
 
} # end of MCMC Schaefer loop 

#------------------------------------
# get results from CMSY
#------------------------------------
# get estimate of most probable r as median of mid log.r-classes above cut-off
# get remaining viable log.r and log.k 
rem.log.r      <- log(rv.all[rv.all > gm.rv])
rem.log.k      <- log(kv.all[rv.all>gm.rv])
# get vectors with numbers of r and mid values in about 25 classes
hist.log.r      <- hist(x=rem.log.r, breaks=25, plot=F)
log.r.counts    <- hist.log.r$counts
log.r.mids      <- hist.log.r$mids
# get most probable log.r as mean of mids with counts > 0
log.r.est       <- median(log.r.mids[which(log.r.counts > 0)])
lcl.log.r       <- as.numeric(quantile(x=log.r.mids[which(log.r.counts > 0)], 0.025))
ucl.log.r       <- as.numeric(quantile(x=log.r.mids[which(log.r.counts > 0)], 0.975))
r.est           <- exp(log.r.est)
lcl.r.est       <- exp(lcl.log.r)
ucl.r.est       <- exp(ucl.log.r)

# do linear regression of log k ~ log r with slope fixed to -1 (from Schaefer)
reg         <- lm(rem.log.k ~ 1 + offset(-1*rem.log.r))
int.reg     <- as.numeric(reg[1])
sd.reg      <- sd(resid(reg))
se.reg      <- summary(reg)$coefficients[2]
# get estimate of log(k) from y where x = log.r.est
log.k.est   <- int.reg + (-1) * log.r.est
# get estimates of CL of log.k.est from y +/- SD where x = lcl.log r or ucl.log.r 
lcl.log.k   <- int.reg + (-1) * ucl.log.r - sd.reg
ucl.log.k   <- int.reg + (-1) * lcl.log.r + sd.reg
k.est       <- exp(log.k.est)
lcl.k.est   <- exp(lcl.log.k)
ucl.k.est   <- exp(ucl.log.k)

# get MSY from remaining log r-k pairs
log.MSY.est     <- mean(rem.log.r + rem.log.k - log(4))
sd.log.MSY.est  <- sd(rem.log.r + rem.log.k - log(4))
lcl.log.MSY.est <- log.MSY.est - 1.96*sd.log.MSY.est 
ucl.log.MSY.est <- log.MSY.est + 1.96*sd.log.MSY.est
MSY.est         <- exp(log.MSY.est)
lcl.MSY.est     <- exp(lcl.log.MSY.est)
ucl.MSY.est     <- exp(ucl.log.MSY.est)

# get predicted biomass vectors as median and quantiles of trajectories
median.btv <- apply(btv.all,2, median)
lastyr.bio <- median.btv[length(median.btv)-1]
nextyr.bio <- median.btv[length(median.btv)]
lcl.btv    <- apply(btv.all,2, quantile, probs=0.025)
q.btv      <- apply(btv.all,2, quantile, probs=0.25)
ucl.btv    <- apply(btv.all,2, quantile, probs=0.975)
lcl.lastyr.bio <- lcl.btv[length(lcl.btv)-1]
ucl.lastyr.bio <- ucl.btv[length(lcl.btv)-1]  
lcl.nextyr.bio <- lcl.btv[length(lcl.btv)]
ucl.nextyr.bio <- ucl.btv[length(lcl.btv)]

# -----------------------------------------
# Plot results 
# -----------------------------------------
# Analysis of viable r-k pairs
plot(x=rv.all, y=kv.all, xlim=start_r, 
     ylim=c(0.9*min(kv.all, ifelse(Btype == "observed",k_out,NA), na.rm=T), 1.1*max(kv.all)), 
     pch=16, col="grey",log="xy", bty="l",
     xlab="r", ylab="k", main="Analysis of viable r-k")
abline(v=gm.rv, lty="dashed")

# plot points and best estimate from full Schaefer analysis
if(Btype == "observed"|Btype=="simulated") {
  # plot r-k pairs from MCMC
  points(x=r_out, y=k_out, pch=16,cex=0.5)
  # plot best r-k pair from MCMC
  points(x=gm.r.jags, y=gm.k.jags, pch=19, col="green")  
  lines(x=c(lcl.r.jags, ucl.r.jags),y=c(gm.k.jags,gm.k.jags), col="green")
  lines(x=c(gm.r.jags,gm.r.jags),y=c(lcl.k.jags, ucl.k.jags), col="green")
}

# if data are from simulation, plot true r and k
if(Btype=="simulated") {
  l.stock <- nchar(stock) # get length of sim stock name
  r.char  <- substr(stock,l.stock-1,l.stock)  # get last character of sim stock name
  r.sim   <- NA # initialize vector for r used in simulation
  if(r.char=="_H") {r.sim=1; lcl.r.sim=0.8; ucl.r.sim=1.25} else 
    if(r.char=="_M") {r.sim=0.5;lcl.r.sim=0.4;ucl.r.sim=0.62} else
      if(r.char=="_L") {r.sim=0.25;lcl.r.sim=0.2;ucl.r.sim=0.31} else {r.sim=0.05;lcl.r.sim=0.04;ucl.r.sim=0.062}
  # plot true r-k point with error bars
  points(x=r.sim, y=1000, pch=19, col="red")
  # add +/- 20% error bars  
  lines(x=c(lcl.r.sim,ucl.r.sim), y=c(1000,1000), col="red")
  lines(x=c(r.sim,r.sim), y=c(800,1250), col="red")
}

# plot blue dot for proposed r-k, with 95% CL lines 
points(x=r.est, y=k.est, pch=19, col="blue")
lines(x=c(lcl.r.est, ucl.r.est),y=c(k.est,k.est), col="blue")
lines(x=c(r.est,r.est),y=c(lcl.k.est, ucl.k.est), col="blue")

# plot biomass graph
# determine k to use for red line in b/k plot 
if(Btype=="simulated") {k2use <- 1000} else 
      if(Btype == "observed")  {k2use <- gm.k.jags} else {k2use <- k.est}
# determine hight of y-axis in plot
max.y  <- max(c(bio/k2use,ucl.btv,0.6,startbio[2], intbio[2],endbio[2]),na.rm=T)

plot(x=yr,y=median.btv[1:nyr], lwd=2, xlab="Year", ylab="Relative biomass b/k", type="l",
     ylim=c(0,max.y), bty="l", main=paste("Pred. biomass vs ", Btype,sep=""))
lines(x=yr, y=lcl.btv[1:nyr],type="l")
lines(x=yr, y=ucl.btv[1:nyr],type="l")
points(x=EndYear,y=q.btv[yr==EndYear], col="purple", cex=1.5, lwd=2)
abline(h=0.5, lty="dashed")
abline(h=0.25, lty="dotted")
lines(x=c(yr[1],yr[1]), y=startbio, col="blue")
lines(x=c(intyr,intyr), y=intbio, col="blue")  
lines(x=c(max(yr),max(yr)), y=endbio, col="blue")  

# if observed biomass is available, plot red biomass line
if(Btype == "observed"|Btype=="simulated") {
  lines(x=yr, y=bio/k2use,type="l", col="red", lwd=1) 
 }

# if CPUE data are available, scale to predicted biomass range, plot red biomass line
if(Btype == "CPUE") {
  par(new=T) # prepares for new plot on top of previous
  plot(x=yr, y=bio, type="l", col="red", lwd=1, 
    ann=F,axes=F,ylim=c(0,1.2*max(bio, na.rm=T))) # forces this plot on top of previous one
  axis(4, col="red", col.axis="red")
}

# plot yield and biomass against equilibrium surplus parabola
max.y <-max(c(ct/MSY.est,ifelse(Btype=="observed"|Btype=="simulated",ct/gm.MSY.jags,NA),1.2),na.rm=T)
# plot parabola
x=seq(from=0,to=2,by=0.001)
y=4*x-(2*x)^2
plot(x=x, y=y, xlim=c(0,1), ylim=c(0,max.y), type="l", bty="l",xlab="Relative biomass b/k", 
     ylab="Catch / MSY", main="Equilibrium curve")
# plot catch against CMSY biomass estimates
points(x=median.btv[1:nyr], y=ct/MSY.est, pch=16, col="grey")
points(x=q.btv[yr==EndYear],y=ct[yr==EndYear]/MSY.est, col="purple", cex=1.5, lwd=2)
# plot catch against observed biomass or CPUE
if(Btype == "observed"|Btype=="simulated") {
  points(x=bio/k2use, y=ct/gm.MSY.jags, pch=16, cex=0.5)
}

# plot exploitation rate u against u.msy
# get u derived from predicted CMSY biomass
u.CMSY <- ct/(median.btv[1:nyr]*k.est)
u.msy.CMSY  <- 1-exp(-r.est/2) # # Fmsy from CMSY expressed as exploitation rate
# get u from observed or simulated biomass
if(Btype == "observed"|Btype=="simulated") {
  u.bio       <- ct/bio
  u.msy.bio   <- 1-exp(-gm.r.jags/2)
}
# get u from CPUE
if(Btype == "CPUE") {
  q=max(median.btv[1:nyr][is.na(bio)==F],na.rm=T)*k.est/max(bio,na.rm=T)
  u.CPUE      <- ct/(q*bio)
}

# determine upper bound of Y-axis
max.y <- max(c(1.5, 1.2*u.CMSY/u.msy.CMSY,ct[yr==EndYear]/(q.btv[yr==EndYear]*k.est)/u.msy.CMSY,
               ifelse(Btype=="observed"|Btype=="simulated",max(u.bio[is.na(u.bio)==F]/u.msy.bio),0),
               na.rm=T))
# plot u from CMSY
plot(x=yr,y=u.CMSY/u.msy.CMSY, type="l", bty="l", ylim=c(0,max.y), xlab="Year", 
     ylab="u / u_msy", main="Exploitation rate")
abline(h=1, lty="dashed")
points(x=EndYear,y=ct[yr==EndYear]/(q.btv[yr==EndYear]*k.est)/u.msy.CMSY, col="purple", cex=1.5, lwd=2)
# plot u from biomass
if(Btype == "observed"|Btype=="simulated") lines(x=yr, y=u.bio/u.msy.bio, col="red")
# plot u from CPUE
if(Btype == "CPUE") {
  par(new=T) # prepares for new plot on top of previous
  plot(x=yr, y=u.CPUE, type="l", col="red", ylim=c(0, 1.2*max(u.CPUE,na.rm=T)),ann=F,axes=F)
  axis(4, col="red", col.axis="red")
}
if(batch.mode == TRUE) {dev.off()}    # close plot window  

# ------------------------------------------
# print input and results to screen
cat("---------------------------------------\n")

cat("Species:", cinfo$ScientificName[cinfo$stock==stock], "\n")
cat("Name and region:", cinfo$EnglishName[cinfo$stock==stock], ",", cinfo$Name[cinfo$stock==stock], "\n")
cat("Stock:",stock,"\n")
cat("Catch data used from years", min(yr),"-", max(yr), "\n")
cat("Prior initial relative biomass =", startbio[1], "-", startbio[2], "\n")
cat("Prior intermediate rel. biomass=", intbio[1], "-", intbio[2], "in year", intyr, "\n")
cat("Prior final relative biomass   =", endbio[1], "-", endbio[2], "\n")
cat("If current catches continue, is the stock likely to crash within 3 years?",FutureCrash,"\n")
cat("Prior range for r =", format(start_r[1],digits=2), "-", format(start_r[2],digits=2),  
    ", prior range for k =", start_k[1], "-", start_k[2],"\n")

# if data are simulated, print true r-k
if(filename_1=="SimCatch.csv") {
cat("True r =", r.sim, "(because input data were simulated with Schaefer model)\n")
cat("True k = 1000 \n")
cat("True MSY =", 1000*r.sim/4,"\n")
cat("True biomass in last year =",bio[length(bio)],"or",bio[length(bio)]/1000,"k \n")
cat("True mean catch / MSY ratio =", mean(ct)/(1000*r.sim/4),"\n")
}
# print results from full Schaefer if available
if(Btype == "observed"|Btype=="simulated") {
cat("Results from Bayesian Schaefer model using catch & biomass (",Btype,")\n")
cat("MSY =", gm.MSY.jags,", 95% CL =", lcl.MSY.jags, "-", ucl.MSY.jags,"\n")
cat("Mean catch / MSY =", mean(ct)/gm.MSY.jags,"\n")
if(Btype != "CPUE") {
  cat("r =", gm.r.jags,", 95% CL =", lcl.r.jags, "-", ucl.r.jags,"\n")
  cat("k =", gm.k.jags,", 95% CL =", lcl.k.jags, "-", ucl.k.jags,"\n")
  }
}
# results of CMSY analysis
cat("Results of CMSY analysis \n")
cat("Altogether", nviablepoints,"unique viable r-k pairs were found \n")
cat(nviablepoints-length(rem.log.r),"r-k pairs above the initial geometric mean of r =", gm.rv, "were analysed\n")
cat("r =", r.est,", 95% CL =", lcl.r.est, "-", ucl.r.est,"\n")
cat("k =", k.est,", 95% CL =", lcl.k.est, "-", ucl.k.est,"\n")
cat("MSY =", MSY.est,", 95% CL =", lcl.MSY.est, "-", ucl.MSY.est,"\n")
cat("Predicted biomass in last year =", lastyr.bio, "2.5th perc =", lcl.lastyr.bio, 
    "97.5th perc =", ucl.lastyr.bio,"\n")
cat("Predicted biomass in next year =", nextyr.bio, "2.5th perc =", lcl.nextyr.bio, 
    "97.5th perc =", ucl.nextyr.bio,"\n")
cat("----------------------------------------------------------\n")

## Write some results into outfile
if(write.output == TRUE) {
# write data into csv file
  output = data.frame(cinfo$ScientificName[cinfo$stock==stock], stock, StartYear, EndYear, mean(ct)*1000,
    ifelse(Btype=="observed"|Btype=="simulate",bio[length(bio)],NA), # last biomass on record 
    ifelse(Btype == "observed"|Btype=="simulated",gm.MSY.jags,NA), # full Schaefer
    ifelse(Btype == "observed"|Btype=="simulated",lcl.MSY.jags,NA),
    ifelse(Btype == "observed"|Btype=="simulated",ucl.MSY.jags,NA),
    ifelse(Btype == "observed"|Btype=="simulated",gm.r.jags,NA), 
    ifelse(Btype == "observed"|Btype=="simulated",lcl.r.jags,NA),
    ifelse(Btype == "observed"|Btype=="simulated",ucl.r.jags,NA),
    ifelse(Btype == "observed"|Btype=="simulated",gm.k.jags,NA),                                          
    ifelse(Btype == "observed"|Btype=="simulated",lcl.k.jags,NA),                    
    ifelse(Btype == "observed"|Btype=="simulated",ucl.k.jags,NA),
    r.est, lcl.r.est, ucl.r.est, # CMSY r
    k.est, lcl.k.est, ucl.k.est, # CMSY k     
    MSY.est, lcl.MSY.est, ucl.MSY.est, # CMSY r
    lastyr.bio, lcl.lastyr.bio, ucl.lastyr.bio, # last year bio
    nextyr.bio, lcl.nextyr.bio, ucl.nextyr.bio)# last year + 1 bio

    write.table(output, file=outfile, append = T, sep = ",", 
              dec = ".", row.names = FALSE, col.names = FALSE)

# write some text into text outfile.txt

cat("Species:", cinfo$ScientificName[cinfo$stock==stock], "\n",
  "Name:", cinfo$EnglishName[cinfo$stock==stock], "\n",
  "Region:", cinfo$Name[cinfo$stock==stock], "\n",
  "Stock:",stock,"\n", 
  "Catch data used from years", min(yr),"-", max(yr),", biomass =", Btype, "\n",
  "Prior initial relative biomass =", startbio[1], "-", startbio[2], "\n",
  "Prior intermediate rel. biomass=", intbio[1], "-", intbio[2], "in year", intyr, "\n",
  "Prior final relative biomass   =", endbio[1], "-", endbio[2], "\n",
  "Future crash with current catches?", FutureCrash, "\n",
  "Prior range for r =", format(start_r[1],digits=2), "-", format(start_r[2],digits=2),  
  ", prior range for k  =", start_k[1], "-", start_k[2],"\n",
  file=outfile.txt,append=T)

  if(filename_1=="SimCatch.csv") {
  cat(" True r =", r.sim, "(because input data were simulated with Schaefer model)\n",
  "True k = 1000, true MSY =", 1000*r.sim/4,"\n",
  "True biomass in last year =",bio[length(bio)],"or",bio[length(bio)]/1000,"k \n",
  "True mean catch / MSY ratio =", mean(ct)/(1000*r.sim/4),"\n",
  file=outfile.txt,append=T)
  }
  if(Btype == "observed"|Btype=="simulated") {
  cat(" Results from Bayesian Schaefer model using catch & biomass \n",
  "r =", gm.r.jags,", 95% CL =", lcl.r.jags, "-", ucl.r.jags,"\n",
  "k =", gm.k.jags,", 95% CL =", lcl.k.jags, "-", ucl.k.jags,"\n",
  "MSY =", gm.MSY.jags,", 95% CL =", lcl.MSY.jags, "-", ucl.MSY.jags,"\n",
  "Mean catch / MSY =", mean(ct)/gm.MSY.jags,"\n",
  file=outfile.txt,append=T)
  }
  cat(" Results of CMSY analysis with altogether", nviablepoints,"unique viable r-k pairs \n",
  nviablepoints-length(rem.log.r),"r-k pairs above the initial geometric mean of r =", gm.rv, "were analysed\n",
  "r =", r.est,", 95% CL =", lcl.r.est, "-", ucl.r.est,"\n",
  "k =", k.est,", 95% CL =", lcl.k.est, "-", ucl.k.est,"\n",
  "MSY =", MSY.est,", 95% CL =", lcl.MSY.est, "-", ucl.MSY.est,"\n",
  "Predicted biomass last year b/k =", lastyr.bio, "2.5th perc b/k =", lcl.lastyr.bio, 
  "97.5th perc b/k =", ucl.lastyr.bio,"\n",
  "Precautionary 25th percentile b/k =",q.btv[yr==EndYear],"\n",
  "----------------------------------------------------------\n",
  file=outfile.txt,append=T)  

  }

} # end of stocks loop
