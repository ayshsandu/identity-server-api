package org.wso2.carbon.identity.rest.api.server.challenge.factories;

import org.wso2.carbon.identity.rest.api.server.challenge.ChallengesApiService;
import org.wso2.carbon.identity.rest.api.server.challenge.impl.ChallengesApiServiceImpl;

public class ChallengesApiServiceFactory {

   private final static ChallengesApiService service = new ChallengesApiServiceImpl();

   public static ChallengesApiService getChallengesApi()
   {
      return service;
   }
}
