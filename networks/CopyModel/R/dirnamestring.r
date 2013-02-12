# returns a standard directory string including final backslash
dirnamestring <- function(rootdir,startstring,Evalue,Nvalue,prvalue,Xvalue,mstring) {
paste(rootdir,startstring,"-te",as.character(Xvalue),"-ni",as.character(Evalue),"-na",as.character(Nvalue),"-pr",format(prvalue,scientific=FALSE),"-mr",mstring,"\\",sep="")
}

dirnamestring2 <- function(rootdir,startstring,RewiringPerUpdate,Evalue,Nvalue,prvalue,Xvalue,mstring) {
paste(rootdir,startstring,"-tu",as.character(RewiringPerUpdate),"-te",as.character(Xvalue),"-ni",as.character(Evalue),"-na",as.character(Nvalue),"-pr",format(prvalue,scientific=FALSE),"-mr",mstring,"\\",sep="")
}


