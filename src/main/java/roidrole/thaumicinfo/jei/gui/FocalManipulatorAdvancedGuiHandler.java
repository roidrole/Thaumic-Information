package roidrole.thaumicinfo.jei.gui;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import thaumcraft.client.gui.GuiFocalManipulator;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FocalManipulatorAdvancedGuiHandler implements IAdvancedGuiHandler<GuiFocalManipulator> {
    @Override
    public Class<GuiFocalManipulator> getGuiContainerClass() {
        return GuiFocalManipulator.class;
    }

    @Nullable
    @Override
    public List<Rectangle> getGuiExtraAreas(GuiFocalManipulator guiContainer) {
        return Collections.singletonList(new Rectangle(
            guiContainer.getXSize() + guiContainer.getGuiLeft(),
            guiContainer.getGuiTop(),
            130,
            guiContainer.getYSize()
        ));
    }
}
