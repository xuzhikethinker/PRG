(* ::Package:: *)

(* ::Input:: *)
(*LfactorLogEquation[LLL_, np_,dim_, Delta_, ccc_] :=(LLL -1 ) Log[np]-dim LogGamma[LLL] - Log[ccc]- LLL Log[Delta]*)


(* ::Input:: *)
(*LfactorLogEquation[L, np,d, Delta, ccc]*)


(* ::Input:: *)
(*LfactorLogsol [np_,dim_, Delta_, ccc_] :={np,x} /. NSolve[  LfactorLogEquation[x np^(1.0/dim), np,dim, Delta, ccc]==0 &&x>0.5 && x<5,x,Reals]*)


(* ::Input:: *)
(*(* LfactorLogsol [np_,dim_, Delta_, ccc_] :={np,x} /. NSolve[(x np^(1.0/dim) -1.0) Log[np]-dim LogGamma[x np^(1.0/dim)] ==Log[ccc]+ x np^(1.0/dim)Log[Delta]  &&x>np^(-1/dim) && x<5,x,Reals] *)*)


(* ::Input:: *)
(*(* Apply[f,{{a,b},{c,d}},{1}] *)*)


(* ::Input:: *)
(*(* res=Apply[LfactorLogsol ,{{100,2,2,1},{120,2,2,1}},{1}] *)*)


(* ::Input:: *)
(*dim=2;*)


(* ::Input:: *)
(*delta=dim;*)


(* ::Input:: *)
(*(* arglist = Table[{nnn,dim,delta,1},{nnn,16,120,1}]; arglist[[1;;3]] *)*)
(**)


(* ::Input:: *)
(*res=Apply[LfactorLogsol ,arglist,{1}]; res[[1;;3]]*)


(* ::Input:: *)
(*ListPlot[res, AxesLabel->{N,L/N^(1/dim)}, PlotMarkers->None]*)


(* ::Input:: *)
(*LfactorLogEquation[np_,dim_, x_] :=N[(x np^(1/dim) -1 ) Log[np^(1/dim)]-dim LogGamma[x np^(1/dim)]]*)


(* ::Input:: *)
(*ListPlot[Table[{L,LfactorLogEquation[L, 100, 2,2,1]},{L,1,30,0.5} ] ]*)
