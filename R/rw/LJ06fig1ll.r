colourlist=c("black", "red", "blue", "green", "magenta", "brown");
# values used orginally
pporiginallist=c(0.006, 0.015, 0.055, 0.254682,0.752179328,0.99009901);
#assuming pp=pe
pelist=c(0.01,0.25,0.75,0.95,0.99,0.999);
prlist=1-pelist;
p1llorig=plot(ddpdf(1,398,398,1.99,prlist[1]), type="l", log="xy" , col=colourlist[1], ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 1 (ll, orig)");
for ( i in 2:length(prlist)) lines(ddpdf(1,398,398,1.99,prlist[i]),col=colourlist[i])
source("SimonData.r");
points(s010, col=colourlist[1]);
points(s250, col=colourlist[2]);
points(s750, col=colourlist[3]);
points(s950, col=colourlist[4]);
points(s990, col=colourlist[5]);
points(s999, col=colourlist[6]);
