#!/bin/bash

set -e
set -o pipefail

SBT=$(which sbt)

if [ $? -ne 0 ]; then
    echo "sbt not found on PATH."
    exit 1
fi

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
BASEDIR=$DIR/..

# There are lots of different ways to configure the memory with sbt, and not every sbt installation supports
# all of them, eg on Travis, sbt doesn't support the -mem option.  But JAVA_OPTS should be standard across
# the board.
export JAVA_OPTS="-Xmx2G -Xss2M -XX:MaxMetaspaceSize=512M -XX:ReservedCodeCacheSize=192M -Dfile.encoding=UTF-8"

build() {
  "$SBT" --warn "$@" | grep --line-buffered -v 'Resolving \|Generating '
}