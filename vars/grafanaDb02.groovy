def call(Map stages) {
    def user = currentBuild.getBuildCauses()[0]?.userName ?: 'Automated'
    def startTime = System.currentTimeMillis()
    def buildTime = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('UTC'))
    def version = "v1.0.0"
    def jobName = env.JOB_NAME ?: 'Unknown'
    def buildNumber = env.BUILD_NUMBER ?: '0'
    def buildResult = 'SUCCESS'
    def totalStages = 0
    def completedStages = 0
    def stageNames = []
    def failedStage = null

    try {
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
                throw e
            }
        }
    } catch (err) {
        echo "Build failed at stage: ${failedStage}"
    } finally {
        def endTime = System.currentTimeMillis()
        def durationInSeconds = ((endTime - startTime) / 1000).toInteger()

        def payload = [
            user             : user,
            job_name         : jobName,
            build_number     : buildNumber,
            version          : version,
            timestamp        : buildTime,
            build_result     : buildResult,
            total_stages     : totalStages,
            completed_stages : completedStages,
            stage_names      : stageNames,
            failed_stage     : failedStage ?: "None",
            duration_seconds : durationInSeconds
        ]

        def jsonPayload = groovy.json.JsonOutput.toJson(payload).replaceAll("'", "\\\\'")

        echo "Sending build data: ${jsonPayload}"

        sh """#!/bin/bash
        curl -X POST http://34.42.18.106:5001/jenkinsdata \\
             -H 'Content-Type: application/json' \\
             -d '${jsonPayload}'
        """
    }
}
