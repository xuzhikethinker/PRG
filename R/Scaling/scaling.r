# Test scaling

testOn=FALSE;

#number of universities
numberUniv =500
minPapers=1000
maxPapers=10000

if (testOn){
 numberUniv =2
 minPapers=10
 maxPapers=20
}

# set up logarithmic university size distribution
multPapers=exp((1/numberUniv) * log((maxPapers/minPapers)))
totalPapers <- rep(-1,times=numberUniv)
totalPapers[1]=minPapers
for (u in (2:numberUniv)) totalPapers[u] <- round(totalPapers[u-1]*multPapers)


# number papers of university u = totalPapers[u] 
#stepPapers=round((maxPapers-minPapers)/numberUniv)
#totalPapers <- seq(from=minPapers, by=stepPapers, length=numberUniv)

sdlnorm=1.3
meanlnorm = -sdlnorm*sdlnorm/2
totalCitations <- rep(-1,times=numberUniv)
for (u in (1:numberUniv)){
  # expectedCitations for paper of given field and age
  #expectedCitations=rep(10,times=totalPapers[u])
  expectedCitations=rnorm(totalPapers[u],mean=40,sd=10)
  citationList <- round(rlnorm(totalPapers[u], meanlog = 0, sdlog = sdlnorm)*expectedCitations)
  totalCitations[u]=sum(citationList)
}

plot(totalPapers,totalCitations,log="xy")
windows()
plot(totalPapers,totalCitations/totalPapers,log="xy")

