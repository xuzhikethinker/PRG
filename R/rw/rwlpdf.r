# gives the natural logarithm of the rw model pdf
# k degree, E edges, K average degree, pr random attachment prob
rwlpdf <- function(k,E,K,pr) {
 pp =1-pr;
 Ktilde = pr*K/pp;
 Ebar = E/pp;
 Etilde = pr*Ebar;
 norm = lgamma(Etilde) + lgamma(E+1) - lgamma(Etilde-Ktilde) - lgamma(Ktilde) - lgamma(Ebar);
 pdf = norm + lgamma(k+Ktilde) + lgamma(Ebar -Ktilde-k) - lgamma(k+1) - lgamma(E+1-k);
} # end of rwlpdf
