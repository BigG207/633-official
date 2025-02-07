package skills.woodcutting;


import java.util.Optional;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.constants.Animations;
import com.rs.constants.Sounds;
import com.rs.game.item.Item;
import com.rs.game.map.GameObject;
import com.rs.game.player.Player;
import com.rs.game.task.Task;
import com.rs.net.encoders.other.Animation;

import skills.HarvestingSkillAction;
import skills.Skills;
import skills.TransformableObject;

public class Woodcutting extends HarvestingSkillAction {
	
	/**
	 * The definition for the hatchet being used.
	 */
	private final Hatchet hatchet;
	
	/**
	 * The definition for the tree being cut.
	 */
	private final Tree tree;
	
	/**
	 * The object we're interfering with.
	 */
	private final GameObject object;
	
	/**
	 * The object's name.
	 */
	private final String objectName;
	
	/**
	 * Constructs a new {@link Woodcutting} skill.
	 */
	public Woodcutting(Player player, Tree tree, GameObject object) {
		super(player, Optional.of(object));
		this.tree = tree;
		this.objectName = object.getDefinitions().getName().toLowerCase();
		this.hatchet = Hatchet.getDefinition(player).orElse(null);
		this.object = object;
	}
	
	@Override
	public double successFactor() {
		return tree.getSuccess() * hatchet.getSpeed();
	}
	
	@Override
	public Optional<Item[]> removeItems() {
		return Optional.empty();
	}
	
	@Override
	public Item[] harvestItems() {
		return (tree.getItem().getId() != -1) ? new Item[]{tree.getItem()} : new Item[]{};
	}
	
	@Override
	public boolean instant() {
		return false;
	}
	
	@Override
	public boolean initialize() {
		if(!checkWoodcutting()) {
			return false;
		}
		if(object.getLife() <= 0) {
			object.setLife(tree.getLogCount());
		}
		getPlayer().getPackets().sendGameMessage("You begin to cut the " + objectName + "...");
		getPlayer().setNextAnimation(hatchet.getAnimation());
		return true;
	}
	
	@Override
	public void onSequence(Task t) {
		if(object.isDisabled()) {
			this.onStop();
			t.cancel();
		}
	}
	
	@Override
	public void onHarvest(Task t, Item[] items, boolean success) {
		if(success) {
			BirdNest.drop(getPlayer());
			player.getDetails().getStatistics()
			.addStatistic(ItemDefinitions.getItemDefinitions(tree.getItem().getId()).getName() + "_Chopped")
			.addStatistic("Logs_Cut");
			object.setLife(object.getLife() - 1);
		}
		if(object.getLife() <= 0 && !tree.isObstacle()) {
			TransformableObject obj = null;
			for(TransformableObject ob : tree.getObject()) {
				if(ob.getObjectId() == object.getId()) {
					obj = ob;
					break;
				}
			}
			if(obj != null) {
				player.getAudioManager().sendSound(Sounds.FALLING_TREE);
				GameObject.spawnTempGroundObject(new GameObject(obj.getTransformable(), 10, 0, object), tree.getRespawnTime());
				t.cancel();
			}
		}
	}
	
	@Override
	public boolean canExecute() {
		return checkWoodcutting();
	}
	
	@Override
	public void onStop() {
		getPlayer().setNextAnimation(Animations.RESET_ANIMATION);
	}
	
	@Override
	public double experience() {
		return fullLumberJack(getPlayer()) ? (tree.getExperience() * 1.05) : tree.getExperience();
	}
	
	@Override
	public Optional<Animation> animation() {
		return Optional.of(hatchet.getAnimation());
	}
	
	@Override
	public int getSkillId() {
		return Skills.WOODCUTTING;
	}
	
	private boolean checkWoodcutting() {
		if(tree == null) {
			return false;
		}
		if(Hatchet.getDefinition(player).orElse(null) == null) {
			getPlayer().getPackets().sendGameMessage("You don't have a hatchet.");
			return false;
		}
		if(player.getSkills().getLevel(Skills.WOODCUTTING) < tree.getRequirement()) {
			getPlayer().getPackets().sendGameMessage("You need a level of " + tree.getRequirement() + " to cut this " + objectName + "!");
			return false;
		}
		if(player.getSkills().getLevel(Skills.WOODCUTTING) < hatchet.getRequirement()) {
			getPlayer().getPackets().sendGameMessage("You need a level of " + hatchet.getRequirement() + " to use this hatchet!");
			return false;
		}
		if(getPlayer().getInventory().getFreeSlots() < 1 && !tree.isObstacle()) {
			getPlayer().getPackets().sendGameMessage("You do not have any space left in your inventory.");
			return false;
		}
		
		return true;
	}
	
	private static boolean fullLumberJack(Player player) {
		return player.getEquipment() != null && player.getEquipment().containsAll(10933, 10939, 10940, 10941);
	}
}