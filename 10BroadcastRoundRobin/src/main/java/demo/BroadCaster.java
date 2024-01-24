package demo;

import java.util.ArrayList;
import java.io.Serializable;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import demo.MyActor.*;

public class BroadCaster extends UntypedAbstractActor {
    // Logger attached to actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    // new a arrayList of ActorRef
    ArrayList<ActorRef> destination = new ArrayList<ActorRef>();

    public BroadCaster() {
    }

    // Static function creating actor
    public static Props createActor() {
        return Props.create(BroadCaster.class, () -> {
            return new BroadCaster();
        });
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TextMessage) {
            TextMessage m = (TextMessage) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with String: [" + m.data + "]");
            if (m.data.equals("JOIN")) {
                destination.add(getSender());
            } else {
                for (ActorRef a : destination) {
                    if (a != getSender())
                        a.tell(m, getSender());
                }
            }
        }

    }

}
