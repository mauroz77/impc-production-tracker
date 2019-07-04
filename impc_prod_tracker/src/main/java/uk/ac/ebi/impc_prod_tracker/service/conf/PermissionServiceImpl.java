package uk.ac.ebi.impc_prod_tracker.service.conf;

import org.springframework.stereotype.Component;
import uk.ac.ebi.impc_prod_tracker.conf.security.abac.spring.ContextAwarePolicyEnforcement;
import uk.ac.ebi.impc_prod_tracker.service.plan.PlanService;

import java.util.HashMap;
import java.util.Map;

@Component
public class PermissionServiceImpl implements PermissionService
{
    private static final String CAN_REGISTER_USER_KEY = "canRegisterUser";
    private static final String CREATE_USER_ACTION = "CREATE_USER";
    private static final String EXECUTE_MANAGER_TASKS_KEY = "canExecuteManagerTasks";
    private static final String EXECUTE_MANAGER_TASKS_ACTION = "EXECUTE_MANAGER_TASKS";

    private static final String UPDATE_PLAN = "canUpdatePlan";
    private static final String UPDATE_PLAN_ACTION = "UPDATE_PLAN";

    private ContextAwarePolicyEnforcement policyEnforcement;
    private PlanService planService;

    public PermissionServiceImpl(ContextAwarePolicyEnforcement policyEnforcement, PlanService planService)
    {
        this.policyEnforcement = policyEnforcement;
        this.planService = planService;
    }

    @Override
    public Map<String, Boolean> getPermissions()
    {
        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put(
            CAN_REGISTER_USER_KEY,
            policyEnforcement.hasPermission(null, CREATE_USER_ACTION));
        permissions.put(
            EXECUTE_MANAGER_TASKS_KEY,
            policyEnforcement.hasPermission(null, EXECUTE_MANAGER_TASKS_ACTION));
        return permissions;
    }

    @Override
    public boolean getPermissionByActionOnResource(String action, String resourceId)
    {
        Object resource = null;
        String actionInPolicySystem = null;
        boolean hasPermission;
        switch (action)
        {
            case UPDATE_PLAN:
                resource = planService.getPlanByPinWithoutCheckPermissions(resourceId);
                actionInPolicySystem = UPDATE_PLAN_ACTION;
                break;

            default:
        }
        if (resource == null)
        {
            hasPermission = false;
        }
        else
        {
            hasPermission = policyEnforcement.hasPermission(resource, actionInPolicySystem);
        }

        return hasPermission;
    }

}