

package parser;

import java.util.Stack;
import java.util.regex.Pattern;

import datastructs.ANDNode;
import datastructs.AttributeNode;
import datastructs.BoolNode;
import datastructs.DivideNode;
import datastructs.EqualsNode;
import datastructs.GTENode;
import datastructs.GTNode;
import datastructs.IntNode;
import datastructs.LTENode;
import datastructs.LTNode;
import datastructs.MinusNode;
import datastructs.ModNode;
import datastructs.MultiplyNode;
import datastructs.Node;
import datastructs.NotEqualsNode;
import datastructs.ORNode;
import datastructs.PlusNode;
import datastructs.StrNode;

public class WhereParser {

    public static final String MUL = "*";
    public static final String DIV = "/";
    public static final String MOD = "%";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String LT = "<";
    public static final String LTE = "<=";
    public static final String GT = ">";
    public static final String GTE = ">=";
    public static final String NE = "!=";
    public static final String EQUALS = "=";
    public static final String AND = "AND";
    public static final String OR = "OR";
    
    private static int precedence(String op) {
        if (op.length() == 0) {
            return 999;
        }
        
        char c = (op.toUpperCase().toCharArray())[0];
        switch (c) {
        case '*':
            ;
        case '/':
            ;
        case '%':
            return 1;
        case '+':
            ;
        case '-':
            return 2;
        case '<':
            ;
        case '>':
            return 3;
        case '!':
            ;
        case '=':
            return 4;
        case 'A':
            ;
        case 'O':
            return 5;
        case '(':
            ;
        case ')':
            return 6;
        }
        return 999;
    }
    
    private Node attemptEval(Stack<Object> whereStack, Object right, Object next) {
        Node expr;
        String op;
        Object left;
        
        if(whereStack.size() > 1) {
            // check instanceof Node or String
            op = (String) whereStack.pop();
            left = whereStack.pop();
        } else return null;
        
        if (precedence(op) <= precedence((String)next)) {
            // expr = op + "(" + left + "," + right + ")";
            expr = buildTree(left, op, right);
        } else expr = null;
        
        if (expr == null) {
            whereStack.push(left);
            whereStack.push(op);
        }
        return expr;
    }
    
    private Node buildTree(Object left, String op, Object right) {
        Node lnode = null;
        Node opNode = null;
        Node rnode = null;

        /*
         *      op
         *     /  \
         *    /    \
         * left    right  
         */
        
        if (left instanceof Node) {
            lnode = (Node) left;    
        } else {
            String l = ((String) left).trim();
            if (Pattern.matches("^\\d*$", l)) {
                // Integer
                lnode = new IntNode(Integer.parseInt(l),null,null);
            } else if (l.equalsIgnoreCase("true") || l.equalsIgnoreCase("false")) {
                // Boolean
                lnode = (l.equalsIgnoreCase("true")) ? 
                        new BoolNode(true, null, null) : 
                            new BoolNode(false, null, null);
            } else if (l.startsWith("'") && l.endsWith("'")) {
                // String
                lnode = new StrNode(l, null, null);
            } else {
                // Attribute

                StringBuffer sb = new StringBuffer(l);
                String stripped;
                if (l.indexOf(".") != -1) {
                	stripped = sb.delete(0, l.indexOf(".")+1).toString();
                } else {
                	stripped = l;
                }
                lnode = new AttributeNode(stripped, null, null);
            }
        }
        
        if (right instanceof Node) {
            rnode = (Node) right;
        } else {
            String r = ((String) right).trim();
            if (Pattern.matches("^\\d*$", r)) {
                // Integer
                rnode = new IntNode(Integer.parseInt(r),null,null);
            } else if (r.equalsIgnoreCase("true") || r.equalsIgnoreCase("false")) {
                // Boolean
                rnode = (r.equalsIgnoreCase("true")) ? 
                        new BoolNode(true, null, null) : 
                            new BoolNode(false, null, null);
            } else if (r.startsWith("'") && r.endsWith("'")) {
                // String
                rnode = new StrNode(r, null, null);
            } else {
                // Attribute
            	
            	StringBuffer sb = new StringBuffer(r);
                String stripped;
                if (r.indexOf(".") != -1) {
                	stripped = sb.delete(0, r.indexOf(".")+1).toString();
                } else {
                	stripped = r;
                }
                rnode = new AttributeNode(stripped, null, null);
            	
                //rnode = new AttributeNode(r, null, null);
            }
        }
        
        if (op.equals(MUL)) {
            opNode = new MultiplyNode(lnode, rnode);
        } else if (op.equals(DIV)) {
            opNode = new DivideNode(lnode, rnode);
        } else if (op.equals(MOD)) {
            opNode = new ModNode(lnode, rnode);
        } else if (op.equals(PLUS)) {
            opNode = new PlusNode(lnode, rnode);
        } else if (op.equals(MINUS)) {
            opNode = new MinusNode(lnode, rnode);
        } else if (op.equals(LT)) {
            opNode = new LTNode(lnode, rnode);
        } else if (op.equals(LTE)) {
            opNode = new LTENode(lnode, rnode);
        } else if (op.equals(GT)) {
            opNode = new GTNode(lnode, rnode);
        } else if (op.equals(GTE)) {
            opNode = new GTENode(lnode, rnode);
        } else if (op.equals(NE)) {
            opNode = new NotEqualsNode(lnode, rnode);
        } else if (op.equals(EQUALS)) {
            opNode = new EqualsNode(lnode, rnode);
        } else if (op.equals(AND)) {
            opNode = new ANDNode(lnode, rnode);
        } else if (op.equals(OR)) {
            opNode = new ORNode(lnode, rnode);
        }
            
        return opNode;
    }
    
    private Stack<Object> evalParentheses(Stack<Object> whereStack) {
        /*
        if (tempStr.trim().length() > 0) whereStack.push(tempStr);
        tempStr = "";
        whereStack.push(String.valueOf(c));
        
        // Pop and evaluate until '('
        whereStack = evalParentheses(whereStack);
        continue;
        */
        
        String rightPar;
        Object right, check;
        
        if(whereStack.size() > 2) {
            // Pop right parenthesis ')'
            rightPar = (String) whereStack.pop();
        } else return null;
        
        if (rightPar.equals(")")) {
            while (true) {
                right = whereStack.pop();
                check = whereStack.pop();
                
                /*
                if (left instanceof Node) {
                    lnode = (Node) left;    
                } else {
                */
                if (check instanceof String && ((String)check).equals("(")) {
                    whereStack.push(right);
                    break;
                } else {
                    whereStack.push(check);
                }
                Node eval = attemptEval(whereStack, right, rightPar);
                if (eval == null) {
                    whereStack.push(right);
                    whereStack.push(rightPar);
                    break;
                } else {
                    whereStack.push(eval);
                }
            }
        }        
        return whereStack;
    }
    
    public Stack<Object> parse(String whereStr) {
        char[] whereChar = whereStr.toCharArray();
        Stack<Object> whereStack = new Stack<Object>();

        String tempStr = "";
        for (int i = 0; i < whereChar.length; i++) {

            char c = whereChar[i];
            
            switch (c) {
            case '*':
                ;
            case '/':
                ;
            case '%':
                ;
            case '+':
                ;
            case '-':
                if (tempStr.trim().length() > 0) whereStack.push(tempStr);
                tempStr = "";
                whereStack.push(String.valueOf(c));
                break;
            case '<':
                ;
            case '>':
                ;
            case '!':
                if (tempStr.trim().length() > 0) whereStack.push(tempStr);
                if (whereChar[i + 1] == '=') {
                    // check if next is '='
                    tempStr = String.valueOf(c).concat("=");
                    i += 1;
                } else {
                    tempStr = String.valueOf(c);
                }
                whereStack.push(tempStr);
                tempStr = "";
                break;
            case '=':
                if (tempStr.trim().length() > 0) whereStack.push(tempStr);
                tempStr = "";
                whereStack.push(String.valueOf(c));
                break;
            case '(':
                if (tempStr.trim().length() > 0) whereStack.push(tempStr);
                tempStr = "";
                whereStack.push(String.valueOf(c));
                continue;
            case ')':                
                if (tempStr.trim().length() > 0) whereStack.push(tempStr);
                tempStr = "";
                whereStack.push(String.valueOf(c));
                
                // Pop and evaluate until '('
                whereStack = evalParentheses(whereStack);
                continue;
            case ' ':
                ;
            case 13:
                ;
            case 9:
                // Ignore white spaces
                continue;
            default:
                String next = "";
                // Handling AND and OR operators
                if (i > 0 && i < whereChar.length - 1) {
                    if ((!Character.isLetter(whereChar[i - 1]))
                            && (c == 'a' || c == 'A')
                            && (whereChar[i + 1] == 'N' || whereChar[i + 1] == 'n')
                            && (whereChar[i + 2] == 'D' || whereChar[i + 2] == 'd')
                            && (!Character.isLetter(whereChar[i + 3]))) {

                        next = "AND";
                        i += 2;
                    } else if ((!Character.isLetter(whereChar[i - 1]))
                            && (c == 'o' || c == 'O')
                            && (whereChar[i + 1] == 'R' || whereChar[i + 1] == 'r')
                            && (!Character.isLetter(whereChar[i + 2]))) {

                        next = "OR";
                        i += 1;
                    }
                    if (next.equals("AND") || next.equals("OR")) {
                        if (tempStr.trim().length() > 0) whereStack.push(tempStr);
                        whereStack.push(next);
                        tempStr = "";
                        break;
                    }
                }
                
                tempStr = tempStr.concat(String.valueOf(c));
                continue;
            }
            
            while (whereStack.size() > 3) {
                Object next = whereStack.pop();
                Object right = whereStack.pop();
                Node eval = attemptEval(whereStack, right, next);
                if (eval == null) {
                    whereStack.push(right);
                    whereStack.push(next);
                    break;
                } else {
                    whereStack.push(eval);
                    whereStack.push(next);
                }
            }
        }
        
        if (tempStr.trim().length() > 0) whereStack.push(tempStr);
        while (whereStack.size() > 1) {
            Object right = whereStack.pop();
            Node eval = attemptEval(whereStack, right, " ");
            if (eval == null) {
                whereStack.push(tempStr);
                break;
            } else {
                whereStack.push(eval);
            }
        }
        return whereStack;
    }
}