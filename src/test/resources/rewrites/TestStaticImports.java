package org.example.test.code;
//
// Static method imports should be adapted properly
//
//: org.example.test.code.util.UtilClass -> org.example.test.code.util.RenamedUtilClass
//: org.example.test.code.util.OtherUtilClass -> org.example.test.code.util.RenamedOtherUtilClass
//

import static org.example.test.code.util./*|*/UtilClass/*->RenamedUtilClass*/.staticallyImportedMethod;
import static org.example.test.code.util./*|*/OtherUtilClass/*->RenamedOtherUtilClass*/.*;

public class StaticImportExample {

    public void method() {

        staticallyImportedMethod("hello");
        otherStaticallyImportedMethod();

    }

}