#!/usr/bin/env bash

BASE_PATH=$(dirname $0)

source ${BASE_PATH}/common.sh

if [[ -n "$@" ]]; then
    CMD="$@"
else
    CMD="up -d"
fi

WORKDIR_UID=$WORKDIR_UID \
   docker-compose -p $COMPOSE_PROJECT_NAME -f ${BASE_PATH}/nosketch.yml $CMD
