package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.GameState;

public class GaussEveryFrame implements EveryFrameWeaponEffectPlugin {
	
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		weapon.getSpec().setUnaffectedByProjectileSpeedBonuses(true);
		
		if (engine.isPaused()) {return;}
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			
			for(DamagingProjectileAPI p : engine.getProjectiles()){
				
				if(p.getWeapon() == weapon){
					float Damage = p.getProjectileSpec().getDamage().getBaseDamage();
					if (p.getElapsed() < 1f)
						p.setDamageAmount(Damage + Damage * p.getElapsed()/2f);
					else
						p.setDamageAmount(Damage * 1.5f);
				}
			}
		}
	}
}
