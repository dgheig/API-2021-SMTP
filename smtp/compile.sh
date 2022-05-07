#!/bin/bash

mvn -f client clean compile assembly:single
mvn -f mockmock package
mvn -f server clean compile assembly:single