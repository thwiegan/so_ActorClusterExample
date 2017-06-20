# so_ActorSupervisionTest
In response to 
- https://stackoverflow.com/questions/44624876/how-can-i-define-seed-nodes-in-runtime/44629218
- https://stackoverflow.com/questions/44647960/broadcast-message-to-actors-watching-a-particular-actor/44652048

Run:
- sbt "run 1337 1337" to run the initial node on port 1337
- sbt "run 1338 1337" to run an additional node on port 1338 and let it join to node on 1337
- sbt "run 1339" to run a node, which takes the seed node in the application.conf file (which fails, because nothing is running on 2551)

ClusterListeners will join the router of the primary node. Primary node broadcasts a test message every 20 seconds to all ClusterListeners in the cluster  
