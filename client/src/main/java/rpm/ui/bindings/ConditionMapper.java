package rpm.ui.bindings;

import rpm.simulation.ChronicCondition;

import java.util.EnumSet;
import java.util.List;

public final class ConditionMapper {

    public static EnumSet<ChronicCondition> fromStrings(List<String> input) {
        EnumSet<ChronicCondition> set = EnumSet.noneOf(ChronicCondition.class);
        if (input == null) return set;

        for (String s : input) {
            ChronicCondition c = mapOne(s);
            if (c != null) set.add(c);
        }
        return set;
    }

    private static ChronicCondition mapOne(String s) {
        if (s == null) return null;
        switch (s.toLowerCase()) {
            case "copd": return ChronicCondition.COPD_TENDENCY;
            case "bradycardia": return ChronicCondition.BRADYCARDIA_TENDENCY;
            case "hypertension": return ChronicCondition.HYPERTENSION;
            case "heart failure risk": return ChronicCondition.HEART_FAILURE_RISK;
            case "infection risk": return ChronicCondition.INFECTION_RISK;
            case "arrhythmia": return ChronicCondition.ARRHYTHMIA_TENDENCY;
            default: return null; // "Healthy" or unknown
        }
    }

    private ConditionMapper() {}
}
