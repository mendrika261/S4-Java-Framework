#!/bin/bash

color_red="\033[0;31m"
color_green="\033[0;32m"
color_blue="\033[0;34m"
color_reset="\033[0m"

function compile {
  source_directory=$1
  out_directory=$2
  classpath=$3
  source_files=($(find "$source_directory" -type f -name "*.java"))
  while [ ${#source_files[@]} -ne 0 ]; do
    echo "Reminding:" ${#source_files[@]}
    for i in "${!source_files[@]}"; do
      javac -cp "$classpath":"$out_directory" -d "$out_directory" "${source_files[i]}" 2>compilation.temp
      compilation=$(<compilation.temp)
      if [ ${#compilation[0]} -eq 0  ]; then
        echo -e "${color_blue}File:${source_files[i]}${color_blue}"
        echo -e "${color_green}[STATUS] Compile${color_reset}\n"
        unset "source_files[$i]"
      fi
    done
  done
  rm -rf compilation.temp
}

tomcat_lib='/opt/homebrew/Cellar/tomcat/10.1.7/libexec/lib/*'
tomcat_webapps='/opt/homebrew/Cellar/tomcat/10.1.7/libexec/webapps'

echo -e "${color_red}*** Compiling framework into jar ***${color_reset}\n"
compile ../framework/src/main/java framework-temp "$tomcat_lib"
jar --create --file src/main/webapp/WEB-INF/lib/framework.jar -C framework-temp .
rm -rf framework-temp

echo -e "${color_red}*** Compiling test-framework and deploy war ***${color_reset}\n"
mkdir project-temp
cp -r src/main/webapp/* project-temp
compile src/main/java project-temp/WEB-INF/classes project-temp/WEB-INF/lib/*
jar --create --file "$tomcat_webapps"/framework.war -C project-temp .
rm -rf project-temp

echo -e "${color_red}*** Restart server and open browser ***${color_reset}\n"
brew services restart tomcat
open -a Safari localhost:8080/framework
