pipeline {
    agent any

    tools{
    maven 'maven_deploy'
    }

    stages {

        stage('Build') {
            steps {
                sh 'mvn -DskipTests clean package'// One or more steps need to be included within the steps block.
            }
        }


        stage("Deploy app") {
            steps{
                 deploy adapters: [tomcat9(credentialsId: 'remote-tomcat10', path: '', url: 'http://35.227.146.153:8080/')], contextPath: null, war: '**/*.jar'
            }
        }
    }
}