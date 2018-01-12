public class Levenshtein {
    public static int levenshtein(String s, String t) {

        if (s.length() == 0) return t.length();
        if (t.length() == 0) return s.length();

        if (s.charAt(0) == t.charAt(0))
            return levenshtein(s.substring(1), t.substring(1));

        int a = levenshtein(s.substring(1), t.substring(1));
        int b = levenshtein(s, t.substring(1));
        int c = levenshtein(s.substring(1), t);

        if (a > b) a = b;
        if (a > c) a = c;

        return a + 1;
    }
}