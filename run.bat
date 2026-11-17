@echo off
setlocal
if not exist bin mkdir bin
dir /s /b src\*.java > sources_tmp.txt
javac -encoding UTF-8 -cp "lib/*" -d bin @sources_tmp.txt
set ERR=%ERRORLEVEL%
del sources_tmp.txt
if %ERR% neq 0 exit /b %ERR%
start "" javaw -cp "bin;resources;lib/*" klinik.Main
