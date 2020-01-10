package grammar;

import atom.Atom;
import atom.Nonterminal;
import atom.Token;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {

    private List<Rule> rules;

    private Map<Atom.ANonterminal, Set<Atom>> first = new HashMap<>();
    private Map<Atom.ANonterminal, Set<Atom>> follow = new HashMap<>();

    public static class Rule {
        private Atom.ANonterminal nonterminal;
        private List<Atom> atoms;

        public Rule(Atom.ANonterminal nonterminal, List<Atom> atoms) {
            this.nonterminal = nonterminal;
            this.atoms = atoms;
        }
    }

    public Grammar(List<Rule> rules) {
        this.rules = rules;
    }

    public void add(Rule rule) {
        rules.add(rule);
    }

    public void init() {
        rules.forEach(rule -> {
            first.put(rule.nonterminal, new HashSet<>());
            follow.put(rule.nonterminal, new HashSet<>());
        });
    }

    private Set<Atom> getFirst(List<Atom> atoms) {
        Set<Atom> res = new HashSet<>();
        for (Atom atom : atoms) {
            if (atom instanceof Atom.AToken) {
                res.add(atom);
                return res;
            }
            if (atom instanceof Atom.ANonterminal) {
                Set<Atom> curfirst = first.get(atom);
                if (curfirst != null) {
                    res.addAll(curfirst);
                    if (!curfirst.contains(new Atom.AToken(Token.EPS))) {
                        return res;
                    }
                } else {
                    return res;
                }
            }
        }
        return res;
    }

    // Строим множество FIRST
    private void buildFirst() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Rule rule : rules) {

                Atom.ANonterminal A = rule.nonterminal;
                List<Atom> a = rule.atoms;

                int lastSize = first.get(A).size();
                first.get(A).addAll(getFirst(a));

                if (lastSize != first.get(A).size()) {
                    changed = true;
                }
            }
        }
    }

    // Строим множество FOLLOW
    private void buildFollow() {

        follow.get(new Atom.ANonterminal(new Nonterminal("START"))).add(new Atom.AToken(Token.END));

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Rule rule : rules) {

                Atom.ANonterminal A = rule.nonterminal;
                List<Atom> a = rule.atoms;

                for (int i = 0; i < a.size(); ++i) {
                    if (a.get(i) instanceof Atom.ANonterminal) {

                        int oldSize = follow.get(a.get(i)).size();

                        int j = i + 1;
                        boolean hasEps = true;

                        while (j < a.size() && hasEps){

                            hasEps = false;

                            if (a.get(j) instanceof Atom.AToken){
                                follow.get(a.get(i)).add(a.get(j));
                                if (a.get(j).equals(new Atom.AToken(Token.EPS))){
                                    hasEps = true;
                                }
                                ++j;
                                continue;
                            }

                            if (a.get(j) instanceof Atom.ANonterminal){
                                Set<Atom> nextFirst = new HashSet<>();
                                nextFirst.addAll(first.get(a.get(j)));
                                if (nextFirst.contains(new Atom.AToken(Token.EPS))) {
                                    hasEps = true;
                                    nextFirst.remove(new Atom.AToken(Token.EPS));
                                }
                                follow.get(a.get(i)).addAll(nextFirst);
                                ++j;
                            }
                        }

                        if (hasEps || i == a.size() - 1){
                            follow.get(a.get(i)).addAll(follow.get(A));
                        }

                        if (oldSize != follow.get(a.get(i)).size()) {
                            changed = true;
                        }
                    }
                }
            }
        }
    }

    public void buildAll() {
        buildFirst();
        buildFollow();
    }

    public Map<Atom.ANonterminal, Set<Atom>> getFirstSet() {
        return first;
    }

    public Map<Atom.ANonterminal, Set<Atom>> getFollowSet() {
        return follow;
    }

    // Избавляемся от левой рекурсии
    public Grammar redemptionLeftRecursion (){
        Grammar res = new Grammar(new ArrayList<>());
        int i =  0;
        for (Rule rule : rules){
            var N = rule.nonterminal;
            if (rule.atoms.get(0).equals(N)){

                var newNonterm = new Nonterminal("N" + i);
                List<Rule> rules_ = rules.stream().filter(r -> r.nonterminal.equals(N) && !r.atoms.contains(N)).collect(Collectors.toList());
                rules_.forEach(r_ -> {
                    List<Atom> list = new ArrayList<>(r_.atoms);
                    list.add(new Atom.ANonterminal(newNonterm));
                    res.add(new Rule(N, list));
                });

                List<Atom> list_ = new ArrayList<>(rule.atoms.subList(1, rule.atoms.size()));
                list_.add(new Atom.ANonterminal(newNonterm));
                res.add(new Rule(new Atom.ANonterminal(newNonterm), list_));
                i += 1;
            } else {
                res.add(rule);
            }
        }
        return res;
    }

    // Проверяем грамматику на LL(k)
    public boolean checkLLK(){
        // Пробегаем с первого (нулевого) правила до последнего и сравниваем
        // нетерминалы между собой; если одинаковые, то добавляем в список sameNonTerm
        var mainList = new ArrayList<List<Rule>>();
        for (Rule rule : rules) {
            List<Rule> rules_ = rules.stream().filter(r -> r.nonterminal.equals(rule.nonterminal)).collect(Collectors.toList());
            var sameNonTerm = new ArrayList<>(rules_);
            mainList.add(sameNonTerm);
        }

        // mainList -- список списков, состоящих из правил с одинаковыми нетерминалами
        mainList.removeIf(list -> list.size()<=1);

        var mainFirstList = new ArrayList<>();
        boolean check = true;
        for (List<Rule> list : mainList) {
            var firstList = new ArrayList<>();
            for (Rule rule : list) {
                for (Atom atom : rule.atoms) {
                    if (!firstList.isEmpty() && firstList.contains(atom)) {
                        check = false;
                    }
                    if(atom instanceof Atom.ANonterminal){
                        firstList.add(this.first.get(atom));
                    }else{
                        firstList.add(atom);
                    }
                }
            }
        }
        return check;
    }
}

