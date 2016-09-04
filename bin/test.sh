#! /bin/bash


source "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/build"

echo "[info]"
echo "[info] ---- BUILDING FNB"
echo "[info]"
build "$@" quickPublish "publishLocal"

echo "[info]"
echo "[info] ---- RUNNING TESTS"
echo "[info]"

build "$@" "test"

echo "[info]"
echo "[info] ALL TESTS PASSED"
echo "[info]"