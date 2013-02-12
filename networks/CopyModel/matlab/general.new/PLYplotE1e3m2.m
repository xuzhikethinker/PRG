% Plot out PLY Copy Model Results

kav=0;
E=100;
kvector=1:E;
knormval=1; % normalise to this value of k
NActive =[];
titlename='PLY m2 E=100 t1e5 r1e4';
filenamemasterroot='PLYr10000m2et100000ni100na100';
% *** **********************************************
nnn=1;
pp(nnn)=0.9;
pr= 1-pp(nnn);
label1=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.9pr0.1'];
[NA,M1,V1] = DataProcess (filenameroot,kav,E,pr,knormval);
DD1= DegreeDistribution(kvector,kav,E,pr,knormval);
NActive=[NActive,NA];
% *** **********************************************
nnn=2;
pp(nnn)=0.99;
pr= 1-pp(nnn);
label2=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.99pr0.01'];
DD2= DegreeDistribution(kvector,kav,E,pr,knormval);
[NA,M2,V2] = DataProcess (filenameroot,kav,E,pr,knormval);
NActive=[NActive,NA];
% *** **********************************************
nnn=3;
pp(nnn)=0.993;
pr= 1-pp(nnn);
label3=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.993pr0.007'];
label3='pr=0.007';
[NA,M3,V3] = DataProcess (filenameroot,kav,E,pr,knormval);
DD3= DegreeDistribution(kvector,kav,E,pr,knormval);
NActive=[NActive,NA];
% *** **********************************************
nnn=4;
pp(nnn)=0.997;
pr= 1-pp(nnn);
filenameroot=[filenamemasterroot,'pp0.997pr0.003'];
label4=['p_r=',num2str(pr)];
[NA,M4,V4] = DataProcess (filenameroot,kav,E,pr,knormval);
DD4= DegreeDistribution(kvector,kav,E,pr,knormval);
NActive=[NActive,NA];
pp(5)=0.99999;
% *** **********************************************
nnn=5;
pp(nnn)=0.995;
pr= 1-pp(nnn);
label5=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.995pr0.005'];
[NA,M5,V5] = DataProcess (filenameroot,kav,E,pr,knormval);
DD5= DegreeDistribution(kvector,kav,E,pr,knormval);
NActive=[NActive,NA];
% *** **********************************************
nnn=6;
pp(nnn)=0.999;
pr= 1-pp(nnn);
label6=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.999pr0.001'];
[NA,M6,V6] = DataProcess (filenameroot,kav,E,pr,knormval);
DD6= DegreeDistribution(kvector,kav,E,pr,knormval);
NActive=[NActive,NA];

figure(1);
loglog(M1(:,1),M1(:,2)/M1(1,2),'+r',kvector(:),DD1(:),'-r');
title(titlename); % Make text color red
xlabel('k');
xlim([1 E]);
ylim([1e-5 10]);
hold
loglog(M2(:,1),M2(:,2)/M2(1,2),'og',kvector(:),DD2(:),'-g');
loglog(M3(:,1),M3(:,2)/M3(1,2),'*b',kvector(:),DD3(:),'-b');
loglog(M4(:,1),M4(:,2)/M4(1,2),'sc',kvector(:),DD4(:),'-c');
loglog(M5(:,1),M5(:,2)/M5(1,2),'dm',kvector(:),DD5(:),'-m');
loglog(M6(:,1),M6(:,2)/M6(1,2),'hy',kvector(:),DD6(:),'-y');
legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
hold off

figure(2);
semilogx(M1(:,1),V1(:,1),'+r');
title(titlename); % Make text color red
xlabel('k');
xlim([1 E]);
ylim([0 2]);
hold
semilogx(M2(:,1),V2(:,1),'og');
semilogx(M3(:,1),V3(:,1),'*b');
semilogx(M4(:,1),V4(:,1),'sc');
semilogx(M5(:,1),V5(:,1),'dm');
semilogx(M6(:,1),V6(:,1),'hy');
legend(label1,label2,label3,label4,label5,label6)
hold off
