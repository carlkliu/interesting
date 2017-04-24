###################################################
#execute the script before build project.
#run the script in the root directory of the project
###################################################
projectName=interesting
projectRootPath=`pwd`
releaseVersion=1.0

#get GIT uuid the first 7 code 
code=`git log | grep commit |awk 'NR==1'| awk '{print $2}'|cut -c 1-7`

#save product VERSION into RPM
sed -i 's/product.version=.*/product.version=product.build.version/g' $projectRootPath/src/main/resources/interesting.properties
sed -i "s/product.build.version/${releaseVersion}-${code}/g" $projectRootPath/src/main/resources/interesting.properties


mvn clean package -Dversion=$releaseVersion

echo "package is success !"

