function p = DegreePDF (kvector,kav,E,pr)
%   Degree Probability Distribution for the Copy Model.
% Normalised properly.
pp = 1-pr;
epsilon=E*pr/pp;
ktilde = kav*pr/pp;
lnnorm = gammaln(epsilon)+gammaln(E+1)-gammaln(epsilon+E)-gammaln(epsilon-ktilde)-gammaln(ktilde);
p = [1:length(kvector)];
for iii = 1:length(kvector)
    k=kvector(iii);
    lnp = gammaln(k+ktilde)+gammaln( E+epsilon-ktilde-k) - gammaln(k+1) - gammaln( E+1-k) + lnnorm ;   
    p(iii) =exp(lnp);
end;