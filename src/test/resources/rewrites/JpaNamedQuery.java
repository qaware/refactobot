package org.example.test.code;
//
// Class names should be changed in JPA named queries, even if they appear unqualified. Using standard naming conventions,
// it should be fine to cover all classes, not just entities.
//
//: org.example.test.code.entity.OldEntity -> org.example.test.code.movedentity.NewEntity
//

@NamedQueries({
        @NamedQuery(name = "queryName",
                query = "SELECT "
                + "/*|*/OldEntity/*->NewEntity*/ e"
                + " WHERE id = :id")
})
public class JPANamedQueryExample {


}