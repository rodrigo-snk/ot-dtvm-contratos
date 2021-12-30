package br.com.sankhya.ot.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.comercial.CentralFinanceiro;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;

import java.math.BigDecimal;

public class RefazerFinanceiro implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        Registro[] linhas = contextoAcao.getLinhas();

        for (Registro linha: linhas) {

            final ImpostosHelpper impostosHelper = new ImpostosHelpper();
            impostosHelper.calcularImpostos((BigDecimal) linha.getCampo("NUNOTA"));
            impostosHelper.totalizarNota((BigDecimal) linha.getCampo("NUNOTA"));

            final CentralFinanceiro centralFinanceiro = new CentralFinanceiro();
            centralFinanceiro.inicializaNota((BigDecimal) linha.getCampo("NUNOTA"));
            centralFinanceiro.refazerFinanceiro();

        }
    }
}
