# Process a count, returns a histogram using log binning
logbincount <- function(countlist, numberbins=25) {
fn <- fivenum(countlist);
binfactor <- (fn[5]/fn[1])^(1/numberbins);
binboundary <- fn[1]*binfactor^(-1:(numberbins));
hist(countlist, breaks=binboundary)
}

