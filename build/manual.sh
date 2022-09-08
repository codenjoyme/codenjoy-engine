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

eval_echo "echo Generating manuals for all clients..."

eval_echo "cd .."
eval_echo "BASE=$(pwd)/.."
eval_echo "MVNW=./mvnw"


eval_echo "GAMES=mollymage,sample"
# eval_echo "GAMES=all"
# eval_echo "GAMES=a2048,excitebike,rawelbbub,chess,clifford,collapse,expansion,fifteen,football,hex,icancode,japanese,kata,lemonade,lunolet,moebius,mollymage,pong,puzzlebox,quadro,quake2d,reversi,rubicscube,sample,sampletext,selfdefense,knibert,namdreab,sokoban,spacerace,startandjump,sudoku,tetris,vacuum,verland,xonix"
eval_echo "LOCALE=all"
# eval_echo "LOCALE=en,ru"
# eval_echo "LOCALE=en"

eval_echo "$MVNW clean compile exec:java -Dfile.encoding=UTF-8 -Dexec.mainClass=com.codenjoy.dojo.services.generator.manual.ManualGeneratorRunner -Dexec.args='$BASE $GAMES $LOCALE'"

echo
color "Press Enter to continue"
read