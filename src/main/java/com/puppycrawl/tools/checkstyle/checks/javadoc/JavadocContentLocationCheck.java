///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2025 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.javadoc;

import java.util.Locale;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtil;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * <div>
 * Checks that the Javadoc content begins from the same position
 * for all Javadoc comments in the project. Any leading asterisks and spaces
 * are not counted as the beginning of the content and are therefore ignored.
 * </div>
 *
 * <p>
 * It is possible to enforce two different styles:
 * </p>
 * <ul>
 * <li>
 * {@code first_line} - Javadoc content starts from the first line:
 * <div class="wrapper"><pre class="prettyprint"><code class="language-java">
 * &#47;** Summary text.
 *   * More details.
 *   *&#47;
 * public void method();
 * </code></pre></div>
 * </li>
 * <li>
 * {@code second_line} - Javadoc content starts from the second line:
 * <div class="wrapper"><pre class="prettyprint"><code class="language-java">
 * &#47;**
 *   * Summary text.
 *   * More details.
 *   *&#47;
 * public void method();
 * </code></pre></div>
 * </li>
 * </ul>
 *
 * <p>
 * Notes:
 * This check does not validate the Javadoc summary itself nor its presence.
 * The check will not report any violations for missing or malformed javadoc summary.
 * To validate the Javadoc summary use
 * <a href="https://checkstyle.org/checks/javadoc/summaryjavadoc.html">
 * SummaryJavadoc</a> check.
 * </p>
 *
 * <p>
 * The <a href="https://docs.oracle.com/en/java/javase/17/docs/specs/javadoc/doc-comment-spec.html">
 * Documentation Comment Specification</a> permits leading asterisks on the first line.
 * For these Javadoc comments:
 * </p>
 * <div class="wrapper"><pre class="prettyprint"><code class="language-java">
 * &#47;***
 *   * Some text.
 *   *&#47;
 * &#47;************
 *   * Some text.
 *   *&#47;
 * &#47;**           **
 *   * Some text.
 *   *&#47;
 * </code></pre></div>
 *
 * <p>
 * The documentation generated will be just "Some text." without any asterisks.
 * Since these asterisks will not appear in the generated documentation,
 * they should not be considered as the beginning of the Javadoc content.
 * In such cases, the check assumes that the Javadoc content begins on the second line.
 * </p>
 * <ul>
 * <li>
 * Property {@code location} - Specify the policy on placement of the Javadoc content.
 * Type is {@code com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocContentLocationOption}.
 * Default value is {@code second_line}.
 * </li>
 * </ul>
 *
 * <p>
 * Parent is {@code com.puppycrawl.tools.checkstyle.TreeWalker}
 * </p>
 *
 * <p>
 * Violation Message Keys:
 * </p>
 * <ul>
 * <li>
 * {@code javadoc.content.first.line}
 * </li>
 * <li>
 * {@code javadoc.content.second.line}
 * </li>
 * </ul>
 *
 * @since 8.27
 */
@StatelessCheck
public class JavadocContentLocationCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties" file.
     */
    public static final String MSG_JAVADOC_CONTENT_FIRST_LINE = "javadoc.content.first.line";

    /**
     * A key is pointing to the warning message text in "messages.properties" file.
     */
    public static final String MSG_JAVADOC_CONTENT_SECOND_LINE = "javadoc.content.second.line";

    /**
     * Specify the policy on placement of the Javadoc content.
     */
    private JavadocContentLocationOption location = JavadocContentLocationOption.SECOND_LINE;

    @Override
    public int[] getRequiredTokens() {
        return new int[] {
            TokenTypes.BLOCK_COMMENT_BEGIN,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public boolean isCommentNodesRequired() {
        return true;
    }

    /**
     * Setter to specify the policy on placement of the Javadoc content.
     *
     * @param value string to decode location from
     * @throws IllegalArgumentException if unable to decode
     * @since 8.27
     */
    public void setLocation(String value) {
        location = JavadocContentLocationOption.valueOf(value.trim().toUpperCase(Locale.ENGLISH));
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (isMultilineComment(ast) && JavadocUtil.isJavadocComment(ast)) {
            final String commentContent = JavadocUtil.getJavadocCommentContent(ast);
            final int indexOfFirstNonBlankLine = findIndexOfFirstNonBlankLine(commentContent);
            if (indexOfFirstNonBlankLine >= 0) {
                if (location == JavadocContentLocationOption.FIRST_LINE
                        && indexOfFirstNonBlankLine != 0) {
                    log(ast, MSG_JAVADOC_CONTENT_FIRST_LINE);
                }
                else if (location == JavadocContentLocationOption.SECOND_LINE
                        && indexOfFirstNonBlankLine != 1) {
                    log(ast, MSG_JAVADOC_CONTENT_SECOND_LINE);
                }
            }
        }
    }

    /**
     * Checks if a DetailAST of type {@code TokenTypes.BLOCK_COMMENT_BEGIN} span
     * more than one line. The node always has at least one child of the type
     * {@code TokenTypes.BLOCK_COMMENT_END}.
     *
     * @param node node to check
     * @return {@code true} for multi-line comment nodes
     */
    private static boolean isMultilineComment(DetailAST node) {
        return !TokenUtil.areOnSameLine(node, node.getLastChild());
    }

    /**
     * Returns the index of the first non-blank line.
     * All lines consists only of asterisks and whitespaces are treated as blank.
     *
     * @param commentContent Javadoc content to process
     * @return the index of the first non-blank line or {@code -1} if all lines are blank
     */
    private static int findIndexOfFirstNonBlankLine(String commentContent) {
        int lineNo = 0;
        boolean noContent = true;
        for (int i = 0; i < commentContent.length(); ++i) {
            final char character = commentContent.charAt(i);
            if (character == '\n') {
                ++lineNo;
            }
            else if (character != '*' && !Character.isWhitespace(character)) {
                noContent = false;
                break;
            }
        }
        if (noContent) {
            lineNo = -1;
        }
        return lineNo;
    }

}
