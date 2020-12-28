#!/bin/bash
source /etc/profile
echo $CI_PROJECT_PATH_SLUG
#如果未在gitlab的pipiline传递环境参数env，则默认传递test，选取develop分支，这样会部署到测试环境
if [[ $env == '' ]]; then
    export   env='test'
    export   CI_BUILD_REF_NAME='develop'
fi
if [[ $env == 'uat' ]]; then
    export   env='uat'
    export   CI_BUILD_REF_NAME='uat'
fi
if [[ $env == 'prod' ]]; then
    export   env='prod'
    export   CI_BUILD_REF_NAME='master'
fi
#以参数CI_PROJECT_PATH_SLUG判断具体使用哪个脚本部署
SCRIPTS_DIR=$(dirname "$0")
if [ "$CI_PROJECT_PATH_SLUG" == 'saleman-marketing-control-api' ];then
   sh $SCRIPTS_DIR/build01-xuke-api.sh || exit 1
elif [ "$CI_PROJECT_PATH_SLUG" == 'saleman-marketing-control-web' ];then
   sh $SCRIPTS_DIR/build05-xuke-web-static.sh || exit 1
else
  echo "This project is not connected to an automated build!"+$CI_PROJECT_PATH_SLUG
fi
