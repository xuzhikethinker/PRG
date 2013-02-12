# Process a count, returns a histogram using log binning.
# Puts maximum and minimum values in the middle of top and bottom bins
# countlist is a list of values to be binned.  Tries to make sure that 
# first and last bin contain the first and last values.

logbincount <- function(countlist, numberbins=25) {
  fn <- fivenum(countlist);
  binfactor <- (fn[5]/fn[1])^(1/(numberbins-2));
  binboundary <- fn[1]*binfactor^(seq(from=-0.5, length=numberbins, by=1.0));
  hist(countlist, breaks=binboundary, plot=FALSE)
}



# This version insists that boundaries are half integers but this is inappropriate for
# cases where we have normalised counts by some factor 
logbinintegercount <- function(countlist, numberbins=25) {
  fn <- fivenum(countlist);
  binfactor <- (fn[5]/fn[1])^(1/(numberbins));
  binboundary <- fn[1]-0.5
  iii=1
  while (binboundary[iii]<fn[5]){
    newbndry <- round( (binboundary[iii]+0.5)*binfactor)+0.5
    binboundary[iii+1] <- max(newbndry,binboundary[iii]+1)
    iii=iii+1
    }
  hist(countlist, breaks=binboundary, plot=FALSE)  
}


