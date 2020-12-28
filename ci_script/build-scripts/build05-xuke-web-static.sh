#!/bin/bash
echo $CI_PROJECT_PATH_SLUG
rm -rf publish/dist-xuke-web-static/
mkdir -p publish/dist-xuke-web-static/
cd cifimaster/
#编译后将包拷贝至新建的publish目录下，gitlab上可以在对应节点下载包。
npm install --registry=http://10.129.32.7:4873/  || exit 1 && \
	npm run build:$env || exit 1 && \
    cp -r dist/* ../publish/dist-xuke-web-static/ && \
    echo 'build successful!'