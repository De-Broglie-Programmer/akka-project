package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.*;

public class ControlledFlooding {
    public static void main(String[] args) throws InterruptedException {
        // Instantiate an actor system
        final ActorSystem system = ActorSystem.create("system");

        // Instantiate actor a, b, c, d, e
        final ActorRef a = system.actorOf(MyActor.createActor(), "a");
        final ActorRef b = system.actorOf(MyActor.createActor(), "b");
        final ActorRef c = system.actorOf(MyActor.createActor(), "c");
        final ActorRef d = system.actorOf(MyActor.createActor(), "d");
        final ActorRef e = system.actorOf(MyActor.createActor(), "e");

        // Send the known actors to each actor
        a.tell(b, ActorRef.noSender());
        a.tell(c, ActorRef.noSender());
        b.tell(d, ActorRef.noSender());
        c.tell(d, ActorRef.noSender());
        c.tell(d, ActorRef.noSender());
        d.tell(e, ActorRef.noSender());
        e.tell(b, ActorRef.noSender()); // This is the cycle which causes the uncontrolled flooding

        // Send one message to start the first round of flooding
        a.tell(new TextMessageWithSequence("Hello", 0), ActorRef.noSender());
        Thread.sleep(3000); // bad use of sleep, but it helps to distinguish each round of flooding

        // Send one message to start the second round of flooding
        a.tell(new TextMessageWithSequence("Hello", 1), ActorRef.noSender());
        Thread.sleep(3000); // bad use of sleep, but it helps to distinguish each round of flooding

        // Send one message to start the third round of flooding
        a.tell(new TextMessageWithSequence("Hello", 2), ActorRef.noSender());

    }
}
