package data.hullmods;

import java.util.ArrayList;
import java.awt.*;
import java.awt.List;

import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;

public class TeslaCoil extends BaseHullMod {

	private static Color YELLOW = new Color(241, 199, 0);
	private final static int FLUX_IMPROVEMENT = 5;
	private final static int EMP_DAMAGE_REDUCTION = 25;
	private final static String HULLMOD_ID = "sw_tesla_coil";
	
	private static String Icon = "graphics/icons/hullsys/emp_emitter.png";
	private static String EXTRA = "Lightning Storm Parameters:";
	private static String S1 = "Energy parameters:";
	
	private static String S2 = "• Flux Capacity Increased: %s/%s/%s/%s.";
	private static String S3 = "• Flux Dissipation Increased: %s/%s/%s/%s.";
	private static String S4 = "• EMP Damage Reduced: %s/%s/%s/%s.";
			
	private static String S5 = "• Lightning Storm Damage/sec: %s/%s/%s/%s.";
	private static String S6 = "• Lightning Storm Radius: %s/%s/%s/%s.";
	private static String S7 = "• Lightning Storm Duration: %s/%s/%s/%s second.";
	private static String S8 = "• Lightning Storm Damage Taken: %s/%s/%s/%s.";
	
	private static String S9 = "• %s: With too many Tesla Coils installed The Lightning Storm will output enough energy to destroy everything within it's range, it's highly unlikely that friendly ships will survive.";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	}
	
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		
		int TeslaCoils = 0;
		for (WeaponAPI weapon : ship.getAllWeapons())
			if (weapon.getId() == "sw_tesla_coil")
				TeslaCoils++;
			
		if (TeslaCoils < 1)
			ship.getVariant().removeMod("sw_tesla_coil");
    }

	//Description
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		
		//Composite Color map (this was a nightmare to learn)
		Color Colors1[] = new Color[]{Misc.getPositiveHighlightColor(),Misc.getGrayColor(),Misc.getGrayColor(),Misc.getGrayColor()};
		Color Colors2[] = new Color[]{Misc.getGrayColor(),Misc.getPositiveHighlightColor(),Misc.getGrayColor(),Misc.getGrayColor()};
		Color Colors3[] = new Color[]{Misc.getGrayColor(),Misc.getGrayColor(),Misc.getPositiveHighlightColor(),Misc.getGrayColor()};
		Color Colors4[] = new Color[]{Misc.getGrayColor(),Misc.getGrayColor(),Misc.getGrayColor(), Misc.getPositiveHighlightColor()};
		
		
		float HEIGHT = 50f;
        float PAD = 10f;
		
		boolean hasLightningStormGenerator = false;
		
		int TeslaCoils = 0;
		
		for (WeaponAPI weapon : ship.getAllWeapons())
			if (weapon.getSpec().hasTag("sw_tesla_coil"))
				TeslaCoils++;
			else if (weapon.getSpec().hasTag("sw_lightning_storm"))
				hasLightningStormGenerator = true;
			
		TeslaCoils = (int)Math.min(4f , TeslaCoils); //max 4
		
		//Construct our description based on the amount of installed Tesla Coils
		
		float multiplier = 1f + TeslaCoils/4f;
		
		
		TooltipMakerAPI Xatrix = tooltip.beginImageWithText(Icon, HEIGHT);
		Xatrix.addPara(S1, 0f, YELLOW, S1);
		
		
		//I wish there was a better way to code a description with dynamic highlights
		if (TeslaCoils == 1){
			Xatrix.addPara(S2, 0f, Colors1, Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%" ,"10%", "15%", "20%");
			Xatrix.addPara(S3, 0f, Colors1, Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%" ,"10%", "15%", "20%");
			Xatrix.addPara(S4, 0f, Colors1, Misc.getRoundedValue(EMP_DAMAGE_REDUCTION * TeslaCoils) + "%" , "50%", "75%", "100%");
			tooltip.addImageWithText(PAD);
		
			if (hasLightningStormGenerator){
			tooltip.addSectionHeading("", Alignment.MID, PAD);
			tooltip.addPara(EXTRA, 0f, YELLOW, EXTRA);
			tooltip.addPara(S5, 0f, Colors1, Misc.getRoundedValue(20000 * multiplier), "30000", "35000", "40000");
			tooltip.addPara(S6, 0f, Colors1, Misc.getRoundedValue(2000 * multiplier), "3000", "3500", "4000");
			tooltip.addPara(S7, 0f, Colors1, "" + 10f * multiplier, "15", "17.5", "20");
			tooltip.addPara(S8, 0f, Colors1, "-15%", "-30%", "-45%", "-60%");
			}
		}
		else if (TeslaCoils == 2){
			Xatrix.addPara(S2, 0f, Colors2, "5%", Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%" , "15%", "20%");
			Xatrix.addPara(S3, 0f, Colors2, "5%", Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%" , "15%", "20%");
			Xatrix.addPara(S4, 0f, Colors2, "25%", Misc.getRoundedValue(EMP_DAMAGE_REDUCTION * TeslaCoils) + "%" , "75%", "100%");
			tooltip.addImageWithText(PAD);
		
			if (hasLightningStormGenerator){
			tooltip.addSectionHeading("", Alignment.MID, PAD);
			tooltip.addPara(EXTRA, 0f, YELLOW, EXTRA);
			tooltip.addPara(S5, 0f, Colors2, "25000", Misc.getRoundedValue(20000 * multiplier), "35000", "40000");
			tooltip.addPara(S6, 0f, Colors2, "2500", Misc.getRoundedValue(2000 * multiplier), "3500", "4000");
			tooltip.addPara(S7, 0f, Colors2, "12.5", "" + 10f * multiplier, "17.5", "20");
			tooltip.addPara(S8, 0f, Colors2, "-15%", "-30%", "-45%", "-60%");
			}
		}
		else if (TeslaCoils == 3){
			Xatrix.addPara(S2, 0f, Colors3, "5%", "10%", Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%", "20%");
			Xatrix.addPara(S3, 0f, Colors3, "5%", "10%", Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%", "20%");
			Xatrix.addPara(S4, 0f, Colors3, "25%", "50%", Misc.getRoundedValue(EMP_DAMAGE_REDUCTION * TeslaCoils) + "%", "100%");
			tooltip.addImageWithText(PAD);
		
			if (hasLightningStormGenerator){
			tooltip.addSectionHeading("", Alignment.MID, PAD);
			tooltip.addPara(EXTRA, 0f, YELLOW, EXTRA);
			tooltip.addPara(S5, 0f, Colors3, "25000", "30000", Misc.getRoundedValue(20000 * multiplier),"40000");
			tooltip.addPara(S6, 0f, Colors3, "2500", "3000", Misc.getRoundedValue(2000 * multiplier),"4000");
			tooltip.addPara(S7, 0f, Colors3, "12.5", "15", "" + 10f * multiplier, "20");
			tooltip.addPara(S8, 0f, Colors3, "-15%", "-30%", "-45%", "-60%");
			}
		}
		else {
			Xatrix.addPara(S2, 0f, Colors4, "5%", "10%", "15%", Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%");
			Xatrix.addPara(S3, 0f, Colors4, "5%", "10%", "15%", Misc.getRoundedValue(FLUX_IMPROVEMENT * TeslaCoils) + "%");
			Xatrix.addPara(S4, 0f, Colors4, "25%", "50%", "75%", Misc.getRoundedValue(EMP_DAMAGE_REDUCTION * TeslaCoils) + "%");
			tooltip.addImageWithText(PAD);
		
			if (hasLightningStormGenerator){
			tooltip.addSectionHeading("", Alignment.MID, PAD);
			tooltip.addPara(EXTRA, 0f, YELLOW, EXTRA);
			tooltip.addPara(S5, 0f, Colors4, "25000", "30000", "35000", Misc.getRoundedValue(20000 * multiplier));
			tooltip.addPara(S6, 0f, Colors4, "2500", "3000", "3500", Misc.getRoundedValue(2000 * multiplier));
			tooltip.addPara(S7, 0f, Colors4, "12.5", "15", "17.5", "" + 10f * multiplier);
			tooltip.addPara(S8, 0f, Colors4, "-15%", "-30%", "-45%", "-60%");
			tooltip.addPara(S9, 10f, Misc.getNegativeHighlightColor(), "WARNING");
			}
		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) 
            return "4";
		return null;
	}
	
	//Not used for now
	public String getUnapplicableReason(ShipAPI ship) {
		return null;
	}
		
	//Not used for now
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && ship.getShield() != null;
	}
	
	
	public void advanceInCombat(ShipAPI ship, float amount) {
		
		int TeslaCoils = 0;
		
		for (WeaponAPI weapon : ship.getAllWeapons())
			if (weapon.getSpec().hasTag("sw_tesla_coil"))
				TeslaCoils++;
			
		TeslaCoils = (int)Math.min(4f , TeslaCoils); //max 4
		MutableShipStatsAPI stats = ship.getMutableStats();
		
		stats.getFluxDissipation().modifyMult(HULLMOD_ID , (float)(1f + (FLUX_IMPROVEMENT * 0.01f * TeslaCoils)));
		stats.getFluxCapacity().modifyMult(HULLMOD_ID, (float)(1f + (FLUX_IMPROVEMENT * 0.01 * TeslaCoils)));
		stats.getEmpDamageTakenMult().modifyPercent(HULLMOD_ID, (float)(-1f * EMP_DAMAGE_REDUCTION * TeslaCoils));
		
	}
}