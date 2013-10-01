#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
#


PRG="$0"
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG="`dirname "$PRG"`/$link"
  fi
done
DIRNAME=`dirname "$PRG"`
PROGNAME=`basename "$PRG"`

source $DIRNAME/zieook.cfg

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi


# name
NAME="cli"

# configurration path:
ZIEOOK_CONFIG="$ZIEOOK_HOME/etc"

# ZIEOOK classpath
CLASSPATH="$ZIEOOK_HOME/lib/*"

# logger (should be one that logs to std out)
LOGS_OPTS="-DZIEOOK_HOME=$ZIEOOK_HOME -Dlogback.configurationFile=$ZIEOOK_HOME/etc/logback-$NAME.xml"
        
# execute zieook client tools:
	eval "( \"$JAVA\" -classpath \"$CLASSPATH\" \
			-Dzieook.data.port=\"$DATA_PORT\" -Dzieook.workflow.port=\"$WORKFLOW_PORT\" -Dzieook.server=\"$ZIEOOK_SERVER\" \
			$JAVA_OPTS $LOGS_OPTS nl.gridline.zieook.client.ZieOok $@ )"


