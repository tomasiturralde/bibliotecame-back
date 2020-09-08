package bibliotecame.back;

@SuppressWarnings("unused")
public class RandomStringGenerator {

    private static final String AlphabeticUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String AlphabeticLowercase = "abcdefghijklmnopqrstuvxyz";
    private static final String Numeric = "0123456789";
    private static final String Symbols = "!@#$%^&*(){}[]?><. ";


    public static String getAlphaNumericStringWithSymbols(int n){
        return getRandom(AlphabeticLowercase + AlphabeticUppercase + Numeric + Symbols, n);
    }

    public static String getAlphaNumericString(int n){
        return getRandom(AlphabeticLowercase + AlphabeticUppercase + Numeric, n);
    }

    public static String getAlphabeticString(int n){
        return getRandom(AlphabeticLowercase + AlphabeticUppercase, n);
    }

    public static String getNumericString(int n){
        return getRandom(Numeric, n);
    }

    private static String getRandom(String options, int n){
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(options.length() * Math.random());
            sb.append(options.charAt(index));
        }

        return sb.toString();
    }
}
