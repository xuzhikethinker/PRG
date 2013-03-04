@echo off
if defined PRGDIR goto :runtg
call c:\bin\setenv
:runtg
@echo --- Running ImperialMedics.ProcessMultiplePublicationLists from runimProcessMultiplePub.bat
java -Xmx1400m  -classpath %PRGDIR%\JAVA\ImperialMedics\dist\ImperialMedics.jar;.  imperialmedics.ProcessMultiplePublicationLists %1 %2 %3 %4 %5 %6 %7 %8 %9
