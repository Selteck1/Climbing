package ru.funtime.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class FunTimeMenuScreen extends Screen {
    private static final int BG = 0xF0191920;
    private static final int SIDEBAR = 0xFF111116;
    private static final int PANEL = 0xFF14141B;
    private static final int PANEL_HOVER = 0xFF23232E;
    private static final int BORDER = 0xFF363742;
    private static final int WHITE = 0xFFF2F2F7;
    private static final int MUTED = 0xFF88889D;
    private static final int ACCENT = 0xFF6764FF;

    private final String[] categories = {"Combat", "Visuals", "Movement", "Player", "Misc", "Settings"};
    private final List<ModuleRow> modules = new ArrayList<>();
    private int selectedCategory = 0;
    private int panelX, panelY, panelW = 430, panelH = 286;
    private boolean dragging;
    private double dragX, dragY;
    private final String serverName = "play.funtime.su";

    public FunTimeMenuScreen() {
        super(Text.literal("FunTime Visual"));
        modules.add(new ModuleRow("HUD", "Настраиваемый интерфейс", 1, true));
        modules.add(new ModuleRow("Fullbright", "Яркость мира", 1, false));
        modules.add(new ModuleRow("Zoom", "Приближение камеры", 2, false));
        modules.add(new ModuleRow("Sprint", "Удобное управление бегом", 2, false));
        modules.add(new ModuleRow("Coords", "Координаты игрока", 3, true));
        modules.add(new ModuleRow("Notifications", "Уведомления меню", 4, true));
    }

    @Override
    protected void init() {
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x88000000);
        roundedPanel(ctx, panelX, panelY, panelW, panelH, BG, BORDER);
        ctx.fill(panelX + 1, panelY + 1, panelX + 125, panelY + panelH - 1, SIDEBAR);
        ctx.fill(panelX + 14, panelY + 15, panelX + 35, panelY + 36, ACCENT);
        ctx.drawText(textRenderer, "F", panelX + 21, panelY + 20, WHITE, false);
        ctx.drawText(textRenderer, "FUNTIME", panelX + 43, panelY + 17, WHITE, false);
        ctx.drawText(textRenderer, "VISUAL CLIENT", panelX + 43, panelY + 29, MUTED, false);
        ctx.fill(panelX + 12, panelY + 48, panelX + 112, panelY + 49, BORDER);

        for (int i = 0; i < categories.length; i++) {
            int y = panelY + 60 + i * 29;
            boolean active = i == selectedCategory;
            if (active) {
                ctx.fill(panelX + 9, y - 4, panelX + 116, y + 19, 0xFF29283F);
                ctx.fill(panelX + 9, y - 4, panelX + 12, y + 19, ACCENT);
            }
            ctx.drawText(textRenderer, categories[i], panelX + 20, y + 2, active ? WHITE : MUTED, false);
        }

        int contentX = panelX + 143;
        ctx.drawText(textRenderer, categories[selectedCategory], contentX, panelY + 17, WHITE, false);
        ctx.drawText(textRenderer, "CLIENT MENU  /  1.21.11", contentX, panelY + 30, MUTED, false);
        ctx.fill(contentX, panelY + 44, panelX + panelW - 16, panelY + 45, BORDER);

        if (selectedCategory == 5) {
            ctx.drawText(textRenderer, "Server profile", contentX, panelY + 59, WHITE, false);
            roundedPanel(ctx, contentX, panelY + 75, 256, 30, PANEL, BORDER);
            ctx.drawText(textRenderer, serverName, contentX + 10, panelY + 86, 0xFFB7B5FF, false);
            ctx.drawText(textRenderer, "Профиль можно расширить позже", contentX, panelY + 118, MUTED, false);
            ctx.drawText(textRenderer, "Открытие меню: RIGHT SHIFT", contentX, panelY + 145, MUTED, false);
        } else {
            int row = 0;
            for (ModuleRow module : modules) {
                if (module.category != selectedCategory) continue;
                int y = panelY + 57 + row * 49;
                boolean hover = mouseX >= contentX && mouseX <= panelX + panelW - 16 && mouseY >= y && mouseY <= y + 39;
                roundedPanel(ctx, contentX, y, 256, 39, hover ? PANEL_HOVER : PANEL, BORDER);
                ctx.drawText(textRenderer, module.name, contentX + 10, y + 7, WHITE, false);
                ctx.drawText(textRenderer, module.description, contentX + 10, y + 21, MUTED, false);
                int sx = panelX + panelW - 47;
                int sy = y + 12;
                ctx.fill(sx, sy, sx + 25, sy + 14, module.enabled ? ACCENT : 0xFF393946);
                int knobX = module.enabled ? sx + 13 : sx + 2;
                ctx.fill(knobX, sy + 2, knobX + 10, sy + 12, WHITE);
                row++;
            }
            if (row == 0) ctx.drawText(textRenderer, "Пока нет модулей в этой вкладке", contentX, panelY + 65, MUTED, false);
        }
        ctx.drawText(textRenderer, "FUN TIME PROFILE  •  UI PREVIEW", panelX + 14, panelY + panelH - 17, MUTED, false);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void roundedPanel(DrawContext ctx, int x, int y, int w, int h, int fill, int border) {
        ctx.fill(x, y, x + w, y + h, border);
        ctx.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (mouseY >= panelY && mouseY <= panelY + 45 && mouseX >= panelX && mouseX <= panelX + panelW) {
            dragging = true; dragX = mouseX - panelX; dragY = mouseY - panelY; return true;
        }
        for (int i = 0; i < categories.length; i++) {
            int y = panelY + 56 + i * 29;
            if (mouseX >= panelX + 8 && mouseX <= panelX + 119 && mouseY >= y && mouseY <= y + 25) {
                selectedCategory = i; return true;
            }
        }
        if (selectedCategory != 5) {
            int contentX = panelX + 143, row = 0;
            for (ModuleRow module : modules) {
                if (module.category != selectedCategory) continue;
                int y = panelY + 57 + row * 49;
                if (mouseX >= contentX && mouseX <= panelX + panelW - 16 && mouseY >= y && mouseY <= y + 39) {
                    module.enabled = !module.enabled; return true;
                }
                row++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            panelX = (int) (mouseX - dragX); panelY = (int) (mouseY - dragY); return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }

    private static final class ModuleRow {
        final String name, description;
        final int category;
        boolean enabled;
        ModuleRow(String name, String description, int category, boolean enabled) {
            this.name = name; this.description = description; this.category = category; this.enabled = enabled;
        }
    }
}
