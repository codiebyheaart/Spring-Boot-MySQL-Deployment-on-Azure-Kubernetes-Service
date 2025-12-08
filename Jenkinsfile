pipeline {
  agent any

  environment {
      IMAGE_NAME = "pankaj8900/springboot-app"
      TAG = "${BUILD_NUMBER}"
  }

  stages {

    stage('Checkout Code from GitHub') {
      steps {
        git branch: 'main',
            url: 'https://github.com/codiebyheaart/Spring-Boot-MySQL-Deployment-on-Azure-Kubernetes-Service.git'
      }
    }

    stage('Compile & Package') {
       steps {
          sh 'chmod +x gradlew'
          sh './gradlew build -x test'
      }
    }

    stage('Run Unit Test (Optional)') {
      steps {
        sh './gradlew test'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh "docker build -t $IMAGE_NAME:$TAG ."
      }
    }

    stage('Push to DockerHub') {
      steps {
        withCredentials([usernamePassword(
            credentialsId: 'dockerhub',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
            sh "docker push $IMAGE_NAME:$TAG"
        }
      }
    }

    stage('Archive Artifacts') {
      steps {
        archiveArtifacts artifacts: '**/*.jar'
      }
    }
  }

  post {
    success {
      echo "✅ Build SUCCESS - Docker Image pushed!"
    }
    failure {
      echo "❌ Build FAILED - Check logs"
    }
  }
}
