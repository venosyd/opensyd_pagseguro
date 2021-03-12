package com.venosyd.open.pagseguro.logic;

import java.util.Map;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public interface PagSeguroBS {

        /**  */
        PagSeguroBS INSTANCE = new PagSeguroBSImpl();

        /**
         * retorna o token unico do cliente no pagseguro
         */
        String token(String conta);

        /**
         * retorna a credencial usada na conta principal do pagseguro
         */
        String credential(String conta);

        /**
         * inicia uma sessao usando token e credencial para fazer as operacoes abaixo
         */
        String createSession(String conta);

        /**
         * Cria um plano
         */
        Map<String, Object> createPlan(String conta, String planoNome, String planoSigla, String planoURLCancelamento,
                        String planoPreco, String trialPeriod);

}