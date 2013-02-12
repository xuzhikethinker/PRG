colourlist=c("black", "red", "blue", "green", "magenta", "brown");
#testtable4 <- read.table("testall.dat", header=TRUE, sep ="\t", comment.char = "")
testtable4a <- read.table("up-tt20000-tu100-te1-ni100-na100-pr0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable4b <- read.table("up-tt20000-tu100-te50-ni100-na100-pr0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable4c <- read.table("up-tt20000-tu100-te100-ni100-na100-pr0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

testtable5a <- read.table("up-tt20000-tu100-te1-ni100-na100-pr0.01-mr5r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable5b <- read.table("up-tt20000-tu100-te50-ni100-na100-pr0.01-mr5r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable5c <- read.table("up-tt20000-tu100-te100-ni100-na100-pr0.01-mr5r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

testtable6a <- read.table("up-tt20000-tu100-te1-ni100-na100-pr0.01-mr6r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable6b <- read.table("up-tt20000-tu100-te50-ni100-na100-pr0.01-mr6r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable6c <- read.table("up-tt20000-tu100-te100-ni100-na100-pr0.01-mr6r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

testtable7a <- read.table("up-tt20000-tu100-te1-ni100-na100-pr0.01-mr7r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable7b <- read.table("up-tt20000-tu100-te50-ni100-na100-pr0.01-mr7r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
testtable7c <- read.table("up-tt20000-tu100-te100-ni100-na100-pr0.01-mr7r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

plot(testtable4a[,1],testtable4a[,17], col=colourlist[1])
points(testtable4b[,1],testtable4b[,17], col=colourlist[2])
points(testtable4c[,1],testtable4c[,17], col=colourlist[3])

#plot(testtable5a[,1],testtable5a[,17], col=colourlist[1], ylim=c(0.002120,0.002130))
points(testtable5a[,1],testtable5a[,17], col=colourlist[4])
points(testtable5b[,1],testtable5b[,17], col=colourlist[5])
points(testtable5c[,1],testtable5c[,17], col=colourlist[6])

#points(testtable6a[,1],testtable6a[,17], col=colourlist[4])
#points(testtable6b[,1],testtable6b[,17], col=colourlist[5])
#points(testtable6c[,1],testtable6c[,17], col=colourlist[6])

#points(testtable7a[,1],testtable7a[,17], col=colourlist[4])
#points(testtable7b[,1],testtable7b[,17], col=colourlist[5])
#points(testtable7c[,1],testtable7c[,17], col=colourlist[6])


