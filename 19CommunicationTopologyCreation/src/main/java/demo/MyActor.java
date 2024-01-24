package demo;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyActor extends UntypedAbstractActor {

    // Logger attached to actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    // list of known actors
    ArrayList<ActorRef> knownActors = new ArrayList<ActorRef>();

    public MyActor() {
    }

    // Static function creating actor
    public static Props createActor() {
        return Props.create(MyActor.class, () -> {
            return new MyActor();
        });
    }

    static public class TextMessage implements Serializable {
        public String data;

        public TextMessage(String data) {
            this.data = data;
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ActorRef) {
            ActorRef m = (ActorRef) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with ActorRef: [" + m + "]");
            knownActors.add(m);
        }
        if (message instanceof TextMessage) {
            TextMessage m = (TextMessage) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with String: [" + m.data + "]");
            for (ActorRef a : knownActors) {
                a.tell(m, getSelf());
            }
        }

    }

}
