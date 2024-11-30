pipeline {
    agent {
        label 'docker'
    }
    stages {
        //Configuración para envío de correo
        stage('PreBuild-Email') {
           steps {
               script {
                   def mailRecipients = 'ibt.ron27@gmail.com'
                   def jobName = currentBuild.fullDisplayName
                   //emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                   emailext body: '''Estimad@:''',
                       mimeTye: 'text/html',
                       subject: "[Jenkins] se ha inicializado ${jobName}",
                       to: "${mailRecipients}",
                       replyTo: "${mailRecipients}",
                       recipientProviders: [[$class: 'CulpritsRecipientProvider']]
                    }
                    }
                }
        stage('Build') {
            steps {
                echo 'Building stage!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps {
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('API tests') {
            steps {
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('EndtoEnd tests') {
            steps {
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }

    }
    post {
        always {
            junit 'results/*_result.xml'
        }
        success {
            /*Verificar nombre del proyecto, número y URL de construcción para validar la ejecución exitosa del pipeline*/
            echo   "emailext body: Compruebe la salida de la consola en:${env.BUILD_URL} para ver los resultados \n para: rcardenas@cntcloud.com \n asunto: La construcción falló en Jenkins: ${currentBuild.fullDisplayName}"
        }
        /*Este código se podría usar cuando falle el pipeline/
        /*failure {
            emailext body: 'Check console output at $BUILD_URL to view the results. \n\n ${CHANGES} \n\n -------------------------------------------------- \n${BUILD_LOG, maxLines=100, escapeHtml=false}', 
                    to: "${EMAIL_TO}", 
                    subject: 'La Construcción Fallo en  Jenkins: $PROJECT_NAME - #$BUILD_NUMBER'
        }*/
    }
}
