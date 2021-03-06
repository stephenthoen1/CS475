/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulatorjava;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.*;
import java.util.Arrays;

/**
 * Algorithms (FCFS, RR1, RR10, SPN)
 * @author JPerry1120
 */
public class ProcessSchedules {
    

    static int globalTime = 0;
    static int globalTimeRR = 0;
    static int globalTimeRRThroughput = 0;
    static int globalTimeSPN = 0;
    static int globalTimeRR10 = 0;
    
    //First Come First Serve Function that returns clock time
    public static Queue<ProcessControlBlock> firstcomefirstserve(Queue<ProcessControlBlock> myQueue) {

        //send process queue to calculate throughput method since we've decided that all of our processes will run
        myQueue = TimeCalculations.calculatethroughput(myQueue);

        Queue<ProcessControlBlock> TimeQueue = new LinkedList();

        //while the queue still contains process objects, run first come first serve schedule algorithm
        while (!myQueue.isEmpty()) {

            //grab first process object off of the queue
            ProcessControlBlock temp = myQueue.poll();

            temp.setresponsetime(globalTime);
            temp.setwaittime(globalTime);

            //get the io time for each process
            int io = temp.getiotime();
            int ioclockTime = 0;

            //if io time > 0, send to IOProcessing
            if (io > 0) {
                ioclockTime = IOProcessing.processIO(temp, ioclockTime);
            }

            globalTime += ioclockTime;
            globalTime += temp.getcontextswitchtime();

            //set the variable burst to the burst time that is within the process object
            int burst = temp.getbursttime();

            //while the process burst time is not 0, decrement the burst time by one and increase clock time by one
            while (burst != 0) {
                burst--;
                globalTime++;
            }

            temp.setturnaroundtime(globalTime);
            TimeQueue.add(temp);
        }
        
        //Calculate Averages (Turnaround, response, Wait) for entire algorithm
        TimeQueue = AverageCalculations.average(TimeQueue);
        globalTime = 0;
        
        return TimeQueue;

    }

    //Round Robin Function with time quantum of 1 that returns clock time
    public static Queue<ProcessControlBlock> rr1(Queue<ProcessControlBlock> myQueue) {
        //send process queue to calculate throughput method since we've decided that all of our processes will run
        myQueue = TimeCalculations.calculatethroughput(myQueue);

        Queue<ProcessControlBlock> TimeQueue = new LinkedList();

        //while the queue still contains process objects, run round robin schedule algorithm
        while (!myQueue.isEmpty()) {
            //grab first process object off of the queue
            ProcessControlBlock temp = myQueue.poll();

            //only calculates response time when the process is first accepted into the CPU, not when it is in the CPU a second time or third...etc.
            if (temp.getpid() != 0 && temp.getresponsetime() == 0) {
                temp.setresponsetime(globalTimeRR);
            }

            temp.setwaittime(globalTimeRR + temp.getwaittime());

            //get the io time for each process
            int io = temp.getiotime();
            int ioclockTime = 0;

            //if io time > 0, send to IOProcessing
            if (io > 0) {
                ioclockTime = IOProcessing.processIO(temp, ioclockTime);
            }

            globalTimeRR += ioclockTime;
            globalTimeRR += temp.getcontextswitchtime();

            //set the variable burst to the burst time that is within the process object
            int burst = temp.getbursttimerr();

            for (int i = 0; i < 1; i++) {
                //if process burst time is 0, get out of the for loop
                if (burst != 0) {
                    burst--;
                    globalTimeRR++;

                    if (burst == 0) {
                        temp.setturnaroundtime(globalTimeRR);
                        TimeQueue.add(temp);
                        break;
                    }
                }
            }
            //if the burst time is not 0, set the new burst time for the process and add the process to the end of the queue
            if (burst != 0) {
                temp.setburstrr(burst);
                myQueue.add(temp);
            }

        }
        
        //Calculate Averages (Turnaround, response, Wait) for entire algorithm
        TimeQueue = AverageCalculations.average(TimeQueue);
        globalTimeRR = 0;
        
        return TimeQueue;
    }

    //Round Robin Function with time quantum of 10 that returns clock time
    public static Queue<ProcessControlBlock> rr10(Queue<ProcessControlBlock> myQueue) {
        //send process queue to calculate throughput method since we've decided that all of our processes will run
        myQueue = TimeCalculations.calculatethroughput(myQueue);

        Queue<ProcessControlBlock> TimeQueue = new LinkedList();

        //while the queue still contains process objects, run round robin schedule algorithm
        while (!myQueue.isEmpty()) {
            //grab first process object off of the queue
            ProcessControlBlock temp = myQueue.poll();

            //only calculates response time when the process is first accepted into the CPU, not when it is in the CPU a second time or third...etc.
            if (temp.getpid() != 0 && temp.getresponsetime() == 0) {
                temp.setresponsetime(globalTimeRR10);
            }

            temp.setwaittime(globalTimeRR10 + temp.getwaittime());

            //get the io time for each process
            int io = temp.getiotime();
            int ioclockTime = 0;

            //if io time > 0, send to IOProcessing
            if (io > 0) {
                ioclockTime = IOProcessing.processIO(temp, ioclockTime);
            }

            globalTimeRR10 += ioclockTime;
            globalTimeRR10 += temp.getcontextswitchtime();

            //set the variable burst to the burst time that is within the process object
            int burst = temp.getbursttimerr();
            //run the process for 10 "clock time seconds" for round robin 10
            for (int i = 0; i < 10; i++) {
                //if process burst time is 0, get out of the for loop
                if (burst != 0) {
                    burst--;
                    globalTimeRR10++;

                    if (burst == 0) {
                        temp.setturnaroundtime(globalTimeRR10);
                        TimeQueue.add(temp);
                        break;
                    }

                } //if the process burst time is not 0, decrease the burst time and increase clock time

            }
            //if the burst time is not 0, set the new burst time for the process and add the process to the end of the queue
            if (burst != 0) {
                temp.setburstrr(burst);
                myQueue.add(temp);
            }
        }
        
        //Calculate Averages (Turnaround, response, Wait) for entire algorithm
        TimeQueue = AverageCalculations.average(TimeQueue);
        globalTimeRR10 = 0;
        
        return TimeQueue;
    }

    //Shortest Process Next Algorithm
    public static Queue shortestnext(Queue<ProcessControlBlock> myQueue) {
       
        ArrayList<ProcessControlBlock> spn = new ArrayList<ProcessControlBlock>();
        int counter = myQueue.size();
        
       //send process queue to calculate throughput method since we've decided that all of our processes will run
       myQueue = TimeCalculations.calculatethroughput(myQueue); 
       Queue<ProcessControlBlock> TimeQueueSPN = new LinkedList();

        // empties queue into array 
        while (!myQueue.isEmpty()) {

            for (int i = 0; i < counter; i++) {
                ProcessControlBlock temp = myQueue.remove();
                spn.add(temp);
            }
        }

        //sort through array and find process with shortest burst time
        while (!spn.isEmpty()) {

            ProcessControlBlock tempshortest = spn.get(0);

            for (int i = 1; i < spn.size(); i++) {

                if (spn.get(i).getbursttime() < tempshortest.getbursttime()) {
                    tempshortest = spn.get(i);

                }
            }
            
            //set response and wait time for current shortest process
            tempshortest.setresponsetime(globalTimeSPN);
            tempshortest.setwaittime(globalTimeSPN);
            
            //get the io time for each process
            int io = tempshortest.getiotime();
            int ioclockTime = 0;

            //if io time > 0, send to IOProcessing
            if (io > 0) {
                ioclockTime = IOProcessing.processIO(tempshortest, ioclockTime);
            }
            
            //Update globalTimeSPN with io and context switch time
            globalTimeSPN += ioclockTime;
            globalTimeSPN += tempshortest.getcontextswitchtime();

            //set shortest burst time to burst
            int burst = tempshortest.getbursttime();

            //execute shortest process (decrement shortest burst time, increment clock & 
            //remove process when finished)
            while (burst != 0) {
                burst--;
                globalTimeSPN++;
            }
            
            //Set turnaround time for process and add to TimeQueueSPN
            tempshortest.setturnaroundtime(globalTimeSPN);
            TimeQueueSPN.add(tempshortest);
            spn.remove(tempshortest);
        }
        
        //Calculate Averages (Turnaround, response, Wait) for entire algorithm
        TimeQueueSPN = AverageCalculations.average(TimeQueueSPN);
        globalTimeSPN = 0;
        
        return TimeQueueSPN;
    }

}
