package br.com.sankhya.ot.dao;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgecomercial.model.facades.helpper.FinanceiroHelpper;
import br.com.sankhya.mgecomercial.model.facades.helpper.ItemNotaHelpper;
import br.com.sankhya.modelcore.comercial.ComercialUtils;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.tgf.FinanceiraVO;
import br.com.sankhya.modelcore.financeiro.util.FinanceiroUtils;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Nota {

    public static CabecalhoNotaVO lancaCabecalhoNota(BigDecimal codParc, BigDecimal codEmp, BigDecimal codNat, BigDecimal codCenCus, BigDecimal codProj, BigDecimal codTipOper, Timestamp dtNeg, String observacao, BigDecimal codObsPad, String serieNota) throws Exception {
        JdbcWrapper jdbc = null;
        CabecalhoNotaVO notaVO;

        try {
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            jdbc = dwfFacade.getJdbcWrapper();
            jdbc.openSession();

            notaVO = (CabecalhoNotaVO) dwfFacade.getDefaultValueObjectInstance("CabecalhoNota", CabecalhoNotaVO.class);
            notaVO.setCODEMP(codEmp);
            notaVO.setCODPARC(codParc);
            notaVO.setCODCENCUS(codCenCus);
            notaVO.setCODPROJ(codProj);
            notaVO.setCODTIPOPER(codTipOper);
            notaVO.setCODNAT(codNat);
            notaVO.setCODTIPVENDA(BigDecimal.valueOf(40)); // A PRAZO
            notaVO.setDTNEG(dtNeg);
            notaVO.setNUMNOTA(BigDecimal.ZERO); // Padrão
            notaVO.setOBSERVACAO(observacao);
            notaVO.setCODOBSPADRAO(codObsPad);
            notaVO.setSERIENOTA(serieNota);
            notaVO.setISSRETIDO("N");

            dwfFacade.createEntity("CabecalhoNota", notaVO);

        } finally {
            if (jdbc != null) jdbc.closeSession();
        }
        return notaVO;
    }
    public static CabecalhoNotaVO lancaCabecalhoNota(BigDecimal codParc, BigDecimal codEmp, BigDecimal codNat, BigDecimal codCenCus, BigDecimal codProj, BigDecimal codTipOper, Timestamp dtNeg, String observacao) throws Exception {
        JdbcWrapper jdbc = null;
        CabecalhoNotaVO notaVO;

        try {
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            jdbc = dwfFacade.getJdbcWrapper();
            jdbc.openSession();

            notaVO = (CabecalhoNotaVO) dwfFacade.getDefaultValueObjectInstance("CabecalhoNota", CabecalhoNotaVO.class);
            notaVO.setCODEMP(codEmp);
            notaVO.setCODPARC(codParc);
            notaVO.setCODCENCUS(codCenCus);
            notaVO.setCODPROJ(codProj);
            notaVO.setCODTIPOPER(codTipOper);
            notaVO.setCODNAT(codNat);
            notaVO.setCODTIPVENDA(BigDecimal.valueOf(40)); // A PRAZO
            notaVO.setDTNEG(dtNeg);
            notaVO.setNUMNOTA(BigDecimal.ZERO); // Padrão
            notaVO.setOBSERVACAO(observacao);
            notaVO.setISSRETIDO("N");

            dwfFacade.createEntity("CabecalhoNota", notaVO);

        } finally {
            if (jdbc != null) jdbc.closeSession();
        }
        return notaVO;
    }

    public static ItemNotaVO montaItemNota(CabecalhoNotaVO notaVO, BigDecimal codEmp, BigDecimal codProd, BigDecimal qtdNeg, BigDecimal vlrUnit) throws Exception {

        ItemNotaVO itemVO = (ItemNotaVO) EntityFacadeFactory.getDWFFacade().getDefaultValueObjectInstance("ItemNota", ItemNotaVO.class);
        DynamicVO produtoVO = (DynamicVO) EntityFacadeFactory.getDWFFacade().findEntityByPrimaryKeyAsVO(DynamicEntityNames.PRODUTO, codProd);
        itemVO.setNUNOTA(notaVO.getNUNOTA());
        itemVO.setCODPROD(codProd);
        itemVO.setCODVOL(produtoVO.asString("CODVOL"));
        itemVO.setUSOPROD(produtoVO.asString("USOPROD"));
        itemVO.setCODEMP(codEmp);
        itemVO.setQTDNEG(qtdNeg);
        itemVO.setVLRUNIT(vlrUnit);
        itemVO.setVLRTOT(vlrUnit.multiply(qtdNeg));
        itemVO.setCONTROLE(" ");
        itemVO.setCODLOCALORIG(BigDecimal.ZERO);
        itemVO.setATUALESTOQUE(BigDecimal.ZERO);

        return itemVO;
    }
}
