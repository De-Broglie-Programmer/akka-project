package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.*;
import demo.Transmitter.*;

public class TellToAndForget {
    public static void main(String[] args) {
        // Instantiate an actor system
        final ActorSystem system = ActorSystem.create("system");

        // Instantiate actor a, b and transmitter
        final ActorRef a = system.actorOf(MyActor.createActor(), "a");
        final ActorRef b = system.actorOf(MyActor.createActor(), "b");
        final ActorRef transmitter = system.actorOf(Transmitter.createActor(), "transmitter");

        RefsMessage refs = new RefsMessage(b, transmitter);
        a.tell(refs, ActorRef.noSender());
        TextMessage m = new TextMessage("start");
        a.tell(m, ActorRef.noSender());

    }
}
