package dk.dtu;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Hello world!
 *
 * 
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        System.out.println(dotenv.get("MPIP"));
    }
}