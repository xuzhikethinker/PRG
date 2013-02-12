# reads in and fits data
readandfitdata <- function(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
{
fullfilename <-fullfilenamestring(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
datatable <- read.table(fullfilename, header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
fitresult <- fitform(datatable,Evalue,Nvalue,prvalue);
#titlestring <- paste("E=" ,Evalue, ", N=" ,Nvalue, ", pr=" ,format(prvalue,scientific=false), ", X=" ,Xvalue, ", m=",mstring,sep="") 
parameters <- list(fullfilename=fullfilename, startstring=startstring,endstring=endstring, E=Evalue, N=Nvalue, pr=prvalue, X=Xvalue, m=mstring ) 
list(time=datatable[,1],F2=datatable[,17],fitresult=fitresult, parameters=parameters)
}
