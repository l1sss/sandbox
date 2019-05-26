package ru.slisenko.algorithms.insertion_sort;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class InsertionSortTest {
    private InsertionSort sorter;

    @Before
    public void init() {
        sorter = new InsertionSort();
    }

    @Test
    public void sortTest() {
        int[] sourceArray = {7, 11, 0, -4, 19};
        int[] expectedArray = {-4, 0, 7, 11, 19};

        sorter.sort(sourceArray);

        assertArrayEquals(expectedArray, sourceArray);
    }
}
