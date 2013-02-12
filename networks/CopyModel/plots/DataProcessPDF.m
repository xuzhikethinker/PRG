function [Pdata,V,DPDF] = DataProcessPDF (kmin,filenameroot,kav,E,pr,logbinfile)
% Full normalisation, requires k=0 and kav>0
%cd 'c:\PRG\networks\CopyModel\output\';
% Change row/column in dlmread
% logbinfile=1 for log bin file, =0 else.
% Produces normalised data Pdata for values of k in the data.  Likewise 
% also produces fractional error of data over
% exact result V.  Finally gives the exact result P for ALL k.
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
Mnorm = E/kav;
%sum(M(:,2)); % Data Normalisation only works if all k values present, k=0
%is not present
DPDF = DegreePDF(M(:,1),kav,E,pr); % (kvector,kav,E,pr)
%nactive =0;
for iii=1:rows
    pk = DegreePDFOneValue(M(iii,1),kav,E,pr);
    Pdata(iii,1) = M(iii,1); % k value
    Pdata(iii,2) = M(iii,2)/Mnorm; % normalise data
    Pdata(iii,3) = M(iii,3)/Mnorm; % normalise error
    V(iii,1)=Pdata(iii,2)/pk; % Calc fractional error of data
    V(iii,2)=Pdata(iii,3)/pk; % Calc fractional uncertainty of data
end;
%nactive;