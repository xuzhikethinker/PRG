# gives the natural logarithm of the Moran model pdf
# a, b and c are usual hypergeometric coeff with b negative integer
rwlpdfabc <- function(k,a,b,c) {
 norm = lgamma(1-b) + lgamma(1-c+a+b) - lgamma(a) - lgamma(1-c+a) - lgamma(1+b-c);
 pdf = norm + lgamma(k+a) + lgamma(1-c-k) - lgamma(k+1) - lgamma(1-b-k);
} # end of rwlpdf
