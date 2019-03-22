#!/usr/bin/env bash

i=0
BUILD_APPS[$i]="nordicsmartgovernment"
BUILD_CMD[$i]="mvn clean install -B -T 2C"

export BUILD_APPS
export BUILD_CMD
