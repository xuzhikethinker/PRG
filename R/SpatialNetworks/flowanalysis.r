# Flow Analysis
# Reads basic data needed for work

#Columns are
#OriginID	DestID	Trips
flowFileName="FlowTable.txt"
flowdf <- read.table(flowFileName, header=TRUE, sep="", fill=TRUE);

summary(flowdf$Trips)
# cummulative distribution function
ecdf(flowdf$Trips)
