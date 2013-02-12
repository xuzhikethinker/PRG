# returns a standard filename string
filenamestring <- function(startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring) {
paste(startstring,"-te",as.character(Xvalue),"-ni",as.character(Evalue),"-na",as.character(Nvalue),"-pr",format(prvalue,scientific=FALSE),"-mr",mstring,endstring,sep="")
}
