package org.example.test.code;
//
// Classes that this class uses was renamed.
//
//: org.example.test.code.OtherClass -> org.example.test.code.OtherClassRenamed
//: org.example.test.otherpackage.SomeClass -> org.example.test.otherpackage.SomeClassRenamed
//

import org.example.test.otherpackage./*|*/SomeClass/*->SomeClassRenamed*/;

public class TestOtherClassRenamed {

    private /*|*/SomeClass/*->SomeClassRenamed*/ someObject = new /*|*/SomeClass/*->SomeClassRenamed*/();
    private /*|*/OtherClass/*->OtherClassRenamed*/ otherObject = new /*|*/OtherClass/*->OtherClassRenamed*/();

    public TestOtherClassRenamed() {

        /*|*/SomeClass/*->SomeClassRenamed*/ var1 = someObject;
        /*|*/OtherClass/*->OtherClassRenamed*/ var2 = otherObject;

    }

}