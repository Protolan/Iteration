import java.io.FileNotFoundException;

public class Test {
    public static void main(String[] args) {

        Matrix mat = new Matrix();
        try {
            mat.Init("src/Input.txt");
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!!!");
        }
        mat.Print();

        Result result;
        double[] array;

       /* System.out.println(mat.checkSCC(new int[]{0,1,2,3}));
        mat.replaceWithCombination(new int[]{0,1,2,3});
        mat.print();*/


        if (mat.CheckForZeros(mat.GetCombination())) {
            if (mat.CheckSCC(mat.GetCombination())) {
                array = mat.SolveByIterations();
                printArray(array);
            } else {
                array = mat.SolveByIterationsWithControl();
                if (array != null) printArray(array);
                else getResult(Result.IMPOSSIBLE_TO_SOLVE);
            }
        } else {
            result = mat.CheckAnswer(mat.GetCombination());
            getResult(result);
            mat.Print();
            if (result == Result.HAVE_SCC) {
                array = mat.SolveByIterations();
                printArray(array);
            }
            if (result == Result.NO_SCC) {
                array = mat.SolveByIterationsWithControl();
                if (array == null) getResult(Result.IMPOSSIBLE_TO_SOLVE);
                else printArray(array);
            }
        }
    }


    public static void getResult(Result result) {
        switch (result) {
            case IMPOSSIBLE_TO_SOLVE -> System.out.println("Систему нельзя решить итерационным методом \n");
            case HAVE_SCC -> System.out.println("Система больше не имеет 0 на диагонали и соблюдается ДУС \n");
            case NO_SCC -> System.out.println("Система больше не имеет 0 на диагонали, но не соблюдается ДУС \n");
        }
    }

    public static void printArray(double [] matrix) {
        System.out.println("Результат: ");
        for (double v : matrix) System.out.printf("%15.6E", v);
        System.out.println();
    }

}