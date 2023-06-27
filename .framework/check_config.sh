#!/bin/bash

# Load environment variables
source conf.env

echo -e "\n*** Checking configuration ***\n"


# Check if all environment variables are set
echo -e "${COLOR_BLUE}Checking environment variables...${COLOR_RESET}"

## Tomcat configuration
if [[ ! -d "$TOMCAT_HOME" ]]; then
    error+=1
    echo "\$TOMCAT_HOME is not a valid directory"
fi

if [[ ! -d "$TOMCAT_LIB" ]]; then
    error+=1
    echo "\$TOMCAT_LIB is not a valid directory"
fi

if [[ ! -d "$TOMCAT_BIN" ]]; then
    error+=1
    echo "\$TOMCAT_BIN is not a valid directory"
fi

if [[ ! -d "$TOMCAT_WEBAPPS" ]]; then
    error+=1
    echo "\$TOMCAT_WEBAPPS is not a valid directory"
fi

if [[ -z "$TOMCAT_PORT" ]]; then
    error+=1
    echo "\$TOMCAT_PORT is not set"
fi

## JDK configuration
if [[ ! -x "$JAVAC" ]]; then
    error+=1
    echo "\$JAVAC is not a valid executable"
fi

if [[ ! -x "$JAVA" ]]; then
    error+=1
    echo "\$JAVA is not a valid executable"
fi


## Report errors
if [[ $error -ne 0 ]]; then
    echo -e "${COLOR_RED}ERROR: Please check your configuration in .env file${COLOR_RESET}\n"
    exit 1
else
    echo -e "${COLOR_GREEN}SUCCESS: All configurations are set${COLOR_RESET}\n"
fi


# Compatibility check
required_jdk_version="17"
required_tomcat_version="10"

## Check if JDK version is compatible
jdk_version=$("$JAVAC" -version 2>&1 | awk -F ' ' '/javac/ {print $2}')
echo -e "${COLOR_BLUE}Checking compatibility with jdk ${jdk_version}...${COLOR_RESET}"

if [[ "$jdk_version" < "$required_jdk_version" ]]; then
    echo -e "${COLOR_RED}ERROR: JDK version >= ${required_jdk_version} is required${COLOR_RESET}\n"
    exit 1
else
    echo -e "${COLOR_GREEN}SUCCESS: JDK version is compatible${COLOR_RESET}\n"
fi

## Check if tomcat version is compatible
tomcat_version=$("$TOMCAT_BIN"/version.sh | awk -F ' ' '/Server number/ {print $3}')
echo -e "${COLOR_BLUE}Checking compatibility with tomcat ${tomcat_version}...${COLOR_RESET}"

if [[ "$tomcat_version" < "$required_tomcat_version" ]]; then
    echo -e "${COLOR_RED}ERROR: Tomcat version >= ${required_tomcat_version} is required${COLOR_RESET}\n"
    exit 1
else
    echo -e "${COLOR_GREEN}SUCCESS: Tomcat version is compatible${COLOR_RESET}\n"
fi

echo "*** All configuration seems OK! ***"
