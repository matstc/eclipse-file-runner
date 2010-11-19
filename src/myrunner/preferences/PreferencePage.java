package myrunner.preferences;

import myrunner.RunnerPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String RUNNER_CLASS = "runner.class";
	public static final String RUNNER_PROJECT = "runner.project";

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(RunnerPlugin.getDefault().getPreferenceStore());
		setDescription("The runner class will be given one parameter: the path of the file you want to run.");
	}

	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferencePage.RUNNER_PROJECT, "Specify the project of the runner class (needs to be an active project in the current workspace):", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferencePage.RUNNER_CLASS, "Specify the name of the runner class (e.g. com.Main):", getFieldEditorParent()));

	}

	public void init(IWorkbench workbench) {
	}

}