colourlist=c("black", "red", "blue", "green", "magenta", "brown");
#testtable4 <- read.table("testall.dat", header=TRUE, sep ="\t", comment.char = "")
testtable4c <- read.table("up-tt20000-tu100-te100-ni100-na100-pp0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

testtable5c <- read.table("up-tt20000-tu100-te100-ni100-na100-pp0.01-mr5r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

testtable6c <- read.table("up-tt20000-tu100-te100-ni100-na100-pp0.01-mr6r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

testtable7c <- read.table("up-tt20000-tu100-te100-ni100-na100-pp0.01-mr7r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

plot(testtable4c[,1],testtable4c[,17], col=colourlist[1], ylim=c(0,0.4))
points(testtable5c[,1],testtable5c[,17], col=colourlist[2])
points(testtable6c[,1],testtable6c[,17], col=colourlist[3])
points(testtable7c[,1],testtable7c[,17], col=colourlist[4])


