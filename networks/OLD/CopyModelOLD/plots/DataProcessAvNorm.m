function [MM,V,DD] = DataProcessAvNorm (filenameroot,kav,E,pr,kmin,logbinfile)
% Produces distributions normalised to k = kmin .. E range
%cd 'c:\PRG\networks\CopyModel\output\';
% Change row/column in dlmread
% logbinfile=1 for log bin file, =0 else.
if (logbinfile==1) 
    extstring='rlb100.totlb.Jdd.dat'; % use 3 0 r c
    firstrow=3;
    firstcolumn=0;
else
    extstring='r.tot.Jdd.dat'; % use 2 0 r c
    firstrow=2;
    firstcolumn=0;
end;
filelocation = ['summaries\',filenameroot,'\',filenameroot,extstring];
s=dir(filelocation)
M = dlmread(filelocation, '\t', firstrow, firstcolumn);
[rows, columns] = size(M);
% Now find the normalisation factor from the data
for firstEntry=1:rows
    if (M(firstEntry,1)>=kmin) break; end;
end;
Mnorm = sum(M(firstEntry:rows,2));
DD = DegreeDistribution(M(firstEntry:rows,1),kav,E,pr,kmin);
DDnorm = sum(DD(:));
DD = DD/DDnorm;
for iii=firstEntry:rows
    jjj=iii-firstEntry+1;
    MM(jjj,1) = M(iii,1);       % k values
    MM(jjj,2) = M(iii,2)/Mnorm; % normalised data
    MM(jjj,3) = M(iii,3)/Mnorm; % error
    V(jjj,1) = MM(jjj,2)/DD(jjj); % data/exact
    V(jjj,2) = MM(jjj,3)/DD(jjj); % data/exact error
end;