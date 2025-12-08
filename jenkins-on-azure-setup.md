# Jenkins CI for Spring Boot + Gradle on Azure VM (with Docker)

This document explains how to set up **Jenkins on an Azure Linux VM** to build a **Spring Boot + Gradle** project, build a **Docker image**, and push it to **DockerHub**.

Repo used:  
`https://github.com/codiebyheaart/Spring-Boot-MySQL-Deployment-on-Azure-Kubernetes-Service.git`  

Docker image used:  
`<dockerusername>/springboot-app`

---

## 0. Prerequisites

- Azure subscription with credits
- DockerHub account
- GitHub repo with:
  - `build.gradle`
  - `gradlew`, `gradlew.bat`
  - `Dockerfile`
  - `Jenkinsfile` (pipeline script)

---

## 1. Create Azure VM for Jenkins

### 1.1. Create Resource Group (optional, via Portal)

Resource Group name (example): `demobypankaj`

### 1.2. Create VM (Azure Portal)

- **Resource group**: `demobypankaj`
- **VM name**: `myVm` (or `jenkins-vm`)
- **Region**: `Central India` (or any region where `B1s` is available)
- **Availability options**: `No infrastructure redundancy required`
- **Image**: `Ubuntu Server 22.04 LTS - x64 Gen2`
- **Size**: `Standard_B1s`
- **Authentication type**: `Password`
  - Username: `azureuser`
  - Password: `<your-strong-password>`
- **Inbound ports**:
  - Allow selected ports:
    - `SSH (22)`
    - Add custom port `8080` (for Jenkins)

Click **Review + Create** → **Create**.

---

## 2. Configure Networking (NSG Rule for 8080)

After VM is created:

1. Go to **Virtual Machines → myVm → Networking**.
2. Under **Inbound port rules**, add:
   - Source: `Any`
   - Source port ranges: `*`
   - Destination: `Any`
   - Destination port ranges: `8080`
   - Protocol: `TCP`
   - Action: `Allow`
   - Priority: e.g. `1000`
   - Name: `Allow-Jenkins`

---

## 3. SSH into the VM

Get the Public IP from VM Overview (example):

```text
ip

# local terminal:
ssh azureuser@<public ip>

##  Install Java (required by Jenkins)

sudo apt update
sudo apt install openjdk-17-jdk -y
java -version

## Install Jenkins
curl -fsSL https://pkg.jenkins.io/debian/jenkins.io-2023.key | sudo tee \
/usr/share/keyrings/jenkins-keyring.asc > /dev/null

echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
https://pkg.jenkins.io/debian binary/ | sudo tee \
/etc/apt/sources.list.d/jenkins.list > /dev/null


## Install Jenkins:
sudo apt update
sudo apt install jenkins -y

## Start & enable:
sudo systemctl start jenkins
sudo systemctl enable jenkins
sudo systemctl status jenkins\

## Install Docker on the VM
sudo apt update
sudo apt install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker

# Add users to docker group
for linux user
sudo usermod -aG docker azureuser

Restart Docker and Jenkins:
sudo systemctl restart docker
sudo systemctl restart jenkins
exit

docker ps


sudo chmod 666 /var/run/docker.sock
docker ps


. Install Useful Jenkins Plugins

From Jenkins:

Manage Jenkins → Plugins → Available

Install:

Git

Pipeline

Docker Pipeline

Credentials Binding

Pipeline Stage View

Blue Ocean (optional, for nice UI)

Restart Jenkins if required:

sudo systemctl restart jenkins
