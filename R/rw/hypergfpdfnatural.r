# gives the natural logarithm of the general hypergeometric pdf
# in terms of the usual parameters
#In usual rw model a=Ktilde>0, b=-E<0 and c=1+Ktilde-Ebar<0
hypergfpdfnatural <- function(k,E,K,pr) {
 pp=1-pr
 a=pr*e/pp
 b=-E
 c=1+a-E/pp
 hypergfpdf (k,a,b,c) 
} # end of rwlpdf
