#testtable4 <- read.table("testall.dat", header=TRUE, sep ="\t", comment.char = "")
testtable4 <- read.table("testni100na100pr0.01mr0r1t1000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)
plot(testtable4[,1],testtable4[,17])
