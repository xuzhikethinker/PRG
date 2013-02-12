function [U] = upperLogErrorBar (value,error)
% Calculates upper error bar for log plot given value and ewrror (not
% logged
U=log10(1+error/value);
