# Plots for LJ06 fig 2 right, Henrik version for parameters
colourlist=c("black", "red", "blue", "green", "magenta", "brown", "cyan");
#Nlist= c(  25,  50, 100, 200, 400, 800,1600);
Klist= c(1.99,3.98,7.96,15.92,31.84,99.5,197.01);
# Stubs list = 2* edges in LJ model
Slist= c(398,796,1592,3184,6368,19900,39402);
#N=25*2^n, Pe=1, Pp=C=Pe=c(0.01,0.02,0.04,0.08,0.16,0.5,0.99), Pn=Pp(1-Pe)/(1-Pp)=0
SubString="N=200, Pp=C0=0.01,0.02,0.04,0.08,0.16,0.5,0.99 ,Pe=1, Pn=0";

i =1;
p1=plot(ddpdf(1,1000,Slist[1],Klist[1],0.005), type="l", log="xy" , col=colourlist[1], ylim=c(1e-6,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 2 right C0=0.01..0.99", sub=SubString);
for ( i in 2:length(prlist)) lines(ddpdf(1,1000,Slist[i],Klist[i],0.005),col=colourlist[i])
#title=("Laird and Jensen EPL 2006 Fig 2 left ", SubString);