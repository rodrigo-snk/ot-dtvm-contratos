USE [OLIVEIRAT_TREINA]
GO
/****** Object:  StoredProcedure [SANKHYA].[STP_IMP_REEMBOLSO]    Script Date: 26/12/2021 10:40:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [SANKHYA].[STP_IMP_REEMBOLSO] (@P_LINHA VARCHAR(4000)) AS
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
    @P_DESCRICAO VARCHAR(4000),
    @P_VLRTOTAL FLOAT,
    @P_CODPARC INT,
    @P_CODPROD INT,
    @P_CODNAT INT,
    @P_CODPROJ INT,
    @P_CODCENCUS INT,
    @P_DTNEG DATE,
    @P_DTVENC DATE,
	@P_CODTIPOPER INT,
	@P_RECDESP INT,
	@P_TIPO VARCHAR,

    -- VARIÁVEIS FORMATADAS PARA O SANKHYA OM
    @P_CODEMP INT,
    @P_NUIMP INT

    -- INCIALIZAÇÃO DO NUIMP
    SET @P_NUIMP = 0;


BEGIN

    SET @P_I = CHARINDEX(';', @P_LINHA)
    SET @P_LINHA2 = @P_LINHA

	-- TOP
    SET @P_LINHA2 = SUBSTRING(@P_LINHA,@P_I-1,LEN(@P_LINHA))
    SET @P_I = CHARINDEX(';', @P_LINHA)
    SET @P_CODTIPOPER = CAST(SUBSTRING(@P_LINHA,1,@P_I-1) AS INT)

	-- Serviço
	SET @P_LINHA2= SUBSTRING(@P_LINHA,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CODPROD = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS INT)

	-- Natureza
	SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CODNAT = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS INT)

    -- CNPJ
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CNPJ = CAST(REPLACE(REPLACE(REPLACE(SUBSTRING(@P_LINHA2,1,@P_I-1),'.',''),'/',''),'-','') AS VARCHAR)

    -- Projeto
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CODPROJ = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS INT)

    -- CR
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_CODCENCUS = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS INT)

    -- Empresa
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_NOMEEMP = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS VARCHAR)

	-- Descrição
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_DESCRICAO = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS VARCHAR(4000))

	-- Data de negociação
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_DTNEG = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS DATE)

    -- Data de vencimento
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_DTVENC = CAST(SUBSTRING(@P_LINHA2,1,@P_I-1) AS DATE)

	-- Valor
    SET @P_LINHA2= SUBSTRING(@P_LINHA2,@P_I+1,LEN(@P_LINHA2))
    SET @P_I = CHARINDEX(';', @P_LINHA2)
    SET @P_VLRTOTAL = CAST(REPLACE(@P_LINHA2,',','.') AS FLOAT)

	-- Seleciona o CODPARC pelo CNPJ do arquivo de importação
	SELECT @P_CODPARC = CODPARC
    FROM TGFPAR WHERE CGC_CPF = @P_CNPJ;

	-- Seleciona o CODEMP com base no arquivo de importação
	SET @P_CODEMP = CASE @P_NOMEEMP WHEN 'DTVM RJ' THEN 1
	                                WHEN 'SERVICER RJ' THEN 3
	                                WHEN 'DTVM SP' THEN 2
	                                WHEN 'SERVICER SP' THEN 12
	                                END;

    -- Seleciona o próximo CODIMP (PK da tabela temporária)
    SELECT @P_NUIMP = ISNULL(MAX(NUIMP),0)+ 1 FROM AD_IMPREEM

    INSERT INTO AD_IMPREEM (NUIMP, DHIMP, CODEMP, CODPARC, CODPROD, CODNAT, CODPROJ, CODCENCUS, VLRTOTAL, OBSERVACAO, DTNEG, DTVENC, CODTIPOPER) VALUES (@P_NUIMP, GETDATE(), @P_CODEMP, @P_CODPARC, @P_CODPROD, @P_CODNAT, @P_CODPROJ, @P_CODCENCUS, @P_VLRTOTAL, @P_DESCRICAO, @P_DTNEG, @P_DTVENC, @P_CODTIPOPER);


END