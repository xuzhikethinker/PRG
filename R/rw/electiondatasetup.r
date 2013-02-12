# Election Data Set constructed from Log Normal distribution
# x = k/<k> is variable used
elecmu <- -0.541;
elecsigma <- sqrt(-2*elecmu);
xmin <- 0.01;
xmax <- 20;
lxmin <- log(xmin);
lxmax <- log(xmax);
lxsteps <- 25;
lxinc <- (lxmax-lxmin)/(lxsteps-1);
lxlist <- seq(lxmin, lxmax*1.001, by=lxinc );
xlist <- exp(lxlist);
eleclpdf <- dlnorm(xlist,elecmu,elecsigma,log=TRUE);
eleclpdfdf <- data.frame(lxlist,eleclpdf); # makes a data frame of log/log data
