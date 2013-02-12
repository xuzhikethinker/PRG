colourlist=c("black", "red", "blue", "green", "magenta", "brown");
p1=plot(ddpdf(1,398,398,1.99,0.006), type="l", log="xy" , col=colourlist[1], ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 1 (ll)");
lines(ddpdf(1,398,398,1.99,0.015),col=colourlist[2])
lines(ddpdf(1,398,398,1.99,0.055),col=colourlist[3])
lines(ddpdf(1,398,398,1.99,0.254682),col=colourlist[4])
lines(ddpdf(1,398,398,1.99,0.99009901),col=colourlist[5])
lines(ddpdf(1,398,398,1.99,0.752179328),col=colourlist[6])
