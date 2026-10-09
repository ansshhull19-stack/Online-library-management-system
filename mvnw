#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper POSIX Shell Script
# ----------------------------------------------------------------------------
set -e
BASE_DIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_PROPERTIES="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$WRAPPER_PROPERTIES" ]; then
    echo "Error: Cannot find $WRAPPER_PROPERTIES" >&2
    exit 1
fi

DIST_URL=$(grep "^distributionUrl=" "$WRAPPER_PROPERTIES" | cut -d= -f2 | tr -d '\r')
MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"
WRAPPER_DIR="$MAVEN_USER_HOME/wrapper/dists/apache-maven-3.9.6"
MAVEN_HOME="$WRAPPER_DIR/apache-maven-3.9.6"

if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    echo "Downloading Maven distribution from $DIST_URL..."
    mkdir -p "$WRAPPER_DIR"
    curl -sSL "$DIST_URL" -o "$WRAPPER_DIR/maven.zip"
    unzip -q "$WRAPPER_DIR/maven.zip" -d "$WRAPPER_DIR"
    rm -f "$WRAPPER_DIR/maven.zip"
    chmod +x "$MAVEN_HOME/bin/mvn"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
