[l20,m20] = evalueList(20,20,0.05);
[l50,m50] = evalueList(50,50,0.05);
[l100,m100]= evalueList(100,100,0.05);
[l150,m150] = evalueList(150,150,0.05);
plot([0:20]/20,1-l20,'+r'); 
title('stuff'); % Make text color red
xlabel('n/E');
ylabel('1-\lambda');
xlim([0 1]);
%ylim([1e-2 2]);
hold
plot([0:50]/50,1-l50,'xb');  plot([0:100]/100,1-l100,'og');  plot([0:150]/150,1-l150,'sm');  hold off;