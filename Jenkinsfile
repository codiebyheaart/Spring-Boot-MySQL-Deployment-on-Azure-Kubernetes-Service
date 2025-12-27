pipeline {
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
