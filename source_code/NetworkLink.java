/*
#                        TEAM MEMBERS & CONTRIBUTION

#  1. Amit Kumar          				(Net-Id: AXK210047): Algorithm Implementation
#  2. Shanmukha Sai Bapiraj Vinnakota   (Net-Id: SXV200113): Inter Process Communication
*/


import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class NetworkLink {

    private ProcessThread process;
    private Queue<LayeredBFSDiscoveryMessage> msgQu;

    public NetworkLink(ProcessThread process) {
        this.process = process;
        msgQu = new ConcurrentLinkedQueue<>();
    }

    public ProcessThread getProcess() {
        return process;
    }

    public void addMessageToQ(LayeredBFSDiscoveryMessage message) {
        try{
            TimeUnit.SECONDS.sleep(message.getRandom_delay());
        }catch(InterruptedException ex){
            System.out.println("Exception in network delay: "+ex.getMessage());
        }
        AsynchLayeredBFS.noOfMessages++;
        this.msgQu.offer(message);
    }

    public ArrayList<LayeredBFSDiscoveryMessage> getMessageList(int phaseNum) {
        ArrayList<LayeredBFSDiscoveryMessage> messageList = new ArrayList<>();

        while (!this.msgQu.isEmpty() && this.msgQu.peek().getPhaseNum() <= phaseNum) {
            messageList.add(this.msgQu.poll());
        }

        return messageList;
    }

}
