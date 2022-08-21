##################################  Project Topic: LayeredBFS algorithm #################################################

#                        TEAM MEMBERS & CONTRIBUTION

#  1. Amit Kumar          				(Net-Id: AXK210047): Algorithm Implementation
#  2. Shanmukha Sai Bapiraj Vinnakota  	(Net-Id: SXV200113): Inter Process Communication

Java files:
    1. AsynchLayeredBFS.java
    2. EnumMessageType.java
    3. LayeredBFSDiscoveryMessage.java
    4. NetworkLink.java
    5. ProcessThread.java

Input file:
    1. input.dat


Steps to compile:
    1. Create a directory (Ex: cs6380_group4)
    2. Extract the attached zip file and put all these files into the newly created folder
    3. Run the command: javac *.java
	4. Copy the input.dat file into the same

Steps to run:
    1. from the new directory use the command: 
        java AsynchLayeredBFS input.dat
        
        Here argument is the input file name

Algorithm:

The algorithm will create n number of threads to simulate each processes, here n will be picked form the firstline of input.dat file 
and BFS rrot id will be assigned to one of the thread based on second line of input.dat file. Root node will start the processes by sending SEARH messages to each of the neighbors.
Neighbors will reply with POSITIVE_ACK messages if it recives the message for the first time. once the root will recive ACK message forall the search messages, it will initiate a 
NEW_PHASE messge to all its children. if the children are intermediate node, it will forward the NEW_MESSAGE to its neighbors and weight for CONVERGE_CAST message, and after receiving 
CONVERGE_CAST message it will forward it to its parent. Where as if the children is those nodes discovered in last round i.e. (phase-1)th round, it will send SEARCH message to its neighbors
and weight for POSITIVE or NEGATIVE_ACK, and then revert to its parent with CONVERGE_CAST message. If in this process, during one phase there were no new node identified, root node will 
start the termination of the process. At the end we will have a BFS tree constructed one layer at a time. 
