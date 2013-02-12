rm(s010,s250,s750,s950,s990,s999,SimonDataList)
s010 <- read.table("TSEnode_ak_d200_c01_id01_n.dat", header=TRUE);
s250 <- read.table("TSEnode_ak_d200_c01_id25_n.dat", header=TRUE);
s750 <- read.table("TSEnode_ak_d200_c01_id75_n.dat", header=TRUE);
s950 <- read.table("TSEnode_ak_d200_c01_id95_n.dat", header=TRUE);
s990 <- read.table("TSEnode_ak_d200_c01_id99_n.dat", header=TRUE);
s999 <- read.table("TSEnode_ak_d200_c01_id999_n.dat", header=TRUE);
SimonDataList <- list(s010 ,s250, s750, s950, s990 , s999) 
