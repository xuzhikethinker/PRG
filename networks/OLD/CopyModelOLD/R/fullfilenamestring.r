# returns a standard directory string including final backslash
fullfilenamestring <- function(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring) {
filename <- filenamestring(startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
dirname <- dirnamestring(rootdirname,startstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
paste(dirname,filename,sep="")
}
