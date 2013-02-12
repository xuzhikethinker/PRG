% Plot out PLY Copy Model Results

kav=1;
kmin=1;
titlename='PLY m2 pr.E=10.0 t1e6 r1e3';
filenamemasterroot='PLYr1000m2et10000000';
logbinfile=1;
% *** **********************************************
nnn=1;
pp(nnn)=0.9;
pr= 1-pp(nnn);
E=100;
E1=E;
%kvector1=1:E1;
%kvector=kvector1;
label1=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'ni100na100pp0.9pr0.1'];
[M1,V1,PDF1] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile); 
[rows1, columns1] = size(M1);
% [NA,M1,V1] = DataProcess (filenameroot,kav,E,pr,knormval,logbinfile);
% DD1= DegreeDistribution(kvector,kav,E,pr,knormval);
% NActive=[NActive,NA];
% *** **********************************************
nnn=2;
pp(nnn)=0.99;
pr= 1-pp(nnn);
E=1000;
E2=E;
%kvector2=1:E;
%kvector=kvector2;
label2=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'ni1000na1000pp0.99pr0.01'];
[M2,V2,PDF2] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile);
[rows2, columns2] = size(M2);
% DD2= DegreeDistribution(kvector,kav,E,pr,knormval);
% [NA,M2,V2] = DataProcess (filenameroot,kav,E,pr,knormval,logbinfile);
% NActive=[NActive,NA];
% *** **********************************************
% nnn=3;
% pp(nnn)=0.993;
% pr= 1-pp(nnn);
% E=1.0/pr;
% E3=E;
% kvector3=1:E;
% label3=['p_r=',num2str(pr)];
% filenameroot=[filenamemasterroot,'pp0.993pr0.007'];
% label3='pr=0.007';
% [NA,M3,V3] = DataProcess (filenameroot,kav,E,pr,knormval);
% DD3= DegreeDistribution(kvector,kav,E,pr,knormval);
% NActive=[NActive,NA];
% *** **********************************************
% nnn=4;
% pp(nnn)=0.997;
% pr= 1-pp(nnn);
% E=1.0/pr;
% E4=E;
% kvector4=1:E;
% filenameroot=[filenamemasterroot,'pp0.997pr0.003'];
% label4=['p_r=',num2str(pr)];
% [NA,M4,V4] = DataProcess (filenameroot,kav,E,pr,knormval);
% DD4= DegreeDistribution(kvector,kav,E,pr,knormval);
% NActive=[NActive,NA];
% pp(5)=0.99999;
% *** **********************************************
nnn=5;
pp(nnn)=0.999;
pr= 1-pp(nnn);
E=10000;
E5=E;
% kvector5=1:E;
% kvector=kvector5;
label5=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'ni10000na10000pp0.999pr0.001'];
[M5,V5,PDF5] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile);
[rows5, columns5] = size(M5);
% [NA,M5,V5] = DataProcess (filenameroot,kav,E,pr,knormval,logbinfile);
% DD5= DegreeDistribution(kvector,kav,E,pr,knormval);
% NActive=[NActive,NA];
% *** **********************************************
nnn=6;
pp(nnn)=0.9999;
pr= 1-pp(nnn);
E=100000;
E6=E;
% kvector6=1:E;
% kvector=kvector6;
label6=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'ni100000na100000pp0.9999pr0.0001'];
[M6,V6,PDF6] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile);
[rows6, columns6] = size(M6);
% [NA,M6,V6] = DataProcess (filenameroot,kav,E,pr,knormval,logbinfile);
% DD6= DegreeDistribution(kvector,kav,E,pr,knormval);
% NActive=[NActive,NA];

figure(1);
%errorbar(M1(:,1),M1(:,2)/M1(1,2),M1(:,3)/M1(1,2),'+r');
plot(M1(:,1)/E1,M1(:,2),'+r');
title(titlename); % Make text color red
xlabel('k/E');
ylabel('p(k)');
xlim([1/E5 1]);
ylim([1e-10 1.0]);
hold
plot(M1(:,1)/E1,PDF1,'-r');
%errorbar(M2(:,1),M2(:,2)/M2(1,2),M2(:,3)/M2(1,2),'og');
plot(M2(:,1)/E2,M2(:,2),'og');
plot(M2(:,1)/E2,PDF2(:),'-g');
% errorbar(M3(:,1),M3(:,2)/M3(1,2),M3(:,3)/M3(1,2),'*b');
% plot(kvector(:),DD3(:),'-b');
% errorbar(M4(:,1),M4(:,2)/M4(1,2),M4(:,3)/M4(1,2),'sc');
% plot(kvector(:),DD4(:),'-c');
%errorbar(M5(:,1),M5(:,2)/M5(1,2),M5(:,3)/M5(1,2),'*b');
plot(M5(:,1)/E5,M5(:,2),'*b');
plot(M5(:,1)/E5,PDF5(:),'-b');
%errorbar(M6(:,1),M6(:,2)/M6(1,2),M6(:,2)/M6(1,2),'sm');
% plot(M6(:,1),M6(:,2),'sm');
% plot([kmin:E],PDF6(:),'-m');
%legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
legend(label1,'',label2,'',label5,'')
hold off


% With error bars - too small to see really
% figure(2);
% %errorbar(M1(:,1),M1(:,2)/M1(1,2),M1(:,3)/M1(1,2),'+r');
% % plot(M1(:,1)/E1,M1(:,2)/M1(1,2),'+r');
% errorbar(log10(M1(:,1)/E1),log10(M1(:,2)),lowerLogErrorBar(M1(:,2),M1(:,3)),upperLogErrorBar(M1(:,2),M1(:,3)),'xr');
% title(titlename); % Make text color red
% xlabel('log_{10}(k/E)');
% ylabel('log_{10}(p(k))');
% xlim([log10(1/E5) 0]);
% ylim([-10 0]);
% hold
% % plot(kvector1(:)/E1,DD1(:),'-r');
% plot(log10(M1(:,1)/E1),PDF1,'-r');
% %errorbar(M2(:,1),M2(:,2)/M2(1,2),M2(:,3)/M2(1,2),'og');
% errorbar(log10(M2(:,1)/E2),log10(M2(:,2)),lowerLogErrorBar(M2(:,2),M2(:,3)),upperLogErrorBar(M2(:,2),M2(:,3)),'og');
% plot(log10(M2(:,1)/E2),PDF2(:),'-g');
% % errorbar(M3(:,1),M3(:,2)/M3(1,2),M3(:,3)/M3(1,2),'*b');
% % plot(kvector(:),DD3(:),'-b');
% % errorbar(M4(:,1),M4(:,2)/M4(1,2),M4(:,3)/M4(1,2),'sc');
% % plot(kvector(:),DD4(:),'-c');
% %errorbar(M5(:,1),M5(:,2)/M5(1,2),M5(:,3)/M5(1,2),'*b');
% errorbar(log10(M5(:,1)/E5),log10(M5(:,2)),lowerLogErrorBar(M5(:,2),M5(:,3)),upperLogErrorBar(M5(:,2),M5(:,3)),'*b');
% plot(log10(M5(:,1)/E5),PDF5(:),'-b');
% %errorbar(M6(:,1),M6(:,2)/M6(1,2),M6(:,2)/M6(1,2),'sm');
% %plot(M6(:,1)/E6,M6(:,2),'sm');
% %plot(M6(:,1)/E6,PDF6(:),'-m');
% %legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
% legend(label1,'',label2,'',label5,'')
% hold off



figure(4);
Delta=0.0;
errorbar(log10(M1(:,1)/E1),V1(:,1)+Delta,V1(:,2),'xr');
title(titlename); % Make text color red
xlabel('log_{10} (k/E)');
ylabel('p_{data}(k)/p_{exact}(k) + \Delta');
xlim([log10(1/E5) 0]);
ylim([0 4]);
hold
plot([log10(M1(1,1)/E1) log10(M1(rows1,1))] ,[Delta+1 Delta+1],'-k');
Delta=Delta+1.0;
errorbar(log10(M2(:,1)/E2),V2(:,1)+Delta,V2(:,2),'og');
plot([log10(M2(1,1)/E2) log10(M2(rows2,1))],[Delta+1 Delta+1],'-k');
% semilogx(M3(:,1),V3(:,1),'*b');
% semilogx(M4(:,1),V4(:,1),'sc');
Delta=Delta+1.0;
errorbar(log10(M5(:,1)/E5),V5(:,1)+Delta,V5(:,2),'*b');
plot([log10(M5(1,1)/E5) log10(M5(rows5,1))],[Delta+1 Delta+1],'-k');
%errorbar(M6(:,1)/E6,V6(:,1)+3,V6(:,2),'sm');
%plot([M6(1,1)/E6 M6(rows6,1)],[4.0 4.0],'-k');
%legend(label1,label2,label3,label4,label5,label6)
legend(label1,'',label2,'',label5,'')
hold off





% 
% figure(2);
% semilogx(M1(:,1)/E1,V1(:,1),'+r');
% title(titlename); % Make text color red
% xlabel('k/E');
% ylabel('n_{data}(k)/n_{exact}(k)');
% xlim([1/E 1.0]);
% ylim([0.6 1.4]);
% hold
% semilogx(M2(:,1)/E2,V2(:,1),'og');
% % semilogx(M3(:,1),V3(:,1),'*b');
% % semilogx(M4(:,1),V4(:,1),'sc');
% semilogx(M5(:,1)/E5,V5(:,1),'*b');
% semilogx(M6(:,1)/E6,V6(:,1),'sm');
% %legend(label1,label2,label3,label4,label5,label6)
% legend(label1,label2,label5,label6)
% hold off
