vectorStats <-function(vec){
 numberRows<-length(vec)
 totalW<-sum(vec)
 rank <- numberRows+1-rank(vec,  ties.method="first")
 Svec <- vec*log(vec/totalW)/totalW
 #S <- -sum(Svec[!is.nan(Svec)])
 S <- -sum(Svec[is.finite(Svec)])
 meanValue <- mean(vec)
 varValue <- var(vec)
 sdValue <- sd(vec)
 hubOneAv <- length(vec[vec>meanValue])
 hubTwoAv <- length(vec[vec>2*meanValue])
 hubOneZ <- length(vec[vec>meanValue+sdValue])
 resultsList <- list(mean=meanValue, sd=sdValue, var=varValue , entropy=S, hubOneAv=hubOneAv, hubTwoAv=hubTwoAv, hubOneZ=hubOneZ, rankVector=rank)
}
