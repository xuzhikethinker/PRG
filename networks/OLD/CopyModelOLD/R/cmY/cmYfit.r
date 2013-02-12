colourlist=c("black", "red", "blue", "green", "magenta", "brown");
dataFrame <- read.table("cmY-tt5009000-tu1000-te1000-tf5000000-ni1000-na1000-pr0.090-mr4r999t5009000.turnoverh.dat", header=TRUE, sep="\t");
Ymax <- 50
fitdf <- data.frame(Y = dataFrame$Y[1:Ymax], AV = dataFrame$AV[1:Ymax]);

fit <- lm(log(AV) ~ log(Y), data = fitdf);
plot(dataFrame$Y, dataFrame$AV,   type="p", log="xy" ,  col=colourlist[1], pch=1, xlim=c(1,90), ylim=c(0.1,100), xlab="y list length", ylab="z turnover", main="test")
lines(fitdf$Y, exp(fitted(fit)), col=colourlist[2]);
summary(fit)
