function presult = DegreePDFOneValue (k,kav,E,pr)
%   Degree Probability Distribution for the Copy Model.
% Normalised properly.
pp = 1-pr;
epsilon=E*pr/pp;
ktilde = kav*pr/pp;
lnnorm = gammaln(epsilon)+gammaln(E+1)-gammaln(epsilon+E)-gammaln(epsilon-ktilde)-gammaln(ktilde);
lnp = gammaln(k+ktilde)+gammaln( E+epsilon-ktilde-k) - gammaln(k+1) - gammaln( E+1-k) + lnnorm ;   
presult =exp(lnp);