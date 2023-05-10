# Validatin Method
## Tesing Reiceiver
### Testing MDB(Message-Driven Bean)
* In ActiveMQ Artemis Management Console , we can mannually add messages in queue 
   

* Type the url in browser `http://127.0.0.1:8080/HelloWorldMDBServletClient`:
  * queue/address named``jms.queue.testQueueRemoteArtemis`` were created in Artemis
  * Servlet will trasnsmmit  the messages into Artemis :jms.queue.testQueueRemoteArtemis
  * MDB(Message Driven Bean) received  messages from jms.queue.testQueueRemoteArtemis .


# Usage Method
## Execute the application directly
Executing command as below
```shell
java -jar target/helloworld-mdb-bootable.jar  --properties dev.properties 
```	
or

```shell
java -jar target/helloworld-mdb-bootable.jar   -Damq.broker.userName=admin  -Damq.broker.passWord=admin  -Damq.broker.port=61616 -Damq.broker.host=192.168.18.30 -Djboss.bind.address=0.0.0.0
```
## In Kubernete

### Preparartion On Laptop
#### Preparartion
* install Skaffold
  * windows environment
   ```
   choco install skaffold
   ```
* install kubectl
  * windows environment
   ```
   choco install kubernetes-cli
   ```
* install minikiube
  * windows environment
   ```
   choco install minikube
   ```
##### Examples
* [Using minikube](https://skaffold.dev/docs/quickstart/#start-minikube)
```
minikube start --profile custom
skaffold config set --global local-cluster true
eval $(minikube -p custom docker-env)
```
### Cloud Enviroment (OpenShiftPlatform/Anthos)


#### Using Operator in Kubernetes/OpenShift environment to install ActiveMQ Artemis
* [Official Document](https://github.com/artemiscloud/activemq-artemis-operator/blob/main/docs/getting-started/quick-start.md)
* Steps  :
  * Step1: Download v1.0.7
    ```
    $ git clone https://github.com/artemiscloud/activemq-artemis-operator.git
    $ cd activemq-artemis-operator
    $ git chekout v1.0.7
    ```
  * Step 2: we cansee (custom resource definition)
    ```
    ./deploy/install
        |-- 010_crd_artemis.yaml
        |-- 020_crd_artemis_security.yaml
        |-- 030_crd_artemis_address.yaml
        |-- 040_crd_artemis_scaledown.yaml
        |-- 050_service_account.yaml
        |-- 060_cluster_role.yaml
        |-- 060_namespace_role.yaml
        |-- 070_cluster_role_binding.yaml
        |-- 070_namespace_role_binding.yaml
        |-- 080_election_role.yaml
        |-- 090_election_role_binding.yaml
         `-- 100_operator_config.yaml 
    ```
  * Step 3: Install Operator
    ```
    kubectl create -f deploy/install
    ```
  * Step 4: Creating Cluster
    * We have to create the ActiveMQArtemis Object。It included broker(1)。
    * The ActiveMQArtemis Object had to creating the accessor (2)[^1]。
    * We also need enable web console (3)。
      ```yaml
      apiVersion: broker.amq.io/v1beta1
      kind: ActiveMQArtemis
      metadata:
        name: ex-aao
      spec:
        env:
          - name: TZ
            value: 'Asia/Taipei'
        deploymentPlan:
          enableMetricsPlugin: true
          size: 3 # (1)
          image: placeholder
          messageMigration: true
          resources:
            limits:
              cpu: "500m"
              memory: "1024Mi"
            requests:
              cpu: "250m"
              memory: "512Mi"
        acceptors: # (2)
          - name: amqp
            protocols: amqp
            port: 5672
            connectionsAllowed: 5
          - name: all-acceptors
            protocols: all
            port: 61616
            connectionsAllowed: 100
        console: # (3)
          expose: true
      ```   
       > Refer: [official document](https://github.com/artemiscloud/activemq-artemis-operator/blob/main/docs/getting-started/quick-start.md#draining-messages-on-scale-down)
    * Once ``ActiveMQArtemis`` was created . Each broker's accessor web console  were created .
      ```shell
      $ kubectl get svc
      NAME                  TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)              AGE
      ex-aao-amqp-0-svc     ClusterIP   10.102.137.85   <none>        5672/TCP             5m40s
      ex-aao-amqp-1-svc     ClusterIP   10.107.188.11   <none>        5672/TCP             5m40s
      ex-aao-amqp-2-svc     ClusterIP   10.102.177.8    <none>        5672/TCP             5m40s
      ex-aao-hdls-svc       ClusterIP   None            <none>        8161/TCP,61616/TCP   5m40s
      ex-aao-ping-svc       ClusterIP   None            <none>        8888/TCP             5m40s
      ex-aao-wconsj-0-svc   ClusterIP   10.101.88.50    <none>        8161/TCP             5m40s
      ex-aao-wconsj-1-svc   ClusterIP   10.102.68.243   <none>        8161/TCP             5m40s
      ex-aao-wconsj-2-svc   ClusterIP   10.103.56.48    <none>        8161/TCP             5m40s
      kubernetes            ClusterIP   10.96.0.1       <none>        443/TCP              12m
      ```
       > If you use minikube , you discover :
      ```shell
      $ kubectl get ing
      NAME                      CLASS    HOSTS   ADDRESS   PORTS   AGE
      ex-aao-wconsj-0-svc-ing   <none>   *                 80      6m6s
      ex-aao-wconsj-1-svc-ing   <none>   *                 80      6m6s
      ex-aao-wconsj-2-svc-ing   <none>   *                 80      6m6s

      $ kubectl edit ing ex-aao-wconsj-0-svc-ing
      ingress.networking.k8s.io/ex-aao-wconsj-0-svc-ing edited

      $ kubectl get ing
      NAME                      CLASS    HOSTS              ADDRESS   PORTS   AGE
      ex-aao-wconsj-0-svc-ing   <none>   one.activemq.com             80      14m
      ex-aao-wconsj-1-svc-ing   <none>   *                            80      14m
      ex-aao-wconsj-2-svc-ing   <none>   *                            80      14m

      $ kubectl describe ing  ex-aao-wconsj-0-svc-ing
      Name:             ex-aao-wconsj-0-svc-ing
      Labels:           ActiveMQArtemis=ex-aao
                        application=ex-aao-app
                        statefulset.kubernetes.io/pod-name=ex-aao-ss-0
      Namespace:        default
      Address:
      Ingress Class:    <none>
      Default backend:  <default>
      Rules:
        Host              Path  Backends
        ----              ----  --------
        one.activemq.com
                          /   ex-aao-wconsj-0-svc:wconsj-0 (172.17.0.6:8161)
      Annotations:        <none>
      Events:             <none>


      $ minikube addons enable ingress
      * ingress is an addon maintained by Kubernetes. For any concerns contact minikube on GitHub.
      You can view the list of minikube maintainers at: https://github.com/kubernetes/minikube/blob/master/OWNERS
        - Using image k8s.gcr.io/ingress-nginx/controller:v1.2.1
        - Using image k8s.gcr.io/ingress-nginx/kube-webhook-certgen:v1.1.1
        - Using image k8s.gcr.io/ingress-nginx/kube-webhook-certgen:v1.1.1
      * Verifying ingress addon...
      * The 'ingress' addon is enabled

      $ minikube addons enable ingress-dns
      * ingress-dns is an addon maintained by Google. For any concerns contact minikube on GitHub.
      You can view the list of minikube maintainers at: https://github.com/kubernetes/minikube/blob/master/OWNERS
        - Using image gcr.io/k8s-minikube/minikube-ingress-dns:0.0.2
      * The 'ingress-dns' addon is enabled

      $ kubectl get ing
      NAME                      CLASS    HOSTS              ADDRESS          PORTS   AGE
      ex-aao-wconsj-0-svc-ing   <none>   one.activemq.com   192.168.59.142   80      22m
      ex-aao-wconsj-1-svc-ing   <none>   *                  192.168.59.142   80      22m
      ex-aao-wconsj-2-svc-ing   <none>   *                  192.168.59.142   80      22m

      ```
  * Step 5: Setting external accessing path
    * If using ingress controller , we can write the mapping in ``/etc/hosts``(linux) or ``C:\Windows\System32\drivers\etc\hosts ``(windows) : 
      ```
      192.168.59.142    one.activemq.com two.activemq.com three.activemq.com
      ```
    * we can use the  URL http://one.activemq.com/console  
  * Ste 6: Creating Queue    
      ```
      apiVersion: broker.amq.io/v1beta1
      kind: ActiveMQArtemisAddress
      metadata:
        name: address-1
      spec:
        addressName: address-1
        queueName: test-1
        routingType: anycast
      ```
  * Step 7: Creating High Availability Services.The below example demonstrated that each broker work through the service to loadbalance.
            Official's Service belong ``StatefulSet`` , its effect is ``session-sticky`` .
    > slb.yaml
      ```yaml
      kind: Service
      apiVersion: v1
      metadata:
        name: ex-aao-lb
      spec:
        ports:
          - name: amqp
            protocol: TCP
            port: 5672
          - name: all-acceptors
            protocol: TCP
            port: 61616
        type: ClusterIP
        selector:
          application: ex-aao-app
      ```
# Other Reference
* ActiveMQ Artemis Operator
  *  [Community Documentation](https://artemiscloud.io/docs/tutorials/using_operator/)
     * [Using JMS or Jakarta Messaging](https://activemq.apache.org/components/artemis/documentation/latest/using-jms.html)
  *  [Redhat Documentation - Chapter 4. Configuring Operator-based broker deployments](https://access.redhat.com/documentation/en-us/red_hat_amq/2020.q4/html-single/deploying_amq_broker_on_openshift/index#assembly-br-configuring-operator-based-deployments_broker-ocp)
* Use cases: 
  *  AMQP: [ActiveMQ Artemis with Spring Boot on Kubernetes](https://piotrminkowski.com/2022/07/26/activemq-artemis-with-spring-boot-on-kubernetes/)
  *  AMQP: [Getting started with JMS and ActiveMQ on Kubernetes](https://github.com/ssorj/hello-world-jms-kubernetes)
     * [DevNation talk video](https://www.youtube.com/watch?v=mkqVxWZfGfI)
     * [DevNation talk slides](https://docs.google.com/presentation/d/1kOsWwLcJWZGoCF8O_NPUB0jkAre9LMhE2VETnafxcMw/edit?usp=sharing)
     * [Igor Brodewicz's Tech Blog - Running ActiveMQ Artemis on Kubernetes step-by-step ](https://brodewicz.tech/2020/05/running-activemq-artemis-on-kubernetes-step-by-step)

[^1]: https://access.redhat.com/documentation/it-it/red_hat_amq_broker/7.10/pdf/deploying_amq_broker_on_openshift/red_hat_amq_broker-7.10-deploying_amq_broker_on_openshift-en-us.pdf