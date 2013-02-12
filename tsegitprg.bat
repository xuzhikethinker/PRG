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
git add *.bat
git add *.java
git add *..r
git add *.c
git add *.cpp
git add *.h
git add *.gpl
git add *.bat
git add *.m
git add *.pas
git add *.py
