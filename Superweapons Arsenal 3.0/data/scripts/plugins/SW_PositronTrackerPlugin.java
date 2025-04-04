package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.List;

public class SW_PositronTrackerPlugin extends BaseEveryFrameCombatPlugin {
	
    @Override
    public void advance(float amount, List events) {
		ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
		CombatEngineAPI engine = Global.getCombatEngine();
		
		if (engine.isPaused()) {return;}
			
        for (ShipAPI Ship : Global.getCombatEngine().getShips()){
			if (Ship.isAlive()){
				Ship.getMutableStats().getDynamic().getStat("SW_POSITRON_SHIELD").modifyFlat("SW_PS1" , -1f);
				Ship.getMutableStats().getDynamic().getStat("SW_POSITRON").modifyFlat("SW_P1" , -1f);
			
				//Shield debuff
				if (Ship.getMutableStats().getDynamic().getStat("SW_POSITRON_SHIELD").getModifiedValue() > 0f){
					Ship.getMutableStats().getDynamic().getStat("SW_POSITRON_SHIELD").modifyFlat("SW_PS2", Ship.getMutableStats().getDynamic().getStat("SW_POSITRON_SHIELD").getModifiedValue() + amount);
				
					Ship.getMutableStats().getEnergyShieldDamageTakenMult().modifyMult("SW_SHIELD_IONIZED", 1.2f);
					Ship.getMutableStats().getHighExplosiveShieldDamageTakenMult().modifyMult("SW_SHIELD_IONIZED", 1.2f);
					
					
					if (Ship == playerShip)
						Global.getCombatEngine().maintainStatusForPlayerShip("SW_SI", "graphics/icons/hullsys/fortress_shield.png",
						"Shield Ionized:","20% more Energy and High Explosive damage taken",true);
				
					if (Ship.getMutableStats().getDynamic().getStat("SW_POSITRON_SHIELD").getModifiedValue() > 10f)
						Ship.getMutableStats().getDynamic().getStat("SW_POSITRON_SHIELD").modifyFlat ("SW_PS2", 0f);
				}
				else{
					Ship.getMutableStats().getEnergyShieldDamageTakenMult().modifyMult("SW_SHIELD_IONIZED", 1f);
					Ship.getMutableStats().getHighExplosiveShieldDamageTakenMult().modifyMult("SW_SHIELD_IONIZED", 1f);
				}
				
				
				
				//Hull + Armor debuff
				if (Ship.getMutableStats().getDynamic().getStat("SW_POSITRON").getModifiedValue() > 0f){
					Ship.getMutableStats().getDynamic().getStat("SW_POSITRON").modifyFlat("SW_P2", Ship.getMutableStats().getDynamic().getStat("SW_POSITRON").getModifiedValue() + amount);
					
					Ship.getMutableStats().getEnergyDamageTakenMult().modifyMult("SW_IONIZATION", 1.2f);
					Ship.getMutableStats().getHighExplosiveDamageTakenMult().modifyMult("SW_IONIZATION", 1.2f);
					
					
					if (Ship == playerShip)
						Global.getCombatEngine().maintainStatusForPlayerShip("SW_HI", "graphics/icons/hullsys/temporal_shell.png",
						"Hull Ionized:","20% more Energy and High Explosive damage taken",true);
				
					if (Ship.getMutableStats().getDynamic().getStat("SW_POSITRON").getModifiedValue() > 10f)
						Ship.getMutableStats().getDynamic().getStat("SW_POSITRON").modifyFlat("SW_P2", 0f);
				}
				else {
					
					Ship.getMutableStats().getEnergyDamageTakenMult().modifyMult("SW_IONIZATION", 1f);
					Ship.getMutableStats().getHighExplosiveDamageTakenMult().modifyMult("SW_IONIZATION", 1f);
				}
			}
		}
	}
}
