package myrunner;

import myrunner.preferences.PreferencePage;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class StoryRunner extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
		Object firstElement = selection.getFirstElement();
		if (!(firstElement instanceof IFile))
			return show(event, "Choose a _file_ to run as a story.");
		
		IResource chosenFile = (IFile) firstElement;

		try {

			launchMyRunnerFor(chosenFile);

		} catch (Exception e) {
			e.printStackTrace();
			show(event, "Could not run the selected file: " + e.getMessage());
			throw new RuntimeException(e);
		}
		return null;
	}

	private void launchMyRunnerFor(IResource chosenFile) throws CoreException {
		IPreferenceStore store = RunnerPlugin.getDefault().getPreferenceStore();
		String mainClass = getRunnerClass(store);
		IJavaProject runnerProject = getRunnerProject(store);
		
		ILaunchConfiguration config = buildConfig(chosenFile, runnerProject, mainClass);
		config.launch(ILaunchManager.RUN_MODE, null);
	}

	public static String getRunnerClass(IPreferenceStore store) {
		String mainClass = store.getString(PreferencePage.RUNNER_CLASS);
		return mainClass;
	}

	public static IJavaProject getRunnerProject(IPreferenceStore store) {
		String runnerProjectName = store.getString(PreferencePage.RUNNER_PROJECT);
		IJavaProject runnerProject = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(runnerProjectName));
		return runnerProject;
	}

	private ILaunchConfiguration buildConfig(IResource chosenFile, IJavaProject projectOfRunnerClass, String runnerClass)
			throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, "MyRunner");
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectOfRunnerClass.getProject().getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, runnerClass);

		String parameter = buildFullPathOf(chosenFile);

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, parameter);
		ILaunchConfiguration config = wc.doSave();
		return config;
	}

	private String buildFullPathOf(IResource chosenFile) {
		IPath root = chosenFile.getProject().getLocation(); 
		IPath file = chosenFile.getFullPath().removeFirstSegments(1);
		
		return root.append(file).toOSString();
	}

	private Object show(ExecutionEvent event, String string) {
		MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Information", string);
		return null;
	}
}
