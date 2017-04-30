#! /bin/bash


source "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/build.sh"

echo "[info]"
echo "[info] ---- BUILDING FNB"
echo "[info]"
build "$@" "coverage compile"

echo "[info]"
echo "[info] ---- RUNNING TESTS"
echo "[info]"

build "$@" "test coverageReport"

echo "[info]"
echo "[info] ALL TESTS PASSED"
echo "[info]"