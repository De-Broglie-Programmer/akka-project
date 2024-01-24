package demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import demo.MyActor.*;

public class LeaderElection {
    public static void main(String[] args) {
        // change the number of processes here
        int process_num = 5;

        // Instantiate an actor system
        final ActorSystem system = ActorSystem.create("system");

        // Generate a list of unique UID randomly, one for each actor/process
        int[] uids = new int[process_num];
        for (int i = 0; i < process_num; i++) {
            uids[i] = (int) (Math.random() * 100000);
        }

        // Print all the UIDs
        System.out.println("UIDs:");
        for (int i = 0; i < process_num; i++) {
            System.out.println(uids[i]);
        }

        // Instantiate those actors/processes with the generated UID
        ActorRef[] actors = new ActorRef[process_num];
        for (int i = 0; i < process_num; i++) {
            actors[i] = system.actorOf(MyActor.createActor(), Integer.toString(uids[i]));
        }

        // tell the next actor's UID to each actor
        for (int i = 0; i < process_num; i++) {
            int next = (i + 1) % process_num;
            actors[i].tell(actors[next], ActorRef.noSender());
        }

        // the leader died
        System.out.println("The leader died.");
        System.out.println("Now we need to elect a new leader.");

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
            actors[0].tell(m, ActorRef.noSender());
    }
}
