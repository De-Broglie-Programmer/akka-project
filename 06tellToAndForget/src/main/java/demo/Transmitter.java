package demo;

import java.io.Serializable;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import demo.MyActor.*;

public class Transmitter extends UntypedAbstractActor {
    // Logger attached to actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    ActorRef sourceRef, destinationRef;

    // Static function creating actor
    public static Props createActor() {
        return Props.create(Transmitter.class, () -> {
            return new Transmitter();
        });
    }

    public Transmitter() {
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TextToTansmitterMessage) {
            TextToTansmitterMessage m = (TextToTansmitterMessage) message;
            TextMessage newMessage = new TextMessage(m.data);
            log.info("[" + getSelf().path().name() + "] received TextToTansmitterMessage from ["
                    + getSender().path().name()
                    + "] with text [" + m.data + "] to destination [" + m.destination + "]");
            (m.destination).tell(newMessage, getSender());
        }
    }
}
