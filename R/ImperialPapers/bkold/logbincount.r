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

