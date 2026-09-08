pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        AWS_REGION    = 'ap-northeast-2'
        EKS_CLUSTER   = 'rimo-eks'
        K8S_NAMESPACE = 'default'

        AUTH_REPO     = 'rimo/auth-api'
        MEMBER_REPO   = 'rimo/member-api'
        TRACKING_REPO = 'rimo/tracking-api'
        ROUTE_REPO    = 'rimo/route-api'
        DATA_REPO     = 'rimo/data-api'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Tool Check') {
            steps {
                sh '''
                    git --version
                    docker --version
                    aws --version
                    kubectl version --client
                '''
            }
        }

        stage('AWS Info') {
            steps {
                script {
                    env.AWS_ACCOUNT_ID = sh(
                        script: 'aws sts get-caller-identity --query Account --output text',
                        returnStdout: true
                    ).trim()

                    env.ECR_REGISTRY =
                        "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_REGION}.amazonaws.com"

                    env.IMAGE_TAG = sh(
                        script: 'git rev-parse --short=7 HEAD',
                        returnStdout: true
                    ).trim()

                    echo "AWS Account: ${env.AWS_ACCOUNT_ID}"
                    echo "ECR Registry: ${env.ECR_REGISTRY}"
                    echo "Backend Git SHA: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('ECR Login') {
            steps {
                sh '''
                    aws ecr get-login-password \
                      --region "$AWS_REGION" \
                    | docker login \
                      --username AWS \
                      --password-stdin "$ECR_REGISTRY"
                '''
            }
        }

        stage('Build auth') {
            steps {
                sh '''
                    docker build \
                      -t "$ECR_REGISTRY/$AUTH_REPO:$IMAGE_TAG" \
                      ./auth
                '''
            }
        }

        stage('Build data-api') {
            steps {
                sh '''
                    docker build \
                      -t "$ECR_REGISTRY/$DATA_REPO:$IMAGE_TAG" \
                      ./data-api
                '''
            }
        }

        stage('Build member-api') {
            steps {
                sh '''
                    docker build \
                      -t "$ECR_REGISTRY/$MEMBER_REPO:$IMAGE_TAG" \
                      ./member-api
                '''
            }
        }

        stage('Build route-api') {
            steps {
                sh '''
                    docker build \
                      -t "$ECR_REGISTRY/$ROUTE_REPO:$IMAGE_TAG" \
                      ./route-api
                '''
            }
        }

        stage('Build tracking-api') {
            steps {
                sh '''
                    docker build \
                      -t "$ECR_REGISTRY/$TRACKING_REPO:$IMAGE_TAG" \
                      ./tracking-api
                '''
            }
        }

        stage('Verify Images') {
            steps {
                sh '''
                    docker image inspect \
                      "$ECR_REGISTRY/$AUTH_REPO:$IMAGE_TAG" > /dev/null

                    docker image inspect \
                      "$ECR_REGISTRY/$DATA_REPO:$IMAGE_TAG" > /dev/null

                    docker image inspect \
                      "$ECR_REGISTRY/$MEMBER_REPO:$IMAGE_TAG" > /dev/null

                    docker image inspect \
                      "$ECR_REGISTRY/$ROUTE_REPO:$IMAGE_TAG" > /dev/null

                    docker image inspect \
                      "$ECR_REGISTRY/$TRACKING_REPO:$IMAGE_TAG" > /dev/null

                    docker images \
                      --format "table {{.Repository}}\\t{{.Tag}}\\t{{.ID}}" \
                    | grep rimo/
                '''
            }
        }

        stage('Push Images to ECR') {
            steps {
                sh '''
                    docker push "$ECR_REGISTRY/$AUTH_REPO:$IMAGE_TAG"
                    docker push "$ECR_REGISTRY/$DATA_REPO:$IMAGE_TAG"
                    docker push "$ECR_REGISTRY/$MEMBER_REPO:$IMAGE_TAG"
                    docker push "$ECR_REGISTRY/$ROUTE_REPO:$IMAGE_TAG"
                    docker push "$ECR_REGISTRY/$TRACKING_REPO:$IMAGE_TAG"
                '''
            }
        }

        stage('Configure EKS') {
            steps {
                sh '''
                    aws eks update-kubeconfig \
                      --region "$AWS_REGION" \
                      --name "$EKS_CLUSTER"

                    kubectl get deployments \
                      -n "$K8S_NAMESPACE"
                '''
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                    kubectl set image deployment/auth-api \
                      auth-api="$ECR_REGISTRY/$AUTH_REPO:$IMAGE_TAG" \
                      -n "$K8S_NAMESPACE"

                    kubectl set image deployment/member-api \
                      member-api="$ECR_REGISTRY/$MEMBER_REPO:$IMAGE_TAG" \
                      -n "$K8S_NAMESPACE"

                    kubectl set image deployment/tracking-api \
                      tracking-api="$ECR_REGISTRY/$TRACKING_REPO:$IMAGE_TAG" \
                      -n "$K8S_NAMESPACE"

                    kubectl set image deployment/route-api \
                      route-api="$ECR_REGISTRY/$ROUTE_REPO:$IMAGE_TAG" \
                      -n "$K8S_NAMESPACE"

                    kubectl set image deployment/data-api \
                      data-api="$ECR_REGISTRY/$DATA_REPO:$IMAGE_TAG" \
                      -n "$K8S_NAMESPACE"
                '''
            }
        }

        stage('Rollout Check') {
            steps {
                sh '''
                    kubectl rollout status deployment/auth-api \
                      -n "$K8S_NAMESPACE" \
                      --timeout=180s

                    kubectl rollout status deployment/member-api \
                      -n "$K8S_NAMESPACE" \
                      --timeout=180s

                    kubectl rollout status deployment/tracking-api \
                      -n "$K8S_NAMESPACE" \
                      --timeout=180s

                    kubectl rollout status deployment/route-api \
                      -n "$K8S_NAMESPACE" \
                      --timeout=180s

                    kubectl rollout status deployment/data-api \
                      -n "$K8S_NAMESPACE" \
                      --timeout=180s
                '''
            }
        }
    }

    post {
        success {
            echo "RIMO backend CI/CD succeeded. Image tag: ${env.IMAGE_TAG}"
        }

        failure {
            echo 'RIMO backend CI/CD failed. Check the Jenkins console log.'
        }
    }
}
