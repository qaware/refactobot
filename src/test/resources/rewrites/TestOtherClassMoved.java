package org.example.test.code;
//
// A class that this class uses was moved to another package.
//
//: java.util.List -> java.myutil.List
//

import java./*|*/util/*->myutil*/.List;
import java.util.ArrayList;

public class TestOtherClassMoved {

    private List<String> stringList = new ArrayList<>();

    public TestOtherClassMoved() {

        stringList.add("a string");
    }

}