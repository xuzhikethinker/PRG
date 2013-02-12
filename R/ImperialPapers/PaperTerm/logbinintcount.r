# Process a count, returns a histogram using log bins for INTEGERS
# Puts maximum and minimum values in the middle of top and bottom bins
logbinintcount <- function(countlist, binfactor, numberbins=25) {
fn <- fivenum(countlist);
#binfactor <- (fn[5]/fn[1])^(1/(numberbins-2));
binboundary <- numeric()
binboundary[1]  <- floor(fn[1])-0.5
if (binboundary[1]<1) binboundary[2]<-1.5
while (fn[5]>binboundary[length(binboundary)]) {
 binboundary[length(binboundary)+1] <- floor(ceiling(binboundary[length(binboundary)])*binfactor)+0.5
}
hist(countlist, breaks=binboundary, plot=FALSE)
}

