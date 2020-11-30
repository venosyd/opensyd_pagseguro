package com.venosyd.open.pagseguro.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public interface PagSeguroRS {

    String PAGSEGURO_BASE_URL = "/pagseguro";

    String PAGSEGURO_TOKEN = "/token";

    String PAGSEGURO_CREDENTIAL = "/credential";

    String PAGSEGURO_SESSION = "/session";

    String PAGSEGURO_CC_BRAND = "/ccbrand";

    String PAGSEGURO_CC_TOKEN = "/cctoken";

    String PAGSEGURO_CC_PARCELAS = "/get-parcelas";

    String PAGSEGURO_CHECKOUT_DO = "/do-checkout";

    String PAGSEGURO_CHECKOUT_SEE = "/see-checkout";

    String PAGSEGURO_PLAN_CREATE = "/create-plan";

    String PAGSEGURO_SUBSCRIPTION_DO = "/do-subscription";

    String PAGSEGURO_SUBSCRIPTION_SEE = "/see-subscription";

    String PAGSEGURO_SUBSCRIPTION_CANCEL = "/cancel-subscription";

    @POST
    @Path(PAGSEGURO_TOKEN)
    @Produces({ MediaType.APPLICATION_JSON })
    Response pagseguroToken(String body);

    @POST
    @Path(PAGSEGURO_CREDENTIAL)
    @Produces({ MediaType.APPLICATION_JSON })
    Response pagseguroCredential(String body);

    @POST
    @Path(PAGSEGURO_SESSION)
    @Produces({ MediaType.APPLICATION_JSON })
    Response createSession(String body);

    @POST
    @Path(PAGSEGURO_PLAN_CREATE)
    @Produces({ MediaType.APPLICATION_JSON })
    Response createPlan(String body);

    @POST
    @Path(PAGSEGURO_CC_BRAND)
    @Produces({ MediaType.APPLICATION_JSON })
    Response ccBrand(String body);

    @POST
    @Path(PAGSEGURO_CC_TOKEN)
    @Produces({ MediaType.APPLICATION_JSON })
    Response ccToken(String body);

    @POST
    @Path(PAGSEGURO_CC_PARCELAS)
    @Produces({ MediaType.APPLICATION_JSON })
    Response getParcelas(String body);

    @POST
    @Path(PAGSEGURO_CHECKOUT_DO)
    @Produces({ MediaType.APPLICATION_JSON })
    Response doCheckout(String body);

    @POST
    @Path(PAGSEGURO_CHECKOUT_SEE)
    @Produces({ MediaType.APPLICATION_JSON })
    Response seeCheckout(String body);

    @POST
    @Path(PAGSEGURO_SUBSCRIPTION_DO)
    @Produces({ MediaType.APPLICATION_JSON })
    Response doSubscription(String body);

    @POST
    @Path(PAGSEGURO_SUBSCRIPTION_SEE)
    @Produces({ MediaType.APPLICATION_JSON })
    Response seeSubscription(String body);

    @POST
    @Path(PAGSEGURO_SUBSCRIPTION_CANCEL)
    @Produces({ MediaType.APPLICATION_JSON })
    Response cancelSubscription(String body);
}
