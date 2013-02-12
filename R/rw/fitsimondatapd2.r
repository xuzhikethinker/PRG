# Fit power and exponential decay to data 
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
#rm(hypergfpdf,fit250,fit250,fit750,fit950,fit990,fit999)
source("pdfit2.r")
rm(pdfit010,pdfit250,pdfit750,pdfit950,pdfit990,pdfit999)

print("Fitting pd to s950")
pdfit950 <- pdfit2(s950, 0.095,0)


print("Fitting pd to s250")
pdfit250 <- pdfit2(s250, 0.8, 0)

print("Fitting pd to s750")
pdfit750 <- pdfit2(s750, 0.362, 0)


#print("Fitting pd to s990")
#pdfit990 <- pdfit2(s990, 0.028,1)

#print("Fitting pd to s999")
#pdfit999 <- pdfit2(s999, 0.012,0)

#print("Fitting pd to s010")
#pdfit010 <- pdfit2(s010, 0.9,0)


#plot(s250$k,log(s250$pk), type="p", log="x", col=colourlist[1], xlim=c(1,200), ylim=c(-10,0), xlab="degree k", ylab="ln(p(k))", main="Laird and Jensen EPL 2006 Fig 1 ");
plot(s999$k,log(s999$pk), type="p", log="x", col=colourlist[1], xlim=c(1,200), ylim=c(-10,0), xlab="degree k", ylab="ln(p(k))", main="Laird and Jensen EPL 2006 Fig 1 ");

points(s750$k,log(s750$pk),  col=colourlist[2] , pch=2)

points(s950$k,log(s950$pk),  col=colourlist[3] , pch=3)

points(s990$k,log(s990$pk),  col=colourlist[4] , pch=4)

points(s250$k,log(s250$pk),  col=colourlist[5] , pch=5)

lines(s250$k[2:length(s250)],fitted(pdfit250), col=colourlist[1]);
#lines(s750$k,fitted(pdfit750), col=colourlist[2])
#lines(s950$k,fitted(pdfit950), col=colourlist[3])
#lines(s990$k,fitted(pdfit990), col=colourlist[4])
#lines(s999$k,fitted(pdfit999), col=colourlist[5])

