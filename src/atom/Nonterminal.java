package atom;

public class Nonterminal{
    private String name;

    public Nonterminal(String name){
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Nonterminal)) return false;

        return name.equals(((Nonterminal) obj).name);
    }

}