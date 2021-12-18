#!/usr/bin/env bash

eval_echo() {
    command=$1
    color=94 # blue
    echo "[${color}m$command[0m"
    echo
    eval $command
}

eval_echo "cd .."
eval_echo "MVNW=./mvnw"

eval_echo "$MVNW clean install -DskipTests=true"

echo Press Enter to continue
read