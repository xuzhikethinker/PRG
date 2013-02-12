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
  fn <- fivenum(countlist)
  cbbindex=1 # current binboundary index

# We need this loop because if there are NO zero cited papers (in the bin 0-0.5) then the log(citations=0) -> -inf.
# The log of the no. of citations, c, is needed in log(c) from the lognormal distribution, therefore c cannot be zero. 
  if(fn[1]==0){
     # second smallest assumed to be 1
     binfactor <- (fn[5])^(1/numberbins)   
     binboundary=0
     binboundary[2]=0.5
     cbbindex=2
  } else {
     binfactor <- (fn[5]/ fn[1])^(1/numberbins) 
     binboundary=fn[1]
     cbbindex=1
    }
 while (binboundary[cbbindex]<fn[5]){
    newbndry <- round( (binboundary[cbbindex]+0.5)*binfactor)+0.5
    binboundary[cbbindex+1] <- max(newbndry,binboundary[cbbindex]+1)
    cbbindex=cbbindex+1
    }
  hist(countlist, breaks=binboundary, plot=FALSE)  
}

