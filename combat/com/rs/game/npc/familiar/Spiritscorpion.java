package com.rs.game.npc.familiar;

import com.rs.game.map.WorldTile;
import com.rs.game.player.Player;

import skills.summoning.Summoning.Pouch;

public class Spiritscorpion extends Familiar {

	public Spiritscorpion(Player owner, Pouch pouch, WorldTile tile,
			boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Venom Shot";
	}

	@Override
	public String getSpecialDescription() {
		return "Chance of next Ranged attack becoming mildly poisonous, given that the Ranged weapon being used can be poisoned";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 6;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ENTITY;
	}

	@Override
	public boolean submitSpecial(Object object) {
		return false;
	}
}
