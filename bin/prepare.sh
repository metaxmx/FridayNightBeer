#!/bin/bash

echo "[info]"
echo "[info] ---- Installing NPM Dependencies"
echo "[info]"
npm install

echo "[info]"
echo "[info] ---- Configure Application"
echo "[info]"
cp -v conf/travis.conf con/instance.conf
mkdir -pv travisdata