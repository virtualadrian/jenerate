package org.jenerate.internal.ui.dialogs.strategy.commonslang;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jenerate.internal.domain.data.EqualsHashCodeGenerationData;
import org.jenerate.internal.domain.data.MethodGenerationData;
import org.jenerate.internal.domain.data.impl.EqualsHashCodeGenerationDataImpl;
import org.jenerate.internal.domain.hashcode.IInitMultNumbers;
import org.jenerate.internal.domain.hashcode.impl.InitMultNumbersCustom;
import org.jenerate.internal.domain.hashcode.impl.InitMultNumbersDefault;
import org.jenerate.internal.domain.hashcode.impl.InitMultNumbersRandom;
import org.jenerate.internal.domain.identifier.CommandIdentifier;
import org.jenerate.internal.domain.identifier.StrategyIdentifier;
import org.jenerate.internal.domain.identifier.impl.MethodsGenerationCommandIdentifier;
import org.jenerate.internal.ui.dialogs.FieldDialog;
import org.jenerate.internal.ui.dialogs.strategy.DialogStrategy;

/**
 * Defines specific dialog behaviors for the equals and hashCode methods generation using the commons-lang[3] method
 * content strategies.
 * 
 * @author maudrain
 */
public class CommonsLangEqualsHashCodeDialogStrategy implements DialogStrategy<EqualsHashCodeGenerationData> {

    /**
     * Dialog settings
     */
    public static final String EQUALS_SETTINGS_SECTION = "EqualsDialog";
    public static final String HASHCODE_SETTINGS_SECTION = "HashCodeDialog";
    private static final String SETTINGS_COMPARE_REFERENCES = "CompareReferences";
    private static final String SETTINGS_INIT_MULT_TYPE = "InitMultType";
    private static final String SETTINGS_INITIAL_NUMBER = "InitialNumber";
    private static final String SETTINGS_MULTIPLIER_NUMBER = "MultiplierNumber";
    private static final String SETTINGS_CLASS_COMPARISON = "ClassComparison";

    private final StrategyIdentifier strategyIdentifier;

    private IDialogSettings equalsDialogSettings;
    private IDialogSettings hashCodeDialogSettings;
    private final Button imButtons[] = new Button[3];

    private Text initText;
    private Text multText;
    private IInitMultNumbers imNumbers[] = new IInitMultNumbers[] { new InitMultNumbersDefault(),
            new InitMultNumbersRandom(), new InitMultNumbersCustom() };
    private int initMultType;
    private int initialNumber;
    private int multiplierNumber;

    private boolean compareReferences;
    private boolean classComparison;
    private Group equalsGroup;
    private Group hashCodeGroup;

    public CommonsLangEqualsHashCodeDialogStrategy(StrategyIdentifier strategyIdentifier) {
        this.strategyIdentifier = strategyIdentifier;
    }

    @Override
    public CommandIdentifier getCommandIdentifier() {
        return MethodsGenerationCommandIdentifier.EQUALS_HASH_CODE;
    }

    @Override
    public StrategyIdentifier getStrategyIdentifier() {
        return strategyIdentifier;
    }

    @Override
    public void configureSpecificDialogSettings(IDialogSettings dialogSettings) {
        IDialogSettings equalsSettings = dialogSettings.getSection(EQUALS_SETTINGS_SECTION);
        if (equalsSettings == null) {
            equalsSettings = dialogSettings.addNewSection(EQUALS_SETTINGS_SECTION);
        }
        this.equalsDialogSettings = equalsSettings;

        compareReferences = equalsSettings.getBoolean(SETTINGS_COMPARE_REFERENCES);

        classComparison = equalsSettings.getBoolean(SETTINGS_CLASS_COMPARISON);

        IDialogSettings hashCodeSettings = dialogSettings.getSection(HASHCODE_SETTINGS_SECTION);
        if (hashCodeSettings == null) {
            hashCodeSettings = dialogSettings.addNewSection(HASHCODE_SETTINGS_SECTION);
        }
        this.hashCodeDialogSettings = hashCodeSettings;

        try {
            initMultType = hashCodeSettings.getInt(SETTINGS_INIT_MULT_TYPE);
        } catch (NumberFormatException e) {
            initMultType = 0;
        }

        try {
            initialNumber = hashCodeSettings.getInt(SETTINGS_INITIAL_NUMBER);
        } catch (NumberFormatException e) {
            initialNumber = 17;
        }

        try {
            multiplierNumber = hashCodeSettings.getInt(SETTINGS_MULTIPLIER_NUMBER);
        } catch (NumberFormatException e) {
            multiplierNumber = 37;
        }
    }

    @Override
    public void callbackBeforeDialogClosing() {
        equalsDialogSettings.put(SETTINGS_COMPARE_REFERENCES, compareReferences);
        equalsDialogSettings.put(SETTINGS_CLASS_COMPARISON, classComparison);
        imNumbers[initMultType].setNumbers(initialNumber, multiplierNumber);
        hashCodeDialogSettings.put(SETTINGS_INIT_MULT_TYPE, initMultType);
        hashCodeDialogSettings.put(SETTINGS_INITIAL_NUMBER, initialNumber);
        hashCodeDialogSettings.put(SETTINGS_MULTIPLIER_NUMBER, multiplierNumber);
    }

    @Override
    public void createSpecificComponents(FieldDialog<EqualsHashCodeGenerationData> fieldDialog) {
        if (equalsGroup == null && hashCodeGroup == null) {
            addEqualsOptions(fieldDialog);
            addInitialMultiplierOptions(fieldDialog);
        }
    }

    @Override
    public void disposeSpecificComponents() {
        if (equalsGroup != null && hashCodeGroup != null) {
            equalsGroup.dispose();
            hashCodeGroup.dispose();
            equalsGroup = null;
            hashCodeGroup = null;
        }
    }

    @Override
    public EqualsHashCodeGenerationData getData(MethodGenerationData methodGenerationData) {
        //@formatter:off
        return new EqualsHashCodeGenerationDataImpl.Builder()
                .withCheckedFields(methodGenerationData.getCheckedFields())
                .withSelectedContentStrategy(methodGenerationData.getSelectedStrategyIdentifier())
                .withElementPosition(methodGenerationData.getElementPosition())
                .withAppendSuper(methodGenerationData.appendSuper())
                .withGenerateComment(methodGenerationData.generateComment())
                .withUseBlockInIfStatements(methodGenerationData.useBlockInIfStatements())
                .withUseGettersInsteadOfFields(methodGenerationData.useGettersInsteadOfFields())
                .withCompareReferences(compareReferences)
                .withClassComparison(classComparison)
                .withInitMultNumbers(imNumbers[initMultType])
                .build();
        //@formatter:on
    }

    private Composite addEqualsOptions(FieldDialog<EqualsHashCodeGenerationData> fieldDialog) {
        Composite editableComposite = fieldDialog.getEditableComposite();
        equalsGroup = new Group(editableComposite, SWT.NONE);
        equalsGroup.setText("Equals");
        equalsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        GridLayout layout = new GridLayout(1, false);
        equalsGroup.setLayout(layout);

        createAndAddCompareReferencesButton(equalsGroup);
        createAndAddClassComparisonButton(equalsGroup);

        return editableComposite;
    }

    private void createAndAddCompareReferencesButton(Group group) {
        Button referencesButton = new Button(group, SWT.CHECK);
        referencesButton.setText("Compare object &references");
        referencesButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        referencesButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                compareReferences = (((Button) e.widget).getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        referencesButton.setSelection(compareReferences);
    }

    private void createAndAddClassComparisonButton(Group group) {
        Button comparisonButton = new Button(group, SWT.CHECK);
        comparisonButton.setText("Use c&lass comparison instead of instanceOf");
        comparisonButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        comparisonButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                classComparison = (((Button) e.widget).getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        comparisonButton.setSelection(classComparison);
    }

    private void addInitialMultiplierOptions(final FieldDialog<EqualsHashCodeGenerationData> fieldDialog) {
        hashCodeGroup = new Group(fieldDialog.getEditableComposite(), SWT.NONE);
        hashCodeGroup.setText("HashCode - Initial and multiplier numbers");
        hashCodeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout(4, false);
        hashCodeGroup.setLayout(layout);

        imButtons[0] = new Button(hashCodeGroup, SWT.RADIO);
        imButtons[0].setText("D&efault");
        GridData data = new GridData();
        data.horizontalSpan = 4;
        imButtons[0].setLayoutData(data);

        imButtons[1] = new Button(hashCodeGroup, SWT.RADIO);
        imButtons[1].setText("&Random generate");
        data = new GridData();
        data.horizontalSpan = 4;
        imButtons[1].setLayoutData(data);

        imButtons[2] = new Button(hashCodeGroup, SWT.RADIO);
        imButtons[2].setText("C&ustom:");
        data = new GridData();
        data.horizontalSpan = 4;
        imButtons[2].setLayoutData(data);

        Label initLabel = new Label(hashCodeGroup, SWT.NONE);
        initLabel.setText("Initial:");
        data = new GridData();
        data.horizontalIndent = 30;
        initLabel.setLayoutData(data);

        initText = new Text(hashCodeGroup, SWT.SINGLE | SWT.RIGHT | SWT.BORDER);
        initText.addVerifyListener(new IntegerVerifyListener(initText));
        initText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                checkInput(fieldDialog);
            }

        });
        data = new GridData(GridData.FILL_HORIZONTAL);
        initText.setLayoutData(data);

        Label multLabel = new Label(hashCodeGroup, SWT.NONE);
        multLabel.setText("Multiplier:");
        data = new GridData();
        data.horizontalIndent = 20;
        multLabel.setLayoutData(data);

        multText = new Text(hashCodeGroup, SWT.SINGLE | SWT.RIGHT | SWT.BORDER);
        multText.addVerifyListener(new IntegerVerifyListener(multText));
        multText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                checkInput(fieldDialog);
            }

        });
        data = new GridData(GridData.FILL_HORIZONTAL);
        multText.setLayoutData(data);

        imButtons[0].addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Button button = (Button) e.widget;
                if (button.getSelection()) {
                    initMultType = 0;
                }
            }
        });

        imButtons[1].addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Button button = (Button) e.widget;
                if (button.getSelection()) {
                    initMultType = 1;
                }
            }
        });

        imButtons[2].addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Button button = (Button) e.widget;
                if (button.getSelection()) {
                    initMultType = 2;
                    initText.setEnabled(true);
                    multText.setEnabled(true);
                } else {
                    initText.setEnabled(false);
                    multText.setEnabled(false);
                }
            }
        });
        setInitialValues();
    }

    private void setInitialValues() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                imButtons[initMultType].setSelection(true);
                initText.setText(String.valueOf(initialNumber));
                multText.setText(String.valueOf(multiplierNumber));
                if (initMultType != 2) {
                    initText.setEnabled(false);
                    multText.setEnabled(false);
                }
            }
        });
    }

    private void checkInput(FieldDialog<EqualsHashCodeGenerationData> fieldDialog) {
        String text = initText.getText();
        int init;
        try {
            init = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            fieldDialog.showErrorMessage("Initial number must be an odd number.");
            return;
        }
        if (init % 2 == 0) {
            fieldDialog.showErrorMessage("Initial Number must be an odd number.");
            return;
        }
        initialNumber = init;

        text = multText.getText();
        int mult;
        try {
            mult = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            fieldDialog.showErrorMessage("Multiplier number must be an odd number.");
            return;
        }
        if (mult % 2 == 0) {
            fieldDialog.showErrorMessage("Multiplier Number must be an odd number.");
            return;
        }
        multiplierNumber = mult;
        fieldDialog.clearErrorMessage();
    }

    private static class IntegerVerifyListener implements VerifyListener {

        private Text inputText;

        public IntegerVerifyListener(Text inputText) {
            super();
            this.inputText = inputText;
        }

        @Override
        public void verifyText(VerifyEvent e) {

            StringBuffer number = new StringBuffer(inputText.getText());
            number.insert(e.start, e.text);

            try {
                Integer.parseInt(number.toString());
            } catch (NumberFormatException nfe) {
                e.doit = false;
                return;
            }
        }

    }
}
