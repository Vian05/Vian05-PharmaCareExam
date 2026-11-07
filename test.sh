#!/bin/bash
set -u

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

rm -rf out-test
mkdir -p out-test

echo "Compiling application and automated tests..."
if ! find src tests -name "*.java" -print0 | xargs -0 javac -d out-test; then
  echo "Compilation FAILED."
  exit 1
fi

echo
java -cp out-test pharmacare.PharmaCareTest
STATUS=$?
rm -rf out-test
exit $STATUS
