package org.gentar.biology.plan.engine.state;

import org.gentar.statemachine.EnumStateHelper;
import org.gentar.statemachine.ProcessState;
import java.util.Arrays;
import java.util.List;

public enum ProductionPlanState implements ProcessState
{
    PlanCreated("Plan Created"),
    AttemptInProgress("Attempt In Progress"),
    EmbryosObtained("Embryos Obtained"),
    GLT("GLT"),
    AttemptAborted("Attempt Aborted");

    private String internalName;

    ProductionPlanState(String internalName)
    {
        this.internalName = internalName;
    }

    public String getInternalName()
    {
        return internalName;
    }

    public static List<ProcessState> getAllStates()
    {
        return Arrays.asList(ProductionPlanState.values());
    }

    public static ProcessState getStateByInternalName(String internalName)
    {
        return EnumStateHelper.getStateByInternalName(
            Arrays.asList(ProductionPlanState.values()), internalName);
    }

    @Override
    public String getName()
    {
        return this.name();
    }
}