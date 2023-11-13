def call(Map pipelineParams){
def projectName = pipelineParams.ecrRepoName
pipeline {
 agent any
  environment {
    registry = "sahaja/$projectName"
    registryCredential = 'dockerhub_credentials'
    dockerImage = ''
  }
  tools
   {
    maven "maven3"
   }
  stages {
     stage('mavenbuild'){
	   steps{
	    sh 'mvn package'
	   }
	   }
    stage('Building image') {
      steps{
        script {
          dockerImage = docker.build registry + ":$BUILD_NUMBER"
        }
      }
    }
    stage('push image') {
      steps{
        script {
          docker.withRegistry( '', registryCredential ) {
            dockerImage.push()
          }
        }
      }
    }
    stage('Remove old docker image') {
      steps{
        sh "docker rmi $registry:$BUILD_NUMBER"
      }
    }
  }
 }
}
