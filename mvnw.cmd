@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Batch Script for Windows
@REM ----------------------------------------------------------------------------
@IF "%DEBUG%" == "" @ECHO OFF
@SETLOCAL EnableExtensions EnableDelayedExpansion

SET "DIRNAME=%~dp0"
IF "%DIRNAME%" == "" SET "DIRNAME=."
SET "BASE_DIR=%DIRNAME%"

@REM Ensure JAVA_HOME is set and free of trailing slashes
IF NOT DEFINED JAVA_HOME SET "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
IF "%JAVA_HOME:~-1%x" == "\x" SET "JAVA_HOME=%JAVA_HOME:~0,-1%"

@REM Find maven-wrapper.properties
SET "WRAPPER_PROPERTIES=%BASE_DIR%\.mvn\wrapper\maven-wrapper.properties"
IF NOT EXIST "%WRAPPER_PROPERTIES%" (
  ECHO Error: Cannot find %WRAPPER_PROPERTIES%
  EXIT /B 1
)

@REM Extract distributionUrl
FOR /F "tokens=2 delims==" %%A IN ('findstr /R "^distributionUrl=" "%WRAPPER_PROPERTIES%"') DO SET "DIST_URL=%%A"
SET "DIST_URL=%DIST_URL:\=%"

@REM Determine local user MAVEN_USER_HOME
IF "%MAVEN_USER_HOME%" == "" SET "MAVEN_USER_HOME=%USERPROFILE%\.m2"
SET "WRAPPER_DIR=%MAVEN_USER_HOME%\wrapper\dists\apache-maven-3.9.6"
SET "MAVEN_HOME=%WRAPPER_DIR%\apache-maven-3.9.6"

IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  ECHO Downloading Maven distribution from %DIST_URL%...
  IF NOT EXIST "%WRAPPER_DIR%" MKDIR "%WRAPPER_DIR%"
  powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%DIST_URL%', '%WRAPPER_DIR%\maven.zip'); Expand-Archive -Path '%WRAPPER_DIR%\maven.zip' -DestinationPath '%WRAPPER_DIR%' -Force; Remove-Item '%WRAPPER_DIR%\maven.zip' -Force"
  IF %ERRORLEVEL% NEQ 0 (
    ECHO Failed to download Maven wrapper.
    EXIT /B 1
  )
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
EXIT /B %ERRORLEVEL%
