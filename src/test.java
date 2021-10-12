import java.io.FileNotFoundException;

public class test {
    public static void main(String[] args) {

        matrix mat = new matrix();
        try {
            mat.Init("src/Input.txt");
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!!!");
        }
        mat.Print();

        int result;
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
                else getResult(1);
            }
        } else {
            result = mat.CheckAnswer(mat.GetCombination());
            getResult(result);
            mat.Print();
            if (result == 2) {
                array = mat.SolveByIterations();
                printArray(array);
            }
            if (result == 3) {
                array = mat.SolveByIterationsWithControl();
                if (array == null) getResult(1);
                else printArray(array);
            }
        }
    }


    public static void getResult(int result) {
        switch (result) {
            case 1 -> System.out.println("Систему нельзя решить итерационным методом \n");
            case 2 -> System.out.println("Система больше не имеет 0 на диагонали и соблюдается ДУС \n");
            case 3 -> System.out.println("Система больше не имеет 0 на диагонали, но не соблюдается ДУС \n");
        }
    }

    public static void printArray(double [] matrix) {
        System.out.println("Результат: ");
        for (double v : matrix) System.out.printf("%15.6E", v);
        System.out.println();
    }

}