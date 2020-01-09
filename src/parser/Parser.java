package parser;

import atom.Token;

import java.text.ParseException;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class Parser {
    private Lexer lexer;

    public Parser(String string) throws ParseException {
        this.lexer = new Lexer(string);
    }

    public ParseTree parse() throws ParseException {
        lexer.nextToken();
        return S();
    }

    private ParseTree S() throws ParseException {
        ParseTree res;
        switch (lexer.curToken){
            case WORD:
                ParseTree type = parseType();
                ParseTree name = parseName();
                if (lexer.curToken != Token.LBR){
                    throw new ParseException("Illegal token in S " + lexer.curToken + " while expecting LBR", lexer.curPos);
                }
                ParseTree lbr = new ParseTree("(");
                lexer.nextToken();
                ParseTree args = parseArgs();
                if (lexer.curToken != Token.RBR){
                    throw new ParseException("Illegal token in S " + lexer.curToken + " while expecting RBR", lexer.curPos);
                }
                ParseTree rbr = new ParseTree(")");
                lexer.nextToken();
                if (lexer.curToken != Token.SEMI){
                    throw new ParseException("Illegal token in S " + lexer.curToken + " while expecting SEMI", lexer.curPos);
                }
                ParseTree semi = new ParseTree(";");
                lexer.nextToken();
                if (lexer.curToken != Token.END){
                    throw new ParseException("Illegal token in S " + lexer.curToken + " while expecting SEMI", lexer.curPos);
                }
                res = new ParseTree("Function", type, name, lbr, args, rbr, semi);
                break;
            default:
                throw new ParseException("Wrong first while parsing START at ", lexer.curPos);
        }
        return res;
    }

    private ParseTree parseArgs() throws ParseException{
        ParseTree res;
        switch (lexer.curToken){
            case WORD:
                ParseTree arg = parseArg();
                ParseTree arg_ = parseArg_();
                if (lexer.curToken != Token.RBR){
                    throw new ParseException("Illegal token in parsingArgs" + lexer.curToken + " while expecting RBR", lexer.curPos);
                }
                res = new ParseTree("Args", arg, arg_);
                break;
            case RBR:
                res = new ParseTree("Args");
                break;
            default:
                throw new ParseException("Wrong first while parsing Args", lexer.curPos);
        }
        return res;
    }

    private ParseTree parseArg() throws ParseException{
        ParseTree res;
        switch (lexer.curToken){
            case WORD:
                ParseTree type = parseType();
                ParseTree star = parseStar();
                ParseTree name = parseName();
                if (lexer.curToken != Token.COMMA && lexer.curToken != Token.RBR){
                    throw new ParseException("Illegal token in parsingArg" + lexer.curToken + " while expecting RBR || COMMA", lexer.curPos);
                }
                res = new ParseTree("Arg", type, star, name);
                break;
            default:
                throw new ParseException("Wrong first while parsing Arg", lexer.curPos);
        }
        return res;
    }

    private ParseTree parseArg_() throws ParseException{
        ParseTree res;
        switch (lexer.curToken){
            case COMMA:
                ParseTree comma = new ParseTree(",");
                lexer.nextToken();
                ParseTree type = parseType();
                ParseTree star = parseStar();
                ParseTree name = parseName();
                ParseTree arg_ = parseArg_();
                if (lexer.curToken != Token.RBR){
                    throw new ParseException("Illegal token in parsingArg_ " + lexer.curToken + " while expecting RBR", lexer.curPos);
                }
                res = new ParseTree("Arg_", comma, type, star, name, arg_);
                break;
            case RBR:
                res = new ParseTree("Arg_");
                break;
            default:
                throw new ParseException("Wrong first while parsingArg_ at ", lexer.curPos);
        }
        return res;
    }

    private ParseTree parseStar() throws ParseException{
        ParseTree res;
        switch (lexer.curToken){
            case STAR:
                ParseTree starT = new ParseTree("*");
                lexer.nextToken();
                ParseTree star = parseStar();
                if (lexer.curToken != Token.WORD){
                    throw new ParseException("Illegal token in parsingStar" + lexer.curToken + " while expecting WORD", lexer.curPos);
                }
                res = new ParseTree("Star", starT, star);
                break;
            case WORD:
                res = new ParseTree("Star");
                break;
            default:
                throw new ParseException("Wrong first while parsingArg_", lexer.curPos);
        }
        return res;
    }

    private ParseTree parseType() throws ParseException{
        ParseTree res;
        switch (lexer.curToken){
            case WORD:
                res = new ParseTree("Name", new ParseTree(lexer.word));
                lexer.nextToken();
                if (lexer.curToken != Token.WORD && lexer.curToken != Token.STAR){
                    throw new ParseException("Illegal token in parsingType" + lexer.curToken + " while expecting WORD || STAR", lexer.curPos);
                }
                break;
            default:
                throw new ParseException("Wrong first while parsingType", lexer.curPos);
        }
        return res;
    }

    private ParseTree parseName() throws ParseException{
        ParseTree res;
        switch (lexer.curToken){
            case WORD:
                res = new ParseTree("Name", new ParseTree(lexer.word));
                lexer.nextToken();
                if (lexer.curToken != Token.LBR && lexer.curToken != Token.RBR && lexer.curToken != Token.COMMA){
                    throw new ParseException("Illegal token in parsingType" + lexer.curToken + " while expecting RBR || LBR || COMMA", lexer.curPos);
                }
                break;
            default:
                throw new ParseException("Wrong first while parsingName", lexer.curPos);
        }
        return res;
    }
}
