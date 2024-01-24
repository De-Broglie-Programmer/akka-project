package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.MyActor.*;

public class BroadcastRoundRobin {
    public static void main(String[] args) {
        // Instantiate an actor system
        final ActorSystem system = ActorSystem.create("system");

        // Instantiate actor a, b and transmitter
        final ActorRef a = system.actorOf(ActorA.createActor(), "a");
        final ActorRef b = system.actorOf(MyActor.createActor(), "b");
        final ActorRef c = system.actorOf(MyActor.createActor(), "c");
        final ActorRef broadcaster = system.actorOf(BroadCaster.createActor(), "broadcaster");

        a.tell(broadcaster, ActorRef.noSender());
        b.tell(broadcaster, ActorRef.noSender());
        c.tell(broadcaster, ActorRef.noSender());

    }
}
