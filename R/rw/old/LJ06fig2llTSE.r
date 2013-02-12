# Plots for LJ06 fig 2 left, TSE version or Henrik version for parameters
colourlist=c("black", "red", "blue", "green", "magenta", "brown", "cyan");
Nlist= c(  25,  50, 100, 200, 400, 800,1600);
Klist= c(0.24,0.49,0.99,1.99,3.99,7.99,15.99);
prlist= c(0.04,0.02,0.01,0.005,0.0025,0.00125,0.000625);
#prlist=c(0.99009901, 0.980200081, 0.96039616,0.920764483,0.841405871,0.502818999,0.01071237);

i =1;
p1=plot(ddpdf(1,398,398,Klist[i],prlist[i]), type="l", log="xy" , col=colourlist[1], ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 2 left (N varies)", sub="Pp=C0=0.01,Pe=1");
for ( i in 2:length(prlist)) lines(ddpdf(1,398,398,Klist[i],prlist[i]),col=colourlist[i])
