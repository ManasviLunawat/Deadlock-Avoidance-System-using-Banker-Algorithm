
public class BankerAlgorithm {

    private int processes;
    private int resources;

    private int[][] allocation;
    private int[][] maximum;
    private int[][] need;
    private int[] available;

    public BankerAlgorithm(int[][] allocation,
                           int[][] maximum,
                           int[] available) {

        if (allocation == null || maximum == null || available == null) {
            throw new IllegalArgumentException("Inputs cannot be empty.");
        }

        processes = allocation.length;

        if (processes == 0 || maximum.length != processes) {
            throw new IllegalArgumentException("Invalid process count.");
        }

        resources = available.length;

        if (resources == 0) {
            throw new IllegalArgumentException("Invalid resource count.");
        }

        this.allocation = new int[processes][resources];
        this.maximum = new int[processes][resources];
        this.need = new int[processes][resources];
        this.available = available.clone();

        for (int j = 0; j < resources; j++) {
            if (available[j] < 0) {
                throw new IllegalArgumentException(
                    "Available resources cannot be negative."
                );
            }
        }

        for (int i = 0; i < processes; i++) {

            if (allocation[i] == null || maximum[i] == null
                    || allocation[i].length != resources
                    || maximum[i].length != resources) {
                throw new IllegalArgumentException(
                    "Invalid matrix dimensions."
                );
            }

            for (int j = 0; j < resources; j++) {

                if (allocation[i][j] < 0 || maximum[i][j] < 0) {
                    throw new IllegalArgumentException(
                        "Resource values cannot be negative."
                    );
                }

                if (allocation[i][j] > maximum[i][j]) {
                    throw new IllegalArgumentException(
                        "Allocation cannot exceed Maximum for P" + i
                    );
                }

                this.allocation[i][j] = allocation[i][j];
                this.maximum[i][j] = maximum[i][j];

                need[i][j] = maximum[i][j] - allocation[i][j];
            }
        }
    }

    public int getProcessCount() {
        return processes;
    }

    public int getResourceCount() {
        return resources;
    }

    public int[][] getAllocation() {
        return copyMatrix(allocation);
    }

    public int[][] getMaximum() {
        return copyMatrix(maximum);
    }

    public int[][] getNeed() {
        return copyMatrix(need);
    }

    public int[] getAvailable() {
        return available.clone();
    }

    private int[][] copyMatrix(int[][] matrix) {

        int[][] copy = new int[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }

        return copy;
    }

    public String checkSafety() {

        int[] work = available.clone();
        boolean[] finished = new boolean[processes];
        int[] safeSequence = new int[processes];

        int count = 0;

        while (count < processes) {

            boolean found = false;

            for (int i = 0; i < processes; i++) {

                if (!finished[i] && canFinish(i, work)) {

                    for (int j = 0; j < resources; j++) {
                        work[j] += allocation[i][j];
                    }

                    finished[i] = true;
                    safeSequence[count] = i;
                    count++;
                    found = true;
                }
            }

            if (!found) {
                return "UNSAFE STATE\nNo safe sequence exists.";
            }
        }

        StringBuilder result =
            new StringBuilder("SAFE STATE\nSafe Sequence: ");

        for (int i = 0; i < processes; i++) {

            result.append("P").append(safeSequence[i]);

            if (i < processes - 1) {
                result.append(" -> ");
            }
        }

        return result.toString();
    }

    private boolean canFinish(int process, int[] work) {

        for (int j = 0; j < resources; j++) {

            if (need[process][j] > work[j]) {
                return false;
            }
        }

        return true;
    }

    public String requestResources(int process, int[] request) {

        if (process < 0 || process >= processes) {
            throw new IllegalArgumentException("Invalid process number.");
        }

        if (request == null || request.length != resources) {
            throw new IllegalArgumentException(
                "Invalid request dimensions."
            );
        }

        boolean nonZero = false;

        for (int j = 0; j < resources; j++) {

            if (request[j] < 0) {
                throw new IllegalArgumentException(
                    "Request values cannot be negative."
                );
            }

            if (request[j] > need[process][j]) {
                return "REQUEST DENIED\nRequest exceeds remaining Need.";
            }

            if (request[j] > available[j]) {
                return "REQUEST DENIED\nInsufficient available resources.";
            }

            if (request[j] > 0) {
                nonZero = true;
            }
        }

        if (!nonZero) {
            return "REQUEST DENIED\nRequest must be greater than zero.";
        }

        for (int j = 0; j < resources; j++) {
            available[j] -= request[j];
            allocation[process][j] += request[j];
            need[process][j] -= request[j];
        }

        String safetyResult = checkSafety();

        if (safetyResult.startsWith("SAFE STATE")) {
            return "REQUEST GRANTED\n" + safetyResult;
        }

        for (int j = 0; j < resources; j++) {
            available[j] += request[j];
            allocation[process][j] -= request[j];
            need[process][j] += request[j];
        }

        return "REQUEST DENIED\nRequest would lead to an unsafe state.";
    }
}
