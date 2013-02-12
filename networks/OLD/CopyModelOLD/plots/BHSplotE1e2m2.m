% Plot out PLY Copy Model Results

kav=0;
E=1e2;
kmin=1; % minimum value of k to consider
kvector=kmin:E;
NActive =[];
titlename='BHS m2 E=1e2 t1e5 r1e3';
filenamemasterroot='BHSr1000m2et100000ni100na100';
logbinfile=0; % 0 for no log bin, 1 for log bin
% *** **********************************************
nnn=1;
pp(nnn)=0.9;
pr= 1-pp(nnn);
label1=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.9pr0.0'];
[M1,V1,DD1] = DataProcessAvNorm(filenameroot,kav,E,pr,kmin,logbinfile);
% *** **********************************************
nnn=2;
pp(nnn)=0.99;
pr= 1-pp(nnn);
label2=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.99pr0.0'];
[M2,V2,DD2] = DataProcessAvNorm(filenameroot,kav,E,pr,kmin,logbinfile);
% *** **********************************************
nnn=3;
pp(nnn)=0.999;
pr= 1-pp(nnn);
label3=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.999pr0.0'];
label3=['p_r=',num2str(pr)];
[M3,V3,DD3] = DataProcessAvNorm(filenameroot,kav,E,pr,kmin,logbinfile);
% *** **********************************************
nnn=4;
pp(nnn)=0.995;
pr= 1-pp(nnn);
filenameroot=[filenamemasterroot,'pp0.995pr0.0'];
label4=['p_r=',num2str(pr)];
[M4,V4,DD4] = DataProcessAvNorm(filenameroot,kav,E,pr,kmin,logbinfile);
% *** **********************************************
% nnn=5;
% pp(nnn)=0.99999;
% pr= 1-pp(nnn);
% label5=['p_r=',num2str(pr)];
% filenameroot=[filenamemasterroot,'pp0.99999pr0.0'];
% [NA,M5,V5] = DataProcess (filenameroot,kav,E,pr,knormval,logbinfile);
% DD5= DegreeDistribution(kvector,kav,E,pr,knormval);
% NActive=[NActive,NA];
% *** **********************************************
% nnn=6;
% pp(nnn)=0.999;
% pr= 1-pp(nnn);
% label6=['p_r=',num2str(pr)];
% filenameroot=[filenamemasterroot,'pp0.999pr0.0'];
% [NA,M6,V6] = DataProcess (filenameroot,kav,E,pr,knormval,logbinfile);
% DD6= DegreeDistribution(kvector,kav,E,pr,knormval);
% NActive=[NActive,NA];

figure(1);
Delta=1;
semilogy(M1(:,1),M1(:,2)*Delta,'xr',M1(:,1),DD1(:)*Delta,'-r');
title(titlename); % Make text color red
xlabel('k');
ylabel('(n(k)/N_{active})*(10)^{\Delta}');
xlim([1 E]);
ylim([1e-5 1000]);
hold
Delta=Delta*10;
semilogy(M2(:,1),M2(:,2)*Delta,'og',M2(:,1),DD2(:)*Delta,'-g');
Delta=Delta*10;
semilogy(M4(:,1),M4(:,2)*Delta,'sm',M4(:,1),DD4(:)*Delta,'-m');
Delta=Delta*10;
semilogy(M3(:,1),M3(:,2)*Delta,'*b',M3(:,1),DD3(:)*Delta,'-b');
%loglog(M5(:,1),M5(:,2)/M5(1,2),'dm',kvector(:),DD5(:),'-c');
%loglog(M6(:,1),M6(:,2)/M6(1,2),'hy',kvector(:),DD6(:),'-y');
%legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
legend(label1,'',label2,'',label4,'',label3,'')
hold off

% figure(2);
% Delta=0.0;
% plot(M1(:,1),V1(:,1)+Delta,'+r');
% title(titlename); % Make text color red
% xlabel('k');
% ylabel('n_{data}(k)/n_{exact}(k)');
% xlim([1 E]);
% ylim([0 2]);
% hold
% plot(M2(:,1),V2(:,1)+Delta,'og');
% plot(M3(:,1),V3(:,1)+Delta,'*b');
% plot(M4(:,1),V4(:,1)+Delta,'sc');
% %semilogx(M5(:,1),V5(:,1),'dm');
% %semilogx(M6(:,1),V6(:,1),'hy');
% %legend(label1,label2,label3,label4,label5,label6)
% legend(label1,label2,label3,label4)
% hold off
% 

figure(4);
Delta=0.0;
errorbar(M1(:,1),V1(:,1)+Delta,V1(:,2),'xr');
title(titlename); % Make text color red
xlabel('k');
ylabel('n_{data}(k)/n_{exact}(k) + 2\Delta');
xlim([0 E]);
ylim([0 9]);
hold
plot([0 E] , [Delta+1 Delta+1],'-k');
% 2
Delta=Delta+2.0;
errorbar(M2(:,1),V2(:,1)+Delta,V2(:,2),'og');
plot([0 E] , [Delta+1 Delta+1],'-k');
% 4
Delta=Delta+2.0;
errorbar(M4(:,1),V4(:,1)+Delta,V4(:,2),'sm');
plot([0 E] , [Delta+1 Delta+1],'-k');
% 3
Delta=Delta+2.0;
errorbar(M3(:,1),V3(:,1)+Delta,V3(:,2),'*b');
plot([0 E] , [Delta+1 Delta+1],'-k');
%errorbar(M6(:,1)/E6,V6(:,1)+3,V6(:,2),'sc');
%plot([M6(1,1)/E6 M6(rows6,1)],[4.0 4.0],'-k');
%legend(label1,label2,label3,label4,label5,label6)
legend(label1,'',label2,'',label4,'',label3,'')
hold off
