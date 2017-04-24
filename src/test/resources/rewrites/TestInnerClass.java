package org.example.test.code;
//
// Imports of inner classes should be adapted properly, when the containing class is moved.
//
//: org.example.test.code.foo.SomeClass -> org.example.test.code.bar.RenamedClass
//: org.example.test.code.foo.OtherClass -> org.example.test.code.bar.RenamedOtherClass
//

import org.example.test.code./*|*/foo.SomeClass/*->bar.RenamedClass*/.InnerClass;

public class InnerClassExample {

    private InnerClass foo;

    private org.example.test.code./*|*/foo.OtherClass/*->bar.RenamedOtherClass*/.Inner bar;

    private void method() {
        org.example.test.code./*|*/foo.OtherClass/*->bar.RenamedOtherClass*/.Inner.field = 1;
    }

}