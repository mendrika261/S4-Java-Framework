#!/bin/bash

# load environment variables
source conf.env

# Show java framework ascii art
if [[ "$2" != "--re-run" ]]; then
  echo -e "${COLOR_BLUE}"
  cat .framework/ascii_art.txt
  echo -e "${COLOR_RESET}\n"
fi

# Show help
if [[ "$1" == "-h" || "$1" == "--help" || "$#" -eq 0 ]]; then
    echo "Usage: $0 [OPTION]"
    echo "  -i, --init      Initialize project repository"
    echo "  -b, --build     Build project and output a .war in $PROJECT_OUTPUT"
    echo "  -r, --run       Build project and run it on tomcat server"
    echo "  --init-dev      Initialize project repository with framework development files"
    echo "  --build-dev     Build framework and output a .jar in $DEV_JAVA_OUTPUT"
    echo "  --run-dev       Build framework, project and run it on tomcat server"
    exit 0
fi

# Initialize project repository
if [[ "$1" == "-i" || "$1" == "--init" ]]; then
    # Check configuration
    source .framework/check_config.sh

    # Create project directories
    mkdir -p "$PROJECT_JAVA_SRC"
    mkdir -p "$PROJECT_JAVA_LIB"
    mkdir -p "$PROJECT_WEB_SRC"
    mkdir -p "$PROJECT_OUTPUT"


    # Create web.xml jakarta from ./.framework/web.xml and evaluate variables in the file
    export PROJECT_JAVA_SRC
    envsubst < .framework/web.xml > "$PROJECT_WEB_XML"

    # Move framework.jar to $PROJECT_JAVA_LIB
    cp .framework/framework.jar "$PROJECT_JAVA_LIB"

    # Project creation test
    error=0

    if [[ -z "$PROJECT_NAME" ]]; then
        error+=1
        echo "\$PROJECT_NAME is not set"
    fi

    if [[ ! -d "$PROJECT_JAVA_SRC" ]]; then
        error+=1
        echo "\$PROJECT_JAVA_SRC cannot be created"
    fi

    if [[ ! -d "$PROJECT_JAVA_LIB" ]]; then
        error+=1
        echo "\$PROJECT_JAVA_LIB cannot be created"
    fi

    if [[ ! -f "$PROJECT_WEB_XML" ]]; then
        error+=1
        echo "\$PROJECT_WEB_XML cannot be created"
    fi

    if [[ ! -d "$PROJECT_WEB_SRC" ]]; then
        error+=1
        echo "\$PROJECT_WEB_SRC cannot be created"
    fi

    if [[ ! -d "$PROJECT_OUTPUT" ]]; then
        error+=1
        echo "\$PROJECT_OUTPUT cannot be created"
    fi

    if [[ ! -f "$PROJECT_JAVA_LIB/framework.jar" ]]; then
        error+=1
        echo "\$PROJECT_JAVA_LIB/framework.jar cannot be created"
    fi

    # Report errors
    if [[ $error -ne 0 ]]; then
        echo -e "${COLOR_RED}ERROR: Please check your repository permission${COLOR_RESET}\n"
        exit 1
    else
        echo -e "${COLOR_GREEN}SUCCESS: Project repository initialized successfully${COLOR_RESET}\n"
    fi
    exit 0
fi

# Initialize project repository with framework development files
if [[ "$1" == "--init-dev" ]]; then
    # Init project repository
    "$0" -i --re-run
    if [ $? -ne 0 ]; then
       exit 1
    fi

    # Create framework dir
    mkdir -p "$DEV_JAVA_SRC"
    mkdir -p "$DEV_JAVA_LIB"
    mkdir -p "$DEV_JAVA_OUTPUT"

    # Copy framework development files from .framework
    cp -r .framework/dev_files/* "$DEV_JAVA_SRC"

    # Copy lib files from .framework
    cp -r .framework/lib/* "$DEV_JAVA_LIB"

    # Report success
    echo -e "${COLOR_GREEN}SUCCESS: Project development files initialized successfully ${COLOR_RESET}\n"

    exit 0
fi


# Function to compile java
function compile {
  source_directory=$1
  out_directory=$2
  classpath=$3
  source_files=($(find "$source_directory" -type f -name "*.java")) # get all java files
  reminding_files=${#source_files[@]}
  while [ ${#source_files[@]} -ne 0 ]; do # while there are files to compile (boucle because of other class dependencies)
    for i in "${!source_files[@]}"; do
      # echo -cp "$classpath":"$out_directory" -d "$out_directory" "${source_files[i]}"
      javac -parameters -cp "$classpath":"$out_directory" -d "$out_directory" "${source_files[i]}" 2>compilation.log
      compilation=$(<compilation.log) # ignore error dependency
      if [ ${#compilation[0]} -eq 0  ]; then # remove compiled files
        unset "source_files[$i]"
      fi
    done
    if [ ${#source_files[@]} -eq "$reminding_files" ]; then # if no file has been compiled
      echo -e "${COLOR_RED}ERROR: Compilation failed due to unresolved dependencies or mistakes in the code ${COLOR_RESET}"
      cat compilation.log
      exit 1
    fi
    reminding_files=${#source_files[@]}
  done
  rm -rf compilation.temp
}


# Build project
if [[ "$1" == "-b" || "$1" == "--build" ]]; then
    # Check if project is initialized
    if [[ ! -d "$PROJECT_JAVA_SRC" ]]; then
        echo -e "${COLOR_RED}ERROR: Project is not initialized${COLOR_RESET}\n"
        exit 1
    fi

    # Make a temp dir in $PROJECT_OUTPUT
    rm -rf "$PROJECT_OUTPUT/project"
    temp_dir=$(mktemp -d "$PROJECT_OUTPUT/project")
    echo -e "${COLOR_BLUE}Building project in $temp_dir...${COLOR_RESET}"

    # Create .war structure
    mkdir -p "$temp_dir/WEB-INF/classes"
    mkdir -p "$temp_dir/WEB-INF/lib"
    mkdir -p "$temp_dir/META-INF"

    # Copy web.xml to $temp_dir/WEB-INF
    cp "$PROJECT_WEB_XML" "$temp_dir/WEB-INF"

    # Copy framework.jar to $temp_dir/WEB-INF/lib
    cp "$PROJECT_JAVA_LIB/framework.jar" "$temp_dir/WEB-INF/lib"

    # Compile java files
    echo -e "${COLOR_BLUE}Compiling java files...${COLOR_RESET}"

    compilation=$(compile "$PROJECT_JAVA_SRC" "$temp_dir/WEB-INF/classes" "$PROJECT_JAVA_LIB/framework.jar:$TOMCAT_LIB/*")
    if [ ${#compilation[0]} -ne 0 ]; then
        echo "$compilation"
        exit 1
    fi

    # Copy web files
    echo -e "${COLOR_BLUE}Copying web files...${COLOR_RESET}"
    cp -r "$PROJECT_WEB_SRC"/* "$temp_dir"

    # Create .war file
    echo -e "${COLOR_BLUE}Creating .war file...${COLOR_RESET}"
    jar -cvf "$PROJECT_OUTPUT/$PROJECT_NAME.war" -C "$temp_dir" .

    # Remove temp dir
    rm -rf "$temp_dir"

    # Report success
    echo -e "${COLOR_GREEN}SUCCESS: Project built successfully in ${PROJECT_OUTPUT} ${COLOR_RESET}\n"
    exit 0
fi

# Build framework
if [[ "$1" == "--build-dev" ]]; then
    # Check if framework dir is initialized
    if [[ ! -d "$DEV_JAVA_SRC" ]]; then
        echo -e "${COLOR_RED}ERROR: Framework dev repository is not initialized${COLOR_RESET}\n"
        exit 1
    fi

    # Make a temp dir in $PROJECT_OUTPUT
    rm -rf "$DEV_JAVA_OUTPUT/framework"
    temp_dir=$(mktemp -d "$DEV_JAVA_OUTPUT/framework")
    echo -e "${COLOR_BLUE}Building framework in $temp_dir...${COLOR_RESET}"

    # Compile java files
    echo -e "${COLOR_BLUE}Compiling java files...${COLOR_RESET}"
    compilation=$(compile "$DEV_JAVA_SRC" "$temp_dir" "$DEV_JAVA_LIB/*:$TOMCAT_LIB/*")
    if [ ${#compilation[0]} -ne 0 ]; then
        echo "$compilation"
        exit 1
    fi

    # Create .jar file
    echo -e "${COLOR_BLUE}Creating .jar file...${COLOR_RESET}"
    jar -cvf "$DEV_JAVA_OUTPUT/framework.jar" -C "$temp_dir" .

    # Remove temp dir
    rm -rf "$temp_dir"

    # Copy framework files to .framework
    cp -r "$DEV_JAVA_LIB"/* .framework/lib
    cp -r "$DEV_JAVA_SRC"/* .framework/dev_files

    # Copy framework.jar to project lib
    cp "$DEV_JAVA_OUTPUT/framework.jar" "$PROJECT_JAVA_LIB"

    # Report success
    echo -e "${COLOR_GREEN}SUCCESS: Framework built successfully in ${DEV_JAVA_OUTPUT} ${COLOR_RESET}\n"
    exit 0
fi

# Run project
if [[ "$1" == "-r" || "$1" == "--run" ]]; then
    # Build project
    "$0" -b --re-run
    if [ $? -ne 0 ]; then
        exit 1
    fi

    # Copy war in webapps
    echo -e "${COLOR_BLUE}Copying .war file in webapps...${COLOR_RESET}"
    cp "$PROJECT_OUTPUT/$PROJECT_NAME.war" "$TOMCAT_WEBAPPS"

    # Run tomcat with hot reload feature
    echo -e "${COLOR_BLUE}Running tomcat server... please wait!${COLOR_RESET}"
    "$TOMCAT_BIN"/catalina.sh start >> 'tomcat.log' 2>&1

    # Check if tomcat is running on port $TOMCAT_PORT
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:"${TOMCAT_PORT}")
    # Max delay 10 seconds
    delay=0
    while [ "$response" != "200" ]; do
        response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:"${TOMCAT_PORT}")
        sleep 1
        delay=$((delay+1))
        if [ "$delay" -gt 15 ]; then
            echo -e "${COLOR_RED}ERROR: (timeout 15s) Tomcat server failed to start${COLOR_RESET}\n"
            echo "Verify your tomcat configuration and if the port $TOMCAT_PORT is not already in use"
            echo "If problem persists, try to run your tomcat server manually"
            echo "You can also check the logs in tomcat.log"
            exit 1
        fi
    done
    # Show tomcat logs
    # tail -f "$TOMCAT_HOME"/logs/catalina.out &

    # Report success
    echo -e "${COLOR_GREEN}Tomcat server is running on port ${TOMCAT_PORT} ${COLOR_RESET}\n"
    echo -e "Open ${COLOR_BLUE}http://localhost:${TOMCAT_PORT}/${PROJECT_NAME}${COLOR_RESET} in your browser"
    echo -e "Changed files will be automatically reloaded (May take a while to see change on browser)\n"
    echo -e "${COLOR_RED}Press Ctrl+C to stop server...${COLOR_RESET}\n"

    touch .framework/timestamp
    while true; do
        changes=$(find "$PWD" -type f -newer .framework/timestamp)

        # If any files have been modified
        if [ ${#changes} -ne 0 ]; then
            # Clear the output
            clear
            echo -ne "\033c"
            echo -e "\n****************************\n${COLOR_BLUE}Reloading web application...${COLOR_RESET}\n\n"


            touch .framework/timestamp
            "$0" -r --re-run
            if [ $? -ne 0 ]; then
                exit 1
            fi
        fi
        # Sleep for 1 second before checking for changes again
        sleep 1
    done

    exit 0
fi

# Run dev
if [[ "$1" == "--run-dev" ]]; then
    # Build framework
    "$0" --build-dev --re-run
    if [ $? -ne 0 ]; then
        exit 1
    fi


    # Run project
    "$0" -r --re-run
    if [ $? -ne 0 ]; then
        exit 1
    fi

    exit 0
fi

# Show error
echo -e "${COLOR_RED}"
echo "Invalid option: $1"
echo "Try '$0 --help' for more information"
echo -e "${COLOR_RESET}"
exit 1
