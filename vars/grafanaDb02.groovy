def call(int totalStages = 0) {
    def user = currentBuild.getBuildCauses()[0]?.userName ?: 'Automated'
    def buildTime = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('UTC'))
    def version = "v1.0.0" // Replace with actual logic if needed
    def jobName = env.JOB_NAME
    def buildNumber = env.BUILD_NUMBER
    def buildResult = currentBuild.currentResult ?: 'SUCCESS' // Defaults to SUCCESS if not set

    sh """
    curl -X POST http://35.192.135.133:5000/jenkinsdata \
         -H 'Content-Type: application/json' \
         -d '{
               "user": "${user}",
               "job_name": "${jobName}",
               "build_number": "${buildNumber}",
               "version": "${version}",
               "timestamp": "${buildTime}",
               "result": "${buildResult}",
               "stages_count": "${totalStages}"
         }'
    """
}
