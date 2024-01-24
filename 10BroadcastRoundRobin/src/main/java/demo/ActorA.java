package demo;

import java.io.Serializable;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import demo.MyActor.*;
import javafx.scene.text.Text;
import java.time.Duration;

public class ActorA extends UntypedAbstractActor {

    // Logger attached to actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ActorRef destination;

    public ActorA() {
    }

    // Static function creating actor
    public static Props createActor() {
        return Props.create(ActorA.class, () -> {
            return new ActorA();
        });
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ActorRef) {

            ActorRef m = (ActorRef) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with ActorRef: [" + m + "]");
            destination = m;
            getContext().system().scheduler().scheduleOnce(Duration.ofMillis(1000), getSelf(), "go",
                    getContext().system().dispatcher(), ActorRef.noSender());

        }
        if (message instanceof String) {
            String m = (String) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with String: [" + m + "]");
            if (m.equals("go")) {
                TextMessage m2 = new TextMessage("m");
                destination.tell(m2, getSelf());

            }
        }
    }

}
