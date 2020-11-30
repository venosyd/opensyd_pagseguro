package com.venosyd.open.pagseguro.logic;

import java.util.Map;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public interface SubscriptionBS {

        /**  */
        SubscriptionBS INSTANCE = new SubscriptionBSImpl();

        /**
         * Aderir ao plano de mensalidade fixa
         */
        Map<String, Object> doSubcription(String conta, String sessionID, String planoID, String planoSigla,
                        String planoPreco, String clienteNome, String clienteCPF, String clienteDDD,
                        String clientePhone, String clienteEmail, String clienteHash, String enderecoRua,
                        String enderecoNumero, String enderecoDistrito, String enderecoCidade, String enderecoEstado,
                        String enderecoCEP, String ccNumero, String ccCVV, String ccMesExpiracao, String ccAnoExpiracao,
                        String ccDiaNascimento, String database);

        /**
         * Verifica se a assinatura foi paga
         */
        Map<String, Object> seeSubscription(String conta, String subscriptionCode);

        /**
         * Verifica se a assinatura foi paga
         */
        Map<String, Object> cancelSubscription(String conta, String subscriptionCode);

}