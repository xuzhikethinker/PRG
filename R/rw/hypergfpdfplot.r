colourlist=c("black", "red", "blue", "brown", "magenta", "green" );
source("hypergfpdf.r")
source("hypergfpdfparam.r")


E=1000
K=4
klist=1:E
n=0

pr=1/(E*10)
n=n+1
paramlist=hypergfpdfparam(E,K,pr)
lnpklist=list()
for (kvalue in klist) {
lll=length(lnpklist)+1
lnpklist[[lll]]=hypergfpdf(kvalue,paramlist$a,paramlist$b,paramlist$c)
}
plot(klist,lnpklist, type="b", log="x", col=colourlist[n], pch=n, xlim=c(1,E), ylim=c(-20,0), xlab="degree k", ylab="ln(p(k))", main="Exemplary Moran Model");

pr=1/E
paramlist=hypergfpdfparam(E,K,pr)
n=n+1
lnpklist=list()
for (kvalue in klist) {
lll=length(lnpklist)+1
lnpklist[[lll]]=hypergfpdf(kvalue,paramlist$a,paramlist$b,paramlist$c)
}
points(klist,lnpklist, type="b", col=colourlist[n], pch=n);


pr=10.0/E
paramlist=hypergfpdfparam(E,K,pr)
n=n+1
lnpklist=list()
for (kvalue in klist) {
lll=length(lnpklist)+1
lnpklist[[lll]]=hypergfpdf(kvalue,paramlist$a,paramlist$b,paramlist$c)
}
points(klist,lnpklist, type="b", col=colourlist[n], pch=n);



pr=0.5/(1+K)
paramlist=hypergfpdfparam(E,K,pr)
n=n+1
lnpklist=list()
for (kvalue in klist) {
lll=length(lnpklist)+1
lnpklist[[lll]]=hypergfpdf(kvalue,paramlist$a,paramlist$b,paramlist$c)
}
points(klist,lnpklist, type="b", col=colourlist[n], pch=n);



pr=0.9999
paramlist=hypergfpdfparam(E,K,pr)
n=n+1
lnpklist=list()
for (kvalue in klist) {
lll=length(lnpklist)+1
lnpklist[[lll]]=hypergfpdf(kvalue,paramlist$a,paramlist$b,paramlist$c)
}
points(klist,lnpklist, type="b", col=colourlist[n], pch=n);





