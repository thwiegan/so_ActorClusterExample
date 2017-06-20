# so_ActorSupervisionTest
In response to https://stackoverflow.com/questions/44624876/how-can-i-define-seed-nodes-in-runtime/44629218

Run:
- sbt "run 1337 1337" to run the initial node on port 1337
- sbt "run 1338 1337" to run an additional node on port 1338 and let it join to node on 1337
- sbt "run 1339" to run a node, which takes the seed node in the application.conf file (which fails, because nothing is running on 2551)
