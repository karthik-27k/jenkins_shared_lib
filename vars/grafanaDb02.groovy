def call() {
    def user = currentBuild.getBuildCauses()[0]?.userName ?: 'Automated'
    def buildTime = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('UTC'))
    def version = "v1.0.0"
    def jobName = env.JOB_NAME
    def buildNumber = env.BUILD_NUMBER
    def buildResult = 'SUCCESS'
    def failedStage = null
    def totalStages = 0
    def completedStages = 0
    def stageNames = []
    
    def stages = [
        "Checkout": {
            // Example stage
            checkout scm
        },
        "Build": {
            // Example stage
            sh 'echo Building...'
        },
        "Test": {
            // Example stage
            sh 'exit 1' // Simulate failure
        },
        "Deploy": {
            // Example stage
            sh 'echo Deploying...'
        }
    ]

    stages.each { stageName, stageLogic ->
        stageNames << stageName
        totalStages++
        try {
            stage(stageName) {
                stageLogic()
                completedStages++
            }
        } catch (e) {
            buildResult = 'FAILURE'
            failedStage = stageName
            echo "Stage '${stageName}' failed: ${e}"
            throw e // Stop pipeline on failure
        }
    }

    // Post-build data sending
    sh """
    curl -X POST http://34.42.18.106:5001/jenkinsdata \
         -H 'Content-Type: application/json' \
         -d '{
               "user": "${user}",
               "job_name": "${jobName}",
               "build_number": "${buildNumber}",
               "version": "${version}",
               "timestamp": "${buildTime}",
               "build_result": "${buildResult}",
               "failed_stage": "${failedStage ?: "None"}",
               "total_stages": ${totalStages},
               "completed_stages": ${completedStages},
               "stage_names": ${groovy.json.JsonOutput.toJson(stageNames)}
         }'
    """
}
