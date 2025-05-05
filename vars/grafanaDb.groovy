def call() {
                    def user = currentBuild.getBuildCauses()[0].userName ?: 'Automated'
                    def buildTime = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('UTC'))
                    def version = "v1.0.0" // Replace with actual logic if needed
                    def jobName = env.JOB_NAME
                    def buildNumber = env.BUILD_NUMBER
                    
                    sh """
                    curl -X POST http://34.42.18.106:5000/jenkinsdata \
                         -H 'Content-Type: application/json' \
                         -d '{
                               "user": "${user}",
                               "job_name": "${jobName}",
                               "build_number": "${buildNumber}",
                               "version": "${version}",
                               "timestamp": "${buildTime}"
                         }'
                    """
