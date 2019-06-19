package org.wso2.carbon.identity.rest.api.server.challenge.v10.factories;

import org.wso2.carbon.identity.rest.api.server.challenge.v10.ChallengesApiService;
import org.wso2.carbon.identity.rest.api.server.challenge.v10.impl.ChallengesApiServiceImpl;

public class ChallengesApiServiceFactory {

   private final static ChallengesApiService service = new ChallengesApiServiceImpl();

   public static ChallengesApiService getChallengesApi()
   {
      return service;
   }
}
