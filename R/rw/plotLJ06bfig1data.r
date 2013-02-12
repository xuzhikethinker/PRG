#rm(list=ls(all=TRUE))
s250 <- read.table("TSEnode_ak_d200_c01_id25_n.dat", header=TRUE);
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
p2=plot(s250, type="p", log="xy" , col=colourlist[1], ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 1 (ll)");

