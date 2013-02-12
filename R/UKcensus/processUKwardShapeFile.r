# Processes the shape file to produce approximate x y coordinates for UK wards

#shapeFileName<-"shpexport.txt"
shapeFileName<-"shpexportsemicolonsep.txt"
print(paste("--- Shape File:",shapeFileName),quote=FALSE);

warddf <- read.table(shapeFileName, header=TRUE, sep=";", fill=TRUE);



