package de.lukweb.fastartifact.settings;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;

public interface ProjectSettings {

    String NOTIFICATION_BUS_NAME = "fast.artifact";
    NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("FastArtifact", NotificationDisplayType.BALLOON, false);

}
