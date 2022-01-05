package br.com.sankhya.ot.dao;

import br.com.sankhya.modelcore.comercial.BoletoHelper;
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static groovy.util.GroovyTestCase.assertEquals;

public class DebugTest {

    public static void main(String[] args)  {


        String contaContabil = "2.1.2.10.00.7652";
        long grau = contaContabil.chars().filter(ch -> ch == '.').count() + 1;

        String contaContabilPai = contaContabil.substring(0,contaContabil.lastIndexOf("."));


        contaContabil = contaContabil.replace(".","");
        StringBuilder sb = new StringBuilder(contaContabil);
        sb.reverse();
        contaContabil = sb.toString();
        int sum=0;
        for (int i = 0; i < contaContabil.length(); i++) {
            int digit = Character.getNumericValue(contaContabil.charAt(i));
            switch (i) {
                case 0:
                case 3:
                case 6:
                case 9:
                    sum +=  digit * 3;
                    System.out.println(digit * 3);
                    break;
                case 1:
                case 4:
                case 7:
                case 10:
                    sum += digit * 7;
                    System.out.println(digit * 7);
                    break;
                case 2:
                case 5:
                case 8:
                case 11:
                    sum += digit;
                    System.out.println(digit);
                    break;
            }
        }
        System.out.println(contaContabil);
        System.out.println("Soma: " +sum);
        System.out.println("Resto da divisão por 10: " + sum%10);
        System.out.println("Digito verificador: " + (10-(sum%10)));
        System.out.println("Grau: " + grau);
        System.out.println("Conta Pai: " + contaContabilPai);





        LocalDate dtVenc = TimeUtils.getNow().toLocalDateTime().toLocalDate().plusDays(2);
        System.out.println(dtVenc);

            Pattern pattern = Pattern.compile("^[1-9]\\.\\d\\.\\d(\\.\\d{2})?(\\.\\d{2})?(\\.\\d{4})?$");
        Matcher matcher = pattern.matcher("1.0.7");
        boolean matchFound = matcher.find();
        if(matchFound) {
            System.out.println("Match found");
        } else {
            System.out.println("Match not found");
        }

    }
}
