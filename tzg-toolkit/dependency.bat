@echo off
	%~d0
	cd %~dp0
call maven.bat dependency
pause
 