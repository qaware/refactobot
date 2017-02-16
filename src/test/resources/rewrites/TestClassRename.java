package org.example.test.code;
//
// Simple rename of a class.
//
//: org.example.test.code.TestClassRename -> org.example.test.code.NewClassName
//: org.example.test.code.UnrelatedClass
//

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class /*|*/TestClassRename/*->NewClassName*/ {

    private List<String> geeting = new ArrayList<>();
    private java.util.ArrayDeque<File> queue = new ArrayDeque<>();

    public /*|*/TestClassRename/*->NewClassName*/() {
        greeting.add("Hello");
        greeting.add("World");
    }

}