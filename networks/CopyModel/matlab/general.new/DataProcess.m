function [nactive,M,V] = DataProcess (filenameroot,kav,E,pr,knormval,logbinfile)
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
nnorm = M(knormval,2); % Use n(knormval) normalisation
DD = DegreeDistribution(M(:,1),kav,E,pr,knormval);
nactive =0;
for iii=1:rows
    Vnorm = (nnorm*DD(iii));
    V(iii,1) = M(iii,2)/Vnorm; % normalised data
            V(iii,2) = M(iii,3)/Vnorm; % error
    nactive = nactive + M(iii,2)*M(iii,4);
end;
nactive;