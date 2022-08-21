/*
#                        TEAM MEMBERS & CONTRIBUTION

#  1. Amit Kumar          				(Net-Id: AXK210047): Algorithm Implementation
#  2. Shanmukha Sai Bapiraj Vinnakota   (Net-Id: SXV200113): Inter Process Communication
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AsynchLayeredBFS {
    private static int[] process_ids;
    private static int noOfProcess = 0;
    private static final List<ArrayList<Integer>> adjList = new ArrayList<>();
    private static int bfsRootID;

    private static int rootChildCount;
    protected static int rootAckPending;
    protected static boolean terminate = false;

    protected static int phaseNum = 0;
    protected static int noOfMessages = 0;

    protected static HashMap<Integer, ArrayList<Integer>> layerNodeMap = new HashMap<>();
    protected static ArrayList<Integer> childNodes = new ArrayList<>();
    public AsynchLayeredBFS() {

    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please provide the file to read the node and its neighbors list");
            System.exit(1);
        }
        final AsynchLayeredBFS asynchLayeredBFS = new AsynchLayeredBFS();

        final String strFileName = args[0];
        System.out.println("Reading Processes and it's details from file " + args[0]);

        Thread mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
			    try {
			        asynchLayeredBFS.readInputFile(strFileName);
			    } catch (IOException e) {
			        e.printStackTrace();
			    }

			    asynchLayeredBFS.simulate();
			}
		});

        mainThread.start();
        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nProcess completed all the required task.");
    }

    public static int getBfsRootID() {
        return bfsRootID;
    }

    private void readInputFile(String strFileName) throws IOException {
        File inputFile = new File(strFileName);
        System.out.println(strFileName);

        if(!inputFile.isFile() || !inputFile.exists()) {
            System.out.println("Given filename doesn't exist. Please check the file name");
            System.exit(1);
        }

        @SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(inputFile));

        int lineCount = 0;
        int k = 0;

        String strReadLine;
        while ((strReadLine = br.readLine()) != null) {
            ++lineCount;

            if (lineCount == 1) {
                noOfProcess = Integer.parseInt(strReadLine);
                System.out.println("\nNo of processes    : " + strReadLine);
                process_ids = new int[noOfProcess];
                int p_id = 1;
                for (int i = 0; i < noOfProcess; i++) {
                    process_ids[i] = p_id++;
                    adjList.add(new ArrayList<Integer>());
                }

            }else if(lineCount == 2){
                bfsRootID =  Integer.parseInt(strReadLine);
                ArrayList<Integer> l = new ArrayList<Integer>();
                l.add(bfsRootID);
                layerNodeMap.put(phaseNum, l);
                System.out.println("Layered BFS root is: " + strReadLine);

            }else {
                // create adjacency list
                int j = 1;
                for (String ids : strReadLine.split(" ")) {
                    if (Integer.parseInt(ids) == 1)
                        adjList.get(k).add(j);
                    j++;
                }

                if(++k == noOfProcess)
                    break;
            }
        }

        System.out.println("\nNetwork topology is as below:");
        System.out.println("Process Id : Neighbor List");

        int i = 1;
        for (ArrayList<Integer> pid : adjList) {
            System.out.println("    "+i+"      : "+pid);

            i++;
        }
    }


    private void simulate() {
        // Create the object of Processes threads and start the thread
        // It will be use to assign ackPending count for root at the start of Every phase
        for(int i: adjList.get(bfsRootID - 1))
            if(i != bfsRootID)
                rootChildCount++;

        ProcessThread[] processes = new ProcessThread[noOfProcess];
        for(int i = 0; i < noOfProcess; i++)
            processes[i] = new ProcessThread(process_ids[i]);

        for(int i = 0; i < noOfProcess; i++){
            for(int chId: adjList.get(i))
                for (ProcessThread pLink : processes)
                    if (pLink.getProcessId() == process_ids[chId - 1]) {
                        processes[i].addProcessConnections(pLink);
                    }
        }

        Thread[] pThread = new Thread[noOfProcess];
        System.out.println("\nStarting threads:");
        for(int i = 0; i < noOfProcess; i++){
            pThread[i] = new Thread(processes[i]);
            pThread[i].start();
        }

        while(true) {

            boolean isThreadActive = false;
            for (Thread th : pThread) {
                if (th.isAlive()) {
                    isThreadActive = true;
                    break;
                }
            }

            if (!isThreadActive) {
                System.out.println("\nAll Processes finished after electing the leader.");
                break;
            }

            if(terminate){
            	System.out.println("\nNumber of messages generated to complete the process: "+noOfMessages);
            	System.out.println("Layerd BFS node summary");
                for(int i = 0; i < phaseNum; i++)
                    System.out.println("Phase No: "+i+", Nodes added to BFS tree are: "+layerNodeMap.get(i));
                
                System.out.println("\nAll task completed. Now Terminating the process with status code: "+200);
                System.exit(200);

            }
        }

        for (Thread pt : pThread) {
            try {
                pt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getRootChildCount() {
        return rootChildCount;
    }

}
