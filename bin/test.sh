#! /bin/bash


source "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/build.sh"

echo "[info]"
echo "[info] ---- BUILDING FNB"
echo "[info]"
build "$@" "compile"

echo "[info]"
echo "[info] ---- RUNNING TESTS"
echo "[info]"

build "$@" "test"

echo "[info]"
echo "[info] ALL TESTS PASSED"
echo "[info]"