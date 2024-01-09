package dk.dtu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class App 
{
    public static void main( String[] args ) throws IOException
    {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Do you want to host (h) or join (j) a pokergame");
        System.out.println("Press h or j");
        String c = input.readLine();
        switch (c) {
            case "h":
                System.out.println("hosting");
                break;
            case "j":
                System.out.println("joining");
                break;
            default:
                break;
        }
    }
}