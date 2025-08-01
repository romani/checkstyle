package com.google.checkstyle.test.chapter4formatting.rule462horizontalwhitespace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/** some javadoc. */
public class InputFormattedNoWhitespaceBeforeEllipsis {

  @Target(ElementType.TYPE_USE)
  @interface NonNull {}

  @Target(ElementType.TYPE_USE)
  @interface Size {
    int max();
  }

  // @NonNull int @NonNull ... field3; // non-compilable
  // @NonNull int @NonNull... field4; // non-compilable

  /** some javadoc. */
  void test1(String... param) {}

  /** some javadoc. */
  void test2(String... param) {}

  /** some javadoc. */
  void test3(String @NonNull ... param) {}

  /** some javadoc. */
  void test4(String @NonNull ... param) {}

  /** some javadoc. */
  void test5(String[]... param) {}

  /** some javadoc. */
  void test6(String[]... param) {}

  /** some javadoc. */
  void test7(String @NonNull []... param) {}

  /** some javadoc. */
  void test8(String @NonNull []... param) {}

  void test9(String @Size(max = 10) ... names) {}

  void test10(String @Size(max = 10) ... names) {}
}
