package com.codenjoy.dojo.services.printer.layeredview;

import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

public abstract class LayeredField<P extends LayeredGamePlayer, H extends PlayerHero> implements GameField<P, H>, LayeredBoardReader<P> {
}
