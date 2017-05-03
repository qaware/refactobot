package org.example.test.code;
//
// Fully-qualified class names should be adapted when they occur in string literals
//
//: org.example.test.code.foo.SomeClass -> org.example.test.code.bar.RenamedClass
//

@NamedQueries({
        @NamedQuery(name = "queryName",
                query = "SELECT new org.example.test.code./*|*/foo.SomeClass/*->bar.RenamedClass*/(...)")
})
public class StringLiteralExample {

    private static final String CLASS_NAME =
            "contains org.example.test.code./*|*/foo.SomeClass/*->bar.RenamedClass*/ among some other text";


}