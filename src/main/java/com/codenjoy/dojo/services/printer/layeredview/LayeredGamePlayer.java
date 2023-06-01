package com.codenjoy.dojo.services.printer.layeredview;

import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.settings.SettingsReader;

import java.util.function.Supplier;

public class LayeredGamePlayer<H extends PlayerHero, F extends GameField> extends GamePlayer<H, F> {

    private Printer<PrinterData> printer;

    public LayeredGamePlayer(EventListener listener, SettingsReader settings) {
        super(listener, settings);
    }

    protected void setupPrinter(int layersCount, Supplier<F> field) {
        printer = new LayeredViewPrinter<>(
                () -> (LayeredBoardReader) field.get(),
                () -> this,
                layersCount);
    }

    public Printer<PrinterData> getPrinter() {
        return printer;
    }
}
