#!/bin/bash
#根据环境区分部署的IP数组值。环境区分是传入的env参数及选取的分支决定。
#替换argv值即可。
if [[ $env == 'test' && $CI_BUILD_REF_NAME == 'develop' ]]; then
   argv=(10.129.37.101)
elif [[ $env == 'uat' ]] && [[ $CI_BUILD_REF_NAME == 'uat' || $CI_BUILD_REF_NAME =~ [v|V][0-9]+\.[0-9]+\.[0-9]+$ ]]; then
   argv=(10.129.37.102)
elif [[ $env == 'uat' ]] && [[ $CI_BUILD_REF_NAME == 'Group' || $CI_BUILD_REF_NAME =~ [v|V][0-9]+\.[0-9]+\.[0-9]+$ ]]; then
   argv=(10.129.37.102)
elif [[ $env == 'test' ]] && [[ $CI_BUILD_REF_NAME == 'feature/baoqianglei' || $CI_BUILD_REF_NAME =~ [v|V][0-9]+\.[0-9]+\.[0-9]+$ ]]; then
   argv=(10.129.37.101)
elif [[ $env == 'prod' ]] && [[ $CI_BUILD_REF_NAME == 'master' || $CI_BUILD_REF_NAME =~ [v|V][0-9]+\.[0-9]+\.[0-9]+$ ]]; then
   argv=(10.129.2.20 10.129.2.21)
else
   echo 'Please enter environment parameters test or prod !'
   exit 1
fi
echo ${argv[@]}
#遍历IP并部署：部署先将gitlab打的包放到机器的/tmp目录下，然后登陆目标机器将包放到部署目录，并删除/tmp下内容。
for i in "${argv[@]}";do
    scp -r $CI_PROJECT_DIR/publish/ROOT.war cifiadmin@$i:/tmp/ && \
    ssh -o PasswordAuthentication=no -o StrictHostKeyChecking=no cifiadmin@$i  "
        source /etc/profile ;
        bash /app/tomcat-salman/bin/predeploy.sh || exit 1 ;
        cd /app/tomcat-salman/ ;
        cp -r /tmp/ROOT.war webapps/;
        bin/startup.sh ;
        exit 0 ;
    " || exit 1
    echo "operate $i "
done
echo 'deploy successful!'
