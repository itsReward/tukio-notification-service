#!/bin/sh

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
JAVA_HOME=""
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
DIRNAME=`dirname "$0"`
SAVED="`pwd`"
cd "`dirname \"$0\"`"
APP_HOME="`pwd -P`"
cd "$SAVED"

GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
GRADLE_WRAPPER_PROPERTIES="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

exec "$JAVA_HOME/bin/java" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS -jar "$GRADLE_WRAPPER_JAR" "$@"
