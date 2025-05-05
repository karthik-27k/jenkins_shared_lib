def call(Map stages) {
    // … all your setup, iteration, payload building …

    try {
        stages.each { name, logic ->
            // run each stage
        }
    } catch (ignored) {
        // swallow, since we still want to send data
    } finally {
        // build your primitive payload map…
        def payload = [
          user:             user,
          job_name:         jobName,
          build_number:     buildNumber,
          version:          version,
          timestamp:        buildTime,
          build_result:     buildResult,
          total_stages:     totalStages,
          completed_stages: completedStages,
          stage_names:      stageNames,
          failed_stage:     failedStage ?: "None",
          duration_seconds: durationInSeconds
        ]
        def json = groovy.json.JsonOutput.toJson(payload).replaceAll("'", "\\\\'")

        // 🔥 **Clear out the closures so nothing non‑serializable remains** 🔥
        stages.clear()

        // now curl only primitives
        sh """
          curl -s -X POST http://34.42.18.106:5001/jenkinsdata \\
               -H 'Content-Type: application/json' \\
               -d '${json}' || true
        """
    }
}
