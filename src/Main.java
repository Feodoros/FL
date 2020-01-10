import atom.Atom;
import atom.Nonterminal;
import atom.Token;
import grammar.Grammar;
import parser.ParseTree;
import parser.Parser;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        Grammar grammar = new Grammar(new ArrayList<>());
        //S -> type name (args) const;
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("START")),
                new ArrayList<>(Arrays.asList(new Atom.ANonterminal(new Nonterminal("TYPE")), new Atom.ANonterminal(new Nonterminal("NAME")),
                        new Atom.AToken(Token.LBR), new Atom.ANonterminal(new Nonterminal("ARGS")), new Atom.AToken(Token.RBR), new Atom.ANonterminal(new Nonterminal("CONST"))))));
        //const -> const
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("CONST")),
                new ArrayList<>(Collections.singletonList(new Atom.AToken(Token.CONST)))));
        //const -> Eps
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("CONST")),
                new ArrayList<>(Collections.singletonList(new Atom.AToken(Token.EPS)))));
        //args -> Eps
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("ARGS")),
                new ArrayList<>(Collections.singletonList(new Atom.AToken(Token.EPS)))));
        //args -> arg arg_
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("ARGS")),
                new ArrayList<>(Arrays.asList(new Atom.ANonterminal(new Nonterminal("ARG")),new Atom.ANonterminal(new Nonterminal("ARG_"))))));
        //arg -> const type star name
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("ARG")),
                new ArrayList<>(Arrays.asList(new Atom.ANonterminal(new Nonterminal("CONST")), new Atom.ANonterminal(new Nonterminal("TYPE")),
                        new Atom.ANonterminal(new Nonterminal("STAR")), new Atom.ANonterminal(new Nonterminal("NAME"))))));
        //arg_ -> Eps
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("ARG_")),
                new ArrayList<>(Collections.singletonList(new Atom.AToken(Token.EPS)))));
        //arg_ -> , const type star name arg_
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("ARG_")),
                new ArrayList<>(Arrays.asList(new Atom.AToken(Token.COMMA), new Atom.ANonterminal(new Nonterminal("CONST")),
                        new Atom.ANonterminal(new Nonterminal("TYPE")),
                        new Atom.ANonterminal(new Nonterminal("STAR")), new Atom.ANonterminal(new Nonterminal("NAME")),
                        new Atom.ANonterminal(new Nonterminal("ARG_"))))));
        //star -> *star
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("STAR")),
                new ArrayList<>(Arrays.asList(new Atom.AToken(Token.STAR), new Atom.ANonterminal(new Nonterminal("STAR"))))));
        //star -> Eps
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("STAR")),
                new ArrayList<>(Collections.singletonList(new Atom.AToken(Token.EPS)))));
        //type -> word
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("TYPE")),
                new ArrayList<>(Collections.singletonList(new Atom.AToken(Token.WORD)))));
        //name -> word
        grammar.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("NAME")),
                new ArrayList<>(Collections.singletonList(new Atom.AToken(Token.WORD)))));

        grammar.init();
        grammar.buildAll();
        Map<Atom.ANonterminal, Set<Atom>> first = grammar.getFirstSet();
        Map<Atom.ANonterminal, Set<Atom>> follow = grammar.getFollowSet();
        boolean isLL = grammar.checkLLK();

        Grammar grammar1 = new Grammar(new ArrayList<>());
        //START -> A
        grammar1.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("START")),
                new ArrayList<>(Collections.singletonList(new Atom.ANonterminal(new Nonterminal("A"))))));
        //A -> S T
        grammar1.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("A")),
                new ArrayList<>(Arrays.asList(new Atom.ANonterminal(new Nonterminal("S")), new Atom.AToken(Token.T)))));
        //A -> A T
        grammar1.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("A")),
                new ArrayList<>(Arrays.asList(new Atom.ANonterminal(new Nonterminal("A")), new Atom.AToken(Token.T)))));
        //S -> T
        grammar1.add(new Grammar.Rule(new Atom.ANonterminal(new Nonterminal("S")),
                new ArrayList<>(Collections.singletonList( new Atom.AToken(Token.T)))));

        Grammar grammarWithoutRec = grammar1.redemptionLeftRecursion();

        grammarWithoutRec.init();
        grammarWithoutRec.buildAll();
        boolean isLL1 = grammarWithoutRec.checkLLK();



        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("test")))) {
            List<String> stringList = bufferedReader.lines().collect(Collectors.toList());
            int index = 0;
            for (String string : stringList){
                Parser parser = new Parser(string);
                ParseTree tree = parser.parse();
                System.out.println(tree.toString());
                File f = new File("graph/" + index + ".gv");
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
                w.write(tree.toGraph());
                w.close();
                index++;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


}