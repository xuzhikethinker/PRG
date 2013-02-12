% Plot out BCM E1e3 m2 variable time Bentley Copy Model Results

kav=0;
E=1e3;
kvector=1:E;
pp(1)=0.99;
NActive =[];
titlename='BCM m2 E1e3 pp0.99';
filenamemasterroot='BCMm2et5000000ni1000na1000';
label1='et5e6';
filenameroot=[filenamemasterroot,'pp0.99pr0.0'];
pr= 1-pp(1);
[NA,M1,V1] = DataProcess (filenameroot,kav,E,pr);
DD1= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];
%pp(2)=0.99;
%pr= 1-pp(2);
filenamemasterroot='BCMm2et500000ni1000na1000';
label2='et5e5';
filenameroot=[filenamemasterroot,'pp0.99pr0.0'];
DD2= DegreeDistribution(kvector,kav,E,pr);
[NA,M2,V2] = DataProcess (filenameroot,kav,E,pr);
NActive=[NActive,NA];
% pp(3)=0.999;
% pr= 1-pp(3);
filenamemasterroot='BCMm2et50000ni1000na1000';
label3='et5e4';
filenameroot=[filenamemasterroot,'pp0.99pr0.0'];
[NA,M3,V3] = DataProcess (filenameroot,kav,E,pr);
DD3= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];
% pp(4)=0.99;
% pr= 1-pp(4);
filenamemasterroot='BCMm2et5000ni1000na1000';
label4='et5e3';
filenameroot=[filenamemasterroot,'pp0.99pr0.0'];
[NA,M4,V4] = DataProcess (filenameroot,kav,E,pr);
DD4= DegreeDistribution(kvector,kav,E,pr);
NActive=[NActive,NA];
% pp(5)=0.999;
% pr= 1-pp(5);
% filenameroot=[filenamemasterroot,'pp0.999pr0.0'];
% label5='et5e5';
% [NA,M5,V5] = DataProcess (filenameroot,kav,E,pr);
% DD5= DegreeDistribution(kvector,kav,E,pr);
% NActive=[NActive,NA];
% pp(6)=0.9999;
% pr= 1-pp(6);
% filenameroot=[filenamemasterroot,'pp0.9999pr0.0'];
% label6='et5e5';
% [NA,M6,V6] = DataProcess (filenameroot,kav,E,pr);
% DD6= DegreeDistribution(kvector,kav,E,pr);
% NActive=[NActive,NA];

figure(1);
label0='Exact MF';
loglog(kvector(:),DD1(:),'-k');
title(titlename); % Make text color red
xlabel('k');
xlim([1 E]);
ylim([1e-5 10]);
hold
loglog(M1(:,1),M1(:,2)/M1(1,2),'+r');
loglog(M2(:,1),M2(:,2)/M2(1,2),'og');
loglog(M3(:,1),M3(:,2)/M3(1,2),'*b');
loglog(M4(:,1),M4(:,2)/M4(1,2),'sc');
legend(label0,label1,label2,label3,label4)
%loglog(M5(:,1),M5(:,2)/M5(1,2),'dm',kvector(:),DD5(:),'-m');
%loglog(M6(:,1),M6(:,2)/M6(1,2),'hy',kvector(:),DD6(:),'-y');
hold off

figure(2);
%errorbar(M1(:,1),V1(:,1),V1(:,2),'+r');
semilogx(M1(:,1),V1(:,1),'+r');
title(titlename); % Make text color red
xlabel('k');
xlim([1 E]);
ylim([0 2]);
hold
semilogx(M2(:,1),V2(:,1),'og');
semilogx(M3(:,1),V3(:,1),'*b');
semilogx(M4(:,1),V4(:,1),'sc');
legend(label1,label2,label3,label4)
%semilogx(M5(:,1),V5(:,1),'dm');
%semilogx(M6(:,1),V6(:,1),'hy');
hold off
