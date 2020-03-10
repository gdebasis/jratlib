package mnist;

import java.io.IOException;

public class MnistReadTest {

    public static void main(String[] args) throws IOException {
        MnistMatrix[] mnistMatrix = new MnistDataReader().readData("mnist/train-images.idx3-ubyte", "mnist/train-labels.idx1-ubyte");
        
        for (MnistMatrix m: mnistMatrix)
            printMnistMatrix(m);
        
        /*
        mnistMatrix = new MnistDataReader().readData("mnist/t10k-images.idx3-ubyte", "mnist/t10k-labels.idx1-ubyte");
        printMnistMatrix(mnistMatrix[0]);
        */
    }

    private static void printMnistMatrix(final MnistMatrix matrix) {
        System.out.println("label: " + matrix.getLabel());
        for (int r = 0; r < Math.min(2, matrix.getNumberOfRows()); r++ ) {
            for (int c = 0; c < Math.min(2, matrix.getNumberOfColumns()); c++) {
                System.out.print(matrix.getValue(r, c) + " ");
            }
        }
        System.out.println();
    }
}
