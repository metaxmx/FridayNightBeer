#!/bin/bash

echo "[info]"
echo "[info] ---- Installing NPM Dependencies"
echo "[info]"
echo "[warn] npm install is disabled at the moment"

echo "[info]"
echo "[info] ---- Configure Application"
echo "[info]"
cp -v conf/travis.conf conf/instance.conf
mkdir -pv travisdata