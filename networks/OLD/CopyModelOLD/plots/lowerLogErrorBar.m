function [L] = lowerLogErrorBar (value,error)
% Calculates lower error bar for log plot given value and ewrror (not
% logged
minBar=1e-99;
L=1-error/value;
if (L<minBar) L=minBar; end; % deal with log(-ve or 0) problem
L=log10(L);    