package br.com.sankhya.ot.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ot.dao.Financeiro;
import com.sankhya.util.BigDecimalUtil;

import java.util.Collection;

public class AtualizaTipoTitulo implements EventoProgramavelJava {
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


    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {
        boolean isConfirmandoNota = JapeSession.getPropertyAsBoolean("CabecalhoNota.confirmando.nota", false);
        DynamicVO notaVO = (DynamicVO) persistenceEvent.getVo();

        if (isConfirmandoNota && !BigDecimalUtil.isNullOrZero(notaVO.asBigDecimalOrZero("NUMCONTRATO"))) {
            DynamicVO contratoVO = (DynamicVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO(DynamicEntityNames.CONTRATO, notaVO.asBigDecimalOrZero("NUMCONTRATO"));

            //Busca financeiros desta nota
            Collection<DynamicVO> financeirosVO = Financeiro.getFinanceirosByNunota(notaVO.asBigDecimalOrZero("NUNOTA"));
            for (DynamicVO finVO : financeirosVO) {
                //Atualiza tipo de título
                Financeiro.atualizaVencimento(finVO.asBigDecimalOrZero("NUFIN"), contratoVO.asBigDecimalOrZero("NUMCONTRATO"), contratoVO.asBigDecimalOrZero("TIPOTITULO"));
            }
        }

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
