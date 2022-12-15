#!/bin/bash
# Script for build Dockerfile

# Product family
PRODUCT=internal

# Customer where the product is implemented
CUSTOMER=fortaleza

# DOCKER Repository
REPOSITORY=repository.grupofortaleza.com.bo:5443

# Getting artifact version from POM
VERSION=`
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    xpath -e '/project/version[1]/text()' pom.xml 2>/dev/null
elif [[ "$OSTYPE" == "darwin"* ]]; then
        # Mac OSX
    	if [[ $(sw_vers -buildVersion) > "20A" ]]; then
                xpath -e '/project/version[1]/text()' 2>/dev/null pom.xml
        else
                xpath pom.xml '/project/version[1]/text()' 2>/dev/null
        fi
else
    xml sel -N my=http://maven.apache.org/POM/4.0.0 -t -m my:project -v my:version pom.xml
fi`

# Getting artifact name from POM
ARTIFACT=`
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    xpath -e '/project/artifactId[1]/text()' pom.xml 2>/dev/null
elif [[ "$OSTYPE" == "darwin"* ]]; then
        # Mac OSX
    	if [[ $(sw_vers -buildVersion) > "20A" ]]; then
                xpath -e '/project/artifactId[1]/text()' 2>/dev/null pom.xml
        else
                xpath pom.xml '/project/artifactId[1]/text()' 2>/dev/null
        fi
else
    xml sel -N my=http://maven.apache.org/POM/4.0.0 -t -m my:project -v my:artifactId pom.xml
fi`

# Displaying build info
echo "===== Processing artifact $PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION ======"

# Building de Uber JAR
./mvnw clean package

# Creating folder for unpack Uber JAR
mkdir target/dependency-docker

# Unpacking Uber JAR. The Dockerfile must copy the artifacts to the container
(cd target/dependency-docker; jar -xf ../*.jar)

# Building the image for micro service
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    sudo docker build . -t $PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION
else
    DOCKER_BUILDKIT=0  docker build . -t $PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION
fi

# Renaming the tag for Google Cloud Registry
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    sudo docker tag $PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION $REPOSITORY/$PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION
else
    docker tag $PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION $REPOSITORY/$PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION
fi

# Deletting untagged images
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    sudo docker rmi $(sudo docker images  | grep "<none>" | awk '{print $3}')
else
    docker rmi $(docker images  | grep "<none>" | awk '{print $3}')
fi

# Putting info for make the publish:
echo ""
echo "Execute the following for publish on Google Container Registry"
echo "  $ gcloud auth configure-docker"
echo "  $ docker push $REPOSITORY/$PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION"
echo ""
echo "For run the container:"
echo "docker run --rm $REPOSITORY/$PRODUCT/$ARTIFACT-$CUSTOMER:$VERSION"
