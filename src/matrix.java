import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.Scanner;
import java.util.regex.*;

public class Matrix {
    private double[][] _array;
    private int _rowAmount, _columnAmount;
    private double _epsilon;
    private double[] _sums;
    private int[] _notNullCombination;


    public void Print() {
        int i, j;
        for (i = 0; i < _rowAmount; i++) {
            for (j = 0; j < _columnAmount; j++)
                System.out.printf("%15.6E", _array[i][j]);
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
        _rowAmount = Integer.parseInt(sn[0]);
        _columnAmount = Integer.parseInt(sn[1]) + 1;
        _epsilon = Math.pow(10, -Double.parseDouble(sn[2]) - 1);
        _notNullCombination = new int[_rowAmount];
        this.Create(_rowAmount, _columnAmount);
        int i, j;
        for (i = 0; i < _rowAmount; i++) {
            str = scan.nextLine();
            sn = pat.split(str);
            _notNullCombination[i] = i;
            for (j = 0; j < _columnAmount; j++)
                _array[i][j] = Double.parseDouble(sn[j]);
        }
        scan.close();
        _sums = SumOfLines(_array);

    }


    public boolean CheckForZeros(int[] combination) {
        for (int i = 0; i < _rowAmount; i++)
            if (CompareToZero(_array[combination[i]][i]))
                return false;
        return true;
    }


    //проверка достаточного условия сходимости нашей изначальной системы
    public boolean CheckSCC(int[] combination) {
        boolean strictlyMore = false;
        boolean haveZeroes = false;
        boolean SCCworked = true;
        for (int i = 0; i < _rowAmount; i++) {
            if (CompareToZero(_array[combination[i]][i])) haveZeroes = true;
            double sum = Math.abs(_sums[combination[i]]) - Math.abs(_array[combination[i]][i]) - Math.abs(_array[combination[i]][i]);
            if (sum <= 0) {
                if (sum < 0)
                    strictlyMore = true;
            } else SCCworked = false;
        }
        if (!haveZeroes) {
            if (_rowAmount >= 0) System.arraycopy(combination, 0, _notNullCombination, 0, _rowAmount);
        }


        return strictlyMore && SCCworked;
    }


    //поиск сумм строк матрицы
    public double[] SolveByIterations() {
        double[] result = new double[_rowAmount];
        double x, summary;
        do {
            x = result[0];
            for (int i = 0; i < _rowAmount; i++) {
                summary = _array[i][_columnAmount - 1];
                for (int j = 0; j < _rowAmount; j++) {
                    if (j != i) summary -= result[j] * _array[i][j];
                }
                result[i] = summary / _array[i][i];
            }
        } while (Math.abs(Math.abs(result[0]) - Math.abs(x)) >= _epsilon);
        return result;
    }


    public double[] SolveByIterationsWithControl() {
        double[] result = new double[_rowAmount];
        double x, summary;
        double delta;
        double localMaximum = Double.MIN_VALUE;
        int i, j;
        //проверка системы на сходимость через проверку первых 10 итераций
        for (int q = 0; q < 10; q++) {
            x = result[0];
            for (i = 0; i < _rowAmount; i++) {
                summary = _array[i][_columnAmount - 1];
                for (j = 0; j < _rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * _array[i][j];
                result[i] = summary / _array[i][i];
            }
            delta = Math.abs(Math.abs(x) - Math.abs(result[0]));
            if (q > 5) {
                if (delta > localMaximum)
                    localMaximum = delta;
            }
        }
        //если система не сходится, то возвращаем null
        if (localMaximum > _epsilon) return null;
        //если система сходится - решаем дальше
        do {
            x = result[0];
            for (i = 0; i < _rowAmount; i++) {
                summary = _array[i][_columnAmount - 1];
                for (j = 0; j < _rowAmount; j++)
                    if (j != i)
                        summary -= result[j] * _array[i][j];
                result[i] = summary / _array[i][i];
            }
        } while (Math.abs(result[0] - Math.abs(x)) >= _epsilon);
        return result;
    }


    public boolean RemoveZeroesFromDiagonal(int diag, int[] combination) {
        //если можно сделать перестановку - делаем
        if (CheckSCC(combination))
            return true;
        else if (diag >= _rowAmount) return false;
        if (RemoveZeroesFromDiagonal(diag + 1, combination))
            return true;

        for (int i = diag + 1; i < _rowAmount; i++) {
            SwapElements(i, diag, combination);
            if (RemoveZeroesFromDiagonal(diag + 1, combination))
                return true;
            SwapElements(diag, i, combination);
        }

        return false;
    }

    public Result CheckAnswer(int[] combination) {
        if (RemoveZeroesFromDiagonal(0, combination)) {
            _array = ReplaceWithCombination(combination);
            return Result.HAVE_SCC;
        }

        if (CheckForZeros(_notNullCombination)) {
            _array = ReplaceWithCombination(_notNullCombination);
            return Result.NO_SCC;
        }
        return Result.IMPOSSIBLE_TO_SOLVE;
    }

    public double[][] ReplaceWithCombination(int[] combination) {
        double[][] matrix = new double[_rowAmount][];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = _array[combination[i]];
        }
        this._array = matrix;
        return matrix;
    }


    public int[] GetCombination() {
        int[] result = new int[_rowAmount];
        for (int i = 0; i < _rowAmount; i++) {
            result[i] = i;
        }
        return result;
    }

    private boolean CompareToZero(double a) {
        return (Math.abs(a) < _epsilon);
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
        this._array = new double[k][];
        int i;
        for (i = 0; i < k; i++)
            this._array[i] = new double[l];
    }

    private void SwapElements(int i, int j, int[] combination) {
        int temp = combination[i];
        combination[i] = combination[j];
        combination[j] = temp;
    }

}
