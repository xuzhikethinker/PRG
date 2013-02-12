% Plot out BCM Bentley Copy Model Results

kav=0;
E=1e4;
kvector=1:E;
pp(1)=0.999;
NActive =[];
titlename='BCM m0 E1e3';
filenamemasterroot='BCMm0et1000000ni1000na1000';
filenameroot=[filenamemasterroot,'pp0.999pr0'];
pr= 1-pp(1);
[NA,M1,V1] = DataProcess (filenameroot,kav,E,pr);
DD1= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];
pp(2)=0.9992;
pr= 1-pp(2);
filenameroot=[filenamemasterroot,'pp0.9992pr0'];
DD2= DegreeDistribution(kvector,kav,E,pr);
[NA,M2,V2] = DataProcess (filenameroot,kav,E,pr);
NActive=[NActive,NA];
pp(3)=0.9996;
pr= 1-pp(3);
filenameroot=[filenamemasterroot,'pp0.9996pr0'];
[NA,M3,V3] = DataProcess (filenameroot,kav,E,pr);
DD3= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];
pp(4)=0.9998;
pr= 1-pp(4);
filenameroot=[filenamemasterroot,'pp0.9998pr0'];
[NA,M4,V4] = DataProcess (filenameroot,kav,E,pr);
DD4= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];
pp(5)=0.9999;
pr= 1-pp(5);
filenameroot=[filenamemasterroot,'pp0.9999pr0'];
[NA,M5,V5] = DataProcess (filenameroot,kav,E,pr);
DD5= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];
pp(6)=0.99995;
pr= 1-pp(6);
filenameroot=[filenamemasterroot,'pp0.99995pr0'];
[NA,M6,V6] = DataProcess (filenameroot,kav,E,pr);
DD6= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];

loglog(M1(:,1),M1(:,2)/M1(1,2),'+r',kvector(:),DD1(:),'-r');
title(titlename); % Make text color red
xlabel('k');
hold
loglog(M2(:,1),M2(:,2)/M2(1,2),'og',kvector(:),DD2(:),'-g');
loglog(M3(:,1),M3(:,2)/M3(1,2),'*b',kvector(:),DD3(:),'-b');
loglog(M4(:,1),M4(:,2)/M4(1,2),'sc',kvector(:),DD4(:),'-c');
loglog(M5(:,1),M5(:,2)/M5(1,2),'dm',kvector(:),DD5(:),'-m');
loglog(M6(:,1),M6(:,2)/M6(1,2),'hy',kvector(:),DD6(:),'-y');
hold off

%errorbar(M(:,1),V(:),Verr(:));
%hold all
%loglog(M(:,1),dd(:));
%hold all
%errorbar(M(:,1),V(:),Verr(:));
%loglog(M(:,1),M(:,2)/M(1,2));
%hold off
