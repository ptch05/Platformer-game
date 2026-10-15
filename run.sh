#!/bin/sh
# Compiles (if needed) and starts the game. Pass -d for debug keys.
cd "$(dirname "$0")" || exit 1
if [ ! -d out ] || [ -n "$(find src -name '*.java' -newer out 2>/dev/null | head -n 1)" ]; then
    rm -rf out
    javac -d out $(find src -name '*.java') || exit 1
    touch out
fi
if [ "$1" = "-d" ]; then
    exec java -Dgv.debug=true -cp out gothicvania.Main
fi
exec java -cp out gothicvania.Main
