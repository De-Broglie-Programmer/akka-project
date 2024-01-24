package demo;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    // keep track of received messages with the sequence number
    Map<String, Integer> receivedMessagesWithSeq = new HashMap<String, Integer>();

    public MyActor() {
    }

    // Static function creating actor
    public static Props createActor() {
        return Props.create(MyActor.class, () -> {
            return new MyActor();
        });
    }

    static public class TextMessageWithSequence implements Serializable {
        public String data;
        public int sequence;

        public TextMessageWithSequence(String data, int sequence) {
            this.data = data;
            this.sequence = sequence;
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
        if (message instanceof TextMessageWithSequence) {
            TextMessageWithSequence m = (TextMessageWithSequence) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with String: [" + m.data + "]" + " and sequence number: [" + m.sequence + "]");
            if (receivedMessagesWithSeq.containsKey(m.data)) {
                if (receivedMessagesWithSeq.get(m.data) < m.sequence) {
                    receivedMessagesWithSeq.put(m.data, m.sequence);
                    for (ActorRef a : knownActors) {
                        a.tell(m, getSelf());
                    }
                }
            } else {
                receivedMessagesWithSeq.put(m.data, m.sequence);
                for (ActorRef a : knownActors) {
                    a.tell(m, getSelf());
                }
            }
        }
    }
}
