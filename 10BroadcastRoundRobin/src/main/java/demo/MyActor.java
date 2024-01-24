package demo;

import java.io.Serializable;

import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import javafx.scene.text.Text;

public class MyActor extends UntypedAbstractActor {

    // Logger attached to actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    ActorRef destination;

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
            destination = m;
            TextMessage m2 = new TextMessage("JOIN");
            destination.tell(m2, getSelf());
        }
        if (message instanceof TextMessage) {
            TextMessage m = (TextMessage) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with String: [" + m.data + "]");
        }

    }

}
