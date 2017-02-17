package org.example.test.code;
//
// This class is moved out of its package, so other classes it uses must now be imported.
// The import appears right before other imports.
//
//: org.example.test.code.ThisClassMovedOutOfPackage -> org.example.test.otherpackage.ThisClassMovedOutOfPackage
//: org.example.test.code.OtherClass
//

/*|*//*->import org.example.test.code.OtherClass;
*/import java.util.List;

public class ThisClassMovedOutOfPackage {

    private List<String> list;

    private OtherClass other;

}