#!/usr/bin/env bash

###
# #%L
# Codenjoy - it's a dojo-like platform from developers to developers.
# %%
# Copyright (C) 2012 - 2022 Codenjoy
# %%
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.
# #L%
###

BLUE=94
GRAY=89
YELLOW=93

color() {
    message=$1
    [[ "$2" == "" ]] && color=$YELLOW || color=$2
    echo "[${color}m${message}[0m"
}

eval_echo() {
    command=$1
    [[ "$2" == "" ]] && color=$BLUE || color=$2
    color "${command}" $color
    echo
    eval $command
}

if [ "x$VERSION" = "x" ]; then
    VERSION=1.1.3
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

build_java_client() {
    echo "[93mBuild java client[0m"

    eval_echo "$MVNW install:install-file -Dfile=client-java-$VERSION.jar -Dsources=client-java-$VERSION-sources.jar -DpomFile=client-java-$VERSION.pom -DgroupId=com.codenjoy -DartifactId=client-java -Dversion=$VERSION -Dpackaging=jar"

    echo "[93m"
    echo "       +--------------------------------------------------+"
    echo "       !         Check that BUILD was SUCCESS             !"
    echo "       +--------------------------------------------------+"
    echo "[0m"
    if [ "x$DEBUG" = "xtrue" ]; then
        read
    fi
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
   eval_echo "build_java_client"
elif [ -d "./../build" ]; then
   eval_echo "build_engine_from_sources"
fi
eval_echo "build_games"