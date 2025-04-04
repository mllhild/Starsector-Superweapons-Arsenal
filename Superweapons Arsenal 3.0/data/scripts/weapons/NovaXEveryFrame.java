package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class NovaXEveryFrame implements EveryFrameWeaponEffectPlugin {
	
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		weapon.getSpec().setUnaffectedByProjectileSpeedBonuses(true);
		
	}
}
