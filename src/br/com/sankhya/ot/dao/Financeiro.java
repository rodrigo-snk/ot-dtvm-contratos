package br.com.sankhya.ot.dao;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public class Financeiro {

    public static DynamicVO getFinanceiroByPK(Object nuFin) throws MGEModelException {
        DynamicVO financeiroVO = null;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper financeiroDAO = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
            financeiroVO = financeiroDAO.findByPK(nuFin);
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
        return financeiroVO;
    }

    public static Collection<DynamicVO> getFinanceirosByNunota(BigDecimal nuNota) throws MGEModelException {

        JapeSession.SessionHandle hnd = null;
        Collection<DynamicVO> financeirosVO = null;
        try {
            hnd = JapeSession.open();
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

            FinderWrapper finder = new FinderWrapper(DynamicEntityNames.FINANCEIRO, "this.NUNOTA = ?", new Object[] { nuNota });
            finder.setOrderBy("CODEMP");
            finder.setMaxResults(-1);
            financeirosVO = dwfFacade.findByDynamicFinderAsVO(finder);

            for (DynamicVO dynamicVO : financeirosVO) {
                System.out.println(dynamicVO.asBigDecimal("CODEMP"));
            }
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
        return financeirosVO;

    }

    public static void atualizaVencimento(Object nuFin, LocalDate dtVenc, BigDecimal tipTitulo) throws Exception {
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeFactory.dao(DynamicEntityNames.FINANCEIRO)
                    .prepareToUpdateByPK(nuFin)
                    .set("DTVENC", Timestamp.valueOf(dtVenc.atTime(LocalTime.MIDNIGHT)))
                    .set("CODTIPTIT", tipTitulo)
                    .update();
            // if (true) throw new MGEModelException(String.valueOf(Timestamp.valueOf(dtVenc.atTime(LocalTime.MIDNIGHT))));
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }

    }

    public static void atualizaVencimento(Object nuFin, Timestamp dtVenc, BigDecimal codEmp) throws Exception {
        JapeSession.SessionHandle hnd = null;
        BigDecimal tipTitulo = BigDecimal.valueOf(4);
        BigDecimal codBco;
        BigDecimal codCtaBcoInt;
        int empresa = codEmp.intValue();
        switch (empresa) {
            case 1: // DTVM RJ
            case 2: // DTVM SP
                codBco = BigDecimal.valueOf(237);
                codCtaBcoInt = BigDecimal.valueOf(68);
                break;
            case 3:   // OT SERVICER RJ
            case 12: // OT SERVICER SP
                codBco = BigDecimal.valueOf(237);
                codCtaBcoInt = BigDecimal.valueOf(70);
                break;
            default:
                throw new MGEModelException("Cód. Empresa não previsto.");
        }
        try {
            hnd = JapeSession.open();
            JapeFactory.dao(DynamicEntityNames.FINANCEIRO)
            .prepareToUpdateByPK(nuFin)
            .set("DTVENC", dtVenc)
            .set("CODTIPTIT", tipTitulo)
            .set("CODBCO", codBco)
            .set("CODCTABCOINT", codCtaBcoInt)
            .update();
           // if (true) throw new MGEModelException(String.valueOf(Timestamp.valueOf(dtVenc.atTime(LocalTime.MIDNIGHT))));
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }

    }


    public static void atualizaVencimento(BigDecimal nuFin, BigDecimal codEmp) throws MGEModelException {
        JapeSession.SessionHandle hnd = null;
        BigDecimal tipTitulo = BigDecimal.valueOf(4);
        BigDecimal codBco = BigDecimal.ZERO;
        BigDecimal codCtaBcoInt = BigDecimal.ZERO;
        int empresa = codEmp.intValue();
        switch (empresa) {
            case 1:  // DTVM RJ
            case 2:  // DTVM SP
                codBco = BigDecimal.valueOf(237);
                codCtaBcoInt = BigDecimal.valueOf(68);
                break;
            case 3:  // OT SERVICER RJ
            case 12: // OT SERVICER SP
                codBco = BigDecimal.valueOf(237);
                codCtaBcoInt = BigDecimal.valueOf(70);
                break;
            default:
                throw new MGEModelException("Cód. Empresa não previsto.");
        }
        try {
            hnd = JapeSession.open();
            JapeFactory.dao(DynamicEntityNames.FINANCEIRO)
                    .prepareToUpdateByPK(nuFin)
                    .set("CODTIPTIT", tipTitulo)
                    .set("CODBCO", codBco)
                    .set("CODCTABCOINT", codCtaBcoInt)
                    .update();
            // if (true) throw new MGEModelException(String.valueOf(Timestamp.valueOf(dtVenc.atTime(LocalTime.MIDNIGHT))));
        } catch (Exception e) {
            MGEModelException.throwMe(e);
        } finally {
            JapeSession.close(hnd);
        }
    }
}
