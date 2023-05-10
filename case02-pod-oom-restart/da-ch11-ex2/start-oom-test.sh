echo "======================================="
echo "Deploying OOM Test"

BASE=$(pwd)
 
 
mvn clean package
eval $(minikube -p sandbox docker-env)
docker build -t da-ch11-ex2:latest   . 
kubectl create -f $BASE/k8s-yaml/ 
mvn clean 
 
echo "======================================="