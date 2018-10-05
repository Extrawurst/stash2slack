package com.pragbits.bitbucketserver.rest;

import com.pragbits.bitbucketserver.NotificationLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

@XmlRootElement
@XmlAccessorType(FIELD)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class SettingsDTO {
    private boolean slackNotificationsOverrideEnabled;
    private boolean slackNotificationsEnabled;
    private boolean slackNotificationsOpenedEnabled;
    private boolean slackNotificationsReopenedEnabled;
    private boolean slackNotificationsUpdatedEnabled;
    private boolean slackNotificationsApprovedEnabled;
    private boolean slackNotificationsUnapprovedEnabled;
    private boolean slackNotificationsDeclinedEnabled;
    private boolean slackNotificationsMergedEnabled;
    private boolean slackNotificationsCommentedEnabled;
    private boolean slackNotificationsEnabledForPush;
    private boolean slackNotificationsEnabledForPersonal;
    private boolean slackNotificationsNeedsWorkEnabled;
    private NotificationLevel notificationLevel;
    private NotificationLevel notificationPrLevel;
    private String slackChannelName;
    private String slackWebHookUrl;
    private String slackUsername;
    private String slackIconUrl;
    private String slackIconEmoji;
}
