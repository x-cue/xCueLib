package com.xcue.xcuelib.blocks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public interface CustomBlock extends Listener {

    @EventHandler
    void onBlockExplode(BlockExplodeEvent e);

    @EventHandler
    void onPistonRetract(BlockPistonRetractEvent e);

    @EventHandler
    void onPistonExtend(BlockPistonExtendEvent e);
}
