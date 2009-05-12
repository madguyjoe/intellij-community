package org.jetbrains.idea.maven.project.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.utils.MavenAction;
import org.jetbrains.idea.maven.utils.MavenSettings;

public class EditSettingsAction extends MavenAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    showSettingsFor(getProject(e));
  }

  protected void showSettingsFor(Project project) {
    MavenSettings configurable = ShowSettingsUtil.getInstance().findProjectConfigurable(project, MavenSettings.class);
    ShowSettingsUtil.getInstance().showSettingsDialog(project, configurable);
  }
}