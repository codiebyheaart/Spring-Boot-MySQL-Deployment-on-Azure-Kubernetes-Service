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

When you’re ready, simply say:

