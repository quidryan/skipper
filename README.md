Skipper is a script that runs the gradle in the gradle/wrapper/gradle-wrapper.properties. No gradle-wrapper.jar is needed when using skipper. Build skipper and add it to your path. If no distribution is found, then the build that build skipper is used.

    skipper dependencies --configuration runtime

To build:
    
    ./gradlew clean executableJar
    cp build/libs/skipper.sh ~/bin

TODO
------
* Add .bat version of file
