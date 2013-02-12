function LogDPDF = LogDDnonorm (k,E,pr,kav)
%   Degree Probability Distribution Function for the Copy Model incl. normalisation.
pp = 1-pr;
epsilon=E*pr/pp;
ktilde = kav*pr/pp;
lnnnonorm = gammaln(k+ktilde)+gammaln( E/pp-ktilde-k) - gammaln(k+1) - gammaln( E+1-k);
LogDPDF = gammaln(epsilon)+gammaln(E+1)-gammaln(epsilon+E)-gammaln(epsilon-ktilde)-gammaln(ktilde) +
lnnnonorm;
