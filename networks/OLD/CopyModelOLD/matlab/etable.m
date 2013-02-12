function [eigentable] = etable (MaxE,N,pr)
clear eigentable
for DummyVariable = 1:MaxE;
[l,m]=evalueList(DummyVariable, N, pr);
lambda2=l(DummyVariable);
eigentable(DummyVariable,:) = lambda2;
end
