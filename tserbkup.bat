@echo off
@echo Tim E.s RECENT FILES BACKUP routine
@echo -----------------------------------
@echo Uses %CMDZIP% to create a compressed copy of
@echo some recent files on the harddrive and on the network.
@echo It will only update the files that have been altered since
@echo a time specified as the argument when calling this command:-
@echo    tserbkup yymmdd
@echo where yymmdd is year (gt 1980, lt 2080), month, day.
@echo The files are called DOCyymmdd.ZIP for the documents
@echo and PRGblahblahblahyymmdd.zip for programmes.
@echo Alter the lists to your requirements.

@Echo Defining standard directory locations
call setenv

rem @echo set up p: drive to point to plato
rem net use p: \\155.198.210.128\time /user:ic\time

rem echo Copying Eudora address book files
rem copy %DATADIR%\Eudora\nndbase.txt %BKUPDIR%\nndbase%1.txt
rem copy %DATADIR%\Eudora\nndbase.txt %BKUPDIR2%\nndbase%1.txt


rem echo Copying PC Bookmarks file to p:\public_html
rem %CMDZIP% -u -r -p -tf%1 %APPDATA%\Mozilla\Firefox\Profiles\bookmarks*.html  %BKUPDIR%\bookmark%1.zip
rem %CMDUNZIP% -n %BKUPDIR%\bookmark%1.zip %BKUPDIR%\
rem %CMDUNZIP% -n %BKUPDIR%\bookmark%1.zip %HOMEPAGE%\


echo Date format is [yy]yymmdd or [yy]yy-[m]m-[d]d

rem echo ziping %DOCDIR%\ recent >%1 files
rem %CMDZIP% -u -r -p -tf%1 %BKUPDIR%\doc%1.zip %DOCDIR%\*.*  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

rem echo ziping %DOCDIR%\ recent >%1 files
rem %CMDZIP% -u -r -p -tf%1 %BKUPDIR%\prg%1.zip %PRGDIR%\*.*

echo ziping recent %DOCDIR%\*.txt files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\doctxt%1.zip  %DOCDIR%\*.txt  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\*.tex files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\texres%1.zip  %DOCDIR%\RESEARCH\*.tex  %DOCDIR%\RESEARCH\*.bib  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\texteach%1.zip  %DOCDIR%\TEACHING\*.tex  %DOCDIR%\TEACHING\*.bib  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\texadmin%1.zip  %DOCDIR%\ADMIN\*.tex  %DOCDIR%\ADMIN\*.bib  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\texcaree%1.zip  %DOCDIR%\CAREER\*.tex  %DOCDIR%\CAREER\*.bib  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\texother%1.zip  %DOCDIR%\*.tex  %DOCDIR%\*.bib  -x%DOCDIR%\RESEARCH\*.* -x%DOCDIR%\TEACHING\*.* -x%DOCDIR%\ADMIN\*.* -x%DOCDIR%\CAREER\*.*

echo ziping recent %DOCDIR% figure files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docfig%1.zip  %DOCDIR%\*.eps %DOCDIR%\*.jpg  %DOCDIR%\*.odg  %DOCDIR%\*.png  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\*.doc and other word files files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docwrd%1.zip  %DOCDIR%\*.doc %DOCDIR%\*.wrd  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\*.ppt files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docppt%1.zip  %DOCDIR%\*.ppt %DOCDIR%\*.wrd  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\*.wps,wks WORKS files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docworks%1.zip  %DOCDIR%\*.wps %DOCDIR%\*.wks %DOCDIR%\*.wdb -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\*.wri files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docwri%1.zip  %DOCDIR%\*.wri  -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\*.htm files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\dochtml%1.zip  %DOCDIR%\*.htm %DOCDIR%\*.html -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\*.xls,gpl,bat.mws,map,mw files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docprg%1.zip  %DOCDIR%\*.xls %DOCDIR%\*.bat %DOCDIR%\*.gpl -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docprg%1.zip  %DOCDIR%\*.mws %DOCDIR%\*.map %DOCDIR%\*.mw -x%DOCDIR%\temp\*.* -x%DOCDIR%\ATTACH\*.*

echo ziping recent %DOCDIR%\ATTACH\*.* files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\docattach%1.zip  %DOCDIR%\ATTACH\*.*

echo ziping recent bin\*.bat files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\bin%1.zip  c:\bin\*.bat

rem back up programmes
call tserbkupprg %1


echo ziping %DATADIR%\*.dbf and c:\uw\skw2\*.dat,skw files
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\data%1.zip %DATADIR%\*.csv %DATADIR%\*.wdb %DATADIR%\*.dbf
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\data%1.zip %DATADIR%\*.Q??
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\data%1.zip %DATADIR%\*.xls

rem echo ziping Palm Pilot data files stored in data directory
rem %CMDZIP% -u -r -p -tf%1   %BKUPDIR%\data%1.zip %DATADIR%\*.TDA %DATADIR%\*.MPA %DATADIR%\*.EXA %DATADIR%\*.DBA %DATADIR%\*.DB
rem %CMDZIP% -u -r -p -tf%1   %BKUPDIR%\data%1.zip %DATADIR%\*.dat %DATADIR%\*.pdb %DATADIR%\*.bib
rem %CMDZIP% -u -r -p -tf%1   %BKUPDIR%\databib%1.zip %DATADIR%\*.bib
rem copy /y %BKUPDIR%\databib%1.zip %PAPERS%\
rem xcopy %DATADIR%\*.bib %PAPERS%\ /s

rem echo ziping email files stored in data directory
rem %CMDZIP% -u -r -p -tf%1   %BKUPDIR%\email%1.zip %DATADIR%\*.mbx
rem %CMDZIP% -u -r -p -tf%1   %BKUPDIR%\email%1.zip %DATADIR%\nndbase*.* %DATADIR%\.txt  %DATADIR%\.pce
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\email%USERNAME%pst%1.zip %USERPROFILE%\*.pst
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\email%USERNAME%pst.zip %LOCALAPPDATA%\*.pst
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\email%USERNAME%pst%1.zip %DATADIR%\*.pst

echo ziping files stored in APPDATA directory
%CMDZIP% -u -r -p -tf%1   %BKUPDIR%\appdata%1.zip %APPDATA%\book*.*

echo ziping papers stored in papers directory
%CMDZIP% -u -r -p -tf%1  %BKUPDIR%\papers%1.zip %PAPERSDIR%\*.*

echo now copying all files to network drive
copy %BKUPDIR%\doc%1.zip %BKUPDIR2%\
copy %BKUPDIR%\prg%1.zip %BKUPDIR2%\
copy %BKUPDIR%\bat%1.zip %BKUPDIR2%\
copy %BKUPDIR%\data%1.zip %BKUPDIR2%\
