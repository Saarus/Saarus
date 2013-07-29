#!/usr/bin/env bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

if [ -z "$JAVA_HOME" ] ; then
  echo "JAVA_HOME is not set. Please install and set the JAVA_HOME variable"
  exit 
fi

OS=`uname`

APP_HOME="."

LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"

CLASSPATH=${CLASSPATH}:$LIB/*;
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  APP_HOME=`cygpath --absolute --windows "$APP_HOME"`
fi


JAVACMD="$JAVA_HOME/bin/java"

CLASS='sample.nlp.tweeter.SentenceNormalizer'
JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx256m"
exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $CLASS "$@"
