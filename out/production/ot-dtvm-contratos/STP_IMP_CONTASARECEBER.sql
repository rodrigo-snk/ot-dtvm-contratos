USE [OLIVEIRAT_TREINA]
GO
/****** Object:  StoredProcedure [SANKHYA].[STP_IMP_CONTASARECEBER]    Script Date: 29/12/2021 11:49:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [SANKHYA].[STP_IMP_CONTASARECEBER] (@P_LINHA VARCHAR(4000)) AS
DECLARE
    @P_ERRMSG VARCHAR(255),
    @P_I INT,
    @P_I2 INT,
    @P_LINHA2 VARCHAR(4000),
    @P_ERRORMSG VARCHAR(4000),

    -- VARIÁVEIS DO ARQUIVO DE IMPORTAÇÃO
    @P_CNPJ CHAR(14),
    @P_NOMEPARC VARCHAR(100),
    @P_NOMEEMP VARCHAR(40),
    @P_VLRBRUTO FLOAT,
    @P_VLRLIQ FLOAT,
    @P_VLRARECEBER FLOAT,
    @P_CODPARC INT,
    @P_CODPROD VARCHAR,
    @P_CODNAT INT,
    @P_DTNEG DATE,
    @P_DTVENC DATE,
    @P_TIPTIT VARCHAR,
    @P_IR FLOAT,
	@P_PIS FLOAT,
	@P_COFINS FLOAT,
	@P_CSLL FLOAT,
	@P_NUFIN INT,
	@P_DOCLEGADO INT,
	@P_CODTIPOPER INT,
	@P_DHTIPOPER DATETIME,
    @P_NUIMP INT,
	@P_CODBCO INT,
    @P_CODCTABCOINT INT,
	@P_OBSERVACAO VARCHAR(4000),
	@P_CODCENCUS INT,
	@P_CODTIPTIT INT,

    -- VARIÁVEIS FORMATADAS PARA O SANKHYA OM
    @P_CODEMP INT

    SET @P_NUIMP = 0
	SET @P_CODTIPTIT = 4 --BOLETO

BEGIN

    SET @P_I = CHARINDEX(';', @P_LINHA)
    SET @P_LINHA2 = @P_LINHA

	-- Nome Empresa
    SET @P_LINHA2 = SUBSTRING(@P_LINHA,@P_I-1,LEN(@P_LINHA))
    SET @P_I = CHARINDEX(';', @P_LINHA)
    SET @P_NOMEEMP = CAST(SUBSTRING(@P_LINHA,1,@P_I-1) AS VARCHAR)

	-- Data de emissão
	SET @P_LINHA2= SUBSTRING(@P_LINHA,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_DTNEG = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS DATE)

    -- Número do documento
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_DOCLEGADO = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS VARCHAR)

	-- Nome cliente
	SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)

	-- CNPJ
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CNPJ = CAST(REPLACE(REPLACE(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),'.',''),'/',''),'-','') AS VARCHAR)

    -- Tipo de titulo
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_TIPTIT = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS VARCHAR)

	-- Natureza
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CODNAT = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS VARCHAR)

    -- Centro de Resultado
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CODCENCUS = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS VARCHAR)

	-- Data de vencimento
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_DTVENC = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS DATE)

    -- IR (CODIMP 32)
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_IR = CAST(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),',','.') AS FLOAT)

    -- PIS (CODIMP 29)
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_PIS = CAST(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),',','.') AS FLOAT)

    -- COFINS (CODIMP 30)
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_COFINS = CAST(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),',','.') AS FLOAT)

    -- CSLL (CODIMP 31)
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CSLL = CAST(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),',','.') AS FLOAT)

    -- Valor bruto
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_VLRBRUTO = CAST(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),',','.') AS FLOAT)

    -- Valor do liquido
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_VLRLIQ = CAST(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),',','.') AS FLOAT)

	-- Valor a receber
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_VLRARECEBER = CAST(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),',','.') AS FLOAT)

    -- Observacao
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_OBSERVACAO = CAST(@P_LINHA2 AS VARCHAR(4000))

	-- Seleciona o CODPARC pelo CNPJ do arquivo de importação
	SELECT @P_CODPARC = CODPARC
    FROM TGFPAR WHERE CGC_CPF = @P_CNPJ;

	-- Seleciona o CODEMP com base no arquivo de importação
	SET @P_CODEMP = CASE @P_NOMEEMP WHEN 'OT DTVM' THEN 1
	                                WHEN 'OT SERVICER' THEN 3
	                                WHEN 'OT DTVM SP' THEN 2
	                                WHEN 'OT SERVICER SP' THEN 12
	                                END;
	SET @P_CODCTABCOINT = CASE @P_NOMEEMP WHEN 'DTVM' THEN 68
                    WHEN 'OT SERVICER' THEN 70
                    WHEN 'DTVM SP' THEN 68
                    WHEN 'OT SERVICER SP' THEN 70
                    END;
	SET @P_CODBCO = 237;
	SET @P_CODTIPOPER = 1300;
	SELECT @P_DHTIPOPER = MAX(DHALTER)
	FROM TGFTOP WHERE CODTIPOPER = @P_CODTIPOPER;

    -- Seleciona o próximo CODIMP (PK da tabela temporária)
    SELECT @P_NUIMP = ISNULL(MAX(NUIMP),0)+ 1 FROM AD_IMPCOA

	EXECUTE SANKHYA.STP_KEYGEN_TGFNUM 'TGFFIN', 1 , 'TGFFIN', 'NUFIN', 1,
	@P_NUFIN OUTPUT

    INSERT INTO TGFFIN (NUFIN, NUMNOTA, AD_DOCLEGADO, DHMOV, DTALTER, CODEMP, CODPARC, DTNEG, DTVENCINIC, DTVENC, CODBCO, CODCTABCOINT, CODTIPTIT, CODTIPOPER, DHTIPOPER, CODNAT, CODCENCUS, VLRDESDOB, RECDESP, HISTORICO)
	VALUES (@P_NUFIN, 0, @P_DOCLEGADO, GETDATE(), GETDATE(), @P_CODEMP, @P_CODPARC, @P_DTNEG, @P_DTVENC, @P_DTVENC, @P_CODBCO, @P_CODCTABCOINT, @P_CODTIPTIT, @P_CODTIPOPER, @P_DHTIPOPER, @P_CODNAT, @P_CODCENCUS, @P_VLRBRUTO, 1, @P_OBSERVACAO);

	INSERT INTO AD_IMPCOA (NUIMP, NUFIN, DHIMP, CODUSU) VALUES (@P_NUIMP, @P_NUFIN, GETDATE(), SANKHYA.STP_GET_CODUSULOGADO());

    -- LANÇA IR SE O VALOR FOR MAIOR QUE ZERO
    IF (@P_IR >= 0)
    INSERT INTO TGFIMF (NUFIN, CODIMP, BASE, ALIQUOTA, VALOR, TIPIMP, DIGITADO) VALUES (@P_NUFIN, 32, @P_VLRBRUTO, @P_IR*100/@P_VLRARECEBER ,@P_IR, -1, 'S');

    -- LANÇA PIS SE O VALOR FOR MAIOR QUE ZERO
    IF (@P_PIS >= 0)
    INSERT INTO TGFIMF (NUFIN, CODIMP, BASE, ALIQUOTA, VALOR, TIPIMP, DIGITADO) VALUES (@P_NUFIN, 29, @P_VLRBRUTO, @P_PIS*100/@P_VLRARECEBER ,@P_PIS, -1, 'S');

    -- LANÇA COFINS SE O VALOR FOR MAIOR QUE ZERO
    IF (@P_COFINS >= 0)
    INSERT INTO TGFIMF (NUFIN, CODIMP, BASE, ALIQUOTA, VALOR, TIPIMP, DIGITADO) VALUES (@P_NUFIN, 30, @P_VLRBRUTO, @P_COFINS*100/@P_VLRARECEBER ,@P_COFINS, -1, 'S');

    -- LANÇA CSLL SE O VALOR FOR MAIOR QUE ZERO
    IF (@P_CSLL >= 0)
    INSERT INTO TGFIMF (NUFIN, CODIMP, BASE, ALIQUOTA, VALOR, TIPIMP, DIGITADO) VALUES (@P_NUFIN, 31, @P_VLRBRUTO, @P_CSLL*100/@P_VLRARECEBER, @P_CSLL, -1, 'S');

END