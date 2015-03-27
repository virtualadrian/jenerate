package org.jenerate.internal.manage;

import java.util.Set;

import org.jenerate.internal.data.JenerateDialogData;
import org.jenerate.internal.domain.UserActionIdentifier;
import org.jenerate.internal.domain.method.skeleton.MethodSkeleton;

public interface MethodSkeletonStrategyManager {

    Set<MethodSkeleton<? extends JenerateDialogData>> getMethodSkeletons(UserActionIdentifier userActionIdentifier);

}
