% Generating Function PLY E1e3 m2 

kav=0;
kmin=1
E=1e3;
kvector=kmin:E;
pp(1)=0.9;
NActive =[];
% titlename='BCM m2 E1e3';
% filenamemasterroot='BCMm2et5000000ni1000na1000';
% filenameroot=[filenamemasterroot,'pp0.9pr0.0'];
pr= 1-pp(1);
% [NA,M1,V1] = DataProcess (filenameroot,kav,E,pr);
DD1= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];

nzvalues = 10
zmax =2
zvalues= 0:nzvalues;
zvalues=zvalues*zmax/nzvalues
for i=1:(nzvalues+1)
    for j=kmin:E
        GFmat(i,j)=zvalues(i)^j;
    end;
end;

