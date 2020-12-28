#!/bin/bash
echo $CI_PROJECT_PATH_SLUG
mkdir -p publish
cd cifimaster/
#编译后将包拷贝至新建的publish目录下，gitlab上可以在对应节点下载包。
mvn clean package -Dmaven.test.skip=true -P $env || exit 1 && \
    warpath=$(find . -name "visolink-sales-api*.war") && \
    mv $warpath ../publish/ROOT.war && \
    echo 'build successful!'
