# Scala Akka Spring job dispatcher

An example project showing how to manage asynchronous jobs 
with a REST API from Spring and Akka actors

API:
(everything is GET)
- `/jobs` shows all the jobs
- `/jobs/start` triggers a new job
- `/jobs/{job-id}` shows one job
- `/hello` shows "hello" to check if the app is alive 

The jobs can fail with a 50% chance

Only one job can be in progress at a time, but you can submit as many jobs as you can 
and they will be queued

### To run it

Use Gradle wrapper and execute

```
./gradlew bootRun
```