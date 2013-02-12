# Do surface plots

vmax=2.0
vstep=0.4
avalue=1.0
bvalue=0.5

ptvec<- seq(from=-vmax,to=vmax,by=vstep)
ptvec2<- ptvec*ptvec
onevec<- array(1,dim=c(length(ptvec) ) )
rxmat<- onevec %o% ptvec2
rymat<- ptvec2 %o% onevec
rmat <- rxmat+rymat
zmat <- avalue*rmat+bvalue*rmat*rmat

#Good starting points for interpolation are the "sequential" and "diverging" ColorBrewer palettes in the RColorBrewer package
#color.palette = colorRampPalette(c("red", "white", "blue"),key.axis=NULL)

filled.contour(x = ptvec, y = ptvec, zmat, nlevels=10, axes=FALSE, key.axes=FALSE, key.title=FALSE, asp=1.0)

windows()
contour(x = ptvec, y = ptvec, zmat, nlevels=10, plot.axes=NULL)
