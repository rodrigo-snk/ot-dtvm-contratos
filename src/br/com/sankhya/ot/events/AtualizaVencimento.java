package br.com.sankhya.ot.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.comercial.util.TipoOperacaoUtils;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ot.dao.Financeiro;
import com.sankhya.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

public class AtualizaVencimento implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

        DynamicVO finVO = (DynamicVO) persistenceEvent.getVo();
        BigDecimal nuNota = finVO.asBigDecimal("NUNOTA");
        BigDecimal numContrato = finVO.asBigDecimal("NUMCONTRATO");
        CabecalhoNotaVO cabVO = (CabecalhoNotaVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO(DynamicEntityNames.CABECALHO_NOTA, nuNota, CabecalhoNotaVO.class);
        DynamicVO topVO = TipoOperacaoUtils.getTopVO(cabVO.getCODTIPOPER());

        // Altera banco e conta para todos os tipos de movimento de Venda
        if (ComercialUtils.ehVenda(topVO.asString("TIPMOV"))) {
            Financeiro.atualizaVencimento(finVO.asBigDecimalOrZero("NUFIN"), finVO.asBigDecimal("CODEMP"));
        }

        // Altera data de vencimento dos contratos faturados
        if (!BigDecimalUtil.isNullOrZero(finVO.asBigDecimal("NUMCONTRATO"))) {
            if (!BigDecimalUtil.isNullOrZero(numContrato) && !BigDecimalUtil.isNullOrZero(nuNota)) {
                DynamicVO contratoVO = (DynamicVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO(DynamicEntityNames.CONTRATO, numContrato);
                LocalDate dtVenc;

                if ((contratoVO.asTimestamp("DTREFPROXFAT") != null)) {
                    LocalDate dtProxFat = contratoVO.asTimestamp("DTREFPROXFAT").toLocalDateTime().toLocalDate();
                    dtVenc = dtProxFat.withDayOfMonth(contratoVO.asInt("DIAPAG"));

                } else {
                    //dtVenc = TimeUtils.dataAddDay(contratoVO.asTimestamp("DTCONTRATO"), contratoVO.asInt("PRAZOVENCTO") +
                    LocalDate dtContrato = contratoVO.asTimestamp("DTCONTRATO").toLocalDateTime().toLocalDate();
                    dtVenc = dtContrato.withDayOfMonth(contratoVO.asInt("DIAPAG"));
                }

                Financeiro.atualizaVencimento(finVO.asBigDecimalOrZero("NUFIN"), dtVenc, contratoVO.asBigDecimalOrZero("TIPOTITULO"));
            }
        }

        atualizaFinImportacoes(finVO.asBigDecimalOrZero("NUNOTA"));

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

        final boolean isModifyingDTVENC = persistenceEvent.getModifingFields().isModifing("DTVENC");
        if (isModifyingDTVENC) {
            DynamicVO finVO = (DynamicVO) persistenceEvent.getVo();
            BigDecimal numContrato = finVO.asBigDecimal("NUMCONTRATO");
            BigDecimal nuNota = finVO.asBigDecimal("NUNOTA");

            // Atualiza vencimento dos contratos
            if (!BigDecimalUtil.isNullOrZero(numContrato) && !BigDecimalUtil.isNullOrZero(nuNota)) {
                DynamicVO contratoVO = (DynamicVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO(DynamicEntityNames.CONTRATO, numContrato);
                LocalDate dtVenc;

                if ((contratoVO.asTimestamp("DTREFPROXFAT") != null)) {
                    LocalDate dtProxFat = contratoVO.asTimestamp("DTREFPROXFAT").toLocalDateTime().toLocalDate();
                    dtVenc = dtProxFat.withDayOfMonth(contratoVO.asInt("DIAPAG"));

                } else {
                    //dtVenc = TimeUtils.dataAddDay(contratoVO.asTimestamp("DTCONTRATO"), contratoVO.asInt("PRAZOVENCTO") +
                    LocalDate dtContrato = contratoVO.asTimestamp("DTCONTRATO").toLocalDateTime().toLocalDate();
                    dtVenc = dtContrato.withDayOfMonth(contratoVO.asInt("DIAPAG"));
                }

                Financeiro.atualizaVencimento(finVO.asBigDecimalOrZero("NUFIN"), dtVenc, contratoVO.asBigDecimalOrZero("TIPTIULO"));
            }

            atualizaFinImportacoes(nuNota);

        }

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }


    public void atualizaFinImportacoes(BigDecimal nuNota) throws Exception {
        Collection<DynamicVO> cobrancasVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper("AD_LOGCOBRANCAS","this.NUNOTA = ?", nuNota));
        Collection<DynamicVO> reembolsosVO = (Collection<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper("AD_LOGIMPREEM","this.NUNOTA = ?", nuNota));

        // Atualizar o financeiro das importações do Remunera
        for (DynamicVO cobrancaVO: cobrancasVO) {
            Collection<DynamicVO> finsVO = Financeiro.getFinanceirosByNunota(nuNota);
            for (DynamicVO financeiro: finsVO) {
                BigDecimal codEmp = financeiro.asBigDecimal("CODEMP");
                Financeiro.atualizaVencimento(financeiro.asBigDecimalOrZero("NUFIN"), cobrancaVO.asTimestamp("DTVENC"), codEmp);
            }
        }
        // Atualizar o financeiro das importações de Reembolsos
        for (DynamicVO reembolsoVO: reembolsosVO) {
            Collection<DynamicVO> finsVO = Financeiro.getFinanceirosByNunota(nuNota);
            for (DynamicVO financeiro: finsVO) {
                BigDecimal codEmp = financeiro.asBigDecimal("CODEMP");
                Financeiro.atualizaVencimento(financeiro.asBigDecimalOrZero("NUFIN"), reembolsoVO.asTimestamp("DTVENCINIC"), codEmp);
            }
        }
    }
}
