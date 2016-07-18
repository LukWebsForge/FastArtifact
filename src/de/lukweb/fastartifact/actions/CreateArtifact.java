package de.lukweb.fastartifact.actions;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.Artifact;
import com.intellij.packaging.artifacts.ArtifactManager;
import com.intellij.packaging.artifacts.ArtifactType;
import com.intellij.packaging.artifacts.ModifiableArtifact;
import com.intellij.packaging.elements.CompositePackagingElement;
import com.intellij.packaging.elements.PackagingElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import de.lukweb.fastartifact.settings.ProjectSettings;

public class CreateArtifact extends AnAction {

    public CreateArtifact() {
        super("Create Artifact");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            show("There isn't a project!", NotificationType.ERROR);
            return;
        }

        Module module = findModule(event);
        if (module == null) return;
        String name = module.getName();
        ArtifactManager manager = ArtifactManager.getInstance(project);
        Artifact searchArtifact = manager.findArtifact(name);
        if (searchArtifact != null) {
            show("An artifact with this name already exists!", NotificationType.ERROR);
            return;
        }
        ArtifactType type = ArtifactType.findById("jar");
        if (type == null) {
            show("Error: Cannot find artifact type!", NotificationType.ERROR);
            return;
        }

        PackagingElementFactory elementFactory = PackagingElementFactory.getInstance();
        CompositePackagingElement<?> packagingElement = elementFactory.createArchive(name + ".jar");
        packagingElement.addOrFindChild(elementFactory.createModuleOutput(module));

        Artifact artifact = manager.addArtifact(name, type, packagingElement);

        if (artifact instanceof ModifiableArtifact) {
            ModifiableArtifact modArtifact = (ModifiableArtifact) artifact;
            modArtifact.setBuildOnMake(true);
        }

        show("Artifact with name \"" + name + "\" created!");
    }

    private void show(String text) {
        show(text, NotificationType.INFORMATION);
    }

    private void show(String text, NotificationType type) {
        ProjectSettings.NOTIFICATION_GROUP.createNotification(text, type).notify(null);
    }

    private Module findModule(AnActionEvent event) {

        Project project = event.getProject();
        if (project == null) return null;

        PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);

        if (psiFile != null) {
            Module module = ModuleUtil.findModuleForPsiElement(psiFile);
            if (module == null) {
                show("The selected file doesn't belong to a module!", NotificationType.ERROR);
            }
            return module;
        }

        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        if (virtualFile != null) {
            psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile != null) {
                Module module = ModuleUtil.findModuleForPsiElement(psiFile);
                if (module != null) {
                    return module;
                } else {
                    show("The selected file doesn't belong to a module!", NotificationType.ERROR);
                }
            }
        }

        show("You haven't got selected a file!", NotificationType.ERROR);
        return null;
    }
}
