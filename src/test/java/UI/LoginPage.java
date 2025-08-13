package UI;

public class LoginPage {
    private static final String startPage = "https://bi.rosseti.digital/";
    public static String loginFieldXPath = "//input[@id='username']";
    public static String passwordFieldXPath = "//input[@id='password']";
    public static String buttonEnterXPath = "//button[@name='button']";

    public static String getStartPage() {
        return "https://bi.rosseti.digital/";
    }
}
