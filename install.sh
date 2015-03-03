#!/usr/bin/env bash

mvn clean package

if [ $? -eq 0 ]; then
    # For mac/brew
    # Change to /usr/share/elasticsearch/bin/plugin on Ubuntu
	plugin -r plugin-example
	plugin --install plugin-example --url file://`pwd`/`ls target/*.jar | head -n 1`
	echo -e "\033[1;33mPlease restart Elasticsearch!\033[0m"
fi
