package parser;

import atom.Token;

import java.text.ParseException;

public class Lexer {
    String string;
    int curPos;
    int curChar;
    Token curToken;
    String word;

    Lexer(String string) throws ParseException {
        this.string = string;
        curPos = 0;
        nextChar();
    }

    private void nextChar() throws ParseException{
        if (curPos < string.length()){
            curChar = string.charAt(curPos);
            curPos++;
        } else {
            curChar = -1;
        }
    }

    private void skipWhiteSpaces() throws ParseException {
        while (Character.isWhitespace(curChar)){
            nextChar();
        }
    }


    void nextToken() throws ParseException {
        skipWhiteSpaces();

        if (Character.isLetter(curChar)){
            StringBuilder sb = new StringBuilder();
            sb.append((char) curChar);
            nextChar();
            while (Character.isLetter(curChar) || Character.isDigit(curChar) || curChar == '\''){
                sb.append((char) curChar);
                nextChar();
            }
            word = sb.toString();
            curToken = Token.WORD;
            return;
        }

        switch (curChar){
            case '(':
                curToken = Token.LBR;
                nextChar();
                break;
            case ')':
                curToken = Token.RBR;
                nextChar();
                break;
            case ',':
                curToken = Token.COMMA;
                nextChar();
                break;
            case ';':
                curToken = Token.SEMI;
                nextChar();
                break;
            case '*':
                curToken = Token.STAR;
                nextChar();
                break;
            case -1:
                curToken = Token.END;
                break;
             default:
                 throw new ParseException("Illegal character " + (char) curChar, curPos);
        }
    }

    public int getCurChar() {
        return curChar;
    }

    public int getCurPos() {
        return curPos;
    }

    public String getWord() {
        return word;
    }
}
