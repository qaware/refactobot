package org.example.test.code;
//
// Test case for generated metamodel classes. These are not formally part of the codebase, but we generate references
// to the original classes instead, as a hack.
//
//: org.example.foobar.MyEntity -> org.example.entity.MyEntity
//: org.example.foobar.OtherEntity -> org.example.entity.NewEntity
//

import org.example./*|*/foobar/*->entity*/.MyEntity_;

public class TestOtherClassMoved {

    private MyEntity_ field1;

    private org.example./*|*/foobar.OtherEntity/*->entity.NewEntity*/_ field2;

}