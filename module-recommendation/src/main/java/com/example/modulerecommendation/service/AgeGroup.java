package com.example.modulerecommendation.service;

public enum AgeGroup {
    TEN("10대", 10, 19),
    TWENTY("20대", 20, 29),
    THIRTY("30대", 30, 39),
    FORTY("40대", 40, 49),
    FIFTY("50대", 50, 59),
    SIXTY("60대", 60, 100);

    private final String group;
    private final int minAge;
    private final int maxAge;

    AgeGroup(String group, int minAge, int maxAge) {
        this.group = group;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public String getGroup() {
        return group;
    }

    public static String fromAge(int age) {
        for (AgeGroup ageGroup : values()) {
            if (age >= ageGroup.minAge && age <= ageGroup.maxAge) {
                return ageGroup.group;
            }
        }
        throw new IllegalArgumentException("Invalid age: " + age);
    }
}
