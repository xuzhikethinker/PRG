% Plot out PLY results
%#rem pr           pp
%#rem 0.002991027 0.997008973
%#rem 0.00990099  0.99009901
%#rem 0.047619048 0.952380952
%#rem 0.090909091 0.909090909
%#rem 0.5         0.5


N=1e5;
E=1e5;
pr(1)=0.5;
NActive =[];
titlename='PLY m0 N1e5';
filenamemasterroot='PLYm0et5000000ni100000na100000';
filenameroot=[filenamemasterroot,'pp0.500pr0.500'];
[NA,M1,V1,DD1] = DataProcess (filenameroot,N,E,pr(1));
NActive=[NActive,NA];
pr(2)=0.091;
filenameroot=[filenamemasterroot,'pp0.909pr0.091'];
[NA,M2,V2,DD2] = DataProcess (filenameroot,N,E,pr(2));
NActive=[NActive,NA];
pr(3)=0.047;
filenameroot=[filenamemasterroot,'pp0.953pr0.047'];
[NA,M3,V3,DD3] = DataProcess (filenameroot,N,E,pr(3));
NActive=[NActive,NA];
pr(4)=0.01;
filenameroot=[filenamemasterroot,'pp0.990pr0.010'];
[NA,M4,V4,DD4] = DataProcess (filenameroot,N,E,pr(4));
NActive=[NActive,NA];
pr(5)=0.003;
filenameroot=[filenamemasterroot,'pp0.997pr0.003'];
[NA,M5,V5,DD5] = DataProcess (filenameroot,N,E,pr(5));
NActive=[NActive,NA];

loglog(M1(:,1),M1(:,2)/M1(1,2),'+r',M1(:,1),DD1(:),'-r');
%title('New Title','Color','r') % Make text color red
xlabel('k')
hold
loglog(M2(:,1),M2(:,2)/M2(1,2),'og',M2(:,1),DD2(:),'-g');
loglog(M3(:,1),M3(:,2)/M3(1,2),'*b',M3(:,1),DD3(:),'-b');
loglog(M4(:,1),M4(:,2)/M4(1,2),'pc',M4(:,1),DD4(:),'-c');
loglog(M5(:,1),M5(:,2)/M5(1,2),'hm',M5(:,1),DD5(:),'-m');
hold off

%errorbar(M(:,1),V(:),Verr(:));
%hold all
%loglog(M(:,1),dd(:));
%hold all
%errorbar(M(:,1),V(:),Verr(:));
%loglog(M(:,1),M(:,2)/M(1,2));
%hold off
