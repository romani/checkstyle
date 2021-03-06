package com.puppycrawl.tools.checkstyle.checks.javadoc.javadocmethod;

/**
 * Config:
 * allowMissingParamTags = true
 * allowMissingReturnTag = true
 */
public class InputJavadocMethodMissingJavadocTagsDefault {
    /**
     * Missing return.
     *
     * @param number to return
     * @throws ThreadDeath sometimes
     */
    int missingReturn(int number) throws ThreadDeath { // violation
        return number;
    }

    /**
     * Missing param.
     *
     * @return number
     * @throws ThreadDeath sometimes
     */
    int missingParam(int number) throws ThreadDeath { // violation
        return number;
    }

    /**
     * Missing throws.
     *
     * @param number to return
     * @return number
     */
    int missingThrows(int number) throws ThreadDeath { // ok
        return number;
    }

    /**
     * Missing return, but {@inheritDoc} is present.
     *
     * @param number to return
     * @throws java.util.NoSuchElementException sometimes
     */ // ok
    int missingReturnButInheritDocPresent(int number) throws java.util.NoSuchElementException {
        return number;
    }

    /**
     * Missing return in the middle.
     *
     * @param number to return
     * @return
     * @throws java.util.NoSuchElementException sometimes
     */
    private int missingReturnInTheMiddle(int number) { // violation
        return number;
    }

    /**
     * Missing return at the end.
     *
     * @param number to return
     * @return
     */
    private int missingReturnAtTheEnd(int number) { // violation
        return number;
    }

    /**
     * Missing return at the end followed by empty line.
     *
     * @param number to return
     * @return
     *
     */
    private int missingReturnAtTheEndFollowedByEmptyLine(int number) { // violation
        return number;
    }
}
