package data.hullmods;

import java.util.ArrayList;
import java.awt.*;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;


public class TitaniumArmor extends BaseHullMod {
	
	//Stats
	private final static float REPAIR_RATE = 0.005f;
	private final static float DAMAGE_REDUCTION = 25f;
	private final static float EMP_DAMAGE_REDUCTION = 50f;
	private final static float ECM_EFFECT_REDUCTION = 50f;
	
	//For a cool looking description
	private static Color YELLOW = new Color(241, 199, 0);
	private static String Icon = "graphics/icons/hullsys/temporal_shell.png";
	private static String S1 = "• Plates Properties:";
	private static String S2 = "• Armor damage taken reduced by: -%s.";
	private static String S3 = "• EMP damage taken reduced by: -%s.";
	private static String S4 = "• ECM effects are reduced by: -%s.";
	private static String S5 = "• The plates are slowly repaired after taking damage at the speed of %s/second.";
	private static String S6 = "• Incompatible with %s.";
	
	
	
	//Modify Stats
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getArmorDamageTakenMult().modifyPercent(id,  -1f * DAMAGE_REDUCTION);
		stats.getEmpDamageTakenMult().modifyPercent(id, -1f * EMP_DAMAGE_REDUCTION);
		stats.getDynamic().getStat(Stats.ELECTRONIC_WARFARE_PENALTY_MULT).modifyPercent(id, -1f * ECM_EFFECT_REDUCTION);
	}
	
	
	
	//Hullmod conflict (if any)
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ArrayList<String> deletionList = new ArrayList<String>();

		for (String hullmods : ship.getVariant().getNonBuiltInHullmods()) {
			if (hullmods.contains("heavyarmor"))
				deletionList.add(hullmods);
		}

		if (deletionList.size() > 0) 
			Global.getSoundPlayer().playUISound("cr_allied_critical", 1f, 1f);

		for (String s : deletionList) 
			ship.getVariant().removeMod(s);
	}
	
	
	
	
	//Description
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		
		float HEIGHT = 50f;
        float PAD = 10f;
		
		TooltipMakerAPI Titanium = tooltip.beginImageWithText(Icon, HEIGHT);
		Titanium.addPara(S1, 0f, YELLOW, S1);
		Titanium.addPara(S2, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(DAMAGE_REDUCTION) + "%");
		Titanium.addPara(S3, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(EMP_DAMAGE_REDUCTION) + "%");
		Titanium.addPara(S4, 0f, Misc.getPositiveHighlightColor(), Misc.getRoundedValue(ECM_EFFECT_REDUCTION) + "%");
		Titanium.addPara(S5, 0f, Misc.getPositiveHighlightColor(), REPAIR_RATE + "%");
		Titanium.addPara(" ", 0f, Misc.getPositiveHighlightColor(), " ");
		Titanium.addPara(S6, 0f, Misc.getHighlightColor(), "Heavy Armor");
		tooltip.addImageWithText(PAD);
		
	}
	
	

	//not needed for now
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}
	
	//not needed for now
	public String getUnapplicableReason(ShipAPI ship) {
		return null;
	}
		
	//not needed for now
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && ship.getShield() != null;
	}
	
	
	
	//Repairing armor in combat
	public void advanceInCombat(ShipAPI ship, float amount) {
		
		final ArmorGridAPI armor = ship.getArmorGrid();				//Get armor grid.
        final float[][] grid = armor.getGrid();						//Get every cell in the grid and save it in a 2d array.
        final float maximumArmor = armor.getMaxArmorInCell();		//Get the maximum possible armor value for any cell in the array.

		
        for (int s = 0; s < grid.length; s++)
        {
            for (int x = 0; x < grid[0].length; x++)
            {
					armor.setArmorValue(s, x, Math.min(grid[s][x] + maximumArmor * REPAIR_RATE * amount, maximumArmor));
            }
        }
	}
}