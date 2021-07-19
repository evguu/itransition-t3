package org.litnine.task3;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import at.favre.lib.bytes.Bytes;

public class Main {
    static byte[] key;
    static SecureRandom secureRandom = new SecureRandom();
    static Mac mac;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args){
        key = new byte[16];
        secureRandom.nextBytes(key);

        if(args.length < 3)
        {
            System.out.println("At least 3 arguments must be passed to the function.\n" +
                    "Example: java -jar game.jar rock paper scissors");
            return;
        }

        if(args.length % 2 == 0)
        {
            System.out.println("Argument count must be odd.\n" +
                    "Example: java -jar game.jar rock paper scissors");
            return;
        }

        if(areThereDuplicates(args))
        {
            System.out.println("Duplicate arguments are not allowed.\n" +
                    "Example: java -jar game.jar rock paper scissors");
            return;
        }

        try {
            mac = Mac.getInstance("HmacSHA3-256");
            mac.init(new SecretKeySpec(key, "HmacSHA3-256"));
        } catch (Exception e){
            System.out.println("An error occured in HMAC module.\n" + e.getMessage());
            return;
        }

        int computerMove = Math.abs(secureRandom.nextInt()) % args.length;
        System.out.println("HMAC: "+HMAC(args[computerMove]));

        int userMove;
        while(true) {
            int i = 1;
            System.out.println("Available moves:");
            for (String arg : args) {
                System.out.println("" + i++ + " - " + arg);
            }
            System.out.println("0 - exit\nEnter your move: ");

            String input = scanner.nextLine();
            try{
                userMove = Integer.parseInt(input);
            }catch (Exception e){
                System.out.println("Invalid input.");
                continue;
            }
            if (userMove >= 0 && userMove <= args.length) break;
            System.out.println("Option out of bounds.");
        }

        System.out.println("Your move: " + args[userMove]);
        System.out.println("Computer move: " + args[computerMove]);

        if (userMove == computerMove){
            System.out.println("It's a draw!");
        } else if (didUserWin(userMove, computerMove, args.length)) {
            System.out.println("You won!");
        } else {
            System.out.println("You lost... Better luck next time!");
        }

        System.out.println("HMAC key: " + Bytes.wrap(key).encodeHex(true));

    }

    public static boolean areThereDuplicates(String[] args){
        Set<String> set = new HashSet<>();
        for (String arg : args)
        {
            if (set.contains(arg))
                return true;
            set.add(arg);
        }
        return false;
    }

    public static String HMAC(String src)
    {
        return Bytes.wrap(mac.doFinal(src.getBytes())).encodeHex(true);
    }

    public static boolean didUserWin(int userMove, int computerMove, int moveCount)
    {
        int distance = computerMove - userMove;
        if(distance < 0) distance += moveCount;
        return distance > moveCount/2;
    }

}