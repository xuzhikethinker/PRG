# Analyse patents
inputDir="input/"
outputDir="output/"
filenumber=0
screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE
OSWindows=TRUE


headerOn=TRUE

if (filenumber==0) { rootName="NBER110426";
 citeFileList <- list(citeName="cite75_99",
 headerOn=TRUE,  sepString=",",  cc="character",  ending=".txt");
 patFileList <- list(patName="pat63_99",
 headerOn=TRUE,  sepString=",",  cc="character",  ending=".txt")
} 
if (filenumber==1) { rootName="test";
 citeFileList <- list(citeName="cite",
 headerOn=TRUE,  sepString=",",  cc="character",  ending=".txt");
 patFileList <- list(patName="pat",
 headerOn=TRUE,  sepString=",",  cc="character",  ending=".txt")
} 
if (filenumber==2) { citeFileList <- list(rootName="NBER110426part",
  headerOn=FALSE, sepString="", cc="character", ending="inputELS.dat")
 patFileList <- list(rootName="NBER110426partpat63_99",
 headerOn=TRUE,  sepString=",",  cc="character",  ending=".txt")
} 
if (filenumber==3) { citeFileList <- list(rootName="NBER110426",
 headerOn=FALSE,  sepString="", cc="character",  ending="inputELS.dat")
}

gdegree <- function(g){
 gin <- degree(g, v=V(g), mode = "in", loops = TRUE)
 gout <- degree(g, v=V(g), mode = "out", loops = TRUE)
 gname <- get.vertex.attribute(g,"name",V(g))
 df <- data.frame(name=gname,indegree=gin, outdegree=gout)
}

gothersummary <- function(g){
 ne <- length(E(g))
 nv <- length(V(g))
 isloop <-is.loop(g)
 hasloops <- all(isloop)
 nloops<- length(isloop[isloop==TRUE])
# nmultiple<- length(is.multiple(g)[is.multiple(g)==FALSE])
 nmultiple<- count.multiple(g)
 df <- data.frame(ne =ne, nv=nv, nloops=nloops, nmultiple=nmultiple)
}
gsummary <- function(g){
 gdeg <- gdegree(g)
 gother <- gothersummary(g)

 df <- data.frame(ne =gother$ne, nv=gother$nv, indegree=gdeg$indegree, outdegree=gdeg$outdegree,   nloops=gother$nloops, nmultiple=gother$nmultiple)
}



edgeListFile <- paste(rootName,citeFileList$citeName,citeFileList$ending,sep="")
 
print(paste(rootName),quote=FALSE)
print(paste("edge list file",edgeListFile),quote=FALSE)

el <- read.table(edgeListFile, header=citeFileList$headerOn, quote = "\"", colClasses= citeFileList$cc, sep=citeFileList$sepString);
#el <- scan(edgeListFile, header=headerOn, quote = "\"", colClasses= cc, sep=sepString);
elm <- cbind(el[[1]],el[[2]])
library(igraph)

pg <- graph.edgelist(elm, directed=TRUE)
print("*** Raw graph",quote=FALSE)
summary(pg)
#degree(graph, v=V(graph), mode = c("all", "out", "in", "total"), loops = TRUE)

print("*** Simplified graph",quote=FALSE)
spg <- simplify(pg)
summary(spg)
spdegreedf <- gdegree(spg)

degreeFileName <- paste(rootName,citeFileList$citeName,"SIMPLEdegree.dat",sep="")
print(paste("degree list file",degreeFileName),quote=FALSE)
write.table(spdegreedf, file = degreeFileName , append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)



# *********************
# patent file

patentFile <- paste(rootName,patFileList$patName,patFileList$ending,sep="")
 
print(paste(rootName),quote=FALSE)
print(paste("patent info file",patentFile),quote=FALSE)

pl <- read.table(patentFile, header=patFileList$headerOn, quote = "\"", colClasses= patFileList$cc, sep=patFileList$sepString, fill=TRUE);

pbasicdf<- data.frame(name=pl$PATENT, year=pl$GYEAR, cat=pl$CAT, subcat=pl$SUBCAT)
patentBasicFileName <- paste(rootName,patFileList$patName,"basic.dat",sep="")
print(paste("basic patent info file",patentBasicFileName),quote=FALSE)
write.table(pbasicdf, file = patentBasicFileName  , append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)

# ****************************
# merge

#pl$PATENT == spdegreedf$name


