function lnnnonorm = LogDDnonorm (k,E,pp,ktilde)
%   Degree Distrubution for the Copy Model.
%   K= degree, Eopp = E/pp, ktilde = K*pr/pp
lnnnonorm = gammaln(k+ktilde)+gammaln( E/pp-ktilde-k) - gammaln(k+1) - gammaln( E+1-k);