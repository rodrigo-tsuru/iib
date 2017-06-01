#!/bin/bash
mvn install:install-file -Dfile=IntegrationAPI.jar -DgroupId=com.ibm.iib \
    -DartifactId=IntegrationAPI -Dversion=10.0.0.8 -Dpackaging=jar
