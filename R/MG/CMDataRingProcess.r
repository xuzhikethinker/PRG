# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");

# Switch to control Windows vs Mac interfaces.
OSWindows=TRUE

logxOn=TRUE

rootNameList <- list("infl_ring201k40r999t201000mr6ppm1prm0pr00500infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr02500infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr05000infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr07500infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr10000infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr12500infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr15000infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr17500infl",
                     "infl_ring201k40r999t201000mr6ppm1prm0pr20000infl")
# infl_ring201k20r999t201000mr6ppm1prm0pr04877infl
# infl_ring201k40r999t582800mr6ppm1prm0pr01242infl
prValues=c(0.05,0.025,0.05,0.075,0.1,0.125,0.150,0.175,0.200)
nivalue=201
Nvalue=nivalue #tobyparamdata$N[ccc-1]
ksaveragevalue=40
pvalue=ksaveragevalue/(Nvalue-1)

graphTypeName="ring"
graphKfitShift = 0

ncolList <- seq(length=nivalue+1, from=22, by=2)
klist    <- seq(length=nivalue+1, from=0, by=1)
cmdata <- klist
cmdatasd <- klist
nList=list()
nFiles=length(rootNameList)
for (fff in 1:nFiles){
#rootNameList=c("infl_ring201k40r999t582800mr6ppm1prm0pr01242infl")

rootname <-  rootNameList[fff]
dataFileName = paste("CopyModeldata/",rootname,".tdatanh.dat",sep="")
print(paste("Data from file",dataFileName), quote=FALSE)
cmdatafull <- read.table(dataFileName, header=TRUE, sep="\t", fill=TRUE);

nList <- cmdatafull[ncolList]
nNamenList <- names(cmdatafull)[ncolList]
nmean=sapply(nList,mean)
nsd=sapply(nList,sd)

cmdata <- cbind(cmdata,nmean)
cmdatasd<- cbind(cmdata,nsd)

} # eo for fff

write.table(cmdata, file = "CopyModeldata/infl_ring201k40r999t201000mr6ppm1prm0MEAN_r.dat", append = FALSE, quote = TRUE, sep = "\t",
 row.names = FALSE, col.names = TRUE, )
write.table(cmdatasd, file = "CopyModeldata/infl_ring201k40r999t201000mr6ppm1prm0SD_r.dat", append = FALSE, quote = TRUE, sep = "\t",
 row.names = FALSE, col.names = TRUE, )
#cmdata<-data.frame(k=klist,nList[[1:nFiles]])

