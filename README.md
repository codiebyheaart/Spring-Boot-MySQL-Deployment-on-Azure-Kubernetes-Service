# Spring Boot + MySQL Deployment on Azure Kubernetes Service

## Overview
This project demonstrates full cloud-native deployment using:
Spring Boot, MySQL, Docker, GitHub Actions, and Azure Kubernetes Service (AKS).

The application is containerized and deployed using Kubernetes YAML configuration.

---

## Architecture

    GitHub
      |
      v
GitHub Actions (Build Image)
      |
      v
DockerHub Registry
      |
      v
Azure Kubernetes Service (AKS)
      |
      |---- Spring Boot Application (Deployment + Service)
      |
      |---- MySQL Database (Deployment + Service)
      |
      v
Browser / API Client

---

## Why GitHub Actions for Docker Build?

Azure Cloud Shell does not allow Docker builds.
Admin permission is required for Azure ACR.
GitHub Actions provides Docker build functionality without laptop dependency.

---


## Kubernetes Files Used

Each service requires two YAML files:

### Application
deployment.yaml
service.yaml

### Database
mysql-deployment.yaml
mysql-service.yaml

These files define:
- Container image
- Environment variables
- Ports
- Networking
- Storage

---

## Commands Used

### Connect to Kubernetes Cluster
```bash
az aks get-credentials --resource-group aks-demo-rg --name demo-aks-cluster


# Apply Kubernetes Configuration
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Check Pod Status
kubectl get pods

# Check Application Logs
kubectl logs -f deployment/springboot-app

# Restart Deployment After New Image
kubectl rollout restart deployment springboot-app

# Check External IP
kubectl get svc

# Deployment Flow
Step 1: Push Code to GitHub

Triggers GitHub Actions.

Step 2: GitHub Builds Docker Image

Image is pushed to DockerHub.

Step 3: Kubernetes Pulls Image

Deployment restart triggers new version.

Step 4: Application Runs

Service LoadBalancer exposes application publicly.

# Concepts Learned
CI/CD with GitHub Actions
Docker containerization
Kubernetes deployments
Secrets usage
Database connection inside cluster

# Rolling deployment

# Auto image pull
Legacy vs Kubernetes
Legacy	Kubernetes
Deploy JAR	Deploy image
Manual install	Automated
OS dependency	Containerized
Single server	Distributed system



---

## WHAT YOU HAVE ACHIEVED

You now know:

* Docker  
* Kubernetes  
* CI/CD  
* Deployment automation  
* Cloud-native workflow  
* Production architecture

---

### NEXT TASK CONFIRMATION

Next we continue:

➡ Jenkins installation in AKS  
➡ GitHub webhook → Jenkins integrate  
➡ Pipeline writing  
➡ Helm charts  
➡ Environments (qa/prod)

---

<artifact identifier="cicd-readme" type="text/markdown" title="Complete CI/CD Setup Guide - README.md">
# Spring Boot MySQL Deployment on Azure Kubernetes Service (AKS)
Complete CI/CD pipeline for deploying a Spring Boot application with MySQL database on Azure Kubernetes Service using Jenkins.
🏗️ Architecture

Application: Spring Boot + MySQL (NFT Management System)
Container Registry: Azure Container Registry (ACR)
Orchestration: Azure Kubernetes Service (AKS)
CI/CD: Jenkins on Azure VM
Database: MySQL 8.0 running in Kubernetes


📋 Prerequisites

Azure subscription
Azure CLI installed
kubectl installed
Docker installed
Git installed

Phase 1: Setup Azure Resources
1.1 Login to Azure
bashaz login
az account set --subscription "<YOUR-SUBSCRIPTION-ID>"
1.2 Verify Existing AKS Cluster
bash# Get AKS credentials
az aks get-credentials --resource-group pankaj-aks-group --name pankaj-aks --overwrite-existing

# Verify connection
kubectl get nodes
kubectl cluster-info
1.3 Verify Azure Container Registry
bash# List ACR
az acr list --output table

# Enable admin access
az acr update -n nindia --admin-enabled true

# Get ACR credentials
az acr credential show --name nindia

# Attach ACR to AKS
az aks update -n pankaj-aks -g pankaj-aks-group --attach-acr nindia

# Verify ACR integration
az aks check-acr -n pankaj-aks -g pankaj-aks-group --acr nindia.azurecr.io

Phase 2: Deploy MySQL Database
2.1 Create Namespace
bashkubectl create namespace myapp
kubectl get namespaces
2.2 Create Kubernetes Resources
Create directory for manifests:
bashmkdir -p ~/k8s-manifests
cd ~/k8s-manifests
Create mysql-secret.yaml:
bashcat > mysql-secret.yaml << 'EOF'
apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
  namespace: myapp
type: Opaque
stringData:
  MYSQL_ROOT_PASSWORD: "Root@123"
  MYSQL_DATABASE: "nft"
  MYSQL_USER: "myappuser"
  MYSQL_PASSWORD: "MyApp@123"
EOF
Create mysql-pvc.yaml:
bashcat > mysql-pvc.yaml << 'EOF'
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pvc
  namespace: myapp
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: managed-csi
EOF

Create mysql-deployment.yaml:
bashcat > mysql-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
  namespace: myapp
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        ports:
        - containerPort: 3306
        env:
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_ROOT_PASSWORD
        - name: MYSQL_DATABASE
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_DATABASE
        - name: MYSQL_USER
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_USER
        - name: MYSQL_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: MYSQL_PASSWORD
        volumeMounts:
        - name: mysql-storage
          mountPath: /var/lib/mysql
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
      volumes:
      - name: mysql-storage
        persistentVolumeClaim:
          claimName: mysql-pvc
EOF
Create mysql-service.yaml:
bashcat > mysql-service.yaml << 'EOF'
apiVersion: v1
kind: Service
metadata:
  name: mysql
  namespace: myapp
spec:
  selector:
    app: mysql
  ports:
  - protocol: TCP
    port: 3306
    targetPort: 3306
  type: ClusterIP
EOF
2.3 Deploy MySQL
bash# Apply all resources
kubectl apply -f mysql-secret.yaml
kubectl apply -f mysql-pvc.yaml
kubectl apply -f mysql-deployment.yaml
kubectl apply -f mysql-service.yaml

# Wait for MySQL to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n myapp --timeout=300s

# Verify deployment
kubectl get all -n myapp
kubectl get pvc -n myapp
2.4 Import Database Dump
bash# Clone database dump repository
git clone https://github.com/codiebyheaart/myapp-db-dump.git
cd myapp-db-dump

# Get MySQL pod name
MYSQL_POD=$(kubectl get pod -n myapp -l app=mysql -o jsonpath='{.items[0].metadata.name}')

# Import SQL dump
kubectl exec -i $MYSQL_POD -n myapp -- mysql -uroot -pRoot@123 < mydatabase.sql

# Verify tables
kubectl exec -it $MYSQL_POD -n myapp -- mysql -uroot -pRoot@123 -e "USE nft; SHOW TABLES;"

 Phase 3: Setup Jenkins VM
3.1 Create Jenkins VM
Via Azure Portal:

Navigate to Virtual Machines → Create
Configure:

Resource Group: pankaj-aks-group
VM Name: jenkins-vm
Region: East US
Image: Ubuntu Server 22.04 LTS
Size: Standard_DC1s_v3 or available size
Authentication: SSH public key
Inbound Ports: 22 (SSH), 80 (HTTP), 8080 (Jenkins)



Via Azure CLI:
bashaz vm create \
  --resource-group pankaj-aks-group \
  --name jenkins-vm \
  --location eastus \
  --image Ubuntu2204 \
  --size Standard_DC1s_v3 \
  --admin-username azureuser \
  --generate-ssh-keys \
  --public-ip-sku Standard

# Open port 8080 for Jenkins
az vm open-port \
  --resource-group pankaj-aks-group \
  --name jenkins-vm \
  --port 8080 \
  --priority 1010

# Get Public IP
az vm show \
  --resource-group pankaj-aks-group \
  --name jenkins-vm \
  --show-details \
  --query publicIps \
  --output tsv
3.2 Connect to Jenkins VM
bash# SSH to VM
ssh -i <path-to-key> azureuser@<VM-PUBLIC-IP>
3.3 Install Java
bashsudo apt update && sudo apt upgrade -y
sudo apt install -y openjdk-17-jdk
java -version
3.4 Install Jenkins
bash# Add Jenkins repository
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Install Jenkins
sudo apt update
sudo apt install -y jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins
sudo systemctl status jenkins

# Get initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
3.5 Install Docker
bash# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add users to docker group
sudo usermod -aG docker $USER
sudo usermod -aG docker jenkins

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Restart Jenkins
sudo systemctl restart jenkins

# Verify
docker --version
3.6 Install kubectl
bash# Download kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"

# Install
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# Verify
kubectl version --client
3.7 Install Azure CLI
bash# Install Azure CLI
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Verify
az --version
3.8 Install Gradle
bashsudo apt install -y gradle
gradle --version
3.9 Configure AKS Access
bash# Login to Azure
az login

# Set subscription
az account set --subscription "<YOUR-SUBSCRIPTION-ID>"

# Get AKS credentials
az aks get-credentials --resource-group pankaj-aks-group --name pankaj-aks --overwrite-existing

# Test connection
kubectl get nodes
kubectl get pods -n myapp

# Copy kubeconfig for Jenkins user
sudo mkdir -p /var/lib/jenkins/.kube
sudo cp ~/.kube/config /var/lib/jenkins/.kube/config
sudo chown -R jenkins:jenkins /var/lib/jenkins/.kube

# Verify Jenkins can access Kubernetes
sudo su - jenkins -s /bin/bash -c "kubectl get nodes"
3.10 Configure ACR Access
bash# Login to ACR
az acr login --name nindia

# Test Docker push
docker pull hello-world
docker tag hello-world nindia.azurecr.io/hello-world:test
docker push nindia.azurecr.io/hello-world:test


Phase 4: Configure Jenkins
4.1 Initial Setup

Open browser: http://<JENKINS-VM-IP>:8080
Enter initial admin password
Install suggested plugins
Create admin user

4.2 Install Additional Plugins
Manage Jenkins → Plugins → Available plugins
Install these plugins:

Docker Pipeline
Kubernetes CLI
Git
Maven Integration
Pipeline
Blue Ocean (optional, for better visualization)
Timestamper
AnsiColor

4.3 Add ACR Credentials

Dashboard → Manage Jenkins → Credentials
System → Global credentials
Add Credentials:

Kind: Username with password
Username: nindia
Password: <ACR-PASSWORD>
ID: acr-credentials
Description: Azure Container Registry

Phase 5: Prepare Application Repository
5.1 Update application.properties
File: src/main/resources/application.properties
propertiesserver.port=8080

# MySQL Configuration
spring.datasource.url=jdbc:mysql://mysql:3306/nft
spring.datasource.username=root
spring.datasource.password=Root@123

# JPA & Hibernate Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
5.2 Create Kubernetes Deployment Manifest
Create directory: k8s/
File: k8s/deployment.yaml
yamlapiVersion: apps/v1
kind: Deployment
metadata:
  name: springboot-app
  namespace: myapp
spec:
  replicas: 2
  selector:
    matchLabels:
      app: springboot-app
  template:
    metadata:
      labels:
        app: springboot-app
    spec:
      containers:
      - name: springboot-app
        image: nindia.azurecr.io/springboot-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:mysql://mysql:3306/nft"
        - name: SPRING_DATASOURCE_USERNAME
          value: "root"
        - name: SPRING_DATASOURCE_PASSWORD
          value: "Root@123"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: springboot-app
  namespace: myapp
spec:
  type: LoadBalancer
  selector:
    app: springboot-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
5.3 Update Jenkinsfile
File: Jenkinsfile (root directory)
groovypipeline {
    agent any
    
    environment {
        ACR_REGISTRY = 'nindia.azurecr.io'
        IMAGE_NAME = 'springboot-app'
        IMAGE_TAG = "${BUILD_NUMBER}"
        KUBE_NAMESPACE = 'myapp'
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/codiebyheaart/Spring-Boot-MySQL-Deployment-on-Azure-Kubernetes-Service.git'
            }
        }
        
        stage('Build with Gradle') {
            steps {
                sh '''
                    chmod +x gradlew
                    ./gradlew clean build -x test
                '''
            }
        }
        
        stage('Run Tests') {
            steps {
                sh './gradlew test'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${ACR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} .
                    docker tag ${ACR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} ${ACR_REGISTRY}/${IMAGE_NAME}:latest
                """
            }
        }
        
        stage('Push to ACR') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'acr-credentials',
                    usernameVariable: 'ACR_USER',
                    passwordVariable: 'ACR_PASS'
                )]) {
                    sh """
                        echo \${ACR_PASS} | docker login ${ACR_REGISTRY} -u \${ACR_USER} --password-stdin
                        docker push ${ACR_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                        docker push ${ACR_REGISTRY}/${IMAGE_NAME}:latest
                    """
                }
            }
        }
        
        stage('Deploy to AKS') {
            steps {
                sh """
                    kubectl apply -f k8s/deployment.yaml
                    kubectl rollout status deployment/springboot-app -n ${KUBE_NAMESPACE} --timeout=5m
                    kubectl get pods -n ${KUBE_NAMESPACE}
                """
            }
        }
        
        stage('Get Application URL') {
            steps {
                sh """
                    echo "Waiting for LoadBalancer IP..."
                    kubectl get svc springboot-app -n ${KUBE_NAMESPACE}
                """
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline SUCCESS! Application deployed to AKS'
            sh 'kubectl get svc springboot-app -n ${KUBE_NAMESPACE}'
        }
        failure {
            echo '❌ Pipeline FAILED! Check logs above'
        }
        always {
            sh "docker logout ${ACR_REGISTRY} || true"
        }
    }
}

🚀 Phase 6: Create and Run Jenkins Pipeline
6.1 Create Pipeline Job

Jenkins Dashboard → New Item
Item name: springboot-aks-cicd
Type: Pipeline
OK

6.2 Configure Pipeline
Pipeline Section:

Definition: Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/codiebyheaart/Spring-Boot-MySQL-Deployment-on-Azure-Kubernetes-Service.git
Branch Specifier: */main
Script Path: Jenkinsfile

Save
6.3 Run Pipeline

Click Build Now
Monitor build progress in Console Output
Wait for all stages to complete (5-10 minutes)

Phase 7: Verify Deployment
7.1 Check Pipeline Status
bash# Check pods
kubectl get pods -n myapp

# Check services
kubectl get svc -n myapp

# Check deployments
kubectl get deployments -n myapp
7.2 Check Application Logs
bash# Get pod name
kubectl get pods -n myapp -l app=springboot-app

# View logs
kubectl logs -l app=springboot-app -n myapp --tail=50

# Follow logs
kubectl logs -f <pod-name> -n myapp
7.3 Access Application
If LoadBalancer IP is assigned:
bashkubectl get svc springboot-app -n myapp
# Access: http://<EXTERNAL-IP>
If LoadBalancer is pending (Azure free tier IP limit):
bash# Change to NodePort
kubectl patch svc springboot-app -n myapp -p '{"spec":{"type":"NodePort"}}'

# Get NodePort
kubectl get svc springboot-app -n myapp

# Access: http://<ANY-PUBLIC-IP>:<NodePort>
7.4 Test Application
bash# Test endpoint
curl http://<APPLICATION-URL>

# Check database connectivity
kubectl exec -it <springboot-pod> -n myapp -- curl localhost:8080/actuator/health

🔍 Troubleshooting
Check Pod Status
bashkubectl get pods -n myapp
kubectl describe pod <pod-name> -n myapp
kubectl logs <pod-name> -n myapp
Check Service Status
bashkubectl get svc -n myapp
kubectl describe svc springboot-app -n myapp
Check Events
bashkubectl get events -n myapp --sort-by='.lastTimestamp'
Restart Deployment
bashkubectl rollout restart deployment/springboot-app -n myapp
kubectl rollout status deployment/springboot-app -n myapp
Check ACR Images
bashaz acr repository list --name nindia --output table
az acr repository show-tags --name nindia --repository springboot-app --output table
Access MySQL
bashkubectl exec -it <mysql-pod> -n myapp -- mysql -uroot -pRoot@123 -e "USE nft; SHOW TABLES;"
```

---

## 📊 Architecture Diagram
```
┌─────────────────┐
│   GitHub Repo   │
└────────┬────────┘
         │ Webhook/Manual Trigger
         ▼
┌─────────────────┐
│  Jenkins VM     │
│  (Build Agent)  │
└────────┬────────┘
         │
         ├─► Build with Gradle
         ├─► Run Tests
         ├─► Build Docker Image
         ▼
┌─────────────────┐
│  Azure ACR      │
│  (nindia)       │
└────────┬────────┘
         │ Pull Image
         ▼
┌─────────────────────────────┐
│  Azure Kubernetes Service   │
│  ┌──────────┐  ┌──────────┐│
│  │  MySQL   │  │SpringBoot││
│  │  Pod     │◄─┤   Pods   ││
│  └──────────┘  └──────────┘│
└─────────────────────────────┘
         │
         ▼
   End Users (via LoadBalancer/NodePort)

📝 Key Configuration Files
FilePurposeJenkinsfileCI/CD pipeline definitionk8s/deployment.yamlKubernetes deployment and serviceDockerfileContainer image build instructionsapplication.propertiesSpring Boot configurationbuild.gradleGradle build configuration

🎯 CI/CD Pipeline Stages

Checkout Code - Clone from GitHub
Build with Gradle - Compile and package application
Run Tests - Execute unit tests
Build Docker Image - Create container image
Push to ACR - Upload to Azure Container Registry
Deploy to AKS - Deploy to Kubernetes cluster
Get Application URL - Display access endpoint


🔒 Security Best Practices

✅ Use Azure Key Vault for secrets (production)
✅ Use Kubernetes Secrets for sensitive data
✅ Enable RBAC on AKS
✅ Use private container registry (ACR)
✅ Implement network policies
✅ Use managed identities instead of service principals
✅ Enable Azure Policy for AKS
✅ Regular security scans of container images


💰 Cost Optimization

Use Azure Free Tier where available
Use B-series VMs for non-production (burstable)
Enable AKS cluster autoscaling
Use spot instances for dev/test workloads
Delete resources when not in use
Use Azure Cost Management for monitoring


📚 Technologies Used
TechnologyVersionPurposeSpring Boot3.xApplication
 FrameworkMySQL8.0DatabaseAzure AKS1.33.5Kubernetes
OrchestrationAzure ACR-Container
 RegistryJenkinsLatestCI/CDDockerLatestContainerizationGradleLatestBuild
 ToolUbuntu22.04Jenkins VM OS

# Check deployment status
kubectl get all -n myapp

# View application logs
kubectl logs -l app=springboot-app -n myapp --tail=50

# Get service URL
kubectl get svc springboot-app -n myapp

# Scale application
kubectl scale deployment springboot-app --replicas=3 -n myapp

# Update to NodePort if LoadBalancer is pending
kubectl patch svc springboot-app -n myapp -p '{"spec":{"type":"NodePort"}}'

















