package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.*;

public class UncontrolledFlooding {
    public static void main(String[] args) {
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

        // Send one message to start-actor a
        a.tell(new TextMessage("Hello"), ActorRef.noSender());

    }
}
