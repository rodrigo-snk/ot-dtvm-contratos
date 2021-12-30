package br.com.sankhya.ot.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ParceiroVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ParceiroHellper;
import br.com.sankhya.modelcore.util.email.FilaMsgUtil;
import br.com.sankhya.timimob.model.aluguel.ParceiroContato;
import com.sankhya.model.entities.vo.ContatoVO;

import java.util.ArrayList;

public class Emails implements EventoProgramavelJava {
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
        DynamicVO filaMsgVo = (DynamicVO) persistenceEvent.getVo();
        if (filaMsgVo.getProperty("NUCHAVE") != null) {
            CabecalhoNotaVO cabVO = (CabecalhoNotaVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO(DynamicEntityNames.CABECALHO_NOTA, filaMsgVo.getProperty("NUCHAVE"), CabecalhoNotaVO.class);
            ArrayList<DynamicVO> contatos = (ArrayList<DynamicVO>) EntityFacadeFactory.getDWFFacade().findByDynamicFinderAsVO(new FinderWrapper("RESPCOBRANCA = 'S' AND CODPARC=", cabVO.getCODPARC().toString()));
            //String lista = "";
            for (DynamicVO contato: contatos) {
                //lista = lista.concat(contato.asString("EMAIL").concat(";"));
                filaMsgVo.setProperty("EMAIL", contato.asString("EMAIL"));
                EntityFacadeFactory.getDWFFacade().createEntity(DynamicEntityNames.FILA_MSG, (EntityVO) filaMsgVo);
            }

        }

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
