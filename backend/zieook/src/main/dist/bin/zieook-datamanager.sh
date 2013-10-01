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

# check if zieook_home is defined:
if [ -n "${ZIEOOK_HOME+x}" ]; then
    echo "ZieOok home '$ZIEOOK_HOME'"
else
    echo "ZIEOOK_HOME not set, failed"
    exit -1
fi


# cd to zieook home:
cd $ZIEOOK_HOME



# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

if [ ! -n $DATA_PORT ]; then
	DATA_PORT="20200"
	echo "PORT not set using default: 20200"
fi

# name
NAME="data"
# configurration path:
ZIEOOK_CONFIG="$ZIEOOK_HOME/etc"
# logback configuration:
LOGS_OPTS="-DZIEOOK_HOME=$ZIEOOK_HOME -Dlogback.configurationFile=$ZIEOOK_HOME/etc/logback-$NAME.xml"
# ZIEOOK classpath
CLASSPATH="$ZIEOOK_HOME/lib/*"
# PID file location:
ZIEOOK_PID="$ZIEOOK_HOME/state/zieook-$NAME.pid"
# WEBAPP OPTS:
WEBAPP="-n DataManager -d "$ZIEOOK_HOME/webapps/$NAME" -p $DATA_PORT -l $DATA_HOST"
# output
OUTPUT="$ZIEOOK_HOME/log/zieook-$NAME.out"
# port
PORT=$DATA_PORT

if [ $# == 1 ];
then
	if [ "$1" = "stop" ];
	then
		if [ -e $ZIEOOK_PID ];
		then
			kill `cat $ZIEOOK_PID`
			rm -f $ZIEOOK_PID
		else
			echo $ZIEOOK_PID "could not be found."
		fi
	elif [ "$1" = "start" ]
	then
        echo "========================================================================="
    	echo " starting ZieOok - $NAME at $PORT"
    	echo "  ZIEOOK_HOME: $ZIEOOK_HOME"
    	echo "  JAVA: $JAVA"
    	echo "  JAVA_OPTS: $JAVA_OPTS"
    	echo "  CLASSPATH: $CLASSPATH"
    	echo "========================================================================="
    	echo ""
         
         # start zieook data engine:
    	eval "(\"$JAVA\" -classpath \"$CLASSPATH\"  $JAVA_OPTS $LOGS_OPTS \
    		nl.gridline.jettyembedder.WebAppServer $WEBAPP  \"$@\" >> $OUTPUT 2>&1) &"

    	if [ ! -z "$ZIEOOK_PID" ]; then
        		echo $! > $ZIEOOK_PID
    	fi	
	elif [ "$1" = "status" ] 
	then
	    if [ -e $ZIEOOK_PID ];
	    then
	        echo "$NAME is running"
	    else
	        echo "$NAME is not running"
	    fi
	else
	    echo "Usage: $0 [start|stop|status]" 
	fi
else
	echo "Usage: $0 [start|stop|status]"
fi

