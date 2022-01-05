package br.com.sankhya.ot.actions;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.mgecomercial.model.facades.helpper.ItemNotaHelpper;
import br.com.sankhya.modelcore.comercial.CentralFinanceiro;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ot.dao.Financeiro;
import br.com.sankhya.ot.dao.Nota;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public class ImportaReembolso implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {

        Registro[] linhas = contextoAcao.getLinhas();

        CabecalhoNotaVO notaVO;

        int i = 0;
        for (Registro linha : linhas) {

            BigDecimal nuImp = (BigDecimal) linha.getCampo("NUIMP");
            DynamicVO importacaoVO = (DynamicVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO("AD_IMPREEM", nuImp);
            BigDecimal codParc = (BigDecimal) linha.getCampo("CODPARC");
            BigDecimal codEmp = (BigDecimal) linha.getCampo("CODEMP");
            BigDecimal codProd = (BigDecimal) linha.getCampo("CODPROD");
            BigDecimal codNat = (BigDecimal) linha.getCampo("CODNAT");
            BigDecimal codCenCus = (BigDecimal) linha.getCampo("CODCENCUS");
            BigDecimal codProj = (BigDecimal) linha.getCampo("CODPROJ");
            Timestamp dtNeg = (Timestamp) linha.getCampo("DTNEG");
            Timestamp dtVenc = (Timestamp) linha.getCampo("DTVENC");
            BigDecimal vlrTotal = (BigDecimal) linha.getCampo("VLRTOTAL");
            String descricao = (String) linha.getCampo("OBSERVACAO");
            BigDecimal codTipOper = (BigDecimal) linha.getCampo("CODTIPOPER");

            //DynamicVO topVO = TipoOperacaoUtils.getTopVO(codTipOper);
            //TipoOperacaoVO tipoOperacaoVO = (TipoOperacaoVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO(DynamicEntityNames.TIPO_OPERACAO, new Object[] { topVO.asBigDecimalOrZero("CODTIPOPER"), topVO.asTimestamp("DHALTER")}, TipoOperacaoVO.class);

            // Lança cabeçalho da nota
            notaVO = Nota.lancaCabecalhoNota(codParc, codEmp, codNat, codCenCus, codProj, codTipOper, dtNeg, descricao);
            // Lança item da nota
            Collection<ItemNotaVO> itens = new ArrayList<>();
            ItemNotaVO itemVO = Nota.montaItemNota(notaVO, codEmp, codProd, BigDecimal.ONE, vlrTotal);
            itens.add(itemVO);
            // Salva itens na nota
            ItemNotaHelpper.saveItensNota(itens, notaVO);
            notaVO.setVLRNOTA(vlrTotal);

            // Recalculo de impostos
            final ImpostosHelpper impostos = new ImpostosHelpper();
            impostos.calcularImpostos(notaVO.getNUNOTA());
            impostos.totalizarNota(notaVO.getNUNOTA());

            // Refaz financeiro
            final CentralFinanceiro centralFinanceiro = new CentralFinanceiro();
            centralFinanceiro.inicializaNota(notaVO.getNUNOTA());
            centralFinanceiro.refazerFinanceiro();

            //Confirma a nota
            /*BarramentoRegra regra = BarramentoRegra.build(CentralFaturamento.class, "regrasConfirmacaoSilenciosa.xml", AuthenticationInfo.getCurrent());
            regra.setValidarSilencioso(true);
            ConfirmacaoNotaHelper.confirmarNota(notaVO.getNUNOTA(), regra, true);*/

            //Busca financeiros desta nota
            Collection<DynamicVO> financeirosVO = Financeiro.getFinanceirosByNunota(notaVO.getNUNOTA());
            for (DynamicVO finVO : financeirosVO) {
                //Atualiza data do(s) vencimento(s)
                Financeiro.atualizaVencimento(finVO.asBigDecimalOrZero("NUFIN"), dtVenc, codEmp);
            }

            // Exclui registro da tabela temporária
            EntityFacadeFactory.getDWFFacade().removeEntity("AD_IMPREEM", new BigDecimal[]{nuImp});

            // Salva log de lançamento de notas
            DynamicVO logReembolso = (DynamicVO) EntityFacadeFactory.getDWFFacade().getDefaultValueObjectInstance("AD_LOGIMPREEM");
            logReembolso.setProperty("NUNOTA", notaVO.getNUNOTA());
            logReembolso.setProperty("DTVENCINIC", dtVenc);
            EntityFacadeFactory.getDWFFacade().createEntity("AD_LOGIMPREEM", (EntityVO) logReembolso);
        }

        contextoAcao.setMensagemRetorno(linhas.length + " notas foram lançadas. Verifique o log de lançamentos.");

    }
}
