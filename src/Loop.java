import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;

public class Loop {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static int numberOfTrials = 100000;
    static int MAXINPUTSIZE  = 93;
    static int MININPUTSIZE  =  0;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/Users/joshsauer/Documents/_StorageVault/School/CSC-482/Lab5/FibLoop/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    static long fibNumbers[] = new long [MAXINPUTSIZE];

    public static void main(String[] args) {

        // Validates Fibonacci calculation.
        //FibLoop(MAXINPUTSIZE);
        //PrintFibNumbers();

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("FibLoop-Exp1-ThrowAway.txt");
        runFullExperiment("FibLoop-Exp2.txt");
        runFullExperiment("FibLoop-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        StopWatch BatchStopwatch = new StopWatch(); // for timing an entire set of trials
        StopWatch TrialStopwatch = new StopWatch(); // for timing an individual trial



        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for(int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize++) {

            // progress message...
            System.out.println("");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;


            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();


            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {

                /* force garbage collection before each trial run so it is not included in the time */
                //System.gc();

                //TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                /* run the function we're testing on the trial input */
                FibLoop(inputSize);

                // batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }
            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    static void PrintFibNumbers() {

        System.out.println("Validating Fibonacci Numbers: ");
        for (int counter = 0; counter < MAXINPUTSIZE; counter++) {
            System.out.println(fibNumbers[counter]);
        }

        System.out.println("\n");
    }

    static void FibLoop(int inputSize) {

        // Even though the fibNumber array will contain the solution calculated in the last run
        // that will not affect the speed of this one.
        // They will simply be overwrote as if they are just uninitialized elements.

        for (int counter = 0; counter < inputSize; counter++) {

            if (counter < 2) {

                if (counter == 0) {
                    fibNumbers[0] = 0;
                } else {
                    fibNumbers[1] = 1;
                }
            } else {

                fibNumbers[counter] = fibNumbers[counter - 1] + fibNumbers[counter - 2];
            }
        }
    }
}
