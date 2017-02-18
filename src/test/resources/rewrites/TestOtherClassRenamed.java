package org.example.test.code;
//
// Classes that this class uses were renamed.
//
//: org.example.test.code.OtherClass -> org.example.test.code.OtherClassRenamed
//: org.example.test.otherpackage.SomeClass -> org.example.test.otherpackage.SomeClassRenamed
//: org.example.test.otherpackage.ThirdClass -> org.example.test.otherpackage.ThirdClassRenamed
//

import org.example.test.otherpackage./*|*/SomeClass/*->SomeClassRenamed*/;

public class TestOtherClassRenamed {

    private /*|*/SomeClass/*->SomeClassRenamed*/ someObject = new /*|*/SomeClass/*->SomeClassRenamed*/();
    private /*|*/OtherClass/*->OtherClassRenamed*/ otherObject = new /*|*/OtherClass/*->OtherClassRenamed*/();

    public TestOtherClassRenamed() {

        /*|*/SomeClass/*->SomeClassRenamed*/ var1 = someObject;
        /*|*/OtherClass/*->OtherClassRenamed*/ var2 = otherObject;

    }

    private void someMethod() {

        String value = /*|*/SomeClass/*->SomeClassRenamed*/.InnerClass.A_STRING_CONSTANT;

        int otherValue = org.example.test.otherpackage./*|*/ThirdClass/*->ThirdClassRenamed*/.INT_CONSTANT;
    }

}