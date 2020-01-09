package atom;

public abstract class Atom {

    @Override
    public abstract boolean equals(Object obj);

    public static class AToken extends Atom {
        private Token token;

        public AToken(Token token){
            this.token = token;
        }

        @Override
        public int hashCode() {
            return token.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AToken)) return false;

            return token.equals(((AToken) obj).token);
        }
    }


    public static class ANonterminal extends Atom {

        private Nonterminal nonterminal;

        public ANonterminal(Nonterminal nonterminal){
            this.nonterminal = nonterminal;
        }

        @Override
        public int hashCode() {
            return nonterminal.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ANonterminal)) return false;

            return nonterminal.equals(((ANonterminal) obj).nonterminal);
        }
    }
}
