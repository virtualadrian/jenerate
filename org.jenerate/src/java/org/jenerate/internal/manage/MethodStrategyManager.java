package org.jenerate.internal.manage;

import java.util.Set;

import org.jenerate.internal.data.JenerateDialogData;
import org.jenerate.internal.domain.UserActionIdentifier;
import org.jenerate.internal.domain.method.Method;
import org.jenerate.internal.domain.method.skeleton.MethodSkeleton;

public interface MethodStrategyManager {

    Set<Method<? extends MethodSkeleton<?>, ? extends JenerateDialogData>> getMethods(
            UserActionIdentifier userActionIdentifier);

}