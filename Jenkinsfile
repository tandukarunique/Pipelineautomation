pipeline {
    agent any

    parameters {
        choice(name: 'BROWSER', choices: ['chrome'], description: 'Browser to run tests on')
        booleanParam(name: 'HEADLESS', defaultValue: false, description: 'Run browser in headless mode')
        choice(name: 'TEST_SUITE', choices: ['all', 'smoke'], description: 'Test suite to run')
        string(name: 'CLIENT_COUNT', defaultValue: '5', description: 'Number of clients to create')
        string(name: 'TICKET_COUNT', defaultValue: '10', description: 'Number of tickets to create')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'Code checked out'
            }
        }

        stage('Clean') {
            steps {
                bat 'mvn clean'
                echo 'Clean completed'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    if (params.TEST_SUITE == 'smoke') {
                        bat 'mvn test -Dsuite=smoke'
                    } else {
                        bat """
                            mvn test ^
                                -Dbrowser=${params.BROWSER} ^
                                -Dheadless=${params.HEADLESS} ^
                                -DclientCount=${params.CLIENT_COUNT} ^
                                -DticketCount=${params.TICKET_COUNT}
                        """
                    }
                }
            }
        }

        stage('Generate Reports') {
            steps {
                junit 'test-output/junitreports/*.xml'
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,   // ← added
                    keepAll: true,                  // ← added
                    reportDir: 'test-output',
                    reportFiles: 'index.html',
                    reportName: 'TestNG Report'
                ])
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            bat '''
                taskkill /F /IM chrome.exe 2>nul
                taskkill /F /IM chromedriver.exe 2>nul
                exit 0
            '''
        }
    }
}