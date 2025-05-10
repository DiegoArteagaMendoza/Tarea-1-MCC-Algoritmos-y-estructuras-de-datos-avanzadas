import java.util.*;

public class algoritmosTarea1 {
    public static void main(String[] args) {
        int[] tamaños = { 100000, 500000, 1000000, 1500000, 2000000 };
        String modo = "pocas"; // Puedes cambiar a "muchas" si quieres

        for (int size : tamaños) {
            int[] arreglo1 = generarArreglo(size, modo);
            int[] arreglo2Asc = arreglo1.clone();
            int[] arreglo2Desc = arreglo1.clone();
            int[] arreglo3 = arreglo1.clone();

            System.out.println("Porte del arreglo: " + size);
            System.out.println("=================================");

            System.out.println("Algoritmo 1");
            ALG1.ejecutar(arreglo1);
            System.out.println("---------------------------------");

            System.out.println("Algoritmo 2");
            System.out.println("ascendente");
            ALG2.ejecutar(arreglo2Asc, "ascendente");
            System.out.println("---------------------------------");
            System.out.println("descendente");
            ALG2.ejecutar(arreglo2Desc, "descendente");
            System.out.println("---------------------------------");

            System.out.println("Algoritmo 3");
            ALG3.ejecutar(arreglo3);
        }
    }

    public static int[] generarArreglo(int size, String modo) {
        Random rand = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(size * 10);
        }

        int repeticiones = modo.equalsIgnoreCase("muchas") ? size / 2 : size / 10;
        for (int i = 0; i < repeticiones; i++) {
            int source = rand.nextInt(size);
            int target = rand.nextInt(size);
            while (target == source) {
                target = rand.nextInt(size);
            }
            array[target] = array[source];
        }

        return array;
    }
}

class ALG1 {
    public static int moda(int[] A) {
        int moda = A[0], frecuenciaMax = 1;
        for (int i = 0; i < A.length; i++) {
            int frecuencia = 1;
            for (int j = i + 1; j < A.length; j++) {
                if (A[i] == A[j]) frecuencia++;
            }
            if (frecuencia > frecuenciaMax || (frecuencia == frecuenciaMax && A[i] < moda)) {
                frecuenciaMax = frecuencia;
                moda = A[i];
            }
        }
        return moda;
    }

    public static void ejecutar(int[] A) {
        long inicio = System.currentTimeMillis();
        int resultado = moda(A);
        long fin = System.currentTimeMillis();
        System.out.println("Moda: " + resultado);
        System.out.println("Tiempo ALG1: " + (fin - inicio) + " ms");
    }
}

class ALG2 {
    public static int[] ordenar(int[] A, String sentido) {
        int[] copia = Arrays.copyOf(A, A.length);
        Arrays.sort(copia);
        if ("descendente".equalsIgnoreCase(sentido)) {
            for (int i = 0, j = copia.length - 1; i < j; i++, j--) {
                int temp = copia[i];
                copia[i] = copia[j];
                copia[j] = temp;
            }
        }
        return copia;
    }

    public static int moda(int[] A, String sentido) {
        int[] ordenado = ordenar(A, sentido);
        int moda = ordenado[0], frecuenciaMax = 1, frecuenciaActual = 1;

        for (int i = 1; i < ordenado.length; i++) {
            if (ordenado[i] == ordenado[i - 1]) {
                frecuenciaActual++;
            } else {
                if (frecuenciaActual > frecuenciaMax || 
                   (frecuenciaActual == frecuenciaMax && ordenado[i - 1] < moda)) {
                    frecuenciaMax = frecuenciaActual;
                    moda = ordenado[i - 1];
                }
                frecuenciaActual = 1;
            }
        }

        if (frecuenciaActual > frecuenciaMax || 
           (frecuenciaActual == frecuenciaMax && ordenado[ordenado.length - 1] < moda)) {
            moda = ordenado[ordenado.length - 1];
        }

        return moda;
    }

    public static void ejecutar(int arreglo[], String sentido) {
        long inicio = System.currentTimeMillis();
        System.out.println("Moda: " + moda(arreglo, sentido));
        long fin = System.currentTimeMillis();
        System.out.println("Tiempo ALG2: " + (fin - inicio) + " ms");
    }
}

class ALG3 {
    static class Rango {
        int[] arreglo;
        int inicio, fin;
        Rango(int[] arreglo, int inicio, int fin) {
            this.arreglo = arreglo;
            this.inicio = inicio;
            this.fin = fin;
        }

        int length() {
            return fin - inicio + 1;
        }

        int valor() {
            return arreglo[inicio]; // todos son iguales
        }
    }

    public static int moda(int[] A) {
        PriorityQueue<Rango> heterog = new PriorityQueue<>((a, b) -> b.length() - a.length());
        List<Rango> homog = new ArrayList<>();

        heterog.add(new Rango(A, 0, A.length - 1));

        while (!heterog.isEmpty()) {
            Rango actual = heterog.poll();

            int mediana = medianOfMedians(actual.arreglo, actual.inicio, actual.fin);
            int low = actual.inicio, high = actual.fin;
            int i = low;
            int lt = low, gt = high;

            while (i <= gt) {
                if (actual.arreglo[i] < mediana) {
                    swap(actual.arreglo, i++, lt++);
                } else if (actual.arreglo[i] > mediana) {
                    swap(actual.arreglo, i, gt--);
                } else {
                    i++;
                }
            }

            if ((gt - lt + 1) > 0) {
                homog.add(new Rango(actual.arreglo, lt, gt));
            }

            if (lt > actual.inicio) {
                heterog.add(new Rango(actual.arreglo, actual.inicio, lt - 1));
            }
            if (gt < actual.fin) {
                heterog.add(new Rango(actual.arreglo, gt + 1, actual.fin));
            }
        }

        // Comparar frecuencias reales entre todos los grupos homogéneos
        Map<Integer, Integer> frecuencias = new HashMap<>();
        for (Rango r : homog) {
            int valor = r.valor();
            frecuencias.put(valor, frecuencias.getOrDefault(valor, 0) + r.length());
        }

        int moda = -1, maxFrecuencia = -1;
        for (Map.Entry<Integer, Integer> entry : frecuencias.entrySet()) {
            int valor = entry.getKey();
            int freq = entry.getValue();
            if (freq > maxFrecuencia || (freq == maxFrecuencia && valor < moda)) {
                maxFrecuencia = freq;
                moda = valor;
            }
        }

        return moda;
    }

    private static int medianOfMedians(int[] arr, int start, int end) {
        int n = end - start + 1;
        if (n <= 5) {
            Arrays.sort(arr, start, end + 1);
            return arr[start + n / 2];
        }

        int numMedians = (int) Math.ceil(n / 5.0);
        int[] medians = new int[numMedians];

        for (int i = 0; i < numMedians; i++) {
            int s = start + i * 5;
            int e = Math.min(s + 4, end);
            Arrays.sort(arr, s, e + 1);
            medians[i] = arr[s + (e - s) / 2];
        }

        return medianOfMedians(medians, 0, medians.length - 1);
    }

    private static void swap(int[] A, int i, int j) {
        int tmp = A[i];
        A[i] = A[j];
        A[j] = tmp;
    }

    public static void ejecutar(int arreglo[]) {
        long inicio = System.currentTimeMillis();
        System.out.println("Moda: " + moda(arreglo));
        long fin = System.currentTimeMillis();
        System.out.println("Tiempo ALG3: " + (fin - inicio) + " ms");
    }
}
