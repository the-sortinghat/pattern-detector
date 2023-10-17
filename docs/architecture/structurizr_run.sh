#!/usr/bin/env bash

docker container run -it --rm                           \
  -p 9999:8080                                          \
  -v $(pwd):/usr/local/structurizr structurizr/lite
