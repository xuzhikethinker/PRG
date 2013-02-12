@echo off
@echo ----------------------------------------------------
@echo Tim E.s RECENT FILES BACKUP routine
@echo -----------------------------------
@echo Uses %CMDZIP% to create a compressed copy of
@echo some recent files on the harddrive and on the network.
@echo It will only update the files that have been altered since
@echo a time specified as the argument when calling this command:-
@echo    tserbkupprg yymmdd
@echo where yymmdd is year (gt 1980, lt 2080), month, day.
@echo The files are called PRGblahblahblahDOCyymmdd.ZIP
@echo Alter the lists to your requirements.
@echo
@echo off
if defined PRGDIR goto :runbatch
@echo Defining standard directory locations
call c:\bin\setenv
:runbatch

echo ziping bin\*.bat files
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\bin%1.zip  c:\bin\*.bat

echo ziping programme files in %PRGDIR%
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSourceJava%1.zip  %PRGDIR%\*.java
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSourceR%1.zip  %PRGDIR%\*.r
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSourceCppC%1.zip  %PRGDIR%\*.c %PRGDIR%\*.cpp %PRGDIR%\*.h
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSourceOther%1.zip  %PRGDIR%\*.edt %PRGDIR%\*.gpl %PRGDIR%\*.bat
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prghtml%1.zip  %PRGDIR%\*.html
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSourceMaple%1.zip  %PRGDIR%\*.mws %PRGDIR%\*.map  %PRGDIR%\*.mw
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSourceMatlab%1.zip  %PRGDIR%\*.m
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSourcePas%1.zip  %PRGDIR%\*.pas

echo ziping programme files in %PRGDIR%
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgJAVALIBRARY%1.zip  %JAVALIB%\*.* -x%DOCDIR%\ATTACH\*%1.zip

echo ziping programme data files in %PRGDIR%
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgtxt%1.zip  %PRGDIR%\*.txt
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgdat%1.zip  %PRGDIR%\*.dat
%CMDZIP% -u -r -p -tf%1      %BKUPDIR%\prgSpreadsheet%1.zip  %PRGDIR%\*.xls %PRGDIR%\*.xlsx %PRGDIR%\*.ods
