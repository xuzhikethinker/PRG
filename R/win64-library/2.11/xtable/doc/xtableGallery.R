###################################################
### chunk number 1:  eval=FALSE
###################################################
## makeme <- function() {
## 	# I am a convenience function for debugging and can be ignored
## 	setwd("C:/JonathanSwinton/PathwayModeling/src/R/SourcePackages/xtable/inst/doc")
## 	Sweave("xtableGallery.RnW",stylepath=FALSE)
## }
## makeme()


###################################################
### chunk number 2: 
###################################################
library(xtable)


###################################################
### chunk number 3: 
###################################################
data(tli)

## Demonstrate data.frame
tli.table <- xtable(tli[1:10,])
digits(tli.table)[c(2,6)] <- 0


###################################################
### chunk number 4: 
###################################################
print(tli.table,floating=FALSE)


###################################################
### chunk number 5: 
###################################################
design.matrix <- model.matrix(~ sex*grade, data=tli[1:10,])
design.table <- xtable(design.matrix)


###################################################
### chunk number 6: 
###################################################
print(design.table,floating=FALSE)


###################################################
### chunk number 7: 
###################################################
fm1 <- aov(tlimth ~ sex + ethnicty + grade + disadvg, data=tli)
fm1.table <- xtable(fm1)


###################################################
### chunk number 8: 
###################################################
print(fm1.table,floating=FALSE)


###################################################
### chunk number 9: 
###################################################
fm2 <- lm(tlimth ~ sex*ethnicty, data=tli)
fm2.table <- xtable(fm2)


###################################################
### chunk number 10: 
###################################################
print(fm2.table,floating=FALSE)


###################################################
### chunk number 11: 
###################################################
print(xtable(anova(fm2)),floating=FALSE)


###################################################
### chunk number 12: 
###################################################
fm2b <- lm(tlimth ~ ethnicty, data=tli)


###################################################
### chunk number 13: 
###################################################
print(xtable(anova(fm2b,fm2)),floating=FALSE)


###################################################
### chunk number 14: 
###################################################

## Demonstrate glm
fm3 <- glm(disadvg ~ ethnicty*grade, data=tli, family=binomial())
fm3.table <- xtable(fm3)


###################################################
### chunk number 15: 
###################################################
print(fm3.table,floating=FALSE)


###################################################
### chunk number 16: 
###################################################
print(xtable(anova(fm3)),floating=FALSE)


###################################################
### chunk number 17: 
###################################################

## Demonstrate aov
## Taken from help(aov) in R 1.1.1
## From Venables and Ripley (1997) p.210.
N <- c(0,1,0,1,1,1,0,0,0,1,1,0,1,1,0,0,1,0,1,0,1,1,0,0)
P <- c(1,1,0,0,0,1,0,1,1,1,0,0,0,1,0,1,1,0,0,1,0,1,1,0)
K <- c(1,0,0,1,0,1,1,0,0,1,0,1,0,1,1,0,0,0,1,1,1,0,1,0)
yield <- c(49.5,62.8,46.8,57.0,59.8,58.5,55.5,56.0,62.8,55.8,69.5,55.0,
           62.0,48.8,45.5,44.2,52.0,51.5,49.8,48.8,57.2,59.0,53.2,56.0)
npk <- data.frame(block=gl(6,4), N=factor(N), P=factor(P), K=factor(K), yield=yield)
npk.aov <- aov(yield ~ block + N*P*K, npk)
op <- options(contrasts=c("contr.helmert", "contr.treatment"))
npk.aovE <- aov(yield ~  N*P*K + Error(block), npk)
options(op)
#summary(npk.aov)


###################################################
### chunk number 18: 
###################################################
print(xtable(npk.aov),floating=FALSE)


###################################################
### chunk number 19: 
###################################################
print(xtable(anova(npk.aov)),floating=FALSE)


###################################################
### chunk number 20: 
###################################################
print(xtable(summary(npk.aov)),floating=FALSE)


###################################################
### chunk number 21: 
###################################################
#summary(npk.aovE)


###################################################
### chunk number 22: 
###################################################
print(xtable(npk.aovE),floating=FALSE)


###################################################
### chunk number 23: 
###################################################
print(xtable(summary(npk.aovE)),floating=FALSE)


###################################################
### chunk number 24: 
###################################################

## Demonstrate lm
## Taken from help(lm) in R 1.1.1
## Annette Dobson (1990) "An Introduction to Generalized Linear Models".
## Page 9: Plant Weight Data.
ctl <- c(4.17,5.58,5.18,6.11,4.50,4.61,5.17,4.53,5.33,5.14)
trt <- c(4.81,4.17,4.41,3.59,5.87,3.83,6.03,4.89,4.32,4.69)
group <- gl(2,10,20, labels=c("Ctl","Trt"))
weight <- c(ctl, trt)
lm.D9 <- lm(weight ~ group)


###################################################
### chunk number 25: 
###################################################
print(xtable(lm.D9),floating=FALSE)


###################################################
### chunk number 26: 
###################################################
print(xtable(anova(lm.D9)),floating=FALSE)


###################################################
### chunk number 27: 
###################################################

## Demonstrate glm
## Taken from help(glm) in R 1.1.1
## Annette Dobson (1990) "An Introduction to Generalized Linear Models".
## Page 93: Randomized Controlled Trial :
counts <- c(18,17,15,20,10,20,25,13,12)
outcome <- gl(3,1,9)
treatment <- gl(3,3)
d.AD <- data.frame(treatment, outcome, counts)
glm.D93 <- glm(counts ~ outcome + treatment, family=poisson())


###################################################
### chunk number 28: 
###################################################
print(xtable(glm.D93,align="r|llrc"),floating=FALSE)


###################################################
### chunk number 29: prcomp
###################################################
if(require(stats,quietly=TRUE)) {
  ## Demonstrate prcomp
  ## Taken from help(prcomp) in mva package of R 1.1.1
  data(USArrests)
  pr1 <- prcomp(USArrests)
}


###################################################
### chunk number 30: 
###################################################
if(require(stats,quietly=TRUE)) {
  print(xtable(pr1),floating=FALSE)
}


###################################################
### chunk number 31: 
###################################################
  print(xtable(summary(pr1)),floating=FALSE)


###################################################
### chunk number 32: 
###################################################
#  ## Demonstrate princomp
#  ## Taken from help(princomp) in mva package of R 1.1.1
#  pr2 <- princomp(USArrests)
#  print(xtable(pr2))


###################################################
### chunk number 33: 
###################################################
temp.ts <- ts(cumsum(1+round(rnorm(100), 0)), start = c(1954, 7), frequency=12)
   temp.table <- xtable(temp.ts,digits=0)
    caption(temp.table) <- "Time series example"


###################################################
### chunk number 34: 
###################################################
    print(temp.table,floating=FALSE)


###################################################
### chunk number 35: savetofile
###################################################
if (FALSE) {
  for(i in c("latex","html")) {
    outFileName <- paste("xtable.",ifelse(i=="latex","tex",i),sep="")
    print(xtable(lm.D9),type=i,file=outFileName,append=TRUE,latex.environment=NULL)
    print(xtable(lm.D9),type=i,file=outFileName,append=TRUE,latex.environment="")
    print(xtable(lm.D9),type=i,file=outFileName,append=TRUE,latex.environment="center")
    print(xtable(anova(glm.D93,test="Chisq")),type=i,file=outFileName,append=TRUE)
    print(xtable(anova(glm.D93)),hline.after=c(1),size="small",type=i,file=outFileName,append=TRUE)
      # print(xtable(pr2),type=i,file=outFileName,append=TRUE)
         }
} 


###################################################
### chunk number 36: 
###################################################
insane <- data.frame(Name=c("Ampersand","Greater than","Less than","Underscore","Per cent","Dollar","Backslash","Hash", "Caret", "Tilde","Left brace","Right brace"),
				Character = I(c("&",">",		"<",		"_",		"%",		"$",		"\\", "#",	"^",		"~","{","}")))
colnames(insane)[2] <- paste(insane[,2],collapse="")


###################################################
### chunk number 37: pxti
###################################################
print( xtable(insane))


###################################################
### chunk number 38: 
###################################################
wanttex <- xtable(data.frame( label=paste("Value_is $10^{-",1:3,"}$",sep="")))


###################################################
### chunk number 39: 
###################################################
print(wanttex,sanitize.text.function=function(str)gsub("_","\\_",str,fixed=TRUE))


###################################################
### chunk number 40: 
###################################################
mat <- round(matrix(c(0.9, 0.89, 200, 0.045, 2.0), c(1, 5)), 4)
rownames(mat) <- "$y_{t-1}$"
colnames(mat) <- c("$R^2$", "$\\bar{R}^2$", "F-stat", "S.E.E", "DW")
mat <- xtable(mat)


###################################################
### chunk number 41: 
###################################################
print(mat, sanitize.text.function = function(x){x})


###################################################
### chunk number 42: 
###################################################
money <- matrix(c("$1,000","$900","$100"),ncol=3,dimnames=list("$\\alpha$",c("Income (US$)","Expenses (US$)","Profit (US$)")))


###################################################
### chunk number 43: 
###################################################
print(xtable(money),sanitize.rownames.function=function(x) {x})


###################################################
### chunk number 44: 
###################################################
   print(xtable(lm.D9,caption="\\tt latex.environment=NULL"),latex.environment=NULL)
    print(xtable(lm.D9,caption="\\tt latex.environment=\"\""),latex.environment="")
    print(xtable(lm.D9,caption="\\tt latex.environment=\"center\""),latex.environment="center")


###################################################
### chunk number 45: 
###################################################
tli.table <- xtable(tli[1:10,])


###################################################
### chunk number 46: 
###################################################
align(tli.table) <- rep("r",6)


###################################################
### chunk number 47: 
###################################################
print(tli.table,floating=FALSE)


###################################################
### chunk number 48: 
###################################################
align(tli.table) <- "|rrl|l|lr|"


###################################################
### chunk number 49: 
###################################################
print(tli.table,floating=FALSE)


###################################################
### chunk number 50: 
###################################################
align(tli.table) <- "|rr|lp{3cm}l|r|"


###################################################
### chunk number 51: 
###################################################
print(tli.table,floating=FALSE)


###################################################
### chunk number 52: 
###################################################
digits(tli.table) <- 3


###################################################
### chunk number 53: 
###################################################
print(tli.table,floating=FALSE,)


###################################################
### chunk number 54: 
###################################################
digits(tli.table) <- 1:(ncol(tli)+1)


###################################################
### chunk number 55: 
###################################################
print(tli.table,floating=FALSE,)


###################################################
### chunk number 56: 
###################################################
digits(tli.table) <- matrix( 0:4, nrow = 10, ncol = ncol(tli)+1 )


###################################################
### chunk number 57: 
###################################################
print(tli.table,floating=FALSE,)


###################################################
### chunk number 58: 
###################################################
print((tli.table),include.rownames=FALSE,floating=FALSE)


###################################################
### chunk number 59: 
###################################################
align(tli.table) <- "|r|r|lp{3cm}l|r|"


###################################################
### chunk number 60: 
###################################################
print((tli.table),include.rownames=FALSE,floating=FALSE)


###################################################
### chunk number 61: 
###################################################
align(tli.table) <- "|rr|lp{3cm}l|r|"


###################################################
### chunk number 62: 
###################################################
print((tli.table),include.colnames=FALSE,floating=FALSE)


###################################################
### chunk number 63: 
###################################################
print(tli.table,include.colnames=FALSE,floating=FALSE,hline.after=c(0,nrow(tli.table)))


###################################################
### chunk number 64: 
###################################################
print((tli.table),include.colnames=FALSE,include.rownames=FALSE,floating=FALSE)


###################################################
### chunk number 65: 
###################################################
print(xtable(anova(glm.D93)),hline.after=c(1),floating=FALSE)


###################################################
### chunk number 66: 
###################################################
print(xtable(anova(glm.D93)),size="small",floating=FALSE)


###################################################
### chunk number 67: longtable
###################################################

## Demonstration of longtable support.
x <- matrix(rnorm(1000), ncol = 10)
x.big <- xtable(x,label='tabbig',
	caption='Example of longtable spanning several pages')


###################################################
### chunk number 68: 
###################################################
print(x.big,tabular.environment='longtable',floating=FALSE)


###################################################
### chunk number 69: 
###################################################
x <- x[1:30,]
x.small <- xtable(x,label='tabsmall',caption='A sideways table')


###################################################
### chunk number 70: 
###################################################
print(x.small,floating.environment='sidewaystable')


###################################################
### chunk number 71: 
###################################################
toLatex(sessionInfo())


