function n = DegreeDistribution (kvector,kav,E,pr,knormval)
%   Degree Distrubution for the Copy Model.
% Normalise using k=knormval value
pp = 1-pr;
Eopp=E/pp;
ktilde = kav*pr/pp;
kval=knormval;
lnnorm =  LogDDnonorm (kval,E,pp,ktilde);
%norm=exp(lnnorm) % Normalise to n(1)
n = [1:length(kvector)];
Ntot=0;
for iii = 1:length(kvector)
    k=kvector(iii);
    lnn = LogDDnonorm (k,E,pp,ktilde) - lnnorm ;   
    n(iii) =exp(lnn);
end;