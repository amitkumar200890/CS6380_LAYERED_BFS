/*
#                        TEAM MEMBERS & CONTRIBUTION

#  1. Amit Kumar          				(Net-Id: AXK210047): Algorithm Implementation
#  2. Shanmukha Sai Bapiraj Vinnakota   (Net-Id: SXV200113): Inter Process Communication
*/


public class LayeredBFSDiscoveryMessage {

    private EnumMessageType msgType;
    private long timeStamp;
    private long messageId;
    private int senderParentId;
    private int messageSenderId;

    private int phaseNum;

    private int random_delay;

    public LayeredBFSDiscoveryMessage(EnumMessageType msgType, long timeStamp, long messageId, int senderParentId, int messageSenderId, int phaseNum) {
        // System.out.println("==========phase num"+phaseNum);
        this.msgType = msgType;
        this.timeStamp = timeStamp;
        this.messageId = messageId;
        this.senderParentId = senderParentId;
        this.messageSenderId = messageSenderId;
        this.phaseNum = phaseNum;
    }


    public EnumMessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(EnumMessageType msgType) {
        this.msgType = msgType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderParentId() {
        return senderParentId;
    }

    public void setSenderParentId(int senderParentId) {
        this.senderParentId = senderParentId;
    }

    public int getMessageSenderId() {
        return messageSenderId;
    }

    public void setMessageSenderId(int messageSenderId) {
        this.messageSenderId = messageSenderId;
    }

    public int getPhaseNum() {
        return phaseNum;
    }

    public void setRandom_delay(int random_delay) {
        this.random_delay = random_delay;
    }

    public int getRandom_delay() {
        return random_delay;
    }

    @Override
    public String toString() {
        return "LayeredBFSDiscoveryMessage{" +
                "msgType=" + msgType +
                ", timeStamp=" + timeStamp +
                ", messageId=" + messageId +
                ", senderParentId=" + senderParentId +
                ", messageSenderId=" + messageSenderId +
                ", phase num=" + phaseNum +
                '}';
    }
}
