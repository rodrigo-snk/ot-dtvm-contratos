package br.com.sankhya.ot.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.EntityPrimaryKey;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;

public class InclusaoContaContabil implements EventoProgramavelJava {
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
        EntityFacade dwf = EntityFacadeFactory.getDWFFacade();

        DynamicVO contaContabilVO = (DynamicVO) persistenceEvent.getVo();
        String contaContabil = contaContabilVO.asString("CODCONTA");
        long grau = contaContabil.chars().filter(ch -> ch == '.').count() + 1;
        String contaContabilPai = contaContabil.substring(0,contaContabil.lastIndexOf("."));
        BigDecimal codCtaCtbPai;

        codCtaCtbPai = findCodCtaCtbByCtaCtb(contaContabilPai);
        BigDecimal dv = geraDigitoVerificador(contaContabil);
        contaContabilVO.setProperty("DV", dv);

        DynamicVO planoConta = (DynamicVO) EntityFacadeFactory.getDWFFacade().getDefaultValueObjectInstance(DynamicEntityNames.PLANO_CONTA);
        planoConta.setProperty("DESCRCTA", contaContabilVO.getProperty("DESCRICAO"));
        planoConta.setProperty("CTACTB", contaContabil);
        planoConta.setProperty("CODEMP", BigDecimal.ONE);
        planoConta.setProperty("GRAU", BigDecimal.valueOf(grau));
        planoConta.setProperty("AD_DV", contaContabilVO.getProperty("DV"));
        planoConta.setProperty("ANALITICA", contaContabilVO.getProperty("ANALITICA"));
        planoConta.setProperty("CODCTACTBPAI", codCtaCtbPai);
        planoConta.setProperty("CODGRUPOCTA", contaContabilVO.getProperty("CODGRUPOCTA"));
        planoConta.setProperty("ATIVA", "S");
        dwf.createEntity(DynamicEntityNames.PLANO_CONTA, (EntityVO) planoConta);

        //if (true) throw new MGEModelException("Grau: " +BigDecimal.valueOf(grau) + " CODCTACTB: " +planoConta.getProperty("CODCTACTB"));

        atualizaCCONTA(contaContabil, dv, BigDecimal.valueOf(grau), (BigDecimal) planoConta.getProperty("CODCTACTB"));

        //dwf.removeEntity("AD_CCONTA", (EntityPrimaryKey) contaContabilVO.getPrimaryKey());

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

    private void atualizaCCONTA(String contaContabil, BigDecimal dv, BigDecimal grau, BigDecimal codCtaCtb) throws MGEModelException {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeFactory.dao("AD_CCONTA")
                    .prepareToUpdateByPK(contaContabil)
                    .set("DV", dv)
                    .set("GRAU", grau)
                    .set("CODCTACTB", codCtaCtb)
                    .update();
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
    }


    private BigDecimal geraDigitoVerificador(String contaContabil) {
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
        return BigDecimal.valueOf(10-(sum%10));
    }

    private BigDecimal findCodCtaCtbByCtaCtb(String contaContabilPai) throws MGEModelException {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper planoContas = JapeFactory.dao(DynamicEntityNames.PLANO_CONTA);
            DynamicVO planoContaVO = planoContas.findOne(" CTACTB = ?", contaContabilPai);
            return planoContaVO.asBigDecimalOrZero("CODCTACTB");
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
        return BigDecimal.valueOf(-999999999);
    }
}
