package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class DevastatorEveryFrame implements EveryFrameWeaponEffectPlugin {

	private float cooldown = 0f;
	private float cooldown2 = 0f;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (engine.isPaused())
            return;


        if (weapon.isFiring() && cooldown == 0f){
			cooldown = 0.1f;
            Global.getSoundPlayer().playUISound("Devastator_Charge", 1f, 0.8f);
		}
		
		if (weapon.getChargeLevel() == 1f && cooldown2 == 0f){
			cooldown2 = 0.1f;
            Global.getSoundPlayer().playUISound("Devastator_Fire", 1f, 0.8f);
		}
		
		
		
		if (cooldown > 0f){
			cooldown += amount;
			if (cooldown > 5f)
				cooldown =0f;
		}
		
		if (cooldown2 > 0f){
			cooldown2 += amount;
			if (cooldown2 > 5f)
				cooldown2 =0f;
		}
		
    }
}


