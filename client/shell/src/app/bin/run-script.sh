#!/bin/bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

APP_HOME=`cd $bin/..; pwd; cd $bin`

LIB="$APP_HOME/lib" ;
CLASSPATH="$JAVA_HOME/lib/tools.jar"
CLASSPATH="${CLASSPATH}:$APP_HOME/lib/*"
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  WEBAPPS=`cygpath --path --windows "$WEBAPPS"`
fi

###########################Start Profiling Config##########################################
JPDA_TRANSPORT=dt_socket
JPDA_ADDRESS=8000

REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

#for linux
LD_LIBRARY_PATH="/opt/Yourkit/bin/linux-x86-64/"
#for MAC
#DYLD_LIBRARY_PATH="/Users/tuannguyen/java/YourKit/bin/mac/"
#for Window

PATH="$PATH:$LD_LIBRARY_PATH"
export LD_LIBRARY_PATH DYLD_LIBRARY_PATH

#YOURKIT_PROFILE_OPTION="$REMOTE_DEBUG -agentlib:yjpagent  -Djava.awt.headless=false"
###########################Eng Profiling Config#############################################

JAVACMD="$JAVA_HOME/bin/java"

function runScript() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx256m"
  CLASS="org.saarus.client.RESTClient"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $CLASS $@
}

runScript $@
