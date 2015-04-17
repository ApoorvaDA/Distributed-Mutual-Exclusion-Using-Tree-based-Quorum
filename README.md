# Distributed-Mutual-Exclusion-Using-Tree-based-Quorum
This is an Academic project which implements Maekawa's Quorum based
algorithm for Distributed Mutual Exclusion.
The setup consists of 7 servers and 5 clients. Clients enter critical
section 20 times only when they have received grants
the servers which are a part of the quorum. The quorum definition is as
specified in the problem statement.
Once the clients enter the critical section, they wait for a specified
time in the CS and upon exiting the CS, they wait for a
time range [x,y]units.
Multiple tests were conducted to observe the behaviour of algorithm by
keeping CS wait-time constant and changing the time range
between critical sections and vice versa.
The results consisted of collecting the total messages sent and received
by each server, the latency(time difference between a
client requesting CS and entering CS), total messages exchanged by a
client to enter CS.
Execution Syntax:
There are two executables : server.jar and client.jar
On servers execute as : java -jar server.jar <node_number>.
On clients execute as : java -jar client.jar <node_number>
<time_spent_in_CS> <x> <y>
