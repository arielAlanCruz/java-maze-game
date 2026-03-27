package com.progra3.laberinto.service.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MergeSort {
    private MergeSort() {}

    public static <T> List<T> sort(List<T> input, Comparator<T> comparator) {
        if (input == null) return List.of();
        if (input.size() <= 1) return new ArrayList<>(input);
        List<T> list = new ArrayList<>(input);
        List<T> aux = new ArrayList<>(list);
        mergeSort(list, aux, 0, list.size() - 1, comparator);
        return list;
    }

    private static <T> void mergeSort(List<T> list, List<T> aux, int left, int right, Comparator<T> comp) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(list, aux, left, mid, comp);
        mergeSort(list, aux, mid + 1, right, comp);
        merge(list, aux, left, mid, right, comp);
    }

    private static <T> void merge(List<T> list, List<T> aux, int left, int mid, int right, Comparator<T> comp) {
        for (int k = left; k <= right; k++) aux.set(k, list.get(k));
        int i = left, j = mid + 1;
        for (int k = left; k <= right; k++) {
            if (i > mid) list.set(k, aux.get(j++));
            else if (j > right) list.set(k, aux.get(i++));
            else if (comp.compare(aux.get(i), aux.get(j)) <= 0) list.set(k, aux.get(i++));
            else list.set(k, aux.get(j++));
        }
    }
}
