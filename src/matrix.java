import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.*;

public class matrix {
    private double[][] array;
    private int rowAmount, columnAmount;
    private double epsilon;
    private double[] sums;
    private int[] notNullCombination;


    public void Print() {
        int i, j;
        for (i = 0; i < rowAmount; i++) {
            for (j = 0; j < columnAmount; j++)
                System.out.printf("%15.6E", array[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    public void Init(String s) throws FileNotFoundException {
        File file = new File(s);
        Scanner scan = new Scanner(file);
        Pattern pat = Pattern.compile("[ \t]+");
        String str = scan.nextLine();
        String[] sn = pat.split(str);
        rowAmount = Integer.parseInt(sn[0]);
        columnAmount = Integer.parseInt(sn[1]) + 1;
        epsilon = Math.pow(10, -Double.parseDouble(sn[2]) - 1);
        notNullCombination = new int[rowAmount];
        this.Create(rowAmount, columnAmount);
        int i, j;
        for (i = 0; i < rowAmount; i++) {
            str = scan.nextLine();
            sn = pat.split(str);
            notNullCombination[i] = i;
            for (j = 0; j < columnAmount; j++)
                array[i][j] = Double.parseDouble(sn[j]);
        }
        scan.close();
        sums = SumOfLines(array);

    }


    public boolean CheckForZeros(int[] combination) {
        for (int i = 0; i < rowAmount; i++)
            if (CompareToZero(array[combination[i]][i]))
                return false;
        return true;
    }


    //проверка достаточного условия сходимости нашей изначальной системы
    public boolean CheckSCC(int[] combination) {
        boolean strictlyMore = false;
        boolean haveZeroes = false;
        boolean SCCworked = true;
        for (int i = 0; i < rowAmount; i++) {
            if (CompareToZero(array[combination[i]][i])) haveZeroes = true;
            double sum = Math.abs(sums[combination[i]]) - Math.abs(array[combination[i]][i]) - Math.abs(array[combination[i]][i]);
            if (sum <= 0) {
                if (sum < 0)
                    strictlyMore = true;
            } else SCCworked = false;
        }
        if (!haveZeroes) {
            if (rowAmount >= 0) System.arraycopy(combination, 0, notNullCombination, 0, rowAmount);
        }


        return strictlyMore && SCCworked;
    }


    //поиск сумм строк матрицы
    public double[] SolveByIterations() {
        double[] result = new double[rowAmount];
        double x, summary;
        do {
            x = result[0];
            for (int i = 0; i < rowAmount; i++) {
                summary = array[i][columnAmount - 1];
                for (int j = 0; j < rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * array[i][j];
                result[i] = summary / array[i][i];
            }
        } while (Math.abs(result[0] - Math.abs(x)) >= epsilon);
        return result;
    }


    public double[] SolveByIterationsWithControl() {
        double[] result = new double[rowAmount];
        double x, summary;
        double delta;
        double localMaximum = Double.MIN_VALUE;
        int i, j;
        //проверка системы на сходимость через проверку первых 10 итераций
        for (int q = 0; q < 10; q++) {
            x = result[0];
            for (i = 0; i < rowAmount; i++) {
                summary = array[i][columnAmount - 1];
                for (j = 0; j < rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * array[i][j];
                result[i] = summary / array[i][i];
            }
            delta = Math.abs(Math.abs(x) - Math.abs(result[0]));
            if (q > 5) {
                if (delta > localMaximum)
                    localMaximum = delta;
            }
        }
        //если система не сходится, то возвращаем null
        if (localMaximum > epsilon) return null;
        //если система сходится - решаем дальше
        do {
            x = result[0];
            for (i = 0; i < rowAmount; i++) {
                summary = array[i][columnAmount - 1];
                for (j = 0; j < rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * array[i][j];
                result[i] = summary / array[i][i];
            }
        } while (Math.abs(result[0] - Math.abs(x)) >= epsilon);
        return result;
    }


    public boolean RemoveZeroesFromDiagonal(int diag, int[] combination) {
        //если можно сделать перестановку - делаем
        if (CheckSCC(combination))
            return true;
        else if (diag >= rowAmount) return false;
        if (RemoveZeroesFromDiagonal(diag + 1, combination))
            return true;

        for (int i = diag + 1; i < rowAmount; i++) {
            SwapElements(i, diag, combination);
            if (RemoveZeroesFromDiagonal(diag + 1, combination))
                return true;
            SwapElements(diag, i, combination);
        }

        return false;
    }

    public int CheckAnswer(int[] combination) {
        if (RemoveZeroesFromDiagonal(0, combination)) {
            array = ReplaceWithCombination(combination);
            return 2;
        }

        if (CheckForZeros(notNullCombination)) {
            array = ReplaceWithCombination(notNullCombination);
            return 3;
        }
        return 1;
    }

    public double[][] ReplaceWithCombination(int[] combination) {
        double[][] matrix = new double[rowAmount][];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = array[combination[i]];
        }
        this.array = matrix;
        return matrix;
    }


    public int[] GetCombination() {
        int[] result = new int[rowAmount];
        for (int i = 0; i < rowAmount; i++) {
            result[i] = i;
        }
        return result;
    }

    private boolean CompareToZero(double a) {
        return (Math.abs(a) < epsilon);
    }

    private double[] SumOfLines(double[][] matrix) {
        double[] temp = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++)
                temp[i] += Math.abs(matrix[i][j]);
        }
        return temp;
    }

    private void Create(int k, int l) {
        this.array = new double[k][];
        int i;
        for (i = 0; i < k; i++)
            this.array[i] = new double[l];
    }

    private void SwapElements(int i, int j, int[] combination) {
        int temp = combination[i];
        combination[i] = combination[j];
        combination[j] = temp;
    }

}
