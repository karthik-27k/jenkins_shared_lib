def call(Map stageInfo) {
    def user = currentBuild.getBuildCauses()[0]?.userName ?: 'Automated'
    def buildTime = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('UTC'))
    def version = "v1.0.0"
    def jobName = env.JOB_NAME
    def buildNumber = env.BUILD_NUMBER
    def buildResult = currentBuild.currentResult ?: 'SUCCESS'
    def startTime = currentBuild.getStartTimeInMillis()
    def endTime = System.currentTimeMillis()
    def durationSeconds = ((endTime - startTime) / 1000).toInteger()

    def stageNames = stageInfo?.keySet() as List ?: []
    def totalStages = stageNames.size()
    def completedStages = stageInfo?.findAll { it.value == 'SUCCESS' }?.size() ?: 0
    def failedStage = stageInfo.find { k, v -> v == 'FAILURE' }?.key ?: "None"

    // Optional: Estimate Jenkinsfile size
    def jenkinsfileSizeKB = 0
    try {
        def file = new File("${env.WORKSPACE}/Jenkinsfile")
        if (file.exists()) {
            jenkinsfileSizeKB = (file.length() / 1024).toInteger()
        }
    } catch (e) {
        echo "Could not read Jenkinsfile size: ${e.message}"
    }

    def jsonPayload = groovy.json.JsonOutput.toJson([
        user             : user,
        job_name         : jobName,
        build_number     : buildNumber,
        version          : version,
        timestamp        : buildTime,
        build_result     : buildResult,
        total_stages     : totalStages,
        completed_stages : completedStages,
        stage_names      : stageNames,
        failed_stage     : failedStage,
        duration_seconds : durationSeconds,
        jenkinsfile_size_kb : jenkinsfileSizeKB
    ])

    sh """
    curl -X POST http://34.42.18.106:5001/jenkinsdata \
         -H 'Content-Type: application/json' \
         -d '${jsonPayload}'
    """
}
