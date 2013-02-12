# Extract @-message and RT graphs from conference tweets
library(igraph);

# Read Twapperkeeper CSV file
tweets <- read.csv("tweets.csv", head=T, sep="|", quote="", fileEncoding="UTF-8");
print(paste("Read ", length(tweets$text), " tweets.", sep=""));

# Get @-messages, senders, receivers
ats <- grep("^\\.?@[a-z0-9_]{1,15}", tolower(tweets$text), perl=T, value=T);
at.sender <- tolower(as.character(tweets$from_user[grep("^\\.?@[a-z0-9_]{1,15}", tolower(tweets$text), perl=T)]));
at.receiver <- gsub("^\\.?@([a-z0-9_]{1,15})[^a-z0-9_]+.*$", "\\1", ats, perl=T);
print(paste(length(ats), " @-messages from ", length(unique(at.sender)), " senders and ", length(unique(at.receiver)), " receivers.", sep=""));

# Get RTs, senders, receivers
rts <- grep("^rt @[a-z0-9_]{1,15}", tolower(tweets$text), perl=T, value=T);
rt.sender <- tolower(as.character(tweets$from_user[grep("^rt @[a-z0-9_]{1,15}", tolower(tweets$text), perl=T)]));
rt.receiver <- gsub("^rt @([a-z0-9_]{1,15})[^a-z0-9_]+.*$", "\\1", rts, perl=T);
print(paste(length(rts), " RTs from ", length(unique(rt.sender)), " senders and ", length(unique(rt.receiver)), " receivers.", sep=""));

# This is necessary to avoid problems with empty entries, usually caused by encoding issues in the source files
at.sender[at.sender==""] <- "<NA>";
at.receiver[at.receiver==""] <- "<NA>";
rt.sender[rt.sender==""] <- "<NA>";
rt.receiver[rt.receiver==""] <- "<NA>";

# Create a data frame from the sender-receiver information
ats.df <- data.frame(at.sender, at.receiver);
rts.df <- data.frame(rt.sender, rt.receiver);

# Transform data frame into a graph
ats.g <- graph.data.frame(ats.df, directed=T);
rts.g <- graph.data.frame(rts.df, directed=T);

# Write sender -> receiver information to a GraphML file
print("Write sender -> receiver table to GraphML file...");
write.graph(ats.g, file="ats.graphml", format="graphml");
write.graph(rts.g, file="rts.graphml", format="graphml");