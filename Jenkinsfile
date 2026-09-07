pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Image Tag') {
            steps {
                script {
                    env.IMAGE_TAG = sh(
                        script: 'git rev-parse --short=7 HEAD',
                        returnStdout: true
                    ).trim()

                    echo "Git Commit SHA: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('Docker Check') {
            steps {
                sh 'docker --version'
            }
        }

        stage('Build auth') {
            steps {
                sh 'docker build -t rimo-auth:${IMAGE_TAG} ./auth'
            }
        }

        stage('Build data-api') {
            steps {
                sh 'docker build -t rimo-data-api:${IMAGE_TAG} ./data-api'
            }
        }

        stage('Build member-api') {
            steps {
                sh 'docker build -t rimo-member-api:${IMAGE_TAG} ./member-api'
            }
        }

        stage('Build route-api') {
            steps {
                sh 'docker build -t rimo-route-api:${IMAGE_TAG} ./route-api'
            }
        }

        stage('Build tracking-api') {
            steps {
                sh 'docker build -t rimo-tracking-api:${IMAGE_TAG} ./tracking-api'
            }
        }

        stage('Verify Images') {
            steps {
                sh '''
                    docker image inspect rimo-auth:${IMAGE_TAG} > /dev/null
                    docker image inspect rimo-data-api:${IMAGE_TAG} > /dev/null
                    docker image inspect rimo-member-api:${IMAGE_TAG} > /dev/null
                    docker image inspect rimo-route-api:${IMAGE_TAG} > /dev/null
                    docker image inspect rimo-tracking-api:${IMAGE_TAG} > /dev/null

                    docker images --format "table {{.Repository}}\\t{{.Tag}}\\t{{.ID}}" | grep rimo-
                '''
            }
        }
    }

    post {
        success {
            echo "All RIMO backend Docker images built successfully. Tag: ${env.IMAGE_TAG}"
        }

        failure {
            echo 'Pipeline failed. Check the Jenkins console log.'
        }
    }
}
