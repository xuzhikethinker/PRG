@echo off
@echo Pack up timgraph java code and other associated code
@echo off
if defined PRGDIR goto :runbatch
@echo Defining standard directory locations
call c:\bin\setenv
:runbatch

call packjavacode %PRGDIR%\networks timgraph
