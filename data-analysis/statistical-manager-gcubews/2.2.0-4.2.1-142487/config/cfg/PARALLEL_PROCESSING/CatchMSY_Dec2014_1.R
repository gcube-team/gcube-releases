set.seed(999)  ## for same random sequence
#require(hacks)

#setwd("C:/Users/Ye/Documents/Data poor fisheries/Martell Froese Method/")

## Read Data for stock, year=yr, catch=ct, and resilience=res. Expects space delimited file with header yr  ct and years in integer and catch in real with decimal point
## For example
## stock	res	 yr     ct       
## cap-icel	Medium 1984  1234.32 

## filename <- "RAM_MSY.csv"
##filename <- "ICESct2.csv"

cat("Step 1","\n")
TestRUN <- F  # if it is true, just run on the test samples, false will go for a formal run!

filename <- "D20.csv"
outfile  <- "CatchMSY_Output.csv"
outfile2 <- paste("NonProcessedSpecies.csv",sep="")

#cdat <- read.csv2(filename, header=T, dec=".")
cdat1 <- read.csv(filename)
cat("\n", "File", filename, "read successfully","\n")

cat("Step 2","\n")
if(file.exists("cdat.RData"))
{load("cdat.RData")} else 
{
  
 dim(cdat1)
 yrs=1950:2012

 # to set NA as 0
 cdat1[is.na(cdat1)] <- 0
 nrow <- length(cdat1[,1])
 ndatColn <- length(cdat1[1,c(-1:-12)])
 rownames(cdat1) <- NULL

 cdat <- NULL
 for(i in 1:nrow)
 {#i=1
  #a <- ctotal3[i,-1]
  tmp=data.frame(stock=rep(as.character(cdat1[i,"Stock_ID"]),ndatColn),
                 species=rep(as.character(cdat1[i,"Scientific_name"]),ndatColn),
                 yr=yrs,ct=unlist(c(cdat1[i,c(-1:-12)])),
                 res=rep(cdat1[i,"ResilienceIndex"],ndatColn))
  
  cdat <- rbind(cdat,tmp)
 #edit(cdat)
 }
} 

StockList=unique(as.character(cdat$stock))

colnames(cdat)


#stock_id <- unique(as.character(cdat$stock)) 
#??
# stock_id <- "cod-2224" ## for selecting individual stocks
# stock=stock_id
#??

cat("Step 3","\n")

## FUNCTIONS are going to be used subsequently
.schaefer  <- function(theta)
{
  with(as.list(theta), {  ## for all combinations of ri & ki
    bt=vector()
    ell = 0  ## initialize ell
    J=0 #Ye
    for (j in startbt)
    {
      if(ell == 0) 
      {
        bt[1]=j*k*exp(rnorm(1,0, sigR))  ## set biomass in first year
        for(i in 1:nyr) ## for all years in the time series
        {
          xt=rnorm(1,0, sigR)
          bt[i+1]=(bt[i]+r*bt[i]*(1-bt[i]/k)-ct[i])*exp(xt) 
          ## calculate biomass as function of previous year's biomass plus net production minus catch
        }
        
        #Bernoulli likelihood, assign 0 or 1 to each combination of r and k
        ell = 0
        if(bt[nyr+1]/k>=lam1 && bt[nyr+1]/k <=lam2 && min(bt) > 0 && max(bt) <=k && bt[which(yr==interyr)]/k>=interbio[1] && bt[which(yr==interyr)]/k<=interbio[2]) 
          ell = 1
        J=j # Ye
      }	
    }
    return(list(ell=ell,J=J)) # Ye adding J=J
    
    
  })
}

sraMSY	<-function(theta, N)
{
  #This function conducts the stock reduction
  #analysis for N trials
  #args:
  #	theta - a list object containing:
  #		r (lower and upper bounds for r)
  #		k (lower and upper bounds for k)
  #		lambda (limits for current depletion)
  
  
  with(as.list(theta), 
{
  ri = exp(runif(N, log(r[1]), log(r[2])))  ## get N values between r[1] and r[2], assign to ri
  ki = exp(runif(N, log(k[1]), log(k[2])))  ## get N values between k[1] and k[2], assing to ki
  itheta=cbind(r=ri,k=ki, lam1=lambda[1],lam2=lambda[2], sigR=sigR) 
  ## assign ri, ki, and final biomass range to itheta
  M = apply(itheta,1,.schaefer) ## call Schaefer function with parameters in itheta
  i=1:N
  ## prototype objective function
  get.ell=function(i) M[[i]]$ell
  ell = sapply(i, get.ell) 
  get.J=function(i) M[[i]]$J # Ye
  J=sapply(i,get.J) # Ye
  return(list(r=ri,k=ki, ell=ell, J=J)) # Ye adding J=J	
})
}


getBiomass  <- function(r, k, j)
{
  BT <- NULL
  bt=vector()
  for (v in 1:length(r))
  {
    bt[1]=j[v]*k[v]*exp(rnorm(1,0, sigR))  ## set biomass in first year
    for(i in 1:nyr) ## for all years in the time series
    {
      xt=rnorm(1,0, sigR)
      bt[i+1]=(bt[i]+r[v]*bt[i]*(1-bt[i]/k[v])-ct[i])*exp(xt) 
      ## calculate biomass as function of previous year's biomass plus net production minus catch
    }
    BT=rbind(BT, t(t(bt)))        
  }
  return(BT)
}

## The End of Functions section

cat("Step 4","\n")
stockLoop <- StockList
# randomly select stocks from randomly selected 5 area codes first
if(TestRUN) 
{
  set.seed(999)
  AreaCodeList <- unique(cdat1$AREA_Code)
  sampledAC <- sample(AreaCodeList,size=5,replace=F)
  stockLoop <- cdat1[cdat1$AREA_Code %in% sampledAC,c("Stock_ID")]
}

#setup counters
counter1 <- 0
counter2 <- 0

cat("Step 4","\n")
## Loop through stocks
for(stock in stockLoop) 
{
  t0<-Sys.time()
##stock = "3845"  # NB only for test single loop!
  ## make graph file names:
 b <- with(cdat1,cdat1[Stock_ID == stock,c(1,3,5,12)])  # Stock_ID,AREA_Names,Country,"Species"
 bb <- do.call(paste,b)
  
 yr   <- cdat$yr[as.character(cdat$stock)==stock]
 ct   <- as.numeric(cdat$ct[as.character(cdat$stock)==stock])/1000  ## assumes that catch is given in tonnes, transforms to '000 tonnes
 res  <- unique(as.character(cdat$res[as.character(cdat$stock)==stock])) ## resilience from FishBase, if needed, enable in PARAMETER SECTION
 nyr  <- length(yr)    ## number of years in the time series
	
 cat("\n","Stock",stock,"\n")
 flush.console()
		
 ## PARAMETER SECTION
 mvlen=3
 ma=function(x,n=mvlen){filter(x,rep(1/n,n),sides=1)}

 ## If resilience is to be used, delete ## in rows 1-4 below and set ## in row 5	below
 start_r  <- if(res == "Very low"){c(0.015, 0.1)}else{
			if(res == "Low") {c(0.05,0.5)}else {
			   if(res == "High") {c(0.6,1.5)}else {c(0.2,1)}
										  }
   										  	} 
 ## Medium, or default if no res is found	
 ##start_r     <- c(0.5,1.5)  ## disable this line if you use resilience
 start_k     <- c(max(ct),50*max(ct)) ## default for upper k e.g. 100 * max catch
 ## startbio 	<- c(0.8,1)   ## assumed biomass range at start of time series, as fraction of k
 ##startbio    <- if(ct[1]/max(ct) < 0.5) {c(0.5,0.9)} else {c(0.3,0.6)} ## use for batch processing

 ## NB: Yimin's new idea on 20Jan14  
 startbio<- if(mean(ct[1:5])/max(ct) < 0.3) {c(0.6,0.95)} else { 
  if(mean(ct[1:5])/max(ct)>0.3&mean(ct[1:5])/max(ct)<0.6) {c(0.3,0.7)} else {
    c(0.2,0.6)}}
  
 interyr 	<- yr[2]   ## interim year within time series for which biomass estimate is available; set to yr[2] if no estimates are available
 interbio 	<- c(0, 1) ## biomass range for interim year, as fraction of k; set to 0 and 1 if not available
 ## finalbio 	<- c(0.8, 0.9) ## biomass range after last catches, as fraction of k
 ## finalbio    <- if(ct[nyr]/max(ct) > 0.5) {c(0.3,0.7)} else {c(0.01,0.4)} ## use for batch processing
 
 ## Yimin's new stuff on 10Mar14
 #######> pre-classification

 pre.clas=ct
 pre.clas[pre.clas==0]=0.1
 tx=ma(as.numeric(pre.clas),n=mvlen)
 Myr=which.max(tx)
 Maxc=pre.clas[which.max(tx)]

 
 if(Myr==1)startbio=c(0.05,0.6)else
 { 
   if (ct[1]/Maxc>=0.5) startbio=c(0.4,0.85)
   else startbio=c(0.65,0.95)
 }
 
 if (Myr==length(yr))finalbio=c(.4,.95) else # ie from fully to overexploited
 {
   if (tx[length(ct)]/Maxc>=0.5) finalbio=c(.4,.85)
   else finalbio=c(.05,.6)
 }
 
 
#  if (Myr==length(yr))finalbio=c(.5,.9)
#  #if (Myr<length(yr)){
#  #	if ((tx[length(ct)]/Maxc)>=0.8) finalbio=c(.4,.8) else
#  #		if (tx[length(ct)]/Maxc>0.5) finalbio=c(.3,.7) else finalbio=c(.05,.6)}
#  # below is the last used (20 Feb)
#  if (Myr<length(yr))
#  {
#   if (tx[length(ct)]/Maxc>0.5) finalbio=c(.2,.8) 
#   else finalbio=c(.05,.6)
#  }

 ##############<
 n           <- 30000  ## number of iterations, e.g. 100000
 sigR        <- 0.0      ## process error; 0 if deterministic model; 0.05 reasonable value? 0.2 is too high

 startbt     <- seq(startbio[1], startbio[2], by = 0.05) ## apply range of start biomass in steps of 0.05	
 parbound <- list(r = start_r, k = start_k, lambda = finalbio, sigR)

 cat("Last year =",max(yr),", last catch =",1000*ct[nyr],"\n")
 cat("Resilience =",res,"\n")
 cat("Process error =", sigR,"\n")
 cat("Assumed initial biomass (B/k) =", startbio[1],"-", startbio[2], " k","\n")
 cat("Assumed intermediate biomass (B/k) in", interyr, " =", interbio[1],"-",interbio[2]," k","\n")
 cat("Assumed final biomass (B/k) =", parbound$lambda[1],"-",parbound$lambda[2]," k","\n")
 cat("Initial bounds for r =", parbound$r[1], "-", parbound$r[2],"\n")
 cat("Initial bounds for k =", format(1000*parbound$k[1], digits=3), "-", format(1000*parbound$k[2],digits=3),"\n")

 flush.console()

 ## MAIN

 R1 = sraMSY(parbound, n)  
		
 ## Get statistics on r, k, MSY and determine new bounds for r and k
 r1 	<- R1$r[R1$ell==1]
 k1 	<- R1$k[R1$ell==1]
 j1   <- R1$J[R1$ell==1] # Ye 
 msy1  <- r1*k1/4
 mean_msy1 <- exp(mean(log(msy1))) 
 max_k1a  <- min(k1[r1<1.1*parbound$r[1]]) ## smallest k1 near initial lower bound of r
 max_k1b  <- max(k1[r1*k1/4<mean_msy1]) ## largest k1 that gives mean MSY
 max_k1 <- if(max_k1a < max_k1b) {max_k1a} else {max_k1b}
 
 if(length(r1)<10) 
 {
  cat("Too few (", length(r1), ") possible r-k combinations, 
      check input parameters","\n")
  appendPar <- ifelse(counter1==0,F,T) 
  colnamePar <- ifelse(counter1==0,T,F)
  
  NoModellingSpe <- as.data.frame(cbind(stock,length(r1),b))
  names(NoModellingSpe) <- c("Stock","No_of_r1",names(b))                       
  write.table(NoModellingSpe,file=outfile2,
              append = appendPar, row.names = FALSE,
              col.names=colnamePar,sep=",")
  flush.console()
  counter1 <- counter1 + 1
 }

 if(length(r1)>=10) 
 {
	## set new upper bound of r to 1.2 max r1
	parbound$r[2] <- 1.2*max(r1)
	## set new lower bound for k to 0.9 min k1 and upper bound to max_k1 
	parbound$k 	  <- c(0.9 * min(k1), max_k1)
	
	cat("First MSY =", format(1000*mean_msy1, digits=3),"\n")
	cat("First r =", format(exp(mean(log(r1))), digits=3),"\n")
	cat("New upper bound for r =", format(parbound$r[2],digits=2),"\n")	
	cat("New range for k =", format(1000*parbound$k[1], digits=3), "-", format(1000*parbound$k[2],digits=3),"\n")

  ## Repeat analysis with new r-k bounds
  R1 = sraMSY(parbound, n)

  ## Get statistics on r, k and msy
  r = R1$r[R1$ell==1]
  k = R1$k[R1$ell==1]
  j = R1$J[R1$ell==1] # Ye
  msy = r * k / 4
  mean_ln_msy = mean(log(msy))

  ##############################################################
  ##> Ye
  # BT=0

  ##
  R2<-getBiomass(r, k, j) 

  #R2<-R2[-1,]
  runs<-rep(1:length(r), each=nyr+1)
  years=rep(yr[1]:(yr[length(yr)]+1),length=length(r)*(length(yr)+1))

  runs=t(runs)
  years=t(years)
  stock_id=rep(stock,length(runs))
  R3<-cbind(as.numeric(runs), as.numeric(years), stock_id, as.numeric(R2) )

  ## changed this, as otherwise biomass is the level of the factor below
  R4<-data.frame(R3, stringsAsFactors=FALSE)
  names(R4)<-c("Run", "Year", "Stock","Biomass")

  Bmsy_x<-k*0.5
  Run<-c(1:length(r)) 
  BMSY<-cbind(Run, Bmsy_x)
  R5<-merge(R4, BMSY, by="Run", all.x=T, all.y=F)
  R5$B_Bmsy<-as.numeric(paste(R5$Biomass))/R5$Bmsy_x

  ### B/Bmsy calculated for all feasible combinations of r,K,B0
  R6<-aggregate(log(B_Bmsy)~as.numeric(Year)+Stock, data=R5, 
	FUN=function(z){c(mean=mean(z),sd=sd(z),upr=exp(quantile(z, p=0.975)), 
	lwr=exp(quantile(z, p=0.025)), lwrQ=exp(quantile(z, p=0.25)), 
	uprQ=exp(quantile(z, p=0.75)))}) # from directly calculated from R5 becasue B_Bmsy has a lognormal dist

  R6<-data.frame(cbind(R6[,1:2],R6[,3][,1],R6[,3][,2],R6[,3][,3],R6[,3][,4],R6[,3][,5], R6[,3][,6]))
    names(R6)<-c("Year", "Stock", "BoverBmsy", "BoverBmsySD","BoverBmsyUpper","BoverBmsyLower","BoverBmsylwrQ","BoverBmsyuprQ")      
  ##remove  last entry as it is 1 greater than number of years
  ## removed final year here for ease of dataframe output below
  R6<-R6[-length(R6),]
  ## geometric mean
  GM_B_Bmsy<-exp(R6$BoverBmsy)
  GM_B_BmsySD=R6$BoverBmsySD #add
  ## arithmetic mean
  M_B_Bmsy<-exp(R6$BoverBmsy+R6$BoverBmsySD^2/2)

  ### r,k, and MSY
  
  #del GM_B_Bmsy=c(rep(0,(min(yr)-1940)),GM_B_Bmsy)
  #del GM_B_BmsySD=c(rep(0,(min(yr)-1940)),GM_B_BmsySD) ######
  #del M_B_Bmsy=c(rep(0,(min(yr)-1940)),M_B_Bmsy)
  #del yr1=seq(1940,max(yr))
  
  yr1=yr #add

  stockInfo <- with(cdat1,cdat1[Stock_ID==stock,1:12])
  temp=c(startbio[1],startbio[2],finalbio[1],finalbio[2],res,
       mean(log(r)),sd(log(r)),mean(log(k)),sd(log(k)),mean(log(msy)),
       sd(log(msy)),sigR,min(yr),max(yr),max(ct),length(r),GM_B_Bmsy,GM_B_BmsySD,M_B_Bmsy)

  #add, adding "GM_B_BmsySD" in the line above

  output=as.data.frame(matrix(temp,nrow=1))
  output <- cbind(stockInfo,output)
  names(output) <- c(names(cdat1)[1:12],"startbio[1]","startbio[2]","finalbio[1]","finalbio[2]",
                   "res","mean(log(r))","sd(log(r))","mean(log(k))","sd(log(k))",
                   "mean(log(msy))","sd(log(msy))","sigR","min(yr)","max(yr)","max(ct)",
                   "length(r)",paste("GM_B_msy",yr1,sep="_"),paste("GM_B_msySD",yr1,sep="_"),paste("M_B_Bmsy",yr1,sep="_"))

  #add, adding "paste("GM_B_msySD",yr1,sep="_")"in the line above
 
  ######< Ye
  ########################################################

 	## plot MSY over catch data
  pdf(paste(bb,"graph.pdf",sep="_"))

	par(mfcol=c(2,3))
	plot(yr, ct, type="l", ylim = c(0, max(ct)), xlab = "Year", 
       ylab = "Catch (1000 t)",main = paste("StockID",stock,sep=":"))
	abline(h=exp(mean(log(msy))),col="red", lwd=2)
	abline(h=exp(mean_ln_msy - 2 * sd(log(msy))),col="red")
	abline(h=exp(mean_ln_msy + 2 * sd(log(msy))),col="red")
		
	hist(r, freq=F, xlim=c(0, 1.2 * max(r)), main = "")
	abline(v=exp(mean(log(r))),col="red",lwd=2)
	abline(v=exp(mean(log(r))-2*sd(log(r))),col="red")
	abline(v=exp(mean(log(r))+2*sd(log(r))),col="red")
	
	plot(r1, k1, xlim = start_r, ylim = start_k, xlab="r", ylab="k (1000t)")
	
	hist(k, freq=F, xlim=c(0, 1.2 * max(k)), xlab="k (1000t)", main = "")
	abline(v=exp(mean(log(k))),col="red", lwd=2)	
	abline(v=exp(mean(log(k))-2*sd(log(k))),col="red")
	abline(v=exp(mean(log(k))+2*sd(log(k))),col="red")


	plot(log(r), log(k),xlab="ln(r)",ylab="ln(k)")
	abline(v=mean(log(r)))
	abline(h=mean(log(k)))
	abline(mean(log(msy))+log(4),-1, col="red",lwd=2)
	abline(mean(log(msy))-2*sd(log(msy))+log(4),-1, col="red")
	abline(mean(log(msy))+2*sd(log(msy))+log(4),-1, col="red")

	hist(msy, freq=F, xlim=c(0, 1.2 * max(msy)), xlab="MSY (1000t)",main = "")
	abline(v=exp(mean(log(msy))),col="red", lwd=2)
	abline(v=exp(mean_ln_msy - 2 * sd(log(msy))),col="red")
	abline(v=exp(mean_ln_msy + 2 * sd(log(msy))),col="red")
	
  graphics.off()


	cat("Possible combinations = ", length(r),"\n")
	cat("geom. mean r =", format(exp(mean(log(r))),digits=3), "\n")
	cat("r +/- 2 SD =", format(exp(mean(log(r))-2*sd(log(r))),digits=3),"-",format(exp(mean(log(r))+2*sd(log(r))),digits=3), "\n")
	cat("geom. mean k =", format(1000*exp(mean(log(k))),digits=3), "\n")
	cat("k +/- 2 SD =", format(1000*exp(mean(log(k))-2*sd(log(k))),digits=3),"-",format(1000*exp(mean(log(k))+2*sd(log(k))),digits=3), "\n")
	cat("geom. mean MSY =", format(1000*exp(mean(log(msy))),digits=3),"\n")
	cat("MSY +/- 2 SD =", format(1000*exp(mean_ln_msy - 2 * sd(log(msy))),digits=3), "-", format(1000*exp(mean_ln_msy + 2 * sd(log(msy))),digits=3), "\n")

  ## Write results into outfile, in append mode (no header in file, existing files will be continued)
  ## output = data.frame(stock, sigR, startbio[1], startbio[2], interbio[1], interbio[2], finalbio[1], finalbio[2], min(yr), max(yr), res, max(ct), ct[1], ct[nyr], length(r), exp(mean(log(r))), sd(log(r)), min(r), quantile(r,0.05), quantile(r,0.25), median(r), quantile(r,0.75), quantile(r,0.95), max(r), exp(mean(log(k))), sd(log(k)), min(k), quantile(k, 0.05), quantile(k, 0.25), median(k), quantile(k, 0.75), quantile(k, 0.95), max(k), exp(mean(log(msy))), sd(log(msy)), min(msy), quantile(msy, 0.05), quantile(msy, 0.25), median(msy), quantile(msy, 0.75), quantile(msy, 0.95), max(msy)) 

  #write.table(output, file = outfile, append = TRUE, sep = ";", dec = ".", row.names = FALSE, col.names = FALSE)
  appendPar <- ifelse(counter2==0,F,T) 
  colnamePar <- ifelse(counter2==0,T,F)
  write.table(output, file = outfile, append = appendPar, sep = ",", dec = ".", 
            row.names = FALSE, col.names = colnamePar)

  counter2 <- counter2 + 1

 }
cat("Elapsed: ",Sys.time()-t0," \n")
}  ## End of stock loop, get next stock or exit
