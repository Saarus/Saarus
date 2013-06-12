@echo off

setLocal EnableDelayedExpansion

set CLASSPATH=".

for /R ./lib %%a in (*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%a
    )
set CLASSPATH=!CLASSPATH!"
echo !CLASSPATH!


java -cp %CLASSPATH% org.saarus.server.http.JettyWebServer -webapp webapps -port 7080
