#!/usr/bin/env bash

eval_echo() {
    command=$1
    color=94 # blue
    echo "[${color}m$command[0m"
    echo
    eval $command
}

if [ "x$VERSION" = "x" ]; then
    VERSION=1.1.1
fi

echo VERSION=$VERSION

build_games() {
    echo "[93mBuild games[0m"

    eval_echo "$MVNW install:install-file -Dfile=games-$VERSION-pom.xml -DpomFile=games-$VERSION-pom.xml -DgroupId=com.codenjoy -DartifactId=games -Dversion=$VERSION -Dpackaging=pom"

    echo "[93m"
    echo "       +--------------------------------------------------+"
    echo "       !         Check that BUILD was SUCCESS             !"
    echo "       !           Then press Enter to exit               !"
    echo "       +--------------------------------------------------+"
    echo "[0m"

    read
}

build_engine_from_zip() {
    echo "[93mBuild engine from zip[0m"

    eval_echo "ROOT=$(pwd)"
    eval_echo "MVNW=$ROOT/mvnw"

    eval_echo "$MVNW install:install-file -Dfile=engine-$VERSION.jar -Dsources=engine-$VERSION-sources.jar -DpomFile=engine-$VERSION-pom.xml -DgroupId=com.codenjoy -DartifactId=engine -Dversion=$VERSION -Dpackaging=jar"

    echo "[93m"
    echo "       +--------------------------------------------------+"
    echo "       !           Check that BUILD was SUCCESS           !"
    echo "       +--------------------------------------------------+"
    echo "[0m"
    if [ "x$DEBUG" = "xtrue" ]; then
        read
    fi
}

build_engine_from_sources() {
    echo "[93mBuild engine from sources[0m"

    eval_echo "cd .."
    eval_echo "ROOT=$(pwd)"
    eval_echo "MVNW=$ROOT/mvnw"

    eval_echo "$MVNW clean install -DskipTests=true"

    echo "[93m"
    echo "       +--------------------------------------------------+"
    echo "       !           Check that BUILD was SUCCESS           !"
    echo "       +--------------------------------------------------+"
    echo "[0m"
    if [ "x$DEBUG" = "xtrue" ]; then
        read
    fi

    eval_echo "cp ../pom.xml ./target/games-$VERSION-pom.xml"
    cd ./target
}

if [ -f "./engine-$VERSION-pom.xml" ]; then
   eval_echo "build_engine_from_zip"
elif [ -d "./../build" ]; then
   eval_echo "build_engine_from_sources"
fi
eval_echo "build_games"