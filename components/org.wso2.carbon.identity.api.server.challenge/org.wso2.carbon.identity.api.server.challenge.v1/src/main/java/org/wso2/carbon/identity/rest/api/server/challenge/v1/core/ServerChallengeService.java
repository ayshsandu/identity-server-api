/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.rest.api.server.challenge.v1.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.identity.api.server.challenge.common.Constants;
import org.wso2.carbon.identity.api.server.challenge.common.error.APIError;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.recovery.ChallengeQuestionManager;
import org.wso2.carbon.identity.recovery.IdentityRecoveryException;
import org.wso2.carbon.identity.recovery.IdentityRecoveryServerException;
import org.wso2.carbon.identity.recovery.model.ChallengeQuestion;
import org.wso2.carbon.identity.rest.api.server.challenge.v1.core.functions.ChallengeQuestionToExternal;
import org.wso2.carbon.identity.rest.api.server.challenge.v1.dto.ChallengeQuestionDTO;
import org.wso2.carbon.identity.rest.api.server.challenge.v1.dto.ChallengeQuestionPatchDTO;
import org.wso2.carbon.identity.rest.api.server.challenge.v1.dto.ChallengeSetDTO;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.wso2.carbon.identity.api.server.challenge.common.Constants.OPERATION_ADD;
import static org.wso2.carbon.identity.recovery.IdentityRecoveryConstants.LOCALE_EN_US;

public class ServerChallengeService {
    private static final Log log = LogFactory.getLog(ServerChallengeService.class);
    private static ChallengeQuestionManager questionManager = ChallengeQuestionManager.getInstance();
    public static final String WSO2_CLAIM_DIALECT = "http://wso2.org/claims/";

    public String getTenantDomainFromContext() {

        String tenantDomain = MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
        if (IdentityUtil.threadLocalProperties.get().get(Constants.TENANT_NAME_FROM_CONTEXT) != null) {
            tenantDomain = (String) IdentityUtil.threadLocalProperties.get().get(Constants.TENANT_NAME_FROM_CONTEXT);
        }
        return tenantDomain;
    }

    public List<ChallengeSetDTO> getChallenges(String locale, Integer offset, Integer limit) {

        if (StringUtils.isEmpty(locale)) {
            try {
                return buildChallengesDTO(questionManager.getAllChallengeQuestions(getTenantDomainFromContext()),
                        offset, limit);
            } catch (IdentityRecoveryServerException e) {
                //TODO handle and throw correct error
                throw new APIError(Response.Status.INTERNAL_SERVER_ERROR, new Error.Builder().withCode("somecodee")
                        .withMessage("some message").withDescription("some description").build());
            }
        } else {
            try {
                return buildChallengesDTO(questionManager.getAllChallengeQuestions(getTenantDomainFromContext(), locale),
                        offset, limit);
            } catch (IdentityRecoveryException e) {
                //TODO handle and throw correct error
                throw new APIError(Response.Status.INTERNAL_SERVER_ERROR, new Error.Builder().withCode("somecodee")
                        .withMessage("some message").withDescription("some description").build());
            }
        }

    }

    public ChallengeSetDTO getChallengeSet(String challengeSetId, String locale, Integer offset, Integer
            limit) {

        try {
            if (StringUtils.isEmpty(locale)) {
                return buildChallengeDTO(questionManager.getAllChallengeQuestions(getTenantDomainFromContext()),
                        challengeSetId,
                        offset, limit);
            } else {
                return buildChallengeDTO(questionManager.getAllChallengeQuestions(getTenantDomainFromContext(), locale),
                        challengeSetId, offset, limit);
            }
        } catch (IdentityRecoveryException e) {
            //TODO handle and throw correct error
            throw new APIError(Response.Status.INTERNAL_SERVER_ERROR, new Error.Builder().withCode("somecodee")
                    .withMessage("some message").withDescription("some description").build());

        }

    }

    public boolean deleteQuestion(String challengeSetId, String questionId, String locale) {

        if (StringUtils.isEmpty(locale)) {
            locale = StringUtils.EMPTY;
        }
        ChallengeQuestion[] toDelete = {new ChallengeQuestion(challengeSetId, questionId, StringUtils.EMPTY, locale)};
        try {
            questionManager.deleteChallengeQuestions(toDelete, getTenantDomainFromContext());
        } catch (IdentityRecoveryException e) {
            //TODO handle and throw correct error
            throw new APIError(Response.Status.INTERNAL_SERVER_ERROR, new Error.Builder().withCode("somecodee")
                    .withMessage("some message").withDescription("some description").build());

        }
        return true;
    }

    public boolean deleteQuestionSet(String challengeSetId, String locale) {

        if (StringUtils.isEmpty(locale)) {
            locale = StringUtils.EMPTY;
        }
//        questionManager.deleteChallengeQuestionSet(challengeSetId, locale, getTenantDomainFromContext());
        return true;
    }

    public boolean addChallengeSets(List<ChallengeSetDTO> challengeSets) {

        ChallengeQuestion[] toAdd = buildChallengeQuestionSets(challengeSets);

        try {
            questionManager.addChallengeQuestions(toAdd, getTenantDomainFromContext());
        } catch (IdentityRecoveryException e) {
            //TODO handle and throw correct error
            throw new APIError(Response.Status.INTERNAL_SERVER_ERROR, new Error.Builder().withCode("somecodee")
                    .withMessage("some message").withDescription("some description").build());

        }
        return true;
    }

    public boolean updateChallengeSets(String challengeSetId, List<ChallengeQuestionDTO> challenges) {

        deleteQuestionSet(challengeSetId, null);

        List<ChallengeQuestion> questions = buildChallengeQuestions(challenges, challengeSetId);
        ChallengeQuestion[] toPut = questions.toArray(new ChallengeQuestion[questions.size()]);
        try {
            questionManager.addChallengeQuestions(toPut, getTenantDomainFromContext());
        } catch (IdentityRecoveryException e) {
            //TODO handle and throw correct error
            throw new APIError(Response.Status.INTERNAL_SERVER_ERROR, new Error.Builder().withCode("somecodee")
                    .withMessage("some message").withDescription("some description").build());
        }
        return true;
    }

    public boolean patchChallengeSet(String challengeSetId, ChallengeQuestionPatchDTO
            challengeQuestionPatchDTO) {

        if (OPERATION_ADD.equalsIgnoreCase(challengeQuestionPatchDTO.getOperation())) {
            List<ChallengeQuestionDTO> challenges = new ArrayList<>();
            ChallengeQuestionDTO challengeQuestion = challengeQuestionPatchDTO.getChallengeQuestion();
            challenges.add(challengeQuestion);
            List<ChallengeQuestion> questions = buildChallengeQuestions(challenges, challengeSetId);

            ChallengeQuestion[] toPatch = questions.toArray(new ChallengeQuestion[questions.size()]);

            try {
                questionManager.addChallengeQuestions(toPatch, getTenantDomainFromContext());
            } catch (IdentityRecoveryException e) {
                //TODO handle and throw correct error
                throw new APIError(Response.Status.INTERNAL_SERVER_ERROR, new Error.Builder().withCode("somecodee")
                        .withMessage("some message").withDescription("some description").build());
            }
            return true;
        } else {
            //TODO throw correct error
            throw new WebApplicationException();
        }

    }

    private ChallengeQuestion[] buildChallengeQuestionSets(List<ChallengeSetDTO> challengeSets) {
        List<ChallengeQuestion> questions = new ArrayList<>();
        for (ChallengeSetDTO challengeSet : challengeSets) {
            String setId = challengeSet.getQuestionSetId();
            questions = buildChallengeQuestions(challengeSet.getQuestions(), setId);
        }
        return questions.toArray(new ChallengeQuestion[questions.size()]);
    }

    private List<ChallengeQuestion> buildChallengeQuestions(List<ChallengeQuestionDTO> challengeSet, String setId) {
        List<ChallengeQuestion> questions = new ArrayList<>();
        for (ChallengeQuestionDTO q : challengeSet) {
            if (StringUtils.isBlank(q.getLocale())) {
                q.setLocale(LOCALE_EN_US);
            }
            questions.add(createChallenceQuestion(setId, q));
        }
        return questions;
    }

    private ChallengeQuestion createChallenceQuestion(String setId, ChallengeQuestionDTO q) {
        return new ChallengeQuestion(WSO2_CLAIM_DIALECT + setId, q.getQuestionId(), q.getQuestion(), q
                .getLocale());
    }

    private List<ChallengeSetDTO> buildChallengesDTO(List<ChallengeQuestion> challengeQuestions, Integer offset,
                                                     Integer limit) {

        Map<String, List<ChallengeQuestion>> challengeSets = groupChallenges(challengeQuestions);
        return challengeSets.entrySet().stream().map((e) ->
                getChallengeSetDTO(e.getKey(), e.getValue())
        ).collect(Collectors.toList());
    }

    private ChallengeSetDTO getChallengeSetDTO(String questionSetId, List<ChallengeQuestion> questions) {
        ChallengeSetDTO challenge = new ChallengeSetDTO();
        challenge.setQuestionSetId(questionSetId);
        List<ChallengeQuestionDTO> questionDTOs = questions.stream().map(new ChallengeQuestionToExternal()).collect(
                Collectors.toList());
        challenge.setQuestions(questionDTOs);
        return challenge;
    }

    private ChallengeSetDTO buildChallengeDTO(List<ChallengeQuestion> challengeQuestions, String
            challengeSetId, Integer offset, Integer limit) {

        List<ChallengeQuestion> challengeSets = filterChallengesBySetId(challengeQuestions, challengeSetId);
        return getChallengeSetDTO(challengeSetId, challengeSets);
    }

    private Map<String, List<ChallengeQuestion>> groupChallenges(List<ChallengeQuestion> challengeQuestions) {
        return challengeQuestions.stream()
                .collect(groupingBy(question -> question.getQuestionSetId().split(WSO2_CLAIM_DIALECT)[1]));
    }

    private List<ChallengeQuestion> filterChallengesBySetId(List<ChallengeQuestion> challengeQuestions, String setId) {
        return challengeQuestions.stream()
                .filter(question -> question.getQuestionSetId().split(WSO2_CLAIM_DIALECT)[1].equals(setId))
                .collect(Collectors.toList());
    }
}
