[l100a,m] = evalueList(100,100,0.01);
[l100b,m] = evalueList(100,100,0.001);
[l100c,m] = evalueList(100,100,0.1);
[l100d,m] = evalueList(100,100,0.05);
xlista= [0:100]/100;
xlistb= [0:100]/100;
xlistc= [0:100]/100;
xlistd= [0:100]/100;
semilogx(xlista,1-l100a,'+r'); 
title('stuff'); % Make text color red
xlabel('n/E');
ylabel('1-\lambda');
xlim([0 1]);
%ylim([1e-2 2]);
hold
semilogx(xlistb,1-l100b,'+r'); 
semilogx(xlistc,1-l100c,'+r'); 
semilogx(xlistd,1-l100d,'+r'); 
hold off;