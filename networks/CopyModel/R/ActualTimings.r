# finds the actual values used in the CopyModle programme,
# Used for file names
ActualTimings <- function(numberRewiringsTotal , numberRewiringsPerUpdate, numberRewiringsPerEvent)
{
numEventsPerUpdateNew = floor(numberRewiringsPerUpdate/numberRewiringsPerEvent)
numRewiringsPerUpdateNew = numEventsPerUpdateNew *numberRewiringsPerEvent;
numUpdatesNew = floor(numberRewiringsTotal/numRewiringsPerUpdateNew)
numRewiringsTotalNew = numUpdatesNew *numRewiringsPerUpdateNew;
numEventsTotalNew = numEventsPerUpdateNew * numUpdatesNew;
list(numberEventsPerUpdate = numEventsPerUpdateNew, numberRewiringsPerUpdate = numRewiringsPerUpdateNew, numberUpdates = numUpdatesNew, numberRewiringsTotal = numRewiringsTotalNew, numberEventsTotal= numEventsTotalNew)
}
