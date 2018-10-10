package com.pragbits.bitbucketserver.rest;

import com.atlassian.bitbucket.AuthorisationException;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionValidationService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.pragbits.bitbucketserver.ImmutableSlackSettings;
import com.pragbits.bitbucketserver.SlackGlobalSettingsService;
import com.pragbits.bitbucketserver.SlackSettings;
import com.pragbits.bitbucketserver.SlackSettingsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/settings")
public class RestResource {

    private final SlackSettingsService slackSettingsService;
    private final SlackGlobalSettingsService globalSettingsService;
    private final RepositoryService repositoryService;
    private final PermissionValidationService validationService;

    public RestResource(SlackSettingsService slackSettingsService,
                        SlackGlobalSettingsService globalSettingsService,
                        RepositoryService repositoryService,
                        PermissionValidationService validationService) {
        this.slackSettingsService = slackSettingsService;
        this.globalSettingsService = globalSettingsService;
        this.repositoryService = repositoryService;
        this.validationService = validationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGlobalSettings() {
        validate(() -> validationService.validateForGlobal(Permission.ADMIN));

        return Response.ok(getDTOFromGlobalSettings(globalSettingsService)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{projectKey}/{repoSlug}")
    public Response getProjectSettings(@PathParam("projectKey") String projectKey,
                                       @PathParam("repoSlug") String repoSlug) {
        Repository repository = repositoryService.getBySlug(projectKey, repoSlug);

        if (repository == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        validate(() -> validationService.validateForRepository(repository, Permission.REPO_READ));

        SlackSettings settings = slackSettingsService.getSlackSettings(repository);

        return Response.ok(getDTOFromSlackSettings(settings)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postGlobalSettings(final SettingsDTO settings) {
        validate(() -> validationService.validateForGlobal(Permission.SYS_ADMIN));

        setGlobalSettings(settings);

        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{projectKey}/{repoSlug}")
    public Response postProjectSettings(@PathParam("projectKey") String projectKey,
                                        @PathParam("repoSlug") String repoSlug,
                                        final SettingsDTO settings) {
        Repository repository = repositoryService.getBySlug(projectKey, repoSlug);

        if (repository == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        validate(() -> validationService.validateForRepository(repository, Permission.REPO_ADMIN));

        slackSettingsService.setSlackSettings(repository, getSlackSettingsFromDTO(settings));

        return Response.ok().build();
    }

    private void validate(Runnable validator) {
        try {
            validator.run();
        } catch (AuthorisationException exception) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    private SlackSettings getSlackSettingsFromDTO(final SettingsDTO settings) {
        return new ImmutableSlackSettings(settings.isSlackNotificationsOverrideEnabled(), settings.isSlackNotificationsEnabled(), settings.isSlackNotificationsOpenedEnabled(), settings.isSlackNotificationsReopenedEnabled(), settings.isSlackNotificationsUpdatedEnabled(), settings.isSlackNotificationsApprovedEnabled(), settings.isSlackNotificationsUnapprovedEnabled(), settings.isSlackNotificationsDeclinedEnabled(), settings.isSlackNotificationsMergedEnabled(), settings.isSlackNotificationsCommentedEnabled(), settings.isSlackNotificationsEnabledForPush(), settings.isSlackNotificationsEnabledForPersonal(), settings.isSlackNotificationsNeedsWorkEnabled(), settings.getNotificationLevel(), settings.getNotificationPrLevel(), settings.getSlackChannelName(), settings.getSlackWebHookUrl(), settings.getSlackUsername(), settings.getSlackIconUrl(), settings.getSlackIconEmoji());
    }

    private void setGlobalSettings(final SettingsDTO settings) {
        this.globalSettingsService.setChannelName(settings.getSlackChannelName());
        this.globalSettingsService.setWebHookUrl(settings.getSlackWebHookUrl());
        this.globalSettingsService.setChannelName(settings.getSlackChannelName());
        this.globalSettingsService.setSlackNotificationsEnabled(settings.isSlackNotificationsEnabled());
        this.globalSettingsService.setSlackNotificationsOpenedEnabled(settings.isSlackNotificationsOpenedEnabled());
        this.globalSettingsService.setSlackNotificationsReopenedEnabled(settings.isSlackNotificationsReopenedEnabled());
        this.globalSettingsService.setSlackNotificationsUpdatedEnabled(settings.isSlackNotificationsUpdatedEnabled());
        this.globalSettingsService.setSlackNotificationsApprovedEnabled(settings.isSlackNotificationsApprovedEnabled());
        this.globalSettingsService.setSlackNotificationsUnapprovedEnabled(settings.isSlackNotificationsUnapprovedEnabled());
        this.globalSettingsService.setSlackNotificationsDeclinedEnabled(settings.isSlackNotificationsDeclinedEnabled());
        this.globalSettingsService.setSlackNotificationsMergedEnabled(settings.isSlackNotificationsMergedEnabled());
        this.globalSettingsService.setSlackNotificationsCommentedEnabled(settings.isSlackNotificationsCommentedEnabled());
        this.globalSettingsService.setSlackNotificationsEnabledForPush(settings.isSlackNotificationsEnabledForPush());
        this.globalSettingsService.setNotificationLevel(settings.getNotificationLevel().value());
        this.globalSettingsService.setNotificationPrLevel(settings.getNotificationPrLevel().value());
        this.globalSettingsService.setSlackNotificationsEnabledForPersonal(settings.isSlackNotificationsEnabledForPersonal());
        this.globalSettingsService.setSlackNotificationsNeedsWorkEnabled(settings.isSlackNotificationsNeedsWorkEnabled());
        this.globalSettingsService.setUsername(settings.getSlackUsername());
        this.globalSettingsService.setIconUrl(settings.getSlackIconUrl());
        this.globalSettingsService.setIconEmoji(settings.getSlackIconEmoji());
    }

    private static SettingsDTO getDTOFromSlackSettings(SlackSettings settings) {
        return new SettingsDTO(settings.isSlackNotificationsOverrideEnabled(),
                settings.isSlackNotificationsEnabled(),
                settings.isSlackNotificationsOpenedEnabled(),
                settings.isSlackNotificationsReopenedEnabled(),
                settings.isSlackNotificationsUpdatedEnabled(),
                settings.isSlackNotificationsApprovedEnabled(),
                settings.isSlackNotificationsUnapprovedEnabled(),
                settings.isSlackNotificationsDeclinedEnabled(),
                settings.isSlackNotificationsMergedEnabled(),
                settings.isSlackNotificationsCommentedEnabled(),
                settings.isSlackNotificationsEnabledForPush(),
                settings.isSlackNotificationsEnabledForPersonal(),
                settings.isSlackNotificationsNeedsWorkEnabled(),
                settings.getNotificationLevel(),
                settings.getNotificationPrLevel(),
                settings.getSlackChannelName(),
                settings.getSlackWebHookUrl(),
                settings.getSlackUsername(),
                settings.getSlackIconUrl(),
                settings.getSlackIconEmoji());
    }

    private static SettingsDTO getDTOFromGlobalSettings(SlackGlobalSettingsService service) {
        return new SettingsDTO(false,
                service.getSlackNotificationsEnabled(),
                service.getSlackNotificationsOpenedEnabled(),
                service.getSlackNotificationsReopenedEnabled(),
                service.getSlackNotificationsUpdatedEnabled(),
                service.getSlackNotificationsApprovedEnabled(),
                service.getSlackNotificationsUnapprovedEnabled(),
                service.getSlackNotificationsDeclinedEnabled(),
                service.getSlackNotificationsMergedEnabled(),
                service.getSlackNotificationsCommentedEnabled(),
                service.getSlackNotificationsEnabledForPush(),
                service.getSlackNotificationsEnabledForPersonal(),
                service.getSlackNotificationsNeedsWorkEnabled(),
                service.getNotificationLevel(),
                service.getNotificationPrLevel(),
                service.getChannelName(),
                service.getWebHookUrl(),
                service.getUsername(),
                service.getIconUrl(),
                service.getIconEmoji());
    }
}
