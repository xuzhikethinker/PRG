% Plot out PLY Copy Model Results

kav=1;
kmin=1;
E=1e5;
kmax=E;
kvector=1:kmax;
N=E/kav;

%titlename='Plot of Analytical Results';
titlename='';

% *** **********************************************
nnn=1;

pr=0;
label1=['p_r=',num2str(pr)];

M1 = binopdf (kvector,E,1/N);

% *** **********************************************
nnn=2;
pr=10/E;

label2=['p_r=',num2str(pr)];
M2 = DegreePDF (kvector,kav,E,pr);

% *** **********************************************
nnn=3;

pr= 1/E;

label3=['p_r=',num2str(pr)];
M3 = DegreePDF (kvector,kav,E,pr)


% *** **********************************************
nnn=4;
pr=0.1/E;

label4=['p_r=',num2str(pr)];
M4 = DegreePDF (kvector,kav,E,pr)



figure(1);
loglog(kvector(:),M1(:),'+r');
title(titlename); % Make text color red
xlabel('k');
ylabel('p(k)');
xlim([1 E]);
ylim([1e-3/E 1.0]);
hold
loglog(kvector(:),M2(:),'og');
loglog(kvector(:),M3(:),'*b');
loglog(kvector(:),M4(:),'sm');
%loglog(kvector(:),1/E,'--k');
%legend(label1,'',label2,'',label3,'',label4,'',label5,'',label6,'')
legend(label1,label2,label3,label4);
hold off



