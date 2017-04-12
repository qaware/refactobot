package org.example.test.code;
/*|*//*->import org.example.test.otherpackage.OtherClass;
import org.example.test.otherpackage.YetAnotherClass;
*/
//
// A class that this class uses was moved out of the common package.
//
//: org.example.test.code.OtherClass -> org.example.test.otherpackage.OtherClass
//: org.example.test.code.YetAnotherClass -> org.example.test.otherpackage.YetAnotherClass
//

public class ClassMovedOutOfPackage {

    private OtherClass other;

    private YetAnotherClass yetanother;

}