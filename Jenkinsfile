pipeline {
  agent any

  environment {
      IMAGE_NAME = "yourdockerhubusername/springboot-app"
      TAG = "${BUILD_NUMBER}"
  }

  stages {

    stage('Checkout Code from GitHub') {
      steps {
        git branch: 'main',
            url: 'https://github.com/yourusername/yourrepo.git'
      }
    }

    stage('Compile & Package') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }

    stage('Run Unit Test (Optional)') {
      steps {
        sh 'mvn test'
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
