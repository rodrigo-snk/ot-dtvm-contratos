package br.com.sankhya.ot.dao;

import com.sankhya.util.TimeUtils;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

public class DebugTest {

    public static void main(String[] args)  {
        ArrayList<String> contatos = new ArrayList<>();

        contatos.add("rodrigobritov@gmail.com");
        contatos.add("britoviana@icloud.com");
        contatos.add("belbrito@gmail.com");
        contatos.add("izacomz1954@gmail.com");
        contatos.add("arturviana51@gmail.com");
        String lista = "";
        for (String contato: contatos) {
            lista = lista.concat(contato.concat(";"));
        }
        System.out.println(lista);





        LocalDate dtVenc = TimeUtils.getNow().toLocalDateTime().toLocalDate().plusDays(2);
        System.out.println(dtVenc);

    }
}
