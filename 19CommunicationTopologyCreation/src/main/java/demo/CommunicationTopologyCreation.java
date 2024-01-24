package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.*;

public class CommunicationTopologyCreation {
    public static void main(String[] args) {
        // Instantiate an actor system
        final ActorSystem system = ActorSystem.create("system");

        // Instantiate actor a, b, c, d
        final ActorRef a = system.actorOf(MyActor.createActor(), "a");
        final ActorRef b = system.actorOf(MyActor.createActor(), "b");
        final ActorRef c = system.actorOf(MyActor.createActor(), "c");
        final ActorRef d = system.actorOf(MyActor.createActor(), "d");

        // Send the known actors to each actor
        a.tell(b, ActorRef.noSender());
        a.tell(c, ActorRef.noSender());
        b.tell(d, ActorRef.noSender());
        c.tell(a, ActorRef.noSender());
        c.tell(d, ActorRef.noSender());
        d.tell(a, ActorRef.noSender());
        d.tell(d, ActorRef.noSender());

        // Send one message to start-actor a
        a.tell(new TextMessage("Hello"), ActorRef.noSender());

    }
}
