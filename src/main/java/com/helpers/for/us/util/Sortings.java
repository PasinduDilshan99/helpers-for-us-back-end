package com.you.and.me.util;

import java.util.List;

public class Sortings {

    public static final List<String> ALLOWED_ACTIVITY_SCHEDULE_SORT_COLUMNS = List.of(
            "activityName",
            "activityScheduleName",
            "destinationName",
            "durationHours",
            "season",
            "status",
            "scheduleAssumeStartDate",
            "scheduleAssumeEndDate",
            "createdAt",
            "updatedAt"
    );
    public static final List<String> ALLOWED_TOUR_SCHEDULE_SORT_COLUMNS = List.of(
            "name",
            "tourName",
            "duration",
            "startLocation",
            "endLocation",
            "season",
            "status",
            "assumeStartDate",
            "assumeEndDate",
            "createdAt",
            "updatedAt"
    );
    public static final List<String> ALLOWED_PACKAGE_SCHEDULE_SORT_COLUMNS = List.of(
            "name",
            "packageName",
            "startDate",
            "endDate",
            "durationStart",
            "durationEnd",
            "status",
            "tourScheduleName",
            "createdAt",
            "updatedAt"
    );
}