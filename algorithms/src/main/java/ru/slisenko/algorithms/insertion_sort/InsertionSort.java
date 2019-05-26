package ru.slisenko.algorithms.insertion_sort;

public class InsertionSort {

    public void sort(int[] arr){
        int i, j;

        for (i = 1; i < arr.length; i++) {
            j = i;

            while (j > 0 && arr[j] < arr[j-1]) {
                swap(arr, j, j - 1);
                j = j - 1;
            }
        }
    }

    private void swap(int[] arr, int a, int b) {
        int tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
    }
}
