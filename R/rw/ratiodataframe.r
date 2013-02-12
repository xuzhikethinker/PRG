# takes a data frame with k (assumed to be consecutive) and pk
# columns and returns a dataframe with k and p(k+1)/p(k) columns
 ratiodataframe <- function( dataf) {
 iii=0;
 vvv = dataf$pk[iii+1]/dataf$pk[iii];
 for (iii in 1:(length(dataf$k)-1))
 { vvv=c(vvv,dataf$pk[iii+1]/dataf$pk[iii]); }
 datafr <- data.frame(k=0:(length(vvv)-1),pkratio=vvv);
 }
