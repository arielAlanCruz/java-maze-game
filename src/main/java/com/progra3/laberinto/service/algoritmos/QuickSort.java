package com.progra3.laberinto.service.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class QuickSort {
    private QuickSort() {}

    public static <T> List<T> sort(List<T> input, Comparator<T> comparator) {
        if (input == null) return List.of();
        List<T> list = new ArrayList<>(input);
        quickSort(list, 0, list.size() - 1, comparator);
        return list;
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> comp) {
        if (low < high) {
            int p = partition(list, low, high, comp);
            quickSort(list, low, p - 1, comp);
            quickSort(list, p + 1, high, comp);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comp) {
        T pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comp.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }
}
