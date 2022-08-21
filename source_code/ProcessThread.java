/*
#                        TEAM MEMBERS & CONTRIBUTION

#  1. Amit Kumar          				(Net-Id: AXK210047): Algorithm Implementation
#  2. Shanmukha Sai Bapiraj Vinnakota   (Net-Id: SXV200113): Inter Process Communication
*/

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ProcessThread implements Runnable {

    private final int processId;
    private int parent = -1;
    private final ArrayList<Integer> son;

    private int processLayer = 0;

    private final ArrayList<NetworkLink> myConnections;

    public ProcessThread(int processId) {
        this.processId = processId;
        this.son = new ArrayList<>();
        this.myConnections = new ArrayList<>();
    }

    public void addProcessConnections(ProcessThread pThread) {
        if(pThread.getProcessId() != this.processId)
            this.myConnections.add(new NetworkLink(pThread));
    }

    public int getProcessId() {
        return processId;
    }

    @Override
    public void run() {

        // check if the Processes can start the current round or wait for instruction from Main thread to start
        while(true) {
            if(this.processId == AsynchLayeredBFS.getBfsRootID() && AsynchLayeredBFS.phaseNum == 0) {
                AsynchLayeredBFS.rootAckPending = AsynchLayeredBFS.getRootChildCount();
                AsynchLayeredBFS.childNodes = new ArrayList<>();
                AsynchLayeredBFS.phaseNum++;

                LayeredBFSDiscoveryMessage layeredBFSDiscoveryMessage
                        = new LayeredBFSDiscoveryMessage(EnumMessageType.SEARCH, System.nanoTime(), System.nanoTime(), this.parent, this.processId, AsynchLayeredBFS.phaseNum);
                messageBroadcastToAllLinks(layeredBFSDiscoveryMessage);

            }else if(AsynchLayeredBFS.phaseNum >= 1 && AsynchLayeredBFS.rootAckPending == 0
                    && this.processId == AsynchLayeredBFS.getBfsRootID()
                    && !AsynchLayeredBFS.childNodes.isEmpty()){

                AsynchLayeredBFS.rootAckPending = AsynchLayeredBFS.getRootChildCount();
                AsynchLayeredBFS.layerNodeMap.put(AsynchLayeredBFS.phaseNum, AsynchLayeredBFS.childNodes);
                AsynchLayeredBFS.childNodes = new ArrayList<>();

                AsynchLayeredBFS.phaseNum++;
                LayeredBFSDiscoveryMessage layeredBFSDiscoveryMessage
                        = new LayeredBFSDiscoveryMessage(EnumMessageType.NEW_PHASE, System.nanoTime(), System.nanoTime(), this.parent, this.processId, AsynchLayeredBFS.phaseNum);
                messageBroadcastToChild(layeredBFSDiscoveryMessage, this.son);

            }else if(AsynchLayeredBFS.phaseNum >= 1 && AsynchLayeredBFS.rootAckPending == 0 && AsynchLayeredBFS.childNodes.isEmpty()){
                AsynchLayeredBFS.terminate = true;

            }else{
                ArrayList<LayeredBFSDiscoveryMessage> msgList = new ArrayList<>();

                for (NetworkLink link : myConnections)
                    msgList.addAll(link.getMessageList(AsynchLayeredBFS.phaseNum));

                for (LayeredBFSDiscoveryMessage messages : msgList) {
                    LayeredBFSDiscoveryMessage layeredBFSDiscoveryMessage;
                    NetworkLink netLink = getLayeredBFSLink(messages.getMessageSenderId());

                    if(messages.getMsgType().equals(EnumMessageType.SEARCH)) {
                        if(this.parent == -1 && this.processLayer == 0) {
                            this.parent = messages.getMessageSenderId();
                            this.processLayer = AsynchLayeredBFS.phaseNum;
                            netLink.getProcess().son.add(this.processId);
                        }

                        if(netLink.getProcess().processId == this.parent)
                            layeredBFSDiscoveryMessage
                                    = new LayeredBFSDiscoveryMessage(EnumMessageType.POSITIVE_ACK, System.nanoTime(), System.nanoTime(), this.parent, this.processId, AsynchLayeredBFS.phaseNum);
                        else
                            layeredBFSDiscoveryMessage
                                    = new LayeredBFSDiscoveryMessage(EnumMessageType.NEGATIVE_ACK, System.nanoTime(), System.nanoTime(), this.parent, this.processId, AsynchLayeredBFS.phaseNum);
                        sendMessage(netLink.getProcess(), layeredBFSDiscoveryMessage);

                    }else if(messages.getMsgType().equals(EnumMessageType.NEW_PHASE)){

                        if(this.processLayer == (AsynchLayeredBFS.phaseNum - 1)) {
                            layeredBFSDiscoveryMessage
                                    = new LayeredBFSDiscoveryMessage(EnumMessageType.SEARCH, System.nanoTime(), System.nanoTime(), this.parent, this.processId, AsynchLayeredBFS.phaseNum);
                            messageBroadcastToAllLinks(layeredBFSDiscoveryMessage);
                        }else {
                            layeredBFSDiscoveryMessage
                                    = new LayeredBFSDiscoveryMessage(EnumMessageType.NEW_PHASE, System.nanoTime(), System.nanoTime(), this.parent, this.processId, AsynchLayeredBFS.phaseNum);
                            messageBroadcastToChild(layeredBFSDiscoveryMessage, this.son);

                        }
                    }else if(messages.getMsgType().equals(EnumMessageType.POSITIVE_ACK)
                            || messages.getMsgType().equals(EnumMessageType.NEGATIVE_ACK)
                            || (messages.getMsgType().equals(EnumMessageType.CONVERGE_CAST))){
                        if(this.parent != -1) {
                            layeredBFSDiscoveryMessage
                                    = new LayeredBFSDiscoveryMessage(EnumMessageType.CONVERGE_CAST, System.nanoTime(), System.nanoTime(), this.parent, this.processId, AsynchLayeredBFS.phaseNum);
                            sendMessage(getLayeredBFSLink(this.parent).getProcess(), layeredBFSDiscoveryMessage);
                        }
                        if(this.processId == AsynchLayeredBFS.getBfsRootID())
                            AsynchLayeredBFS.rootAckPending--;
                        if(messages.getMsgType().equals(EnumMessageType.POSITIVE_ACK))
                            AsynchLayeredBFS.childNodes.add(messages.getMessageSenderId());

                    }
                }
            }
        }
    }

    /* get Network link for child process */
    private NetworkLink getLayeredBFSLink(int toPid) {
        for (NetworkLink nLink: myConnections) {
            if (toPid == nLink.getProcess().getProcessId()) {
                return nLink;
            }
        }
        return null;
    }

    /* broadcast a given message to all neighbors except the parent */
    private void messageBroadcastToAllLinks(LayeredBFSDiscoveryMessage message) {
        for (NetworkLink nLink : myConnections) {
            if(message.getMsgType() == EnumMessageType.SEARCH
                    && !nLink.getProcess().equals(this.parent)
                    && !nLink.getProcess().equals(this.processId)
                    && (nLink.getProcess().getProcessId() != AsynchLayeredBFS.getBfsRootID())
            ) {
                sendMessage(nLink.getProcess(), message);
            }

            else if(!nLink.getProcess().equals(this.processId) && (nLink.getProcess().getProcessId() != AsynchLayeredBFS.getBfsRootID()))
                sendMessage(nLink.getProcess(), message);
        }
    }

    /* broadcast a given message to all neighbors except the parent */
    private void messageBroadcastToChild(LayeredBFSDiscoveryMessage message, ArrayList<Integer> child) {
        for (NetworkLink nLink : myConnections) {
            if(child.contains(nLink.getProcess().getProcessId())
                    && (nLink.getProcess().getProcessId() != AsynchLayeredBFS.getBfsRootID())
                    && !nLink.getProcess().equals(this.processId))
                sendMessage(nLink.getProcess(), message);
        }
    }

    private void sendMessage(ProcessThread toProcess, LayeredBFSDiscoveryMessage message) {
        int networkDelay = delay_generator();
        message.setRandom_delay(networkDelay);

        System.out.println("SEND: [Type: "+String.format("%1$"+14+ "s", message.getMsgType().toString()) +"] [From: " + this.processId + " (" + AsynchLayeredBFS.phaseNum + ")]" +
                "-> To: " + toProcess.getProcessId() + ",  Message: "+message.getMessageId()+", Network delay time: "+networkDelay);
        toProcess.putMessage(message);
    }

    public void putMessage(LayeredBFSDiscoveryMessage message) {
        if(getLayeredBFSLink(message.getMessageSenderId()) != null) {
            Objects.requireNonNull(getLayeredBFSLink(message.getMessageSenderId())).addMessageToQ(message);
           // getLayeredBFSLink(message.getMessageSenderId()).addMessageToQ(message);
        }
    }

    private int delay_generator(){
        Random rand = new Random();
        return rand.nextInt((12 - 1) + 1) + 1;
    }

}

