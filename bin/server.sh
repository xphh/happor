#/bin/sh
if [ "$1" == "" ]; then
	java -server -jar happor.jar
elif [ "$1" = "reload" ]; then
	java -jar happor.jar reload
fi
