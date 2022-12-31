package com.z.fileselectorrelease;

import org.junit.Test;

import static org.junit.Assert.*;

import android.net.Uri;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String path_pattern = "/storage/emulated/0/Android/data/";
        String path = "/storage/emulated/0/Android/data/";
        path = path.replace(path_pattern, "");
        System.out.println("path = " + path);
    }
}