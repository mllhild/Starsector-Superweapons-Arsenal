package data.hullmods;

import java.awt.*;
import java.util.Objects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

public class ReflectiveShield extends BaseHullMod {

	public static Color SHIELD_COLOR = new Color(0.55f, 0.3f, 1f, 0.9f);

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldDamageTakenMult().modifyMult(id, 0.9f);
		stats.getShieldUpkeepMult().modifyMult(id, 3f);
		stats.getShieldTurnRateMult().modifyPercent(id, 40f);
		stats.getShieldUnfoldRateMult().modifyPercent(id, 40f);
	}
	
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		int WM_70_count;
		
		WM_70_count = 0;
		for (WeaponAPI weapon : ship.getAllWeapons())
			if (Objects.equals(weapon.getId(), "sw_reflective_shield"))
				WM_70_count++;
			
		if (WM_70_count < 1)
			ship.getVariant().removeMod("sw_reflective_shield");
    }


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) 
            return "WM-70 module";
		if (index == 1) 
            return "Reflect";
		if (index == 2)
            return "10%";
		if (index == 3)
            return "20%";
		if (index == 4)
            return "200%";
		return null;
	}
	
	
	public String getUnapplicableReason(ShipAPI ship) {
		return null;
	}
		
		
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && ship.getShield() != null;
	}
	
	
	public void advanceInCombat(ShipAPI ship, float amount) {
		ship.getShield().setInnerColor(SHIELD_COLOR);
		ship.getShield().setRingColor(SHIELD_COLOR.brighter());
		
		if (ship.getShield().isOn()){
			Global.getSoundPlayer().playLoop("Reflect_Shield", ship, 1f, 1f, ship.getLocation(), ship.getVelocity());
		}
	}
}