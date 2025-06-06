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

package com.puppycrawl.tools.checkstyle.checks.indentation;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * Handler for case statements.
 *
 */
public class CaseHandler extends AbstractExpressionHandler {

    /**
     * The child elements of a case expression.
     */
    private static final int[] CASE_CHILDREN = {
        TokenTypes.LITERAL_CASE,
        TokenTypes.LITERAL_DEFAULT,
    };

    /**
     * Construct an instance of this handler with the given indentation check,
     * abstract syntax tree, and parent handler.
     *
     * @param indentCheck   the indentation check
     * @param expr          the abstract syntax tree
     * @param parent        the parent handler
     */
    public CaseHandler(IndentationCheck indentCheck,
        DetailAST expr, AbstractExpressionHandler parent) {
        super(indentCheck, "case", expr, parent);
    }

    @Override
    protected IndentLevel getIndentImpl() {
        return new IndentLevel(getParent().getIndent(),
                               getIndentCheck().getCaseIndent());
    }

    /**
     * Check the indentation of the case statement.
     */
    private void checkCase() {
        // only need to check if the case statement is not on the same line as
        // the parent switch statement
        if (!isSameLineAsSwitch()) {
            checkChildren(getMainAst(), CASE_CHILDREN, getIndent(), true, false);
        }
    }

    @Override
    public IndentLevel getSuggestedChildIndent(AbstractExpressionHandler child) {
        return getIndent();
    }

    @Override
    public void checkIndentation() {
        checkCase();
    }

    /**
     * Checks if CASE_GROUP node is placed at the same line as SWITCH_LITERAL node.
     *
     * @return true, if CASE_GROUP node is places at the same line as SWITCH_LITERAL node.
     */
    private boolean isSameLineAsSwitch() {
        final DetailAST parentNode = getMainAst().getParent();
        return TokenUtil.areOnSameLine(getMainAst(), parentNode);
    }

}
