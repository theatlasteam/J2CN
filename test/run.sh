#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
./build.sh
mkdir -p build/test
javac -d build/test src/j2cn/Theme.java src/j2cn/Style.java src/j2cn/StyleSheet.java src/j2cn/Layout.java test/J2cnTest.java
java -cp build/test j2cn.J2cnTest
