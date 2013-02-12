# Estimate a power law fit using two data points
# n1 n2 are the entries in data.frame df to be used
# nx and ny are the columns storing x and y coefficents
# fits y=Ax^(-gamma) and returns list 
powerestimate <- function(n1,n2, nx, ny, df) {
x1<- df[n1,nx]
y1<- df[n1,ny]
x2<- df[n2,nx]
y2<- df[n2,ny]
powerest <- -log(y2/y1)/log(x2/x1)
normest <- (log(x1)*log(y2)-log(x2)*log(y1) )/ (log(x1)-log(x2))
list(gamma=powerest, A=exp(normest))
}
#nls( n ~ A*c^(-gamma) * exp(-c/c0), m90df, 