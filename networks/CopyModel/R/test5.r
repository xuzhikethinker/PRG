colourlist=c("black", "red", "blue", "green", "magenta", "brown");
source("C:\\PRG\\networks\\CopyModel\\R\\fitform.r")
source("C:\\PRG\\networks\\CopyModel\\R\\filenamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\readandfitdata.r")

startstring="up-tt20000-tu100"
endstring = "r9999t20000.tdatanh.dat"
Xvalue="1"
mstring="4"
Evalue=100
Nvalue=100
prvalue=0.01

filename <-filenamestring(startstring,endstring,Evalue,Nvalue,prvalue,Xstring,mstring)
table4001 <- read.table(filename, header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
#fitdf <- data.frame(tt = table4001$X.t, F2 = table4001$F2);
fitres4001 <- fitform(table4001,Evalue,Nvalue,prvalue);
summary(fitres4001)

table4050 <- read.table("up-tt20000-tu100-te50-ni100-na100-pr0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
fitres4050 <- fitform(table4050,Evalue,Nvalue,prvalue);
summary(fitres4050)

table4100 <- read.table("up-tt20000-tu100-te100-ni100-na100-pr0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
fitres4100 <- fitform(table4100,Evalue,Nvalue,prvalue);
summary(fitres4100)

table6001 <- read.table("up-tt20000-tu100-te1-ni100-na100-pr0.01-mr6r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
#fitdf <- data.frame(tt = table6001$X.t, F2 = table6001$F2);
fitres6001 <- fitform(table6001,Evalue,Nvalue,prvalue);
summary(fitres6001)

table6050 <- read.table("up-tt20000-tu100-te50-ni100-na100-pr0.01-mr6r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
fitres6050 <- fitform(table6050,Evalue,Nvalue,prvalue);
summary(fitres6050)

table6100 <- read.table("up-tt20000-tu100-te100-ni100-na100-pr0.01-mr6r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
fitres6100 <- fitform(table6100,Evalue,Nvalue,prvalue);
summary(fitres6100)

plot(table4001[,1],table4001[,17], col=colourlist[1])
#points(fitdf$tt,fitted(fitres4001),col=colourlist[2])
lines(spline(fitdf$tt,fitted(fitres4001)), col=colourlist[3])
