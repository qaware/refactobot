package de.qaware.tools.bulkrename.scanner.test.a;

import java.lang.annotation.*;

/**
 * Test annotation.
 */
/*non-public*/
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationA {
}
