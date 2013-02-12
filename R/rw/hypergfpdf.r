# gives the natural logarithm of the general hypergeometric pdf
#  Requires b to be a negative integer
# In usual rw model a=Ktilde>0, b=-E<0 and c=1+Ktilde-Ebar<0
hypergfpdf <- function(k,a,b,c) {
 norm = lgamma(1-c+a+b) + lgamma(1-b) - lgamma(a) - lgamma(1+a-c) - lgamma(1+b-c);
 pdf = norm + lgamma(k+a) + lgamma(1-c-k) - lgamma(k+1) - lgamma(1-b-k);
} # end of rwlpdf
