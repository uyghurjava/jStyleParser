package cz.vutbr.web.csskit.antlr4;

public class CSSLexerState {

    public enum RecoveryMode {
        BALANCED,
        FUNCTION,
        RULE,
        DECL,
        NOBALANCE
    }

    public short curlyNest;
    public short parenNest;
    public short sqNest;
    public boolean quotOpen;
    public boolean aposOpen;

    public CSSLexerState() {
        this.curlyNest = 0;
        this.parenNest = 0;
        this.sqNest = 0;
        this.quotOpen = false;
        this.aposOpen = false;
    }

    public CSSLexerState(CSSLexerState clone) {
        this.curlyNest = clone.curlyNest;
        this.parenNest = clone.parenNest;
        this.sqNest = clone.sqNest;
        this.quotOpen = clone.quotOpen;
        this.aposOpen = clone.aposOpen;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CSSLexerState) {
            CSSLexerState that = (CSSLexerState) o;
            return (this.curlyNest == that.curlyNest &&
                    this.parenNest == that.parenNest &&
                    this.sqNest == that.sqNest &&
                    this.quotOpen == that.quotOpen &&
                    this.aposOpen == that.aposOpen);
        }
        return false;
    }

    /**
     * Checks whether all pair characters (single and double quotatation marks,
     * curly braces) are balanced
     */
    public boolean isBalanced() {
        return !aposOpen && !quotOpen && curlyNest == 0 && parenNest == 0 && this.sqNest == 0; 
    }

    /**
     * Checks whether some pair characters are balanced. Modes are:
     * <ul>
     * <li>BALANCED - everything must be balanced: single and double quotatation marks, curly braces, parentheses
     * <li>FUNCTION - within the function arguments: parentheses must be balanced
     * <li>RULE - within the CSS rule: all but curly braces
     * <li>DECL - within declaration: all, keep curly braces at desired state
     * </ul>
     *
     * @param mode  the desired recovery node
     * @param state the required lexer state (used for DECL mode)
     * @param t     the token that is being processed (used for DECL mode)
     */
    public boolean isBalanced(RecoveryMode mode, CSSLexerState state, CSSToken t) {
        if (mode == RecoveryMode.BALANCED)
            return !aposOpen && !quotOpen && curlyNest == 0 && parenNest == 0 && sqNest == 0;
        else if (mode == RecoveryMode.FUNCTION)
            return parenNest == 0 && sqNest == 0;
        else if (mode == RecoveryMode.RULE)
            return !aposOpen && !quotOpen && parenNest == 0 && sqNest == 0;
        else if (mode == RecoveryMode.DECL) {
            if (t.getType() == CSSLexer.RCURLY) //if '}' is processed the curlyNest has been already decreased
                return !aposOpen && !quotOpen && parenNest == 0 && sqNest == 0 && curlyNest == state.curlyNest - 1;
            else
                return !aposOpen && !quotOpen && parenNest == 0 && sqNest == 0 && curlyNest == state.curlyNest;
        } else
            return true;
    }

    @Override
    public String toString() {
        return "{=" + curlyNest +
                ", (=" + parenNest +
                ", [=" + sqNest +
                ", '=" + (aposOpen ? "1" : "0") +
                ", \"=" + (quotOpen ? "1" : "0");
    }
}
