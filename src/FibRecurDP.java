import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class FibRecurDP {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static int numberOfTrials = 100000;
    static int MAXINPUTSIZE  = 93;
    static int MININPUTSIZE  =  0;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/Users/joshsauer/Documents/_StorageVault/School/CSC-482/Lab5/FibRecurDP/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    // The globalCache is initialized to MAXINPUTSIZE + 1 in order to be able to just use x
    // as an index into the array without having to do (x - 1) every time we want to access it.
    static long globalCache[] = new long [MAXINPUTSIZE + 1];

    public static void main(String[] args) {

        // Validates Fibonacci calculation.
        //ValidateFibRecurDP(MAXINPUTSIZE);

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("FibRecurDP-Exp1-ThrowAway.txt");
        runFullExperiment("FibRecurDP-Exp2.txt");
        runFullExperiment("FibRecurDP-Exp3.txt");
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
        for(int inputSize = MININPUTSIZE; inputSize < MAXINPUTSIZE; inputSize++) {

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
                FibRecurDP(inputSize);

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

    static void ValidateFibRecurDP (int x) {

        for (int counter = x - 1; counter >= 0; counter--) {

            System.out.println(FibRecurDP((counter)));
        }
    }
    static long FibRecurDP(int x) {

        // Clears the global cache used for the Fibonacci algorithm.
        ClearCache();

        return FibRecur(x);
    }

    static long FibRecur (int x) {

        // If x is less than or equal to 1 then we have reached the first two Fibonacci numbers.
        // Therefore we return them so that they can be used for the rest of the function calls to calculate their Fibonacci numbers.
        if (x <= 1) {
            return x;
        }

        // Checks to see if the Fibonnaci number at x - 1 & x - 2 has previously been calculated.
        if (globalCache[x - 1] != -1 && globalCache[x - 2] != -1) {

            // Sets the new Fibonnaci number in the globalCache
            globalCache[x] = globalCache[x - 1] + globalCache[x - 2];
            return globalCache[x];
        } else { // The number has not been calculated yet

            // Sets the new Fibonnaci number in the globalCache
            globalCache[x] = FibRecur(x-1) + FibRecur(x-2);
            return globalCache[x];
        }
    }

    static void ClearCache() {

        // Goes through the globalCache and resets it to -1;
        for (int counter = 0; counter < MAXINPUTSIZE + 1; counter++) {
            globalCache[counter] = -1;
        }
    }
}
