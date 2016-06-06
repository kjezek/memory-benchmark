#!/bin/sh

mvn clean install -DskipTests -q
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt -q

CP=`cat classpath.txt`

java -cp "$CP":target/classes/:target/test-classes/ cz.zcu.kiv.memory.TesterMain



