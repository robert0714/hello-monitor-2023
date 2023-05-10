#!/bin/bash
 
echo "======================================="
echo "Startup Minikube"

minikube -p sandbox delete && minikube -p sandbox start --kubernetes-version=v1.24.13 \
--cpus=4 \
--memory=8g \
--network-plugin=cni    \
--bootstrapper=kubeadm \
--extra-config=kubelet.authentication-token-webhook=true \
--extra-config=kubelet.authorization-mode=Webhook \
--extra-config=scheduler.bind-address=0.0.0.0 \
--extra-config=controller-manager.bind-address=0.0.0.0 \
--extra-config=kubeadm.pod-network-cidr=192.168.0.0/16



echo "======================================="
echo "Add the loki helm chart"
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

BASE=$(pwd)

# echo "======================================="
# echo "Install Calico on minikubet"
# echo "https://docs.tigera.io/calico/latest/getting-started/kubernetes/minikube"

# kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.25.1/manifests/tigera-operator.yaml
# kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.25.1/manifests/custom-resources.yaml

# sleep 30s

# kubectl wait \
#  --for=condition=ready pod  -l k8s-app=calico-node\
#  --namespace=calico-system 


echo "======================================="
echo "Deploying Prometheus"

cd $BASE/kube-prometheus-v0.12.0 
kubectl apply --server-side -f $BASE/kube-prometheus-v0.12.0/manifests/setup

kubectl wait \
 --for condition=Established \
 --all CustomResourceDefinition \
 --namespace=monitoring

kubectl apply -f $BASE/kube-prometheus-v0.12.0/manifests/
 
echo "======================================="
echo "Deploying Loki"
helm install loki-stack grafana/loki-stack --values $BASE/loki-stack-values.yaml -n monitoring 

echo "======================================="
echo "Deploying single artemis borker"

cd $BASE/activemq-artemis-operator-v1.0.7
kubectl create namespace activemq-artemis-operator
kubectl config set-context --current --namespace activemq-artemis-operator
kubectl create -f $BASE/activemq-artemis-operator-v1.0.7/deploy/install
kubectl create -f $BASE/activemq-artemis-operator-v1.0.7/examples/artemis-basic-deployment.yaml -n activemq-artemis-operator
kubectl create -f $BASE/activemq-artemis-operator-v1.0.7/examples/prometheus_030_role.yaml -n activemq-artemis-operator
kubectl create -f $BASE/activemq-artemis-operator-v1.0.7/examples/prometheus_040_serviceMonitor.yaml -n activemq-artemis-operator
 
echo "======================================="
echo "Deploying Helloworld-MDB"

cd $BASE/helloworld-mdb
kubectl create namespace helloworld-mdb
kubectl config set-context --current --namespace helloworld-mdb
mvn clean package
eval $(minikube -p sandbox docker-env)
docker build -t helloworld-mdb:latest .
# kubectl create -f $BASE/helloworld-mdb/k8s-yaml/mq
kubectl create -f $BASE/helloworld-mdb/k8s-yaml/
kubectl create -f $BASE/helloworld-mdb/k8s-yaml/prometheus
mvn clean 
 
echo "======================================="
echo "Deploying Helloworld-Anthos" 
cd $BASE/helloworld-anthos
kubectl create namespace helloworld-anthos
kubectl config set-context --current --namespace helloworld-anthos
mvn clean package
eval $(minikube  -p sandbox docker-env)
docker build -t helloworld-anthos:latest .
kubectl create -f $BASE/helloworld-anthos/k8s-yaml/
kubectl create -f $BASE/helloworld-anthos/k8s-yaml/prometheus
mvn clean 
cd ..

echo "=====see https://docs.docker.com/build/buildkit/#getting-started ======"
echo "Deploying wildfly-todo-backend" 
cd $BASE/wildfy-26.1.3.Final-sample-todo-backend
kubectl create namespace wildfly-ex-app
kubectl config set-context --current --namespace wildfly-ex-app

mvn dependency:go-offline
# mvn dependency:copy-dependencies
mvn clean package

eval $(minikube  -p sandbox docker-env) 
# DOCKER_BUILDKIT=1 docker build -f Dockerfile-Buildkit -t wildfly-todo-backend:latest .
docker build -t wildfly-todo-backend:latest .
kubectl create -f $BASE/wildfy-26.1.3.Final-sample-todo-backend/k8s-yaml/
kubectl create -f $BASE/wildfy-26.1.3.Final-sample-todo-backend/k8s-yaml/prometheus
mvn clean 
cd ..

echo "======================================="
echo "Deploying wildfly-microprofile-metrics" 
cd $BASE/wildfy-26.1.3.Final-sample-microprofile-metrics 
mvn clean package
eval $(minikube  -p sandbox docker-env)
docker build -t wildfly-microprofile-metrics:latest .
kubectl create -f $BASE/wildfy-26.1.3.Final-sample-microprofile-metrics/k8s-yaml/ 
kubectl create -f $BASE/wildfy-26.1.3.Final-sample-microprofile-metrics/k8s-yaml/prometheus
mvn clean 
cd ..

echo "======================================="
echo "Cleaning temporary directory" 
rm -rf /tmp/repository