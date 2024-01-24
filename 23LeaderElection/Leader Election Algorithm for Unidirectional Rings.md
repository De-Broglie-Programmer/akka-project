## Leader Election Algorithm for Unidirectional Rings

#### 1. About the algorithm

The algorithm is raised by **Chang, Ernest and Roberts, Rosemary** in paper "**An improved algorithm for decentralized extrema-finding in circular configurations of processes**", which was published on [Communications of the ACM](https://dl.acm.org/toc/cacm/1979/22/5)

The paper presents an improvement to LeLann's algorithm for finding the  largest (or smallest) of a set of uniquely numbered processes arranged  in a circle, in which no central controller exists and the number of  processes is not known a priori. This decentralized algorithm uses a  technique of selective message extinction in order to achieve an average number of message passes of order (*n* log *n*) rather than *O*(*n*2).

#### 2.How we implement the algorithm

In our code, we implement this ring-based leader election algorithm using **akka** library to pass messages between distributed systems. Before the test, you can modify the number of processes (in akka, we say actors), in file LeaderElection.java

```java
public static void main(String[] args) {
    // change the number of processes here
    int process_num = 5;
    ...
}
```

Our code simulates the situation where the leader dumps  and each actor has a probability of 30% to sense the failure and start election.

```java
// for each actor, determine randomly if to send a start message
// at least one actor will send a start message
boolean start = false;
TextMessage m = new TextMessage("START");
for (int i = 0; i < process_num; i++) {
	if (Math.random() < 0.3) {
		actors[i].tell(m, ActorRef.noSender());
      	start = true;
    }
}
if (start == false)
     ctors[0].tell(m, ActorRef.noSender());
```

Of course, the topology of the actor ring is given in advance in main function so that each actor knows its clockwise neighbor.

#### 3. How to test the correctness

To verify the correctness of our implementation, we attach one logger to each actor, and print all the sending/forwarding of messages. In the log.info, you can see:

- the actor(s) who start the election

- the sending/forwarding of each "Election" message, from whom to whom

- the leader who win the election (you can verify that it has the biggest UID)

- the sending/forwarding of each "Elected" message, from whom to whom

  In the forwarding of "Elected" message, we also test if there are two leaders.

  ```java
  else if (m.data.equals("Elected")) {
      if (isLeader == false) {
          ...
      } else {
              // if send's UID is not the same as self's UID, raise an error
              int senderUID = Integer.parseInt(getSender().path().name());
              int selfUID = Integer.parseInt(getSelf().path().name());
              if (senderUID != selfUID) {
                  // raise exception here "TWO LEADERS EXIST"
                  log.error("Error: TWO LEADERS EXIST: " + getSender().path().name() + " 							and "+ getSelf().path().name());
              }
          ...
  ```

  