package com.vaadin.flow.component.spreadsheet.test;

import com.vaadin.flow.component.spreadsheet.testbench.SpreadsheetElement;
import com.vaadin.flow.component.spreadsheet.tests.fixtures.TestFixtures;
import com.vaadin.flow.testutil.TestPath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

@TestPath("vaadin-spreadsheet")
public class SheetTabSheetIT extends AbstractSpreadsheetIT {

    @Before
    public void init() {
        open();
        createNewSpreadsheet();
    }

    @Test
    public void focus_createTab_sheetIsFocused() {
        verifySheetFocused();
        getSpreadsheet().addSheet();
        verifySheetFocused();
    }

    @Test
    public void focus_changeTab_sheetIsFocused() {
        SpreadsheetElement spreadsheet = getSpreadsheet();
        verifySheetFocused();
        spreadsheet.addSheet();
        spreadsheet.addSheet();
        verifySheetFocused();
        spreadsheet.selectSheetAt(0);
        verifySheetFocused();
        spreadsheet.selectSheetAt(1);
        verifySheetFocused();
    }

    @Test
    public void cellFocus_moveFromSheetOneToSheetTwoAndBack_cellSelectionRemains()
            throws InterruptedException {
        SpreadsheetElement spreadsheet = getSpreadsheet();

        clickCell("C5");
        spreadsheet.addSheet();
        spreadsheet.selectSheetAt(1);
        selectRegion("C3", "E5");
        spreadsheet.selectSheetAt(0);
        waitUntil(e -> spreadsheet.getCellAt("C5").isCellSelected());
        spreadsheet.selectSheetAt(1);
        getCommandExecutor().waitForVaadin();
        String[] cols = { "C", "D", "E" };
        for (String column : cols) {
            for (int row = 3; row <= 5; row++) {
                Assert.assertTrue("Cell " + column + row + " is not selected",
                        spreadsheet.getCellAt(column + "" + row)
                                .isCellSelected());
            }
        }
    }

    @Test
    public void cellFocus_selectCellThenDeleteSheetAndMoveToNextSheet_cellSelectionIsDefault() {
        SpreadsheetElement spreadsheet = getSpreadsheet();
        spreadsheet.addSheet();
        spreadsheet.addSheet();
        spreadsheet.selectSheetAt(1);
        clickCell("C4");

        loadTestFixture(TestFixtures.RemoveFixture);
        getCommandExecutor().waitForVaadin();
        Assert.assertTrue(spreadsheet.getCellAt("A1").isCellSelected());
    }

    /**
     * Uses JavaScript to determine the currently focused element.
     *
     * @return Focused element or null
     */
    protected WebElement getFocusedElement() {
        Object focusedElement = executeScript(
                "return arguments[0].shadowRoot.activeElement",
                getSpreadsheet().getWrappedElement());
        if (null != focusedElement) {
            return (WebElement) focusedElement;
        } else {
            return null;
        }
    }

    private void verifySheetFocused() {
        Assert.assertTrue("Sheet lost focus", getFocusedElement()
                .getAttribute("class").contains("bottom-right-pane"));
    }
}
