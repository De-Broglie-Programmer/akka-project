package demo;

import java.io.Serializable;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyActor extends UntypedAbstractActor {

    // Logger attached to actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    // two actor references
    ActorRef firstRef, secondRef;

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

    static public class RefsMessage implements Serializable {
        public ActorRef a;
        public ActorRef b;

        public RefsMessage(ActorRef a, ActorRef b) {
            this.a = a;
            this.b = b;
        }
    }

    static public class TextToTansmitterMessage implements Serializable {
        public String data;
        public ActorRef destination;

        public TextToTansmitterMessage(String data, ActorRef destination) {
            this.data = data;
            this.destination = destination;
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TextMessage) {
            TextMessage m = (TextMessage) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with data: [" + m.data + "]");
            if (m.data.equals("start")) {
                TextToTansmitterMessage m2 = new TextToTansmitterMessage("hello", firstRef);
                secondRef.tell(m2, getSelf());
            }
            if (m.data.equals("hello")) {
                TextMessage m2 = new TextMessage("hi");
                getSender().tell(m2, getSelf());
            }
        }
        if (message instanceof RefsMessage) {
            RefsMessage m = (RefsMessage) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with two ActorRefs: [" + m.a + "][" + m.b + "]");
            firstRef = m.a;
            secondRef = m.b;
        }
    }

}
