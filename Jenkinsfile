pipeline {
    agent any
    
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser to run tests on')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
        choice(name: 'TEST_SUITE', choices: ['smoke', 'regression', 'stress', 'all'], description: 'Test suite to run')
        string(name: 'INVITATION_COUNT', defaultValue: '3', description: 'Number of invitations to send')
        string(name: 'CLIENT_COUNT', defaultValue: '5', description: 'Number of clients to create')
        string(name: 'TICKET_COUNT', defaultValue: '10', description: 'Number of tickets to create')
    }
    
    environment {
        CHROME_DRIVER_PATH = '/usr/bin/chromedriver'
        DISPLAY = ':99'  // For headless mode
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'Code checked out successfully'
            }
        }
        
        stage('Setup Environment') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            # Install Chrome for Linux agents if needed
                            if ! command -v google-chrome &> /dev/null; then
                                wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
                                sudo sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list'
                                sudo apt-get update
                                sudo apt-get install -y google-chrome-stable
                            fi
                        '''
                    }
                }
                echo 'Environment setup complete'
            }
        }
        
        stage('Maven Clean') {
            steps {
                sh 'mvn clean'
                echo 'Maven clean completed'
            }
        }
        
        stage('Run Tests') {
            steps {
                script {
                    def testngXml = 'src/test/resources/testng.xml'
                    
                    // Modify testng.xml based on parameters
                    if (params.TEST_SUITE == 'smoke') {
                        testngXml = 'src/test/resources/testng-smoke.xml'
                    } else if (params.TEST_SUITE == 'regression') {
                        testngXml = 'src/test/resources/testng-regression.xml'
                    } else if (params.TEST_SUITE == 'stress') {
                        testngXml = 'src/test/resources/testng-stress.xml'
                    }
                    
                    sh """
                        mvn test \
                            -Dbrowser=${params.BROWSER} \
                            -Dheadless=${params.HEADLESS} \
                            -DinvitationCount=${params.INVITATION_COUNT} \
                            -DclientCount=${params.CLIENT_COUNT} \
                            -DticketCount=${params.TICKET_COUNT} \
                            -Dsurefire.suiteXmlFiles=${testngXml}
                    """
                }
            }
        }
        
        stage('Generate Reports') {
            steps {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output',
                    reportFiles: 'emailable-report.html',
                    reportName: 'TestNG Report'
                ])
                
                // Publish JUnit results
                junit 'test-output/*.xml'
                
                echo 'Reports generated'
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                echo 'Artifacts archived'
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up...'
            // Clean up any remaining browser processes
            sh 'pkill -f chrome || true'
            sh 'pkill -f geckodriver || true'
        }
        success {
            echo 'All tests passed successfully!'
            emailext (
                subject: "Jenkins Build ${env.JOB_NAME} - ${env.BUILD_NUMBER} - SUCCESS",
                body: "The build completed successfully. Check ${env.BUILD_URL} for details.",
                to: 'your-email@example.com'
            )
        }
        failure {
            echo 'Some tests failed!'
            emailext (
                subject: "Jenkins Build ${env.JOB_NAME} - ${env.BUILD_NUMBER} - FAILED",
                body: "The build failed. Check ${env.BUILD_URL} for details.",
                to: 'your-email@example.com'
            )
        }
    }
}