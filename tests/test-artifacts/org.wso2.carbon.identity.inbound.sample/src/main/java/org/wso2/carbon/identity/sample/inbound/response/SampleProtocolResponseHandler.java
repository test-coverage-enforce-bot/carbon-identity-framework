/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.sample.inbound.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.common.base.message.MessageContext;
import org.wso2.carbon.identity.gateway.api.exception.GatewayException;
import org.wso2.carbon.identity.gateway.api.exception.GatewayRuntimeException;
import org.wso2.carbon.identity.gateway.context.AuthenticationContext;
import org.wso2.carbon.identity.gateway.handler.GatewayHandlerResponse;
import org.wso2.carbon.identity.gateway.exception.AuthenticationHandlerException;
import org.wso2.carbon.identity.gateway.handler.response.AbstractResponseHandler;
import org.wso2.carbon.identity.gateway.exception.ResponseHandlerException;
import org.wso2.carbon.identity.gateway.service.GatewayClaimResolverService;
import org.wso2.carbon.identity.mgt.claim.Claim;
import org.wso2.carbon.identity.mgt.exception.IdentityStoreException;
import org.wso2.carbon.identity.mgt.exception.UserNotFoundException;
import org.wso2.carbon.identity.sample.inbound.request.SampleProtocolRequest;

import java.util.Optional;
import java.util.Set;

public class SampleProtocolResponseHandler extends AbstractResponseHandler {

    Logger logger = LoggerFactory.getLogger(SampleProtocolResponseHandler.class);
    @Override
    public GatewayHandlerResponse buildErrorResponse(AuthenticationContext authenticationContext,
                                                     GatewayException exception) throws ResponseHandlerException {
        return null;
    }

    @Override
    public GatewayHandlerResponse buildErrorResponse(AuthenticationContext authenticationContext,
                                                     GatewayRuntimeException exception)
            throws ResponseHandlerException {
        return null;
    }

    @Override
    public GatewayHandlerResponse buildResponse(AuthenticationContext authenticationContext) throws
                                                                                               ResponseHandlerException {

        SampleLoginResponse.SampleLoginResponseBuilder builder = new SampleLoginResponse.SampleLoginResponseBuilder
                (authenticationContext);
        builder.setSubject(authenticationContext.getSequenceContext().getStepContext(1).getUser().getUserIdentifier());
        setClaims(authenticationContext, builder);
        try {
            getResponseBuilderConfigs(authenticationContext);
        } catch (AuthenticationHandlerException e) {
            throw new ResponseHandlerException("Error while getting response configs");
        }
        addSessionKey(builder, authenticationContext);
        GatewayHandlerResponse response = new GatewayHandlerResponse(GatewayHandlerResponse.Status.REDIRECT, builder);
        return response;
    }

    @Override
    public boolean canHandle(MessageContext messageContext, GatewayException exception) {
        return true;
    }

    @Override
    public boolean canHandle(MessageContext messageContext, GatewayRuntimeException exception) {
        return true;
    }


    protected String getValidatorType() {
        return "SAMPLE";
    }


    public boolean canHandle(MessageContext messageContext) {
        if (messageContext instanceof AuthenticationContext) {
            return ((AuthenticationContext) messageContext).getInitialAuthenticationRequest() instanceof
                    SampleProtocolRequest;
        }
        return false;
    }

    private void setClaims(AuthenticationContext authenticationContext, SampleLoginResponse
            .SampleLoginResponseBuilder builder) {
            Set<Claim> claims = authenticationContext.getSequenceContext().getAllClaims();
            claims = GatewayClaimResolverService.getInstance().transformToOtherDialect(claims, authenticationContext
                    .getServiceProvider().getClaimConfig().getDialectUri(), Optional.of(authenticationContext.getServiceProvider
                    ().getClaimConfig().getProfile()));
            StringBuilder claimParam = new StringBuilder("");
            claims.stream().forEach(claim -> claimParam.append(claim.getClaimUri()).append(",").append(claim.getValue
                    ()).append("-"));
            builder.setClaims(claimParam.toString());

    }

}