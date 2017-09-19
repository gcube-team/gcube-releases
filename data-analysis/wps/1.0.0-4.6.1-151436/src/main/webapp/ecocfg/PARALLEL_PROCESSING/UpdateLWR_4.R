#### R and JAGS code for estimating LWR-parameters from previous studies
#### Meant for updating the ESTIMATE table in FishBase
#### Created by Rainer Froese in March 2013, including JAGS models by James Thorston
#### Modified in June 2013 to include subfamilies

rm(list=ls(all=TRUE)) # remove previous variables and data
options(digits=3) # 3 significant digits as default
library(R2jags)  # Interface with JAGS
runif(1)         # sets random seed  

#### Read in data
DataFile = "RF_LWR2.csv" # RF_LWR4 was extracted from FishBase in June 2013
Data = read.csv(DataFile, header=TRUE)
cat("Start", date(), "\n")
cat("Data file =", DataFile, "\n")
# Get unique, sorted list of Families
Fam.All <- sort(unique(as.character(Data$Family)))
Families <- Fam.All[Fam.All== "Acanthuridae" | Fam.All == "Achiridae"]

OutFile = "LWR_Test1.csv"
JAGSFILE = "dmnorm_0.bug"

# Get unique, sorted list of body shapes
Bshape <- sort(unique(as.character(Data$BodyShapeI)))

#------------------------------------------
# Functions
#------------------------------------------

#---------------------------------------------------------
# Function to get the priors for the respective body shape
#---------------------------------------------------------
Get.BS.pr <- function(BS) {
  ### Assignment of priors based on available body shape information
  # priors derived from 5150 LWR studies in FishBase 02/2013
  
  if (BS == "eel-like") { # eel-like prior for log(a) and b
    prior_mean_log10a = -2.99
    prior_sd_log10a = 0.175
    prior_tau_log10a = 1/prior_sd_log10a^2
    prior_mean_b = 3.06 
    prior_sd_b = 0.0896
    prior_tau_b = 1/prior_sd_b^2
  } else
  if (BS == "elongated") { # elongate prior for log(a) and b
    prior_mean_log10a = -2.41
    prior_sd_log10a = 0.171
    prior_tau_log10a = 1/prior_sd_log10a^2
    prior_mean_b = 3.12 
    prior_sd_b = 0.09
    prior_tau_b = 1/prior_sd_b^2
  } else
  if (BS == "fusiform / normal") { # fusiform / normal prior for log(a) and b
    prior_mean_log10a = -1.95
    prior_sd_log10a = 0.173
    prior_tau_log10a = 1/prior_sd_log10a^2
    prior_mean_b = 3.04 
    prior_sd_b = 0.0857
    prior_tau_b = 1/prior_sd_b^2
  } else
  if (BS == "short and / or deep") { # short and / or deep prior for log(a) and b
    prior_mean_log10a = -1.7
    prior_sd_log10a = 0.175
    prior_tau_log10a = 1/prior_sd_log10a^2
    prior_mean_b = 3.01 
    prior_sd_b = 0.0905
    prior_tau_b = 1/prior_sd_b^2
  } else
  # priors across all shapes, used for missing or other BS 
   {
    prior_mean_log10a = -2.0
    prior_sd_log10a = 0.313
    prior_tau_log10a = 1/prior_sd_log10a^2
    prior_mean_b = 3.04 
    prior_sd_b = 0.119
    prior_tau_b = 1/prior_sd_b^2
  }  
  
  # Priors for measurement error (= sigma) based on 5150 studies
  # given here as shape mu and rate r, for gamma distribution
  SD_rObs_log10a = 6520
  SD_muObs_log10a = 25076 
  SD_rObs_b = 6808
  SD_muObs_b = 37001
  # Priors for between species variability (= sigma) based on 5150 studies for 1821 species
  SD_rGS_log10a = 1372
  SD_muGS_log10a = 7933
  SD_rGS_b = 572
  SD_muGS_b = 6498
  
 prior.list <- list(mean_log10a=prior_mean_log10a, sd_log10a=prior_sd_log10a, 
          tau_log10a=prior_tau_log10a, mean_b=prior_mean_b, sd_b=prior_sd_b, 
          tau_b=prior_tau_b, SD_rObs_log10a=SD_rObs_log10a, SD_muObs_log10a=SD_muObs_log10a, 
          SD_rObs_b=SD_rObs_b, SD_muObs_b=SD_muObs_b, SD_rGS_log10a=SD_rGS_log10a, 
          SD_muGS_log10a=SD_muGS_log10a, SD_rGS_b=SD_rGS_b, SD_muGS_b=SD_muGS_b)  
return(prior.list)  
}

#--------------------------------------------------------------------
# Function to do a Bayesian analysis including LWR from relatives
#--------------------------------------------------------------------
SpecRelLWR <- function(a, b, wts, GenusSpecies, Nspecies, prior_mean_b, prior_tau_b, 
                       prior_mean_log10a, prior_tau_log10a, SD_rObs_log10a, SD_muObs_log10a,  
                       SD_rObs_b, SD_muObs_b, SD_rGS_log10a, SD_muGS_log10a,
                       SD_rGS_b, SD_muGS_b){
  ### Define JAGS model 
  Model = "
model {               
  #### Process model -- effects of taxonomy
  # given the likelihood distributions and the priors, 
  # create normal posterior distributions for log10a, b, 
  # and for the process error (=between species variability sigmaGS)  
  
  abTrue[1] ~ dnorm(prior_mean_log10a,prior_tau_log10a) 
  abTrue[2] ~ dnorm(prior_mean_b,prior_tau_b) 
  sigmaGSlog10a ~ dgamma( SD_rGS_log10a, SD_muGS_log10a) 
  sigmaGSb ~ dgamma( SD_rGS_b, SD_muGS_b)
  
  # given the posterior distributions and the process errors,
  # establish for every species the expected witin-species 
  # parameter distributions; no correlation roGS between species 
  
  roGS <- 0 
  tauGenusSpecies[1] <- pow(sigmaGSlog10a,-2)
  tauGenusSpecies[2] <- pow(sigmaGSb,-2)
  for(k in 1:Nspecies){
  abGenusSpecies[k,1] ~ dnorm(abTrue[1],tauGenusSpecies[1]) 
  abGenusSpecies[k,2] ~ dnorm(abTrue[2],tauGenusSpecies[2]) 
  }
  
  ### Observation model  
  ## Errors 
  # given the data and the priors, establish distributions  	
  # for the observation errors sigmaObs 	
  
  sigmaObslog10a ~ dgamma( SD_rObs_log10a, SD_muObs_log10a) 
  sigmaObsb ~ dgamma( SD_rObs_b, SD_muObs_b) 	
  
  # create inverse covariance matrix, with negative parameter correlation roObs
  roObs ~ dunif(-0.99,0)     
  CovObs[1,1] <- pow(sigmaObslog10a,2)  
  CovObs[2,2] <- pow(sigmaObsb,2) 
  CovObs[1,2] <- roObs * sigmaObslog10a * sigmaObsb 
  CovObs[2,1] <- CovObs[1,2]
  TauObs[1:2,1:2] <- inverse(CovObs[1:2,1:2]) 
  
  ## likelihood
  # given the data, the priors and the covariance, 
  # create multivariate likelihood distributions for log10(a) and b 
  
  for(i in 1:N){
  TauObsI[i,1:2,1:2] <- TauObs[1:2,1:2] * pow(Weights[i],2)   # weighted precision
  ab[i,1:2] ~ dmnorm(abGenusSpecies[GenusSpecies[i],1:2],TauObsI[i,1:2,1:2])   
  }
}
  "
  
  # Write JAGS model 
  cat(Model, file=JAGSFILE)
  # JAGS settings
  Nchains = 3	# number of MCMC chains to be used in JAGS
  Nburnin = 1e4 # number of burn-in iterations, to be discarded; 1e4 = 10000 iterations for burn-in
  Niter = 3e4 # number of iterations after burn-in; 3e4 = 30000 iterations
  Nthin = 1e1 # subset of iterations to be used for analysis; 1e1 = every 10th iteration 
  
  # Run JAGS: define data to be passed on in DataJags; 
  # determine parameters to be returned in Param2Save; 
  # call JAGS with function Jags()
  DataJags = list(ab=cbind(log10(a),b), N=length(a), Weights=wts, Nspecies=Nspecies, GenusSpecies=GenusSpecies,
                  prior_mean_b=prior_mean_b, prior_tau_b=prior_tau_b, 
                  prior_mean_log10a=prior_mean_log10a, prior_tau_log10a=prior_tau_log10a, 
                  SD_rObs_log10a=SD_rObs_log10a, SD_muObs_log10a=SD_muObs_log10a,  
                  SD_rObs_b=SD_rObs_b, SD_muObs_b=SD_muObs_b, 
                  SD_rGS_log10a=SD_rGS_log10a, SD_muGS_log10a=SD_muGS_log10a,
                  SD_rGS_b=SD_rGS_b, SD_muGS_b=SD_muGS_b)
  Params2Save = c("abTrue","abGenusSpecies","sigmaGSlog10a","sigmaGSb","sigmaObslog10a","sigmaObsb","roObs")
  Jags <- jags(inits=NULL, model.file=JAGSFILE, working.directory=NULL, data=DataJags, 
               parameters.to.save=Params2Save, n.chains=Nchains, n.thin=Nthin, n.iter=Niter, n.burnin=Nburnin)
  Jags$BUGSoutput # contains the results from the JAGS run
  
  # Analyze output for the relatives
  abTrue <- Jags$BUGSoutput$sims.list$abTrue
  R_mean_log10a  <- mean(abTrue[,1]) # true mean of log10(a)
  R_sd_log10a    <- sd(abTrue[,1])   # true SE of log10(a)
  R_mean_b       <- mean(abTrue[,2])         # true mean of b
  R_sd_b         <- sd(abTrue[,2])           # true SE of b
  
  # Analyze output for the target species
  abGenusSpecies <- Jags$BUGSoutput$sims.list$abGenusSpecies
  mean_log10a  <- mean(abGenusSpecies[,1,1]) # true mean of log10(a) for the first species= target species
  sd_log10a    <- sd(abGenusSpecies[,1,1])   # true SE of log10(a)
  mean_b       <- mean(abGenusSpecies[,1,2])         # true mean of b
  sd_b         <- sd(abGenusSpecies[,1,2])           # true SE of b
  mean_sigma_log10a <- mean(Jags$BUGSoutput$sims.list$sigmaObslog10a) # measurement error of log10(a)
  sd_sigma_log10a <- apply(as.matrix(Jags$BUGSoutput$sims.list$sigmaObslog10a), 2, sd)
  mean_sigma_b    <- mean(Jags$BUGSoutput$sims.list$sigmaObsb) # measurement error of b
  sd_sigma_b		<- apply(as.matrix(Jags$BUGSoutput$sims.list$sigmaObsb), 2, sd)
  ro_ab        <- mean(Jags$BUGSoutput$sims.list$roObs) # measurement correlation of log10(a),b
  
  out.list <- list(N=length(a), mean_log10a=mean_log10a, sd_log10a=sd_log10a, mean_b=mean_b, sd_b=sd_b,
                   R_mean_log10a=R_mean_log10a, R_sd_log10a=R_sd_log10a, R_mean_b=R_mean_b, R_sd_b=R_sd_b)
  return(out.list)
  }

#-----------------------------------------------------------------------------
# Function to do a Bayesian LWR analysis with studies for target species only
#-----------------------------------------------------------------------------
SpecLWR <- function(a, b, wts, prior_mean_b, prior_tau_b, 
                       prior_mean_log10a, prior_tau_log10a, SD_rObs_log10a, SD_muObs_log10a,  
                       SD_rObs_b, SD_muObs_b, SD_rGS_log10a, SD_muGS_log10a,
                       SD_rGS_b, SD_muGS_b){
  
  # Define JAGS model 
  Model = "
  model {               
  sigma1 ~ dgamma( SD_rObs_log10a, SD_muObs_log10a) # posterior distribution for measurement error in log10a  
  sigma2 ~ dgamma( SD_rObs_b, SD_muObs_b) # posterior distribution for measurement error in log10a   
  
  ro ~ dunif(-0.99,0)     # uniform prior for negative correlation between log10a and b
  abTrue[1] ~ dnorm(prior_mean_log10a,prior_tau_log10a) # normal posterior distribution for log10a
  abTrue[2] ~ dnorm(prior_mean_b,prior_tau_b) # normal posterior distribution for b
  CovObs[1,1] <- pow(sigma1,2)  
  CovObs[2,2] <- pow(sigma2,2) 
  CovObs[1,2] <- ro * sigma1 * sigma2 
  CovObs[2,1] <- CovObs[1,2]
  TauObs[1:2,1:2] <- inverse(CovObs[1:2,1:2]) # create inverse covariance matrix
  for(i in 1:N){
  TauObsI[i,1:2,1:2] <- TauObs[1:2,1:2] * pow(Weights[i],2)   # converts prior SD into prior weighted precision
  
  # given the data, the priors and the covariance, create multivariate normal posteriors for log(a) and b 
  ab[i,1:2] ~ dmnorm(abTrue[1:2],TauObsI[i,1:2,1:2]) 
  }
  }
  "

# Write JAGS model 
cat(Model, file=JAGSFILE)
# JAGS settings
Nchains = 3  # number of MCMC chains to be used in JAGS
Nburnin = 1e4 # number of burn-in runs, to be discarded; 10000 iterations for burn-in
Niter = 3e4 # number of iterations after burn-in; 3e4 = 30000 iterations
Nthin = 1e1 # subset of iterations to be used for analysis; 1e1 = every 10th iteration 
# Run JAGS: define data to be passed on in DataJags; determine parameters to be returned in Param2Save; call JAGS with function Jags()
DataJags = list(ab=cbind(log10(a),b), N=length(a), Weights=wts, prior_mean_b=prior_mean_b, 
		prior_tau_b=prior_tau_b, prior_mean_log10a=prior_mean_log10a, prior_tau_log10a=prior_tau_log10a, 
		SD_rObs_log10a=SD_rObs_log10a, SD_muObs_log10a=SD_muObs_log10a,
		SD_rObs_b=SD_rObs_b, SD_muObs_b=SD_muObs_b)
Params2Save = c("abTrue","sigma1","sigma2","ro")
Jags <- jags(inits=NULL, model.file=JAGSFILE, working.directory=NULL, data=DataJags, parameters.to.save=Params2Save, n.chains=Nchains, n.thin=Nthin, n.iter=Niter, n.burnin=Nburnin)
Jags$BUGSoutput # contains the results from the JAGS run
# Analyze output
abTrue <- Jags$BUGSoutput$sims.list$abTrue
mean_log10a  <- mean(abTrue[,1]) # true mean of log10(a)
sd_log10a    <- sd(abTrue[,1])   # true SE of log10(a)
mean_b       <- mean(abTrue[,2])         # true mean of b
sd_b         <- sd(abTrue[,2])           # true SE of b
mean_sigma_log10a <- mean(Jags$BUGSoutput$sims.list$sigma1) # measurement error of log10(a)
sd_sigma_log10a <- apply(as.matrix(Jags$BUGSoutput$sims.list$sigma1), 2, sd)
mean_sigma_b    <- mean(Jags$BUGSoutput$sims.list$sigma2) # measurement error of b
sd_sigma_b		<- apply(as.matrix(Jags$BUGSoutput$sims.list$sigma2), 2, sd)
ro_ab        <- mean(Jags$BUGSoutput$sims.list$ro) # measurement correlation of log10(a),b

out.list <- list(N=length(a), mean_log10a=mean_log10a, sd_log10a=sd_log10a, mean_b=mean_b, sd_b=sd_b)
return(out.list)

} # End of Functions section

#--------------------------------
# Analysis by Family
#--------------------------------
# Do LWR analysis by Family, Subfamily and Body shape, depending on available LWR studies
# for(Fam in "Acanthuridae") {
for(Fam in Families) {
 Subfamilies <- sort(unique(Data$Subfamily[Data$Family==Fam]))
 for(SF in Subfamilies) { 
  for(BS in Bshape) {
    # get species (SpecCodes) in this Subfamily and with this body shape 
    SpecCode.SF.BS <- unique(Data$SpecCode[Data$Family==Fam & Data$Subfamily==SF & Data$BodyShapeI==BS])
    # if there are species with this body shape
    if(length(SpecCode.SF.BS > 0)) {
    # get priors for this body shape
    prior <- Get.BS.pr(BS) 
    # get LWR data for this body shape
    b_raw      <- Data$b[Data$Family==Fam & Data$Subfamily==SF & Data$BodyShapeI==BS]
    cat("\n")
    cat("Family =", Fam, ", Subfamily =", SF, ", Body shape =", BS, ", Species =", length(SpecCode.SF.BS), ", LWR =", 
          length(b_raw[is.na(b_raw)==F]), "\n")
    # if no LWR studies exist for this body shape, assign the respective priors to all species 
    if(length(b_raw[is.na(b_raw)==F])==0) {
      # assign priors to species with no LWR in this Subfamily with this body shape
      cat("Assigning overall body shape prior to", length(SpecCode.SF.BS), " species \n")
      for(SpC in SpecCode.SF.BS) {
        out.prior <- data.frame(Fam, SF, BS, SpC, 0, prior$mean_log10a, prior$sd_log10a, prior$mean_b, prior$sd_b,
              paste("all LWR estimates for this BS"))    
        write.table(out.prior, file=OutFile, append = T, sep=",", dec=".", row.names=F, col.names=F)
        }
      } else {
        
        # Update priors for this body shape using existing LWR studies 
        # get LWR data for this Subfamily and body shape
        Keep <- which(Data$Family==Fam & Data$Subfamily==SF & Data$BodyShapeI==BS & is.na(Data$b)==F & Data$Score>0)
        wts  <- Data$Score[Keep]  # Un-normalized weights (so that Cov is comparable among analyses)
        a    <- Data$a[Keep] 
        b    <- Data$b[Keep]
        GenSpec <- paste(Data$Genus[Keep],Data$Species[Keep])
        # add a first dummy record with prior LWR and low score = 0.3, as pseudo target species
        # Name of dummy target species is Dum1 dum1
        TargetSpec = paste("Dum1", "dum1")
        wts <- c(0.3, wts)
        a   <- c(10^(prior$mean_log10a), a)
        b   <- c(prior$mean_b, b)
        GenSpec <- c(TargetSpec, GenSpec)
        # Relabel GenSpec so that TargetSpec = level 1
        OtherSpecies = unique(GenSpec[GenSpec != TargetSpec])
        GenusSpecies = factor(GenSpec, levels=c(TargetSpec, OtherSpecies))
        Nspecies = nlevels(GenusSpecies) # number of species
        # run Bayesian analysis for pseudo target species with Subfamily members
        # The resulting R_mean_log10a, R_sd_log10a, R_mean_b, R_sd_b will be used for species without LWR
        cat("Updating Subfamily-Bodyshape prior using", Nspecies-1, "species with LWR studies \n")
        prior.SFam.BS <- SpecRelLWR(a, b, wts, GenusSpecies, Nspecies, prior_mean_b=prior$mean_b, 
                    prior_tau_b=prior$tau_b, prior_mean_log10a=prior$mean_log10a, 
                    prior_tau_log10a=prior$tau_log10a, SD_rObs_log10a=prior$SD_rObs_log10a, 
                    SD_muObs_log10a=prior$SD_muObs_log10a, SD_rObs_b=prior$SD_rObs_b, 
                    SD_muObs_b=prior$SD_muObs_b, SD_rGS_log10a=prior$SD_rGS_log10a, 
                    SD_muGS_log10a=prior$SD_muGS_log10a, SD_rGS_b=prior$SD_rGS_b, 
                    SD_muGS_b=prior$SD_muGS_b)
        
        #------------------------------------------------------------------------------------------
        # if there are Genera with >= 5 species with LWR, update body shape priors for these Genera
        #------------------------------------------------------------------------------------------
        Genera <- unique(as.character(Data$Genus[Keep]))
        # create empty list of lists for storage of generic priors
        prior.Gen.BS <- rep(list(list()),length(Genera)) # create a list of empty lists
        names(prior.Gen.BS) <- Genera # name the list elements according to the Genera
        for(Genus in Genera){
          # check if Genus contains >= 5 species with LWR data
          if(length(unique(Data$SpecCode[Data$Family==Fam & Data$Subfamily==SF & Data$BodyShapeI==BS & is.na(Data$b)==F & 
                          Data$Score>0 & Data$Genus==Genus]))>=5) {
          # run Subfamily analysis with only data for this genus
          Keep <- which(Data$Family==Fam & Data$Subfamily==SF & Data$BodyShapeI==BS & is.na(Data$b)==F & Data$Score>0 &
                            Data$Genus==Genus)
          wts  <- Data$Score[Keep]  # Un-normalized weights (so that Cov is comparable among analyses)
          a    <- Data$a[Keep] 
          b    <- Data$b[Keep]
          GenSpec <- paste(Data$Genus[Keep],Data$Species[Keep])
          # add a first dummy record with prior LWR and low score = 0.3, as pseudo target species
          # Name of dummy target species is Dum1 dum1
          TargetSpec = paste("Dum1", "dum1")
          wts <- c(0.3, wts)
          a   <- c(10^(prior$mean_log10a), a)
          b   <- c(prior$mean_b, b)
          GenSpec <- c(TargetSpec, GenSpec)
          # Relabel GenSpec so that TargetSpec = level 1
          OtherSpecies = unique(GenSpec[GenSpec != TargetSpec])
          GenusSpecies = factor(GenSpec, levels=c(TargetSpec, OtherSpecies))
          Nspecies = nlevels(GenusSpecies) # number of species
          # run Bayesian analysis for pseudo target species with Genus members
          # R_mean_log10a, R_sd_log10a, R_mean_b, R_sd_b will be used for species without LWR
          cat("Updating prior for Genus =", Genus, ", with", Nspecies -1, "LWR Species \n")
          prior.Gen.BS[[Genus]] <- SpecRelLWR(a, b, wts, GenusSpecies, Nspecies, 
                      prior_mean_b=prior.SFam.BS$R_mean_b, 
                      prior_tau_b=1/prior.SFam.BS$R_sd_b^2, 
                      prior_mean_log10a=prior.SFam.BS$R_mean_log10a, 
                      prior_tau_log10a=1/prior.SFam.BS$R_sd_log10a, 
                      SD_rObs_log10a=prior$SD_rObs_log10a, 
                      SD_muObs_log10a=prior$SD_muObs_log10a, SD_rObs_b=prior$SD_rObs_b, 
                      SD_muObs_b=prior$SD_muObs_b, SD_rGS_log10a=prior$SD_rGS_log10a, 
                      SD_muGS_log10a=prior$SD_muGS_log10a, SD_rGS_b=prior$SD_rGS_b, 
                      SD_muGS_b=prior$SD_muGS_b)    
          }
        }
      # new Subfamily-BS priors have been generated 
      # for some genera, new Genus-BS priors have been generated 
      # ---------------------------------------------------------------------
      # Loop through all species in this Subfamily-BS; assign LWR as appropriate
      # ---------------------------------------------------------------------
        for(SpC in SpecCode.SF.BS) {
          Genus    <- as.character(unique(Data$Genus[Data$SpecCode==SpC]))
          Species  <- as.character(unique(Data$Species[Data$SpecCode==SpC]))
          TargetSpec = paste(Genus, Species)
          LWR      <- length(Data$b[Data$SpecCode==SpC & is.na(Data$b)==F & Data$Score>0])
          LWRGenspec  <- length(unique(Data$SpecCode[Data$BodyShapeI==BS & is.na(Data$b)==F & 
                        Data$Score>0 & Data$Genus==Genus]))
          LWRSFamspec  <- length(unique(Data$SpecCode[Data$BodyShapeI==BS & is.na(Data$b)==F & 
                        Data$Score>0 & Data$Family==Fam & Data$Subfamily==SF]))
          #---------------------------------------------------------
          # >= 5 LWR in target species, run single species analysis
          if(LWR >= 5) {
            # Run analysis with data only for this species
            Keep <- which(Data$SpecCode==SpC & is.na(Data$b)==F & Data$Score>0)
            wts = Data$Score[Keep]  # Un-normalized weights (so that Cov is comparable among analyses)
            a = Data$a[Keep] 
            b = Data$b[Keep]
            
          # determine priors to be used
            if(LWRGenspec >= 5) {
            prior_mean_b=prior.Gen.BS[[Genus]]$R_mean_b 
            prior_tau_b=1/prior.Gen.BS[[Genus]]$R_sd_b^2 
            prior_mean_log10a=prior.Gen.BS[[Genus]]$R_mean_log10a
            prior_tau_log10a=1/prior.Gen.BS[[Genus]]$R_sd_log10a^2 
            } else 
            if (LWRSFamspec > 0) {
            prior_mean_b=prior.SFam.BS$R_mean_b 
            prior_tau_b=1/prior.SFam.BS$R_sd_b^2 
            prior_mean_log10a=prior.SFam.BS$R_mean_log10a 
            prior_tau_log10a=1/prior.SFam.BS$R_sd_log10a^2  
            } else {
            prior_mean_b=prior$mean_b 
            prior_tau_b=prior$tau_b
            prior_mean_log10a=prior$mean_log10a 
            prior_tau_log10a=prior$tau_log10a   
            }
            cat("Running single species analysis for", TargetSpec, "LWR =", LWR, ", LWR species in Genus=",LWRGenspec,"\n" )
            # call function for single species analysis  
            post <- SpecLWR(a, b, wts, prior_mean_b=prior_mean_b, 
                    prior_tau_b=prior_tau_b, prior_mean_log10a=prior_mean_log10a, 
                    prior_tau_log10a=prior_tau_log10a, SD_rObs_log10a=prior$SD_rObs_log10a, 
                    SD_muObs_log10a=prior$SD_muObs_log10a, SD_rObs_b=prior$SD_rObs_b, 
                    SD_muObs_b=prior$SD_muObs_b, SD_rGS_log10a=prior$SD_rGS_log10a, 
                    SD_muGS_log10a=prior$SD_muGS_log10a, SD_rGS_b=prior$SD_rGS_b, 
                    SD_muGS_b=prior$SD_muGS_b)  
            out.SpC <- data.frame(Fam, SF, BS, SpC, LWR, format(post$mean_log10a, digits=3), format(post$sd_log10a, digits=3), format(post$mean_b, disgits=3), format(post$sd_b, digits=3),
                        paste("LWR estimates for this species"))    
            write.table(out.SpC, file=OutFile, append = T, sep=",", dec=".", row.names=F, col.names=F)
         
            } else 
            #--------------------------------------------------------
            # 1-4 LWR in target species and >= 5 LWR species in Genus  
            # run hierarchical analysis for genus members, with Subfamily-BS prior
            if(LWR >= 1 & LWRGenspec >=5) {
              # run Subfamily analysis with only data for this genus
              Keep <- which(Data$Family==Fam & Data$Subfamily==SF & Data$BodyShapeI==BS & is.na(Data$b)==F & Data$Score>0 &
                                 Data$Genus==Genus)
              wts  <- Data$Score[Keep]  # Un-normalized weights (so that Cov is comparable among analyses)
              a    <- Data$a[Keep] 
              b    <- Data$b[Keep]
              GenSpec <- paste(Data$Genus[Keep],Data$Species[Keep])
                 
              # Relabel GenSpec so that TargetSpec = level 1
              OtherSpecies = unique(GenSpec[GenSpec != TargetSpec])
              GenusSpecies = factor(GenSpec, levels=c(TargetSpec, OtherSpecies))
              Nspecies = nlevels(GenusSpecies) # number of species
              # run Bayesian analysis for target species with Genus members
              cat("Running analysis with congeners for", TargetSpec, ", LWR =", LWR,", LWR species in Genus =", LWRGenspec,"\n")
              post <- SpecRelLWR(a, b, wts, GenusSpecies, Nspecies, 
                        prior_mean_b=prior.SFam.BS$R_mean_b, 
                        prior_tau_b=1/prior.SFam.BS$R_sd_b^2, 
                        prior_mean_log10a=prior.SFam.BS$R_mean_log10a, 
                        prior_tau_log10a=1/prior.SFam.BS$R_sd_log10a^2, 
                        SD_rObs_log10a=prior$SD_rObs_log10a, 
                        SD_muObs_log10a=prior$SD_muObs_log10a, SD_rObs_b=prior$SD_rObs_b, 
                        SD_muObs_b=prior$SD_muObs_b, SD_rGS_log10a=prior$SD_rGS_log10a, 
                        SD_muGS_log10a=prior$SD_muGS_log10a, SD_rGS_b=prior$SD_rGS_b, 
                        SD_muGS_b=prior$SD_muGS_b)  
              out.SpC <- data.frame(Fam, SF, BS, SpC, LWR, format(post$mean_log10a, digits=3), format(post$sd_log10a, digits=3), format(post$mean_b, disgits=3), format(post$sd_b, digits=3),
                              paste("LWR estimates for species & Genus-BS"))    
              write.table(out.SpC, file=OutFile, append = T, sep=",", dec=".", row.names=F, col.names=F)
              } else
              
              #-------------------------------------------------------
              # 1-4 LWR in target species and < 5 LWR species in Genus
              # run hierarchical analysis for Subfamily members, with bodyshape prior         
        
              if(LWR >= 1 & LWRSFamspec > 1) {
                # run Subfamily analysis 
                Keep <- which(Data$Family==Fam & Data$Subfamily==SF & Data$BodyShapeI==BS & is.na(Data$b)==F & Data$Score>0)
                wts  <- Data$Score[Keep]  # Un-normalized weights (so that Cov is comparable among analyses)
                a    <- Data$a[Keep] 
                b    <- Data$b[Keep]
                GenSpec <- paste(Data$Genus[Keep],Data$Species[Keep])
                # Relabel GenSpec so that TargetSpec = level 1
                OtherSpecies = unique(GenSpec[GenSpec != TargetSpec])
                GenusSpecies = factor(GenSpec, levels=c(TargetSpec, OtherSpecies))
                Nspecies = nlevels(GenusSpecies) # number of species
                # run Bayesian analysis for target species with Subfamily members
                cat("Running analysis with Subfamily members for", TargetSpec, ", LWR =", LWR,", LWR species in Subfamily-BS =", 
                      LWRSFamspec, "\n")
                post <- SpecRelLWR(a, b, wts, GenusSpecies, Nspecies, 
                        prior_mean_b=prior$mean_b, 
                        prior_tau_b=prior$tau_b, 
                        prior_mean_log10a=prior$mean_log10a, 
                        prior_tau_log10a=prior$tau_log10a, 
                        SD_rObs_log10a=prior$SD_rObs_log10a, 
                        SD_muObs_log10a=prior$SD_muObs_log10a, SD_rObs_b=prior$SD_rObs_b, 
                        SD_muObs_b=prior$SD_muObs_b, SD_rGS_log10a=prior$SD_rGS_log10a, 
                        SD_muGS_log10a=prior$SD_muGS_log10a, SD_rGS_b=prior$SD_rGS_b, 
                        SD_muGS_b=prior$SD_muGS_b)  
                out.SpC <- data.frame(Fam, SF, BS, SpC, LWR, format(post$mean_log10a, digits=3), format(post$sd_log10a, digits=3), 
                                format(post$mean_b, disgits=3), format(post$sd_b, digits=3),
                                paste("LWR estimates for species & Subfamily-BS"))    
                write.table(out.SpC, file=OutFile, append = T, sep=",", dec=".", row.names=F, col.names=F)
              } else
                #--------------------------------------------------
                # assign Genus-BS priors to target species
                if(LWRGenspec >= 5) {
                  cat("Assign Genus-BS prior for", TargetSpec, "\n")
                  out.SpC <- data.frame(Fam, SF, BS, SpC, LWR, format(prior.Gen.BS[[Genus]]$mean_log10a, digits=3), 
                                  format(prior.Gen.BS[[Genus]]$sd_log10a, digits=3), 
                                  format(prior.Gen.BS[[Genus]]$mean_b, digits=3), format(prior.Gen.BS[[Genus]]$sd_b, digits=3),
                                  paste("LWR estimates for this Genus-BS"))    
                  write.table(out.SpC, file=OutFile, append = T, sep=",", dec=".", row.names=F, col.names=F)
                } else {
                  # -----------------------------------------------
                  # assign Subfamily-BS priors to target species
                  cat("Assign Subfamily-BS prior for", TargetSpec,"\n")
                  out.SpC <- data.frame(Fam, SF, BS, SpC, LWR, format(prior.SFam.BS$mean_log10a, digits=3), format(prior.SFam.BS$sd_log10a, digits=3),
                                format(prior.SFam.BS$mean_b, digits=3), format(prior.SFam.BS$sd_b, digits=3), paste("LWR estimates for this Subfamily-BS")) 
                  write.table(out.SpC, file=OutFile, append = T, sep=",", dec=".", row.names=F, col.names=F) 
                  }
      } # end of species loop for this Subfamily and body shape 
    
    } # end of section dealing with Subfamily - body shapes that contain LWR estimates
    
   } # end of section that deals with Subfamily - body shapes that contain species  
     
  } # end of body shape section
 
 } # end of Subfamily section
  
} # end of Family section
cat("End", date(),"\n")









