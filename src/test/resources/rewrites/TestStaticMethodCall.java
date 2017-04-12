package org.example.test.code;
//
// A class that this class uses was moved to another package.
//
//: org.example.test.util.UtilClass -> org.example.test.util.RenamedUtilClass
//: org.example.test.util.OtherUtilClass -> org.example.test.util.OtherRenamedUtilClass
//

import java.util.ArrayList;
import org.example.test.util./*|*/UtilClass/*->RenamedUtilClass*/;

public class TestOtherClassMoved {


    public void method() {

        /*|*/UtilClass/*->RenamedUtilClass*/.staticMethod("hello");

        /*|*/UtilClass/*->RenamedUtilClass*/.staticField = /*|*/UtilClass/*->RenamedUtilClass*/.otherStaticField;

        Class<?> clazz = /*|*/UtilClass/*->RenamedUtilClass*/.class;

        org.example.test.util./*|*/OtherUtilClass/*->OtherRenamedUtilClass*/.staticMethod("hello");
    }

}