#!/bin/bash

mvn -f mockmock package
mvn -f server clean compile assembly:single