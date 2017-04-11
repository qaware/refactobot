package org.example.test.code;
//
// Simple rename of a class.
//
//: org.example.test.code.TypeAnnotation -> org.example.test.newpackage.NewTypeAnnotation
//: org.example.test.code.MethodAnnotation -> org.example.test.code.NewMethodAnnotation
//: org.example.test.code.LocalAnnotation -> org.example.test.code.NewLocalAnnotation
//: org.example.test.code.MarkerAnnotation -> org.example.test.code.NewMarkerAnnotation
//: org.example.test.code.SingleArgumentAnnotation -> org.example.test.code.NewSingleArgumentAnnotation
//: org.example.test.code.NamedQueries -> org.example.test.code.NewNamedQueries
//: org.example.test.code.NamedQuery -> org.example.test.code.NewNamedQuery
//: org.example.test.code.OtherAnnotation -> org.example.test.code.NewOtherAnnotation
//: my.fully.qualified.Annotation -> moved.and.RenamedAnnotation
//

/*|*//*->import org.example.test.newpackage.NewTypeAnnotation;
*/import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/*|*/@TypeAnnotation/*->@NewTypeAnnotation*/(booleanArgument = true, stringArgument = "ABC")
/*|*/@MarkerAnnotation/*->@NewMarkerAnnotation*/
/*|*/@SingleArgumentAnnotation/*->@NewSingleArgumentAnnotation*/("Hello")
/*|*/@NamedQueries/*->@NewNamedQueries*/({
        /*|*/@NamedQuery/*->@NewNamedQuery*/(name = "name",
                query = "query1"),
        /*|*/@NamedQuery/*->@NewNamedQuery*/(name = "name2",
                query = "query2" +
                        " in pieces")
})
public class TestAnnotation {

    private List<String> geeting = new ArrayList<>();
    private java.util.ArrayDeque<File> queue = new ArrayDeque<>();

    /*|*/@my.fully.qualified.Annotation/*->@moved.and.RenamedAnnotation*/
    private String something;

    /*|*/@MethodAnnotation/*->@NewMethodAnnotation*/
    public TestAnnotation() {
        greeting.add("Hello");

        /*|*/@LocalAnnotation/*->@NewLocalAnnotation*/
        String foo = "bar";
    }

}