package org.example.test.code;
//
// Static method imports should be adapted properly
//
//: org.example.test.code.util.UtilClass -> org.example.test.code.util.RenamedUtilClass
//

import static org.example.test.code.util./*|*/UtilClass/*->RenamedUtilClass*/.staticallyImportedMethod;

public class StaticImportExample {

    public void method() {

        staticallyImportedMethod("hello");

    }

}