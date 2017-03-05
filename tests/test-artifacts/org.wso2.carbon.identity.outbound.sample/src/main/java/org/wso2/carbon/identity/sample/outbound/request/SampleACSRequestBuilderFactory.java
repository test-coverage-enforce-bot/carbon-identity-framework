package org.wso2.carbon.identity.sample.outbound.request;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.identity.gateway.api.exception.GatewayClientException;
import org.wso2.carbon.identity.gateway.api.exception.GatewayServerException;
import org.wso2.carbon.identity.gateway.api.request.GatewayRequestBuilderFactory;
import org.wso2.carbon.identity.gateway.processor.handler.authentication.impl.util.Utility;
import org.wso2.msf4j.Request;

public class SampleACSRequestBuilderFactory extends GatewayRequestBuilderFactory {

    @Override
    public boolean canHandle(Request request) throws GatewayClientException {
        try {
            super.canHandle(request);
        } catch (GatewayServerException e) {
        }
        String assertion = Utility.getParameter(request, "Assertion");
        if (StringUtils.isNotBlank(assertion)) {
            return true;
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 66;
    }


    public SampleACSRequest.SampleACSRequestBuilder create(Request request) throws GatewayClientException {
        super.create(request);
        SampleACSRequest.SampleACSRequestBuilder builder = new SampleACSRequest.SampleACSRequestBuilder();
        this.create(builder, request);
        return builder;
    }

    public void create(SampleACSRequest.SampleACSRequestBuilder builder, Request request) throws GatewayClientException {
        super.create(builder, request);
        builder.setRequestDataKey(Utility.getParameter(request, "RelayState"));
    }

}