package org.example.test.code;
//
// FQCNs should be replaced in javadoc.
//
//: org.example.test.code.pkg.MyClass -> org.example.test.code.newpkg.RenamedClass
//

/**
 * Note that syntax highlighing is confusing here.
 *
 * @see /*|*/org.example.test.code.pkg.MyClass/*->org.example.test.code.newpkg.RenamedClass*/
 */
public class JavaDocExample {

    /**
     * @see /*|*/org.example.test.code.pkg.MyClass/*->org.example.test.code.newpkg.RenamedClass*/
     */
    void method() {}


}