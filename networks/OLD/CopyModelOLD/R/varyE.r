colourlist=c("black", "red", "blue", "green", "magenta", "brown");
source("C:\\PRG\\networks\\CopyModel\\R\\fitform.r")
source("C:\\PRG\\networks\\CopyModel\\R\\ActualTimings.r")
source("C:\\PRG\\networks\\CopyModel\\R\\filenamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\dirnamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\fullfilenamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\readandfitdata.r")

rootdirname="C:\\PRG\\networks\\CopyModel\\output\\UPnew\\"

#Next two values are those attempted in the batch file.  Can be modified if not divisors of X
numberRewiringsTotal =504000

# endstring set by programme so needs to be calculated
runstring = "r9999"
extensionstring =".tdatanh.dat"


Evaluelist=c(seq(100,1000,by=100))
Nvalue=100
prvaluelist=c(0.01,0.005, 0.00333333, 0.0025, 0.002, 0.001666666, 0.001428571, 0.00125, 0.001111111, 0.001)
valuelist=1:length(Evaluelist)

Xvalue=20
#Xvaluelist=c(1,10,20,40,50,80,100)
#Xvaluelist=c(1,seq(10,100,by=10)) 
#Xvaluelist=c(1, seq(10,100,by=10) ,5,15,24,35,45,56,64,75,84,96)

mstring="4"


# create empty objects of correct mode
F20list4=numeric()
F2inflist4=numeric()
F2inflowlist4=numeric()
F2infuplist4=numeric()
lam2list4=numeric()
lam2lowlist4=numeric()
lam2uplist4=numeric()
resultlist4 <- list()

for (iii in valuelist) {
Evalue=Evaluelist[iii]
Nvalue=Evalue
prvalue=prvaluelist[iii]

print(Evalue)

# start string set by batch file so uses input values
numberRewiringsPerUpdate = Evalue
startstring=paste("up-tt",numberRewiringsTotal,"-tu",numberRewiringsPerUpdate,sep="")
timings=ActualTimings(numberRewiringsTotal , numberRewiringsPerUpdate, Xvalue);
endstring=paste(runstring,"t",timings$numberRewiringsTotal,extensionstring,sep="")

lll=length(resultlist4)+1
resultlist4[[lll]] <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
ccc <- coef(resultlist4[[lll]]$fitres) 
#conf <- confint(resultlist4[[lll]]$fitres) 
lam2list4[lll]  = ccc[1][[1]]
#lam2lowlist4[lll] = conf[1]
#lam2uplist4[lll] = conf[4]
F2inflist4[lll] = ccc[2][[1]]
#F2inflowlist4[lll] = conf[2]
#F2infuplist4[lll] = conf[5]
F20list4[lll]   = ccc[3][[1]]
}
tau2list4=-1/log(lam2list4)
tau2X4lm <- lm(tau2 ~ E, data.frame(E=Evaluelist,tau2=tau2list4))

tau2uplist4=-1/log(lam2lowlist4)
tau2lowlist4=-1/log(lam2uplist4)

mstring="6"
# create empty objects of correct mode
F20list6=numeric()
F2inflist6=numeric()
lam2list6=numeric()
lam2lowlist6=numeric()
lam2uplist6=numeric()
resultlist6 <- list()

for (iii in valuelist) {
Evalue=Evaluelist[iii]
Nvalue=Evalue
prvalue=prvaluelist[iii]

print(Evalue)

# start string set by batch file so uses input values
numberRewiringsPerUpdate = Evalue
startstring=paste("up-tt",numberRewiringsTotal,"-tu",numberRewiringsPerUpdate,sep="")
timings=ActualTimings(numberRewiringsTotal , numberRewiringsPerUpdate, Xvalue);
endstring=paste(runstring,"t",timings$numberRewiringsTotal,extensionstring,sep="")

lll=length(resultlist6)+1
resultlist6[[lll]] <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
ccc <- coef(resultlist6[[lll]]$fitres) 
#conf <- confint(resultlist6[[lll]]$fitres) 
lam2list6[lll]  = ccc[1][[1]]
#lam2lowlist6[lll] = conf[1]
#lam2uplist6[lll] = conf[4]
F2inflist6[lll] = ccc[2][[1]]
F20list6[lll]   = ccc[3][[1]]
}
tau2list6=-1/log(lam2list6)
tau2X6lm <- lm(tau2 ~ E, data.frame(E=Evaluelist,tau2=tau2list6))

#tau2uplist6=-1/log(lam2lowlist6)
#tau2lowlist6=-1/log(lam2uplist6)

# error bar tricks from http://wiki.r-project.org/rwiki/doku.php?id=tips:graphics-base:errbars

windows()
titlestring <- paste("E=various, N=E, p_rE= 1.0, X=20, m= 4(",colourlist[1],"), 6(",colourlist[2],")")
plot(Evaluelist,tau2list4,col=colourlist[1], pch=1,  xlab="E", ylab=expression(tau[2]), main=titlestring)
#segments(Evaluelist,tau2lowlist4,Evaluelist,tau2uplist4,col=colourlist[1])
points(Evaluelist,tau2list6,col=colourlist[2], pch=2)
#segments(Evaluelist,tau2lowlist6,Evaluelist,tau2uplist6,col=colourlist[2])
#lines(spline(Evaluelist,fitted(tau2X4lm)), col=colourlist[1], lty=2)
#lines(spline(Evaluelist,fitted(tau2X6lm)), col=colourlist[2], lty=2)

windows()
titlestring <- paste("E=various, N=E, p_rE=1.0, X=20, m= 4(",colourlist[1],"), 6(",colourlist[2],")")
plot(Evaluelist,F2inflist4,col=colourlist[1], pch=1,  xlab="E", ylab=expression(F[2](infinity)), main=titlestring)
#segments(Evaluelist,F2inflowlist4,Evaluelist,F2infuplist4,col=colourlist[1])
points(Evaluelist,F2inflist6,col=colourlist[2], pch=2)
