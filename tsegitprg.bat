@echo off
@echo ----------------------------------------------------
@echo GIT BACKUP routine
@echo -----------------------------------
@echo
@echo off
if defined PRGDIR goto :runbatch
@echo Defining standard directory locations
call c:\bin\setenv
:runbatch
@echo adding all programme files to git
@echo adding bat
call git add *.bat
@echo adding java
call git add *.java
@echo adding r
call git add *.r
@echo adding c
call git add *.c
@echo adding cpp
call git add *.cpp
@echo adding h
call git add *.h
@echo adding gpl
call git add *.gpl
@echo adding m
call git add *.m
@echo adding pas
call git add *.pas
@echo adding py
call git add *.py

@echo commit %1_%COMPUTERNAME%_%TODAY%
call git commit -a -m %1_%COMPUTERNAME%_%TODAY%
goto :endbatch

:nocomment
@echo no comment given

:endbatch
