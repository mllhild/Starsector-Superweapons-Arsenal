package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.List;

public class SW_PhotonTrackerPlugin extends BaseEveryFrameCombatPlugin {
	
    @Override
    public void advance(float amount, List events) {
		ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
		CombatEngineAPI engine = Global.getCombatEngine();
		
		if (engine.isPaused()) {return;}
			
        for (ShipAPI Ship : Global.getCombatEngine().getShips()){
			if (Ship.isAlive()){
				Ship.getMutableStats().getDynamic().getStat("SW_PHOTON_SHIELD").modifyFlat("SW_PS1" , -1f);
				Ship.getMutableStats().getDynamic().getStat("SW_PHOTON").modifyFlat("SW_P1" , -1f);
			
				//Shield debuff
				if (Ship.getMutableStats().getDynamic().getStat("SW_PHOTON_SHIELD").getModifiedValue() > 0f){
					Ship.getMutableStats().getDynamic().getStat("SW_PHOTON_SHIELD").modifyFlat("SW_PS2", Ship.getMutableStats().getDynamic().getStat("SW_PHOTON_SHIELD").getModifiedValue() + amount);
				
					Ship.getMutableStats().getKineticShieldDamageTakenMult().modifyMult("SW_SHIELD_DESTABILIZATION", 1.3f);
					if (Ship == playerShip)
						Global.getCombatEngine().maintainStatusForPlayerShip("SW_SD", "graphics/icons/hullsys/fortress_shield.png",
						"Shield Destabilized:","30% more Kinetic damage taken",true);
				
					if (Ship.getMutableStats().getDynamic().getStat("SW_PHOTON_SHIELD").getModifiedValue() > 10f)
						Ship.getMutableStats().getDynamic().getStat("SW_PHOTON_SHIELD").modifyFlat ("SW_PS2", 0f);
				}
				else
					Ship.getMutableStats().getKineticShieldDamageTakenMult().modifyMult("SW_SHIELD_DESTABILIZATION", 1f);
				
				//Hull + Armor debuff
				if (Ship.getMutableStats().getDynamic().getStat("SW_PHOTON").getModifiedValue() > 0f){
					Ship.getMutableStats().getDynamic().getStat("SW_PHOTON").modifyFlat("SW_P2", Ship.getMutableStats().getDynamic().getStat("SW_PHOTON").getModifiedValue() + amount);
				
					Ship.getMutableStats().getKineticDamageTakenMult().modifyMult("SW_DESTABILIZATION", 2f);
					if (Ship == playerShip)
						Global.getCombatEngine().maintainStatusForPlayerShip("SW_HD", "graphics/icons/hullsys/temporal_shell.png",
						"Hull Destabilized:","100% more Kinetic damage taken",true);
				
					if (Ship.getMutableStats().getDynamic().getStat("SW_PHOTON").getModifiedValue() > 10f)
						Ship.getMutableStats().getDynamic().getStat("SW_PHOTON").modifyFlat("SW_P2", 0f);
				}
				else
					Ship.getMutableStats().getKineticDamageTakenMult().modifyMult("SW_DESTABILIZATION", 1f);
			}
		}
	}
}
