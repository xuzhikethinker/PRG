# gives the natural logarithm of the general hypergeometric pdf
# in terms of the usual parameters
#In usual rw model a=Ktilde>0, b=-E<0 and c=1+Ktilde-Ebar<0
hypergfpdfparam <- function(E,K,pr) {
 pp=1-pr
 a=pr*K/pp
 b=-E
 c=1+a-(E/pp)
 list(a=a,b=b,c=c)
 } 
