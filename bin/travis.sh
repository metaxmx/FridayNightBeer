#!/bin/bash

set -ev

declare -a TASKS=(prepare test)

for TASK in "${TASKS[@]}"; do
  # We have multi-threaded tests and see concurrent modification when starting logback,
  # so always run tests sequentually.
  bin/${TASK}.sh "set concurrentRestrictions in Global += Tags.limitAll(1)"
done