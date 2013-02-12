# Plots for LJ06 fig 2 left, TSE version or Henrik version for parameters
colourlist=c("black", "red", "blue", "green", "magenta", "brown", "cyan");
Nlist= c(  25,  50, 100, 200, 400, 800,1600);
Klist= c(0.24,0.49,0.99,1.99,3.99,7.99,15.99);
#TSE list 
#N=25*2^n, C=0.01, Pe=1, Pp=C, Pn=Pp(1-Pe)/(1-Pp)
prlist= c(0.04,0.02,0.01,0.005,0.0025,0.00125,0.000625);
SubString="Pp=C0=0.01,Pe=1, Pn=Pp(1-Pe)/(1-Pp)";

#HJ list
#N=25*2^n, C=0.01, Pe=c(0.01,0.02,0.04,0.08,0.16,0.5,0.99), Pp=C, Pn=Pp(1-Pe)/(1-Pp)
#prlist=c(0.99009901, 0.980200081, 0.96039616,0.920764483,0.841405871,0.502818999,0.01071237);
#SubString="Pp=C0=0.01, Pe=0.01,0.02,0.04,0.08,0.16,0.5,0.99, Pn=Pp(1-Pe)/(1-Pp)";


# N=25*2^n, C=0.01, Pe=1, Pp=c(0.01,0.02,0.04,0.08,0.16,0.5,0.99), Pn=0
#prlist=c(0.04,0.039215686,0.038834951,0.038647343,0.038554217,0.058892815,0.058303887);
#SubString="C0=0.01,Pe=1, Pp=0.01,0.02,0.04,0.08,0.16,0.5,0.99, Pn=Pp(1-Pe)/(1-Pp)";


i =1;
p1=plot(ddpdf(1,398,398,Klist[i],prlist[i]), type="l", log="xy" , col=colourlist[1], ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 2 left N=25*2^n", sub=SubString);
for ( i in 2:length(prlist)) lines(ddpdf(1,398,398,Klist[i],prlist[i]),col=colourlist[i])
#title=("Laird and Jensen EPL 2006 Fig 2 left ", SubString);