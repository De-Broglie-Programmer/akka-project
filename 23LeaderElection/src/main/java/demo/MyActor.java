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
    ActorRef nexActorRef = null;
    boolean isParticipant = false;
    boolean isLeader = false;

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
            nexActorRef = m;
        }
        if (message instanceof TextMessage) {
            TextMessage m = (TextMessage) message;
            log.info("[" + getSelf().path().name() + "] received message from [" + getSender().path().name()
                    + "] with String: [" + m.data + "]");
            if (m.data.equals("START")) {
                TextMessage m2 = new TextMessage("Election");
                nexActorRef.tell(m2, getSelf());
                isParticipant = true;
                log.info("[" + getSelf().path().name() + "] sends Election message to [" + nexActorRef.path().name()
                        + "]");
            } else if (m.data.equals("Election")) {
                // compare sender's UID with self's UID
                int senderUID = Integer.parseInt(getSender().path().name());
                int selfUID = Integer.parseInt(getSelf().path().name());
                if (senderUID > selfUID) {
                    nexActorRef.tell(m, getSender());
                    isParticipant = true;
                    log.info("[" + getSelf().path().name() + "] forwards the Election message to ["
                            + nexActorRef.path().name() + "]");
                } else if (senderUID < selfUID) {
                    if (isParticipant == true) {
                        log.info("[" + getSelf().path().name() + "] discards the Election message from ["
                                + getSender().path().name() + "]");
                    } else {
                        nexActorRef.tell(m, getSelf());
                        isParticipant = true;
                        log.info("[" + getSelf().path().name() + "] forwards the Election message to ["
                                + nexActorRef.path().name() + "]");
                    }
                } else if (senderUID == selfUID) {
                    isLeader = true;
                    isParticipant = false;
                    log.info("[" + getSelf().path().name() + "] starts acting as the leader");
                    TextMessage m2 = new TextMessage("Elected");
                    nexActorRef.tell(m2, getSelf());
                }
            } else if (m.data.equals("Elected")) {
                if (isLeader == false) {
                    nexActorRef.tell(m, getSender());
                    isParticipant = false;
                    log.info("[" + getSelf().path().name() + "] forwards the Elected message to ["
                            + nexActorRef.path().name() + "]");
                } else {
                    // if send's UID is not the same as self's UID, raise an error
                    int senderUID = Integer.parseInt(getSender().path().name());
                    int selfUID = Integer.parseInt(getSelf().path().name());
                    if (senderUID != selfUID) {
                        // raise exception here "TWO LEADERS EXIST"
                        log.error("Error: TWO LEADERS EXIST: " + getSender().path().name() + " and "
                                + getSelf().path().name());
                    }
                    log.info("[" + getSelf().path().name() + "] discards the Elected message from ["
                            + getSender().path().name() + "]");
                    log.info("Election procedure ends.");
                }
            }

        }

    }

}
