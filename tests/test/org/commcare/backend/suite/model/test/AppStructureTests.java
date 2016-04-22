package org.commcare.backend.suite.model.test;

import org.commcare.test.utilities.MockApp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for general app structure, like menus and commands
 *
 * @author ctsims
 */
public class AppStructureTests {

    private MockApp mApp;

    @Before
    public void setUp() throws Exception {
        mApp = new MockApp("/app_structure/");
    }

    @Test
    public void testMenuStyles() {
        Assert.assertEquals("Root Menu Style",
                "grid",
                mApp.getSession().getPlatform().getMenuDisplayStyle("root"));

        Assert.assertEquals("Common Menu Style",
                "list",
                mApp.getSession().getPlatform().getMenuDisplayStyle("m1"));

        Assert.assertEquals("Disperate Menu Style",
                null,
                mApp.getSession().getPlatform().getMenuDisplayStyle("m2"));

        Assert.assertEquals("Empty Menu",
                null,
                mApp.getSession().getPlatform().getMenuDisplayStyle("m0"));

        Assert.assertEquals("Specific override",
                "grid",
                mApp.getSession().getPlatform().getMenuDisplayStyle("m3"));
    }


}
