% Plot out PLY Copy Model PDF Results 
% M will be the PDF from data, V are the fractional deviations of data 
% from prediction and the PDF arrays are the prediction.

kav=1;
E=100;
kmin=0; % As PLY can look at k=0
%NActive =[];
titlename='PLY m2 E=100 t1e5 r1e4';
filenamemasterroot='PLYr10000m2et100000ni100na100';
logbinfile=0;
% *** **********************************************
nnn=1;
pp(nnn)=0.9;
pr= 1-pp(nnn);
label1=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.9pr0.1'];
[M1,V1,PDF1] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile); 
% *** **********************************************
nnn=2;
pp(nnn)=0.99;
pr= 1-pp(nnn);
label2=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.99pr0.01'];
[M2,V2,PDF2] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile);
% *** **********************************************
% nnn=3;
% pp(nnn)=0.993;
% pr= 1-pp(nnn);
% label3=['p_r=',num2str(pr)];
% filenameroot=[filenamemasterroot,'pp0.993pr0.007'];
% label3='pr=0.007';
% [NA,M3,V3] = DataProcessPDF (kmin,filenameroot,kav,E,pr);
% DD3= DegreeDistribution(kvector,kav,E,pr);
% *** **********************************************
% nnn=4;
% pp(nnn)=0.997;
% pr= 1-pp(nnn);
% filenameroot=[filenamemasterroot,'pp0.997pr0.003'];
% label4=['p_r=',num2str(pr)];
% [NA,M4,V4] = DataProcessPDF (kmin,filenameroot,kav,E,pr);
% DD4= DegreeDistribution(kvector,kav,E,pr);
% NActive=[NActive,NA];
% pp(5)=0.99999;
% *** **********************************************
nnn=5;
pp(nnn)=0.995;
pr= 1-pp(nnn);
label5=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.995pr0.005'];
[M5,V5,PDF5] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile);
% *** **********************************************
nnn=6;
pp(nnn)=0.999;
pr= 1-pp(nnn);
label6=['p_r=',num2str(pr)];
filenameroot=[filenamemasterroot,'pp0.999pr0.001'];
[M6,V6,PDF6] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile);

% *** **********************************************

% figure(1);
% %errorbar(M1(:,1),M1(:,2)/M1(1,2),M1(:,3)/M1(1,2),'+r');
% plot(M1(:,1),M1(:,2),'+r');
% title(titlename); % Make text color red
% xlabel('k');
% ylabel('p(k)');
% xlim([0 E]);
% ylim([0.5e-6 1.1]);
% hold
% plot([kmin:E],PDF1,'-r');
% %errorbar(M2(:,1),M2(:,2)/M2(1,2),M2(:,3)/M2(1,2),'og');
% plot(M2(:,1),M2(:,2),'og');
% plot([kmin:E],PDF2(:),'-g');
% % errorbar(M3(:,1),M3(:,2)/M3(1,2),M3(:,3)/M3(1,2),'*b');
% % plot(kvector(:),DD3(:),'-b');
% % errorbar(M4(:,1),M4(:,2)/M4(1,2),M4(:,3)/M4(1,2),'sc');
% % plot(kvector(:),DD4(:),'-c');
% %errorbar(M5(:,1),M5(:,2)/M5(1,2),M5(:,3)/M5(1,2),'*b');
% plot(M5(:,1),M5(:,2),'*b');
% plot([kmin:E],PDF5(:),'-b');
% %errorbar(M6(:,1),M6(:,2)/M6(1,2),M6(:,2)/M6(1,2),'sm');
% plot(M6(:,1),M6(:,2),'sm');
% plot([kmin:E],PDF6(:),'-m');
% %legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
% legend(label1,'',label2,'',label5,'',label6,'')
% hold off
% 

% With error bars - too small to see really
figure(2);
%errorbar(M1(:,1),M1(:,2)/M1(1,2),M1(:,3)/M1(1,2),'+r');
% plot(M1(:,1)/E1,M1(:,2)/M1(1,2),'+r');
errorbar((M1(:,1)),log10(M1(:,2)),lowerLogErrorBar(M1(:,2),M1(:,3)),upperLogErrorBar(M1(:,2),M1(:,3)),'xr');
title(titlename); % Make text color red
xlabel('k');
ylabel('log_{10}(p(k))');
xlim([0 E]);
ylim([-6.5 0]);
hold
% plot(kvector1(:)/E1,DD1(:),'-r');
plot((M1(:,1)),log10(PDF1),'-r');
%errorbar(M2(:,1),M2(:,2)/M2(1,2),M2(:,3)/M2(1,2),'og');
errorbar(M2(:,1),log10(M2(:,2)),lowerLogErrorBar(M2(:,2),M2(:,3)),upperLogErrorBar(M2(:,2),M2(:,3)),'og');
plot((M2(:,1)),log10(PDF2(:)),'-g');
% errorbar(M3(:,1),M3(:,2)/M3(1,2),M3(:,3)/M3(1,2),'*b');
% plot(kvector(:),DD3(:),'-b');
% errorbar(M4(:,1),M4(:,2)/M4(1,2),M4(:,3)/M4(1,2),'sc');
% plot(kvector(:),DD4(:),'-c');
%errorbar(M5(:,1),M5(:,2)/M5(1,2),M5(:,3)/M5(1,2),'*b');
errorbar((M5(:,1)),log10(M5(:,2)),lowerLogErrorBar(M5(:,2),M5(:,3)),upperLogErrorBar(M5(:,2),M5(:,3)),'*b');
plot((M5(:,1)),log10(PDF5(:)),'-b');
errorbar((M6(:,1)),log10(M6(:,2)),lowerLogErrorBar(M6(:,2),M6(:,3)),upperLogErrorBar(M6(:,2),M6(:,3)),'sm');
%plot(M6(:,1)/E6,M6(:,2),'sm');
plot(M6(:,1),log10(PDF6(:)),'-m');
%legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
legend(label1,'',label2,'',label5,'',label6,'')
hold off


% figure(2);
% errorbar(M5(:,1),M5(:,2),M5(:,3),'*b');
% %errorbar(M1(:,1),M1(:,2),M1(:,3),'+r');
% %plot(M1(:,1),M1(:,2),'+r');
% title(titlename); % Make text color red
% xlabel('k');
% ylabel('p(k)');
% xlim([0 E]);
% ylim([0.5e-4 1.1]);
% hold
% % plot([kmin:E],PDF1,'-r');
% % errorbar(M2(:,1),M2(:,2),M2(:,3),'og');
% % %plot(M2(:,1),M2(:,2),'og');
% % plot([kmin:E],PDF2(:),'-g');
% % % errorbar(M3(:,1),M3(:,2)/M3(1,2),M3(:,3)/M3(1,2),'*b');
% % % plot(kvector(:),DD3(:),'-b');
% % % errorbar(M4(:,1),M4(:,2)/M4(1,2),M4(:,3)/M4(1,2),'sc');
% % % plot(kvector(:),DD4(:),'-c');
% % errorbar(M5(:,1),M5(:,2),M5(:,3),'*b');
% % %plot(M5(:,1),M5(:,2),'*b');
% plot([kmin:E],PDF5(:),'-b');
% % errorbar(M6(:,1),M6(:,2),M6(:,2),'sm');
% % %plot(M6(:,1),M6(:,2),'sm');
% % plot([kmin:E],PDF6(:),'-m');
% % %legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
% % legend(label1,'',label2,'',label5,'',label6,'')
% hold off
% 
% figure(3);
% plot(M1(:,1),V1(:,1),'+r');
% title(titlename); % Make text color red
% xlabel('k');
% ylabel('n_{data}(k)/n_{exact}(k)');
% xlim([0 E]);
% ylim([0.6 1.2]);
% hold
% plot(M2(:,1),V2(:,1),'og');
% % semilogx(M3(:,1),V3(:,1),'*b');
% % semilogx(M4(:,1),V4(:,1),'sc');
% plot(M5(:,1),V5(:,1),'*b');
% plot(M6(:,1),V6(:,1),'sm');
% %legend(label1,label2,label3,label4,label5,label6)
% legend(label1,label2,label5,label6)
% hold off

figure(4);
errorbar(M1(:,1),V1(:,1),V1(:,2),'xr');
title(titlename); % Make text color red
xlabel('k');
ylabel('n_{data}(k)/n_{exact}(k) + \Delta');
xlim([0 E]);
ylim([0 5]);
hold
plot([kmin E],[1.0 1.0],'-k');
errorbar(M2(:,1),V2(:,1)+1,V2(:,2),'og');
plot([kmin E],[2.0 2.0],'-k');
% semilogx(M3(:,1),V3(:,1),'*b');
% semilogx(M4(:,1),V4(:,1),'sc');
errorbar(M5(:,1),V5(:,1)+2,V5(:,2),'*b');
plot([kmin E],[3.0 3.0],'-k');
errorbar(M6(:,1),V6(:,1)+3,V6(:,2),'sm');
plot([kmin E],[4.0 4.0],'-k');
%legend(label1,label2,label3,label4,label5,label6)
legend(label1,label2,label5,label6)
hold off

